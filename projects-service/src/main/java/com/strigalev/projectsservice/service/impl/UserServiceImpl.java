package com.strigalev.projectsservice.service.impl;

import com.strigalev.projectsservice.domain.Project;
import com.strigalev.projectsservice.domain.Task;
import com.strigalev.projectsservice.domain.User;
import com.strigalev.projectsservice.mapper.UserMapper;
import com.strigalev.projectsservice.repository.UserRepository;
import com.strigalev.projectsservice.service.ProjectService;
import com.strigalev.projectsservice.service.UserService;
import com.strigalev.starter.dto.MailMessageDTO;
import com.strigalev.starter.dto.UserDTO;
import com.strigalev.starter.exception.ResourceNotFoundException;
import com.strigalev.starter.model.UserAction;
import com.strigalev.starter.rabbit.RabbitMQService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.strigalev.starter.model.Role.ADMIN;
import static com.strigalev.starter.model.Role.MANAGER;
import static com.strigalev.starter.model.UserAction.CREATE_PROJECT;
import static com.strigalev.starter.util.MethodsUtil.*;

@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final ProjectService projectService;
    private final RabbitMQService rabbitMQService;

    public UserServiceImpl(
            UserMapper userMapper,
            UserRepository userRepository,
            @Lazy ProjectService projectService,
            RabbitMQService rabbitMQService
    ) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.projectService = projectService;
        this.rabbitMQService = rabbitMQService;
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(getUserNotExistsMessage(id))
                );
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public Page<UserDTO> getUsersPageByProjectId(Long projectId, Pageable pageable) {
        Page<User> users = userRepository.findAllByProjectId(pageable, projectId);
        if (users.getContent().isEmpty()) {
            throw new ResourceNotFoundException("Page not found");
        }

        return users.map(this::mapUser);
    }

    @Override
    public Page<UserDTO> getUsersPageByTaskId(Long taskId, Pageable pageable) {
        Page<User> users = userRepository.findAllByTaskId(pageable, taskId);
        if (users.getContent().isEmpty()) {
            throw new ResourceNotFoundException("Page not found");
        }

        return users.map(this::mapUser);
    }

    private UserDTO mapUser(User user) {
        UserDTO mappedUser = userMapper.map(user);
        mappedUser.setWorkingProjectsIds(userRepository.getProjectsIdsByUserId(mappedUser.getId()));
        mappedUser.setWorkingTasksIds(userRepository.getTasksIdsByUserId(mappedUser.getId()));
        return mappedUser;
    }

    @Override
    public Page<UserDTO> getUsersPageByProjectName(String projectName, Pageable pageable) {
        return getUsersPageByProjectId(projectService.getProjectByName(projectName).getId(), pageable);
    }

    @Override
    public UserDTO getUserDto(Long id) {
        return mapUser(getUserById(id));
    }

    @Override
    public List<UserDTO> getUsersDtoByFullName(String firstName, String lastName) {
        List<User> users = userRepository.findAllByFirstNameAndLastName(firstName, lastName);

        if (users.isEmpty()) {
            throw new ResourceNotFoundException(String.format("Users with first name: %s and last name: %s are not found",
                    firstName, lastName));
        }

        return users.stream()
                .map(this::mapUser)
                .toList();
    }

    @Override
    public void sendUserTaskAction(UserAction action, Task task) {
        User user = getPrincipal();
        Project project = projectService.getProjectByTask(task);

        rabbitMQService
                .sendAuditMessage(
                        action,
                        LocalDateTime.now(),
                        userMapper.map(user),
                        project.getId(),
                        task.getId(),
                        null)
        ;
        String projectName = project.getName();

        sendEmailMessageToTaskEmployees(user, task, action, projectName);
    }

    @Override
    public void sendManagerTaskAction(UserAction action, Task task) {
        User manager = getPrincipal();
        Project project = projectService.getProjectByTask(task);
        rabbitMQService
                .sendAuditMessage(
                        action,
                        LocalDateTime.now(),
                        userMapper.map(manager),
                        project.getId(),
                        task.getId(),
                        null
                );

        String taskTittle = task.getTitle();

        sendEmailMessageToProjectManagers(manager, project, action, taskTittle);

        sendEmailMessageToTaskEmployees(manager, task, action, project.getName());
    }

    @Override
    public void sendManagerProjectAction(UserAction action, Project project, Task task) {
        User manager = getPrincipal();

        String taskTittle = task == null ? null : task.getTitle();
        Long taskId = task == null ? null : task.getId();

        rabbitMQService
                .sendAuditMessage(
                        action,
                        LocalDateTime.now(),
                        userMapper.map(manager),
                        project.getId(),
                        taskId,
                        null
                );

        if (action != CREATE_PROJECT) {
            sendEmailMessageToProjectEmployees(manager, project, action, taskTittle);
        }
    }

    @Override
    public void sendManagerAction(UserAction action, Project project, Task task, Long actionedUserId) {
        User manager = getPrincipal();

        String taskTittle = task == null ? null : task.getTitle();
        Long taskId = task == null ? null : task.getId();

        rabbitMQService
                .sendAuditMessage(
                        action,
                        LocalDateTime.now(),
                        userMapper.map(manager),
                        project.getId(),
                        taskId,
                        actionedUserId
                );

        sendEmailMessage(manager, action, project.getName(), taskTittle, getUserById(actionedUserId));
        sendEmailMessageToProjectManagers(manager, project, action, taskTittle);
    }

    private void sendEmailMessageToTaskEmployees(User sender, Task task, UserAction action, String projectName) {
        task.getEmployees().stream()
                .filter(user -> !user.equals(sender))
                .forEach(user -> sendEmailMessage(sender, action, projectName, task.getTitle(), user));
    }

    private void sendEmailMessageToProjectManagers(User sender, Project project, UserAction action, String taskTittle) {
        project.getEmployees().stream()
                .filter(user -> user.getRole() == MANAGER || user.getRole() == ADMIN)
                .filter(user -> !user.equals(sender))
                .toList()
                .forEach(user -> sendEmailMessage(sender, action, project.getName(), taskTittle, user));
    }

    private void sendEmailMessageToProjectEmployees(User sender, Project project, UserAction action, String taskTittle) {
        project.getEmployees().stream()
                .filter(user -> !user.equals(sender))
                .toList()
                .forEach(user -> sendEmailMessage(sender, action, project.getName(), taskTittle, user));
    }

    private void sendEmailMessage(
            User actionUser,
            UserAction action,
            String projectName,
            String taskTittle,
            User actionedUser
    ) {

        String managerFullNameAndEmail = actionUser.getFirstName() + " "
                + actionUser.getLastName() + " (" + actionUser.getEmail() + ")";

        rabbitMQService.sendActionMailMessage(
                action,
                taskTittle,
                projectName,
                managerFullNameAndEmail,
                actionedUser.getFirstName(),
                MailMessageDTO.builder()
                        .toEmail(actionedUser.getEmail())
                        .build()
        );
    }

    public User getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return getUserById((Long) authentication.getPrincipal());
    }

}