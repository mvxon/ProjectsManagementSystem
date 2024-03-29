package com.strigalev.projectsservice.service;


import com.strigalev.projectsservice.domain.Task;
import com.strigalev.projectsservice.domain.TaskStatus;
import com.strigalev.projectsservice.dto.TaskDTO;
import com.strigalev.starter.dto.DateIntervalDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {
    Task createTask(TaskDTO taskDTO);

    Task getTaskById(Long id);

    TaskDTO getTaskDtoById(Long id);

    void updateTask(TaskDTO taskDTO);

    void deleteTask(Long id);

    Page<TaskDTO> getTasksPageByProjectId(Pageable pageable, Long projectId);

    Page<TaskDTO> getTasksPageByProjectIdAndStatus(Pageable pageable, Long projectId, TaskStatus status);

    Page<TaskDTO> getTasksPageByProjectIdAndCreationDate(Pageable pageable, DateIntervalDTO interval, Long projectId);

    void openTask(Long taskId);

    void assignTaskToUser(Long taskId, Long userId);

    void unAssignTaskToUser(Long taskId, Long userId);

    TaskDTO setTaskDeveloping(Long taskId);

    void setTaskCompleted(Long taskId);

    TaskDTO setTaskTesting(Long taskId);

    void setTaskTested(Long taskId);
    void setTaskDocumented(Long taskId);
}