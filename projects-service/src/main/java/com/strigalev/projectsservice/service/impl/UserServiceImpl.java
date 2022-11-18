package com.strigalev.projectsservice.service.impl;

import com.strigalev.projectsservice.domain.User;
import com.strigalev.projectsservice.dto.SignUpRequest;
import com.strigalev.projectsservice.exception.ResourceNotFoundException;
import com.strigalev.projectsservice.mapper.UserMapper;
import com.strigalev.projectsservice.repository.UserRepository;
import com.strigalev.projectsservice.service.ProjectService;
import com.strigalev.projectsservice.service.UserService;
import com.strigalev.starter.dto.UserDTO;
import com.strigalev.starter.model.Role;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.strigalev.starter.util.MethodsUtil.getUserNotExistsMessage;
import static com.strigalev.starter.util.MethodsUtil.getUserWithEmailNotExistsMessage;

@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProjectService projectService;


    public UserServiceImpl(
            UserMapper userMapper,
            UserRepository userRepository,
            @Lazy PasswordEncoder passwordEncoder,
            @Lazy ProjectService projectService
    ) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.projectService = projectService;
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
    public void saveUser(SignUpRequest signUpRequest) {
        User mappedUser = userMapper.map(signUpRequest);
        mappedUser.setRole(Role.DEVELOPER);
        mappedUser.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        userRepository.save(mappedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDTO getUserDtoById(Long id) {
        return userMapper.map(getUserById(id));
    }


    @Override
    public UserDTO getUserDetailsByEmail(String email) {
        return userMapper.mapWithPassword(getUserByEmail(email));
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(
                        () -> new ResourceNotFoundException(getUserWithEmailNotExistsMessage(email))
                );
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
    public boolean isPrincipalHaveTask(Long taskId) {
        Long principalId = getPrincipalId();
        if (!getUserWorkingTasksIds(principalId).contains(taskId)) {
            throw new ResourceNotFoundException(String.format("User %oid don't have assigned task with %oid", principalId,
                    taskId));
        }
        return true;
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

    private List<Long> getUserWorkingTasksIds(Long id) {
        List<Long> tasksIds = userRepository.getTasksIdsByUserId(id);
        if (tasksIds.isEmpty()) {
            throw new ResourceNotFoundException(String.format("User with %oid don't have assigned tasks", id));
        }
        return tasksIds;
    }

    private Long getPrincipalId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = ((UserDTO) authentication.getPrincipal()).getEmail();
        return getUserByEmail(email).getId();
    }

}
