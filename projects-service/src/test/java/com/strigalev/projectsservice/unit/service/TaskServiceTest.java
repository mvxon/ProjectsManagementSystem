package com.strigalev.projectsservice.unit.service;

import com.strigalev.projectsservice.domain.Task;
import com.strigalev.projectsservice.dto.TaskDTO;
import com.strigalev.projectsservice.mapper.TaskMapper;
import com.strigalev.projectsservice.repository.TaskRepository;
import com.strigalev.projectsservice.service.ProjectService;
import com.strigalev.projectsservice.service.TaskService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@SpringBootTest
public class TaskServiceTest {
    private static final long ID = 1L;
    @Autowired
    private TaskService taskService;
    @MockBean
    private TaskMapper taskMapper;
    @MockBean
    private ProjectService projectService;
    @MockBean
    private TaskRepository taskRepository;
    @Mock
    private Task task;
    @Mock
    private TaskDTO taskDTO;

    @Test
    void createTask_shouldCallMapperAndRepository() {
        when(taskMapper.map(taskDTO)).thenReturn(task);
        when(taskDTO.getDeadLineDate()).thenReturn("2030-12-31");

        taskService.createTaskInProject(taskDTO, ID);

        verify(taskMapper).map(taskDTO);
        verify(taskRepository).save(task);
    }

    @Test
    void getTaskById_shouldCallRepository() {
        when(taskRepository.findById(ID)).thenReturn(Optional.of(task));

        final Task actual = taskService.getTaskById(ID);

        assertNotNull(actual);
        assertEquals(task, actual);
        verify(taskRepository).findById(ID);
    }

    @Test
    void updateTask_shouldCallMapperAndRepository() {
        when(taskDTO.getId()).thenReturn(ID);
        when(taskRepository.findById(ID)).thenReturn(Optional.of(task));

        taskService.updateTask(taskDTO);

        verify(taskMapper).updateTaskFromDto(taskDTO, task);
        verify(taskRepository).save(task);
    }

    @Test
    void softDeleteAllTasksByProjectId_shouldCallProjectServiceAndRepository() {
        when(projectService.isProjectWithIdExists(ID)).thenReturn(true);

        taskService.deleteTask(ID);

        verify(projectService).isProjectWithIdExists(ID);
    }

}
