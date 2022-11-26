package com.strigalev.projectsservice.service.impl;

import com.strigalev.projectsservice.domain.User;
import com.strigalev.projectsservice.dto.CompletedTaskDTO;
import com.strigalev.projectsservice.dto.SignUpRequest;
import com.strigalev.projectsservice.dto.UserStatisticDTO;
import com.strigalev.projectsservice.exception.ResourceNotFoundException;
import com.strigalev.projectsservice.feign.FeignClientService;
import com.strigalev.projectsservice.mapper.UserMapper;
import com.strigalev.projectsservice.repository.TaskRepository;
import com.strigalev.projectsservice.repository.UserRepository;
import com.strigalev.projectsservice.service.ProjectService;
import com.strigalev.projectsservice.service.UserService;
import com.strigalev.starter.dto.AuditDTO;
import com.strigalev.starter.dto.DateIntervalDTO;
import com.strigalev.starter.dto.UserDTO;
import com.strigalev.starter.model.Role;
import com.strigalev.starter.model.UserAction;
import com.strigalev.starter.rabbit.RabbitMQService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.strigalev.starter.util.MethodsUtil.getUserNotExistsMessage;
import static com.strigalev.starter.util.MethodsUtil.getUserWithEmailNotExistsMessage;

@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProjectService projectService;
    private final RabbitMQService rabbitMQService;
    private final FeignClientService feignClientService;

    public UserServiceImpl(
            UserMapper userMapper,
            UserRepository userRepository,
            TaskRepository taskRepository, @Lazy PasswordEncoder passwordEncoder,
            @Lazy ProjectService projectService,
            RabbitMQService rabbitMQService,
            FeignClientService feignClientService) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.passwordEncoder = passwordEncoder;
        this.projectService = projectService;
        this.rabbitMQService = rabbitMQService;
        this.feignClientService = feignClientService;
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
        User user = getPrincipal();
        if (user.getRole() == Role.ADMIN || user.getRole() == Role.MANAGER) {
            return true;
        }
        if (getUserWorkingTasksIds(user.getId()).contains(taskId)) {
            return true;
        }

        throw new ResourceNotFoundException(String.format("User %oid don't have assigned task with %oid", user.getId(),
                taskId));
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
    public void sendUserTaskAction(UserAction action, Long taskId) {
        User user = getPrincipal();
        rabbitMQService
                .sendAuditMessage(action, LocalDateTime.now(), user.getEmail(), user.getRole(), null, taskId,
                        null);
    }

    @Override
    public void sendUserProjectAction(UserAction action, Long projectId) {
        User user = getPrincipal();
        rabbitMQService
                .sendAuditMessage(action, LocalDateTime.now(), user.getEmail(), user.getRole(), projectId, null,
                        null);
    }

    @Override
    public void sendManagerAction(UserAction action, Long projectId, Long taskId, Long actionedUserId) {
        User user = getPrincipal();
        rabbitMQService
                .sendAuditMessage(action, LocalDateTime.now(), user.getEmail(), user.getRole(), projectId, taskId,
                        actionedUserId
                );
    }


    @Override
    public List<UserStatisticDTO> getUserStatisticBetween(
            Long[] ids,
            LocalDateTime from,
            LocalDateTime to
    ) {
        List<UserStatisticDTO> usersStatistics = new ArrayList<>();

        Arrays.sort(ids);
        Arrays.stream(ids).forEach(id -> {
            String email;

            try {
                email = getUserById(id).getEmail();
            } catch (ResourceNotFoundException e) {
                usersStatistics.add(getUserStatisticDtoOnFault(id, getUserNotExistsMessage(id)));
                return;
            }

            List<AuditDTO> statistics =
                    feignClientService.getUserCompletedTasks(
                            email,
                            DateIntervalDTO.builder()
                                    .from(from)
                                    .to(to)
                                    .build()
                    ).getBody();

            if (statistics == null || statistics.isEmpty()) {
                usersStatistics.add(getUserStatisticDtoOnFault(id, "THERE IS NO TASKS AT THAT PERIOD"));
                return;
            }

            double rate =
                    (double) statistics.size() / ChronoUnit.DAYS.between(from.toLocalDate(), to.toLocalDate());
            double scale = Math.pow(10, 4);
            double resultRate = Math.ceil(rate * scale) / scale;

            usersStatistics.add(
                    UserStatisticDTO.builder()
                            .userEmail(email)
                            .userId(id)
                            .completedTasksStatistic(mapAuditToCompletedTasks(statistics))
                            .completedTasksCount(statistics.size())
                            .completionRate(resultRate)
                            .build());
        });
        if (usersStatistics.isEmpty()) {
            throw new ResourceNotFoundException("STATISTICS NOT FOUND");
        }

        return usersStatistics;
    }

    private UserStatisticDTO getUserStatisticDtoOnFault(Long userId, String message) {
        return UserStatisticDTO.builder()
                .userId(userId)
                .message(message)
                .build();
    }

    private List<CompletedTaskDTO> mapAuditToCompletedTasks(List<AuditDTO> statistics) {

        return statistics.stream()
                .map(audit -> {
                    CompletedTaskDTO task = CompletedTaskDTO.builder()
                            .taskId(audit.getTaskId())
                            .completionDate(audit.getDate())
                            .dateOfDevStarted(audit.getDateOfDevStart())
                            .projectId(taskRepository.findProjectIdById(audit.getTaskId()))
                            .build();

                    Duration hoursAndMinutes =
                            Duration.between(audit.getDateOfDevStart().toLocalTime(), audit.getDate().toLocalTime());


                    task.setDaysTook(ChronoUnit.DAYS.between(audit.getDateOfDevStart().toLocalDate(),
                            audit.getDate().toLocalDate()));
                    task.setHoursTook(hoursAndMinutes.toHoursPart());
                    task.setMinutesTook(hoursAndMinutes.toMinutesPart());
                    return task;
                })
                .toList();
    }

    private List<Long> getUserWorkingTasksIds(Long id) {
        List<Long> tasksIds = userRepository.getTasksIdsByUserId(id);
        if (tasksIds.isEmpty()) {
            throw new ResourceNotFoundException(String.format("User with %oid don't have assigned tasks", id));
        }
        return tasksIds;
    }

    private User getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = ((UserDTO) authentication.getPrincipal()).getEmail();

        return getUserByEmail(email);
    }

}
