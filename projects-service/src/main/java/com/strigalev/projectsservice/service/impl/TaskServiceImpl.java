package com.strigalev.projectsservice.service.impl;

import com.strigalev.projectsservice.domain.Project;
import com.strigalev.projectsservice.domain.Task;
import com.strigalev.projectsservice.dto.TaskDTO;
import com.strigalev.projectsservice.exception.ResourceNotFoundException;
import com.strigalev.projectsservice.mapper.TaskListMapper;
import com.strigalev.projectsservice.mapper.TaskMapper;
import com.strigalev.projectsservice.repository.TaskRepository;
import com.strigalev.projectsservice.service.ProjectService;
import com.strigalev.projectsservice.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.strigalev.projectsservice.util.MethodsUtil.getProjectNotExistsMessage;
import static com.strigalev.projectsservice.util.MethodsUtil.getTaskNotExistsMessage;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskMapper taskMapper;
    private final TaskListMapper taskListMapper;
    private final TaskRepository taskRepository;
    private final ProjectService projectService;

    @Override
    public List<TaskDTO> getAllTasksByProjectId(Long projectId) {
        Project project = projectService.getProjectById(projectId);
        return taskListMapper.map(project.getTasks());
    }

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
    public void updateTask(TaskDTO taskDTO) {
        Task savedTask = getTaskById(taskDTO.getId());
        taskMapper.updateTaskFromDto(taskDTO, savedTask);
        taskRepository.save(savedTask);
    }

    @Override
    @Transactional
    public void softDeleteAllTasksByProjectId(Long projectId) {
        if (!projectService.isProjectWithIdExists(projectId)) {
            throw new ResourceNotFoundException(getProjectNotExistsMessage(projectId));
        }
        taskRepository.setActiveFalseAllTasksByProjectId(projectId);
    }

    @Override
    @Transactional
    public Long createTask(TaskDTO taskDTO) {
        Task task = taskMapper.map(taskDTO);
        task.setCreationDate(LocalDate.now());
        task.setDeadLineDate(LocalDate.parse(taskDTO.getDeadLineDate()));
        task.setActive(true);
        taskRepository.save(task);
        return task.getId();
    }

    @Override
    @Transactional
    public void softDeleteTask(Long id) {
        Task task = getTaskById(id);
        task.setActive(false);
        taskRepository.save(task);
    }

    @Override
    public Page<TaskDTO> getAllProjectTasksPage(Pageable pageable, Long projectId) {
        Page<Task> tasks = taskRepository.findAllByProjectId(pageable, projectId);
        if (tasks.getContent().isEmpty()) {
            throw new ResourceNotFoundException("Page not found");
        }
        return tasks.map(taskListMapper::map);
    }

    @Override
    public Page<TaskDTO> getProjectActiveTasksPage(Pageable pageable, Long projectId) {
        Page<Task> tasks = taskRepository.findAllByProjectIdAndActiveIsTrue(pageable, projectId);
        if (tasks.getContent().isEmpty()) {
            throw new ResourceNotFoundException("Page not found");
        }
        return tasks.map(taskListMapper::map);
    }

}
