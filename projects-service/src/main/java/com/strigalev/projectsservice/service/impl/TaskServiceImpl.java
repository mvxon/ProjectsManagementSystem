package com.strigalev.projectsservice.service.impl;

import com.strigalev.projectsservice.domain.Task;
import com.strigalev.projectsservice.domain.TaskStatus;
import com.strigalev.projectsservice.domain.User;
import com.strigalev.projectsservice.dto.DateDTO;
import com.strigalev.projectsservice.dto.TaskDTO;
import com.strigalev.projectsservice.exception.EmployeeException;
import com.strigalev.projectsservice.exception.InvalidStatusException;
import com.strigalev.projectsservice.exception.ResourceNotFoundException;
import com.strigalev.projectsservice.mapper.TaskListMapper;
import com.strigalev.projectsservice.mapper.TaskMapper;
import com.strigalev.projectsservice.repository.TaskRepository;
import com.strigalev.projectsservice.scheduling.SchedulingService;
import com.strigalev.projectsservice.service.ProjectService;
import com.strigalev.projectsservice.service.TaskService;
import com.strigalev.projectsservice.service.UserService;
import com.strigalev.starter.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Set;

import static com.strigalev.projectsservice.domain.TaskStatus.*;
import static com.strigalev.starter.model.Role.DEVELOPER;
import static com.strigalev.starter.model.Role.TESTER;
import static com.strigalev.starter.model.UserAction.*;
import static com.strigalev.starter.util.MethodsUtil.getTaskNotExistsMessage;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskMapper taskMapper;
    private final TaskListMapper taskListMapper;
    private final TaskRepository taskRepository;
    private final UserService userService;
    private final ProjectService projectService;
    private final SchedulingService schedulingService;

    @Override
    public Task getTaskById(Long id) {
        return taskRepository.findTaskByIdAndDeletedIsFalse(id).orElseThrow(
                () -> new ResourceNotFoundException(getTaskNotExistsMessage(id))
        );
    }

    @Override
    public TaskDTO getTaskDtoById(Long id) {
        return taskMapper.map(getTaskById(id));
    }

    @Override
    @Transactional
    public void updateTask(TaskDTO taskDTO) {
        Task savedTask = getTaskById(taskDTO.getId());
        taskMapper.updateTaskFromDto(taskDTO, savedTask);

        userService.sendUserTaskAction(UPDATE_TASK, taskRepository.save(savedTask).getId());
    }

    @Override
    @Transactional
    public void deleteTask(Long id) {
        Task task = getTaskById(id);
        task.setDeleted(true);
        task.getEmployees().clear();

        userService.sendUserTaskAction(DELETE_TASK, taskRepository.save(task).getId());
    }

    @Override
    @Transactional
    public void setTaskStatus(Long taskId, TaskStatus status) {
        Task task = getTaskById(taskId);
        task.setStatus(status);

        taskRepository.save(task);
    }

    @Override
    @Transactional
    public Long createTaskInProject(TaskDTO taskDTO, Long projectId) {
        Task task = taskMapper.map(taskDTO);
        task.setDeadLineDate(LocalDate.parse(taskDTO.getDeadLineDate()));
        task.setStatus(CREATED);
        task.setProjectId(projectId);

        userService.sendManagerAction(ADD_TASK_TO_PROJECT, projectId, taskRepository.save(task).getId(),
                null);

        return task.getId();
    }

    @Override
    public Page<TaskDTO> getTasksPageByProjectId(Pageable pageable, Long projectId) {
        Page<Task> tasks = taskRepository.findAllByProjectIdAndDeletedIsFalse(pageable, projectId);
        if (tasks.getContent().isEmpty()) {
            throw new ResourceNotFoundException("Page not found");
        }
        return tasks.map(taskListMapper::map);
    }

    @Override
    public Page<TaskDTO> getTasksPageByProjectIdAndStatus(Pageable pageable, Long projectId, TaskStatus status) {
        Page<Task> tasks =
                taskRepository.findAllByProjectIdAndStatusAndDeletedIsFalse(pageable, projectId, status);
        if (tasks.getContent().isEmpty()) {
            throw new ResourceNotFoundException("Page not found");
        }
        return tasks.map(taskListMapper::map);
    }

    @Override
    public Page<TaskDTO> getTasksPageByProjectIdAndCreationDate(Pageable pageable, DateDTO dateDTO, Long projectId) {
        Page<Task> tasks;
        LocalDate searchingDate;

        if (dateDTO.getYear() == null) {
            throw new DateTimeException("INVALID DATE");
        }

        if (dateDTO.getMonth() == null && dateDTO.getDay() == null) { // by year
            searchingDate = LocalDate.of(dateDTO.getYear(), 1, 1);
            tasks = taskRepository.findAllByCreationDateBetweenAndProjectIdAndDeletedIsFalse(
                    pageable,
                    searchingDate,
                    searchingDate.plusYears(1),
                    projectId
            );
        } else if (dateDTO.getDay() == null) { // by month
            searchingDate = LocalDate.of(dateDTO.getYear(), dateDTO.getMonth(), 1);
            tasks = taskRepository.findAllByCreationDateBetweenAndProjectIdAndDeletedIsFalse(
                    pageable,
                    searchingDate,
                    searchingDate.plusMonths(1),
                    projectId
            );
        } else { // full date
            searchingDate = LocalDate.of(dateDTO.getYear(), dateDTO.getMonth(), dateDTO.getDay());
            tasks = taskRepository.findAllByCreationDateAndProjectIdAndDeletedIsFalse(
                    pageable,
                    searchingDate,
                    projectId
            );
        }

        if (tasks.getContent().isEmpty()) {
            throw new ResourceNotFoundException("Page not found");
        }

        return tasks.map(taskMapper::map);
    }

    @Override
    @Transactional
    public void openTask(Long taskId) {
        Task task = getTaskById(taskId);
        if (task.getStatus() == CREATED) {
            task.setStatus(OPEN);
            taskRepository.save(task);

            userService.sendUserTaskAction(OPEN_TASK, taskId);
        } else {
            throw new InvalidStatusException(String.format("Task with %oid is already in %s status", taskId,
                    task.getStatus()));
        }
    }

    @Override
    @Transactional
    public void assignTaskToUser(Long taskId, Long userId) {
        Task task = getTaskById(taskId);
        User user = userService.getUserById(userId);
        Set<User> employees = task.getEmployees();

        if (projectService.isUserAndTaskMatchesOnProject(task, user) && !isTaskHasUserWithRole(task, user.getRole())) {
            if (!employees.add(user)) {
                throw new InvalidStatusException(
                        String.format("User with %oid is already assigned with %oid task", userId, taskId));
            }
        } else {
            throw new EmployeeException(String.format("User with %s role is already assigned with %oid task",
                    DEVELOPER, taskId));
        }

        userService.sendManagerAction(ASSIGN_TASK_TO_USER, null, taskId, userId);

        taskRepository.save(task);
    }

    private boolean isTaskHasUserWithRole(Task task, Role role) {
        return task.getEmployees().stream().anyMatch(user -> user.getRole() == role);
    }

    @Override
    @Transactional
    public void unAssignTaskToUser(Long taskId, Long userId) {
        Task task = getTaskById(taskId);
        User user = userService.getUserById(userId);


        if (task.getStatus() == DEVELOPING && user.getRole() == DEVELOPER) {
            throw new InvalidStatusException(String.format("Task with %oid is already in %s status",
                    taskId, task.getStatus()));
        }
        if (!task.getEmployees().remove(user)) {
            throw new EmployeeException(String.format("User %oid don't have assigned task with %oid", userId,
                    taskId));
        }

        userService.sendManagerAction(UNASSIGN_TASK_TO_USER, null, taskId, userId);

        taskRepository.save(task);
    }


    @Override
    @Transactional
    public TaskDTO setTaskDeveloping(Long taskId) {
        Task task = getTaskById(taskId);

        if (task.getStatus() == OPEN) {
            if (isTaskHasUserWithRole(task, DEVELOPER)) {
                if (userService.isPrincipalHaveTask(taskId)) {
                    setTaskStatus(taskId, DEVELOPING);

                    userService.sendUserTaskAction(TAKE_TASK_FOR_DEVELOPING, taskId);
                }
            } else {
                throw new EmployeeException(String.format("Task with %oid does not have user with role %s", taskId,
                        DEVELOPER));
            }
        } else {
            throw new InvalidStatusException(String.format("Task with %oid is already in %s status", taskId,
                    task.getStatus()));
        }
        return getTaskDtoById(taskId);
    }

    @Override
    @Transactional
    public void setTaskCompleted(Long taskId) {
        Task task = getTaskById(taskId);

        if (userService.isPrincipalHaveTask(taskId)) {
            if (task.getStatus() == DEVELOPING) {
                setTaskStatus(taskId, COMPLETED);

                userService.sendUserTaskAction(COMPLETED_TASK, taskId);
            } else {
                throw new InvalidStatusException(String.format("Task with %oid is already in %s status", taskId,
                        task.getStatus()));
            }
        }

    }

    @Override
    @Transactional
    public TaskDTO setTaskTesting(Long taskId) {
        Task task = getTaskById(taskId);

        if (task.getStatus() == COMPLETED) {
            if (isTaskHasUserWithRole(task, TESTER)) {
                if (userService.isPrincipalHaveTask(taskId)) {

                    setTaskStatus(taskId, TESTING);

                    userService.sendUserTaskAction(SET_TASK_TESTING, taskId);

                    return getTaskDtoById(taskId);
                }
            } else {
                throw new EmployeeException(String.format("Task with %oid does not have user with role %s", taskId,
                        TESTER));
            }
        }
        throw new InvalidStatusException(String.format("Task with %oid is not in %s status", taskId, COMPLETED));
    }

    @Override
    @Transactional
    public void setTaskTested(Long taskId) {
        Task task = getTaskById(taskId);

        if (userService.isPrincipalHaveTask(taskId)) {
            if (task.getStatus() == TESTING) {
                setTaskStatus(taskId, TESTED);

                userService.sendUserTaskAction(COMPLETED_TASK_TESTING, taskId);
            } else {
                throw new InvalidStatusException(String.format("Task with %oid is not in %s status", taskId, TESTING));
            }
        }

    }

    @Override
    @Transactional
    public void setTaskDocumented(Long taskId) {
        Task task = getTaskById(taskId);

        if (userService.isPrincipalHaveTask(taskId)) {
            if (task.getStatus() == TESTED) {
                setTaskStatus(taskId, DOCUMENTED);

                userService.sendUserTaskAction(SET_TASK_DOCUMENTED, taskId);

                schedulingService.schedule(taskId);
            } else {
                throw new InvalidStatusException(String.format("Task with %oid is not in %s status", taskId, COMPLETED));
            }
        }

    }

}
