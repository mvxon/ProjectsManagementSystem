package com.strigalev.projectsservice.service;


import com.strigalev.projectsservice.domain.Task;
import com.strigalev.projectsservice.domain.TaskStatus;
import com.strigalev.projectsservice.dto.TaskDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {
    Long createTask(TaskDTO taskDTO);

    Task getTaskById(Long id);

    TaskDTO getTaskDtoById(Long id);

    void updateTask(TaskDTO taskDTO);

    void deleteTask(Long id);

    void setTaskStatus(Long taskId, TaskStatus status);

    Page<TaskDTO> getAllProjectTasksPage(Pageable pageable, Long projectId);

    Page<TaskDTO> getProjectTasksPageByStatus(Pageable pageable, Long projectId, TaskStatus status);

    void openTask(Long taskId);

    void assignTaskToUser(Long taskId, Long userId);

    void unAssignTaskToUser(Long taskId, Long userId);

    TaskDTO takeTaskForDeveloping(Long taskId);

    void setTaskCompleted(Long taskId);

    TaskDTO takeTaskForTesting(Long taskId);

    void setTaskDocumented(Long taskId);
}