package com.strigalev.projectsservice.service.impl;

import com.strigalev.projectsservice.domain.Task;
import com.strigalev.projectsservice.domain.TaskStatus;
import com.strigalev.projectsservice.dto.DateDTO;
import com.strigalev.projectsservice.dto.TaskDTO;
import com.strigalev.projectsservice.exception.InvalidStatusException;
import com.strigalev.projectsservice.exception.ResourceNotFoundException;
import com.strigalev.projectsservice.mapper.TaskListMapper;
import com.strigalev.projectsservice.mapper.TaskMapper;
import com.strigalev.projectsservice.repository.TaskRepository;
import com.strigalev.projectsservice.service.TaskService;
import com.strigalev.projectsservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.time.LocalDate;

import static com.strigalev.starter.util.MethodsUtil.getTaskNotExistsMessage;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskMapper taskMapper;
    private final TaskListMapper taskListMapper;
    private final TaskRepository taskRepository;
    private final UserService userService;

    @Override
    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(
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

        taskRepository.save(savedTask);
    }

    @Override
    @Transactional
    public void deleteTask(Long id) {
        Task task = getTaskById(id);
        task.setStatus(TaskStatus.DELETED);
        task.getEmployees().clear();
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
    public Long createTask(TaskDTO taskDTO) {
        Task task = taskMapper.map(taskDTO);
        task.setDeadLineDate(LocalDate.parse(taskDTO.getDeadLineDate()));
        task.setStatus(TaskStatus.CREATED);

        return taskRepository.save(task).getId();
    }

    @Override
    public Page<TaskDTO> getTasksPageByProjectId(Pageable pageable, Long projectId) {
        Page<Task> tasks = taskRepository.findAllByProjectId(pageable, projectId);
        if (tasks.getContent().isEmpty()) {
            throw new ResourceNotFoundException("Page not found");
        }
        return tasks.map(taskListMapper::map);
    }

    @Override
    public Page<TaskDTO> getTasksPageByProjectIdAndStatus(Pageable pageable, Long projectId, TaskStatus status) {
        Page<Task> tasks = taskRepository.findAllByProjectIdAndStatus(pageable, projectId, status.name());
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
            tasks = taskRepository.findAllByCreationDateBetweenAndProjectId(
                    pageable,
                    searchingDate,
                    searchingDate.plusYears(1),
                    projectId
            );
        } else if (dateDTO.getDay() == null) { // by month
            searchingDate = LocalDate.of(dateDTO.getYear(), dateDTO.getMonth(), 1);
            tasks = taskRepository.findAllByCreationDateBetweenAndProjectId(
                    pageable,
                    searchingDate,
                    searchingDate.plusMonths(1),
                    projectId
            );
        } else { // full date
            searchingDate = LocalDate.of(dateDTO.getYear(), dateDTO.getMonth(), dateDTO.getDay());
            tasks = taskRepository.findAllByCreationDateAndProjectId(pageable, searchingDate, projectId);
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
        if (task.getStatus() == TaskStatus.CREATED) {
            setTaskStatus(taskId, TaskStatus.OPEN);
        } else {
            throw new InvalidStatusException(String.format("Task with %oid is in " +
                    task.getStatus() + " status", taskId));
        }
    }

    @Override
    @Transactional
    public void assignTaskToUser(Long taskId, Long userId) {
        var task = getTaskById(taskId);
        TaskStatus taskStatus = task.getStatus();
        if (taskStatus == TaskStatus.CREATED || taskStatus == TaskStatus.DOCUMENTED) {
            throw new InvalidStatusException(String.format("Task with %oid is in " +
                    taskStatus + " status", taskId));
        }

        if (!task.getEmployees().add(userService.getUserById(userId))) {
            throw new InvalidStatusException(
                    String.format("User with %oid is already assigned with " + taskId + "id task", userId));
        }
        if (taskStatus == TaskStatus.OPEN) {
            task.setStatus(TaskStatus.ASSIGNED);
        }
        taskRepository.save(task);
    }

    @Override
    @Transactional
    public void unAssignTaskToUser(Long taskId, Long userId) {
        var task = getTaskById(taskId);

        if (!task.getEmployees().remove(userService.getUserById(userId))) {
            throw new ResourceNotFoundException(String.format("User %oid don't have assigned task with %oid", userId,
                    taskId));
        }

        if (task.getEmployees().isEmpty() && task.getStatus() == TaskStatus.ASSIGNED) {
            task.setStatus(TaskStatus.OPEN);
        }

        taskRepository.save(task);
    }


    @Override
    @Transactional
    public TaskDTO takeTaskForDeveloping(Long taskId) {
        TaskStatus status = getTaskById(taskId).getStatus();

        if (userService.isPrincipalHaveTask(taskId) && status == TaskStatus.ASSIGNED) {
            setTaskStatus(taskId, TaskStatus.DEVELOPING);
        } else {
            throw new InvalidStatusException(String.format("Task with %oid is in %s status", taskId, status));
        }
        return getTaskDtoById(taskId);
    }

    @Override
    @Transactional
    public void setTaskCompleted(Long taskId) {
        TaskStatus status = getTaskById(taskId).getStatus();

        if (userService.isPrincipalHaveTask(taskId) && status == TaskStatus.DEVELOPING) {
            setTaskStatus(taskId, TaskStatus.COMPLETED);
        } else {
            throw new InvalidStatusException(String.format("Task with %oid is in %s status", taskId, status));
        }
    }

    @Override
    @Transactional
    public TaskDTO takeTaskForTesting(Long taskId) {
        if (userService.isPrincipalHaveTask(taskId)
                && getTaskById(taskId).getStatus() == TaskStatus.COMPLETED) {
            setTaskStatus(taskId, TaskStatus.TESTING);
        } else {
            throw new InvalidStatusException(String.format("Task with %oid is not in %s status", taskId,
                    TaskStatus.COMPLETED));
        }
        return getTaskDtoById(taskId);
    }

    @Override
    @Transactional
    public void setTaskDocumented(Long taskId) {
        Task task = getTaskById(taskId);

        if (task.getStatus() != TaskStatus.TESTING) {
            throw new InvalidStatusException(String.format("Task with %oid is not in %s status", taskId,
                    TaskStatus.TESTING));
        }
        setTaskStatus(taskId, TaskStatus.DOCUMENTED);
    }

}
