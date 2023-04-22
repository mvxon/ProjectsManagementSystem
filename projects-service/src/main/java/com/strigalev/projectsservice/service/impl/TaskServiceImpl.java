package com.strigalev.projectsservice.service.impl;

import com.strigalev.projectsservice.domain.Task;
import com.strigalev.projectsservice.domain.TaskStatus;
import com.strigalev.projectsservice.domain.User;
import com.strigalev.projectsservice.dto.TaskDTO;
import com.strigalev.projectsservice.exception.EmployeeException;
import com.strigalev.projectsservice.exception.InvalidStatusException;
import com.strigalev.projectsservice.mapper.TaskListMapper;
import com.strigalev.projectsservice.mapper.TaskMapper;
import com.strigalev.projectsservice.repository.TaskRepository;
import com.strigalev.projectsservice.scheduling.SchedulingService;
import com.strigalev.projectsservice.service.ProjectService;
import com.strigalev.projectsservice.service.TaskService;
import com.strigalev.projectsservice.service.UserService;
import com.strigalev.starter.dto.DateIntervalDTO;
import com.strigalev.starter.exception.ResourceNotFoundException;
import com.strigalev.starter.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.strigalev.projectsservice.domain.TaskStatus.*;
import static com.strigalev.starter.model.Role.*;
import static com.strigalev.starter.model.UserAction.*;
import static com.strigalev.starter.util.MethodsUtil.*;

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

        userService.sendManagerTaskAction(UPDATE_TASK, savedTask);
    }

    @Override
    @Transactional
    public void deleteTask(Long id) {
        Task task = getTaskById(id);
        task.setDeleted(true);

        userService.sendManagerTaskAction(DELETE_TASK, task);
    }

    @Override
    public Task createTask(TaskDTO taskDTO) {
        Task task = taskMapper.map(taskDTO);
        task.setStatus(CREATED);

        return taskRepository.save(task);
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
    public Page<TaskDTO> getTasksPageByProjectIdAndCreationDate(
            Pageable pageable,
            DateIntervalDTO interval,
            Long projectId
    ) {
        Page<Task> tasks;

        tasks = taskRepository.findAllByCreationDateBetweenAndProjectIdAndDeletedIsFalse(
                pageable,
                interval.getFrom(),
                interval.getTo(),
                projectId
        );

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

            userService.sendManagerTaskAction(OPEN_TASK, task);
        } else {
            throw new InvalidStatusException(getTaskIsAlreadyInStatusMessage(taskId, task.getStatus().name()));
        }
    }

    @Override
    @Transactional
    public void assignTaskToUser(Long taskId, Long userId) {
        Task task = getTaskById(taskId);
        User user = userService.getUserById(userId);
        Set<User> employees = task.getEmployees();

        if (projectService.isUserAndTaskMatchesOnProject(task, user) && isTaskNotHasUserWithRole(task, user.getRole())) {
            if (employees.add(user)) {
                employees.add(userService.getPrincipal()); // add principal(manager/admin) to track task
            } else {
                throw new InvalidStatusException(getUserIsAlreadyAssignedWithTaskMessage(userId, taskId));
            }
        }
        userService.sendManagerAction(ASSIGN_TASK_TO_USER, projectService.getProjectByTask(task), task, userId);

        taskRepository.save(task);
    }

    private boolean isTaskNotHasUserWithRole(Task task, Role role) {
        if (task.getEmployees().stream().noneMatch(user -> user.getRole() == role)) {
            return true;
        } else {
            throw new EmployeeException(getTaskIsAlreadyHasUserWithRoleMessage(task.getId(), role));
        }
    }

    @Override
    @Transactional
    public void unAssignTaskToUser(Long taskId, Long userId) {
        Task task = getTaskById(taskId);
        User user = userService.getUserById(userId);
        TaskStatus status = task.getStatus();


        if ((status.ordinal() >= DEVELOPING.ordinal() && user.getRole() == DEVELOPER) ||
                (status.ordinal() >= TESTING.ordinal() && user.getRole() == TESTER)
        ) {
            throw new InvalidStatusException(getTaskIsAlreadyInStatusMessage(taskId, task.getStatus().name()));
        }
        if (!task.getEmployees().remove(user)) {
            throw new EmployeeException(getUserNotAssignedWithTaskMessage(userId, taskId));
        }

        userService.sendManagerAction(UNASSIGN_TASK_TO_USER, projectService.getProjectByTask(task), task, userId);

        taskRepository.save(task);
    }


    @Override
    @Transactional
    public TaskDTO setTaskDeveloping(Long taskId) {
        Task task = getTaskById(taskId);

        if (isPrincipalHaveTask(task)) {
            if (task.getStatus() == OPEN) {
                task.setStatus(DEVELOPING);

                userService.sendUserTaskAction(TAKE_TASK_FOR_DEVELOPING, task);
            } else {
                throw new InvalidStatusException(getTaskIsAlreadyInStatusMessage(taskId, task.getStatus().name()));
            }
        }
        return taskMapper.map(task);
    }

    @Override
    @Transactional
    public void setTaskCompleted(Long taskId) {
        Task task = getTaskById(taskId);

        if (isPrincipalHaveTask(task)) {
            if (task.getStatus() == DEVELOPING) {
                task.setStatus(COMPLETED);

                userService.sendUserTaskAction(COMPLETED_TASK, task);
            } else {
                throw new InvalidStatusException(getTaskIsAlreadyInStatusMessage(taskId, task.getStatus().name()));
            }
        }
    }

    @Override
    @Transactional
    public TaskDTO setTaskTesting(Long taskId) {
        Task task = getTaskById(taskId);

        if (isPrincipalHaveTask(task)) {
            if (task.getStatus() == COMPLETED) {
                task.setStatus(TESTING);

                userService.sendUserTaskAction(TAKE_TASK_FOR_TESTING, task);

                return taskMapper.map(task);
            }
        }
        throw new InvalidStatusException(getTaskIsNotInStatusMessage(taskId, COMPLETED.name()));
    }

    @Override
    @Transactional
    public void setTaskTested(Long taskId) {
        Task task = getTaskById(taskId);

        if (isPrincipalHaveTask(task)) {
            if (task.getStatus() == TESTING) {
                task.setStatus(TESTED);

                userService.sendUserTaskAction(COMPLETED_TASK_TESTING, task);

            } else {
                throw new InvalidStatusException(getTaskIsNotInStatusMessage(taskId, TESTING.name()));
            }
        }
    }

    @Override
    @Transactional
    public void setTaskDocumented(Long taskId) {
        Task task = getTaskById(taskId);

        if (isPrincipalHaveTask(task)) {
            if (task.getStatus() == TESTED) {
                task.setStatus(DOCUMENTED);

                userService.sendUserTaskAction(SET_TASK_DOCUMENTED, task);

                schedulingService.scheduleTaskArchiving(taskId);
            } else {
                throw new InvalidStatusException(getTaskIsNotInStatusMessage(taskId, TESTED.name()));
            }
        }
    }

    public boolean isPrincipalHaveTask(Task task) {
        User principal = userService.getPrincipal();

        if (principal.getRole() == Role.ADMIN || principal.getRole() == MANAGER) {
            return true;
        }

        if (task.getEmployees().contains(principal)) {
            return true;
        } else {
            throw new ResourceNotFoundException(getUserNotAssignedWithTaskMessage(principal.getId(), task.getId()));
        }
    }
}