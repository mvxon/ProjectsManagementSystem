package com.strigalev.projectsservice.unit.service;

import com.strigalev.projectsservice.domain.Project;
import com.strigalev.projectsservice.dto.ProjectDTO;
import com.strigalev.projectsservice.mapper.ProjectMapper;
import com.strigalev.projectsservice.repository.ProjectRepository;
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
class ProjectServiceTest {
    private static final long ID = 1L;
    private static final String PROJECT_NAME = "ProjectX";
    @Autowired
    private ProjectService projectService;
    @MockBean
    private ProjectMapper projectMapper;
    @MockBean
    private TaskService taskService;
    @MockBean
    private ProjectRepository projectRepository;
    @Mock
    private Project project;
    @Mock
    private ProjectDTO projectDTO;

    @Test
    void getProjectById_shouldCallRepository() {
        when(projectRepository.findById(ID)).thenReturn(Optional.of(project));

        final Project actual = projectService.getProjectById(ID);

        assertNotNull(actual);
        assertEquals(project, actual);
        verify(projectRepository).findById(ID);
    }

    @Test
    void createProject_shouldCallMapperAndRepository() {
        when(projectMapper.map(projectDTO)).thenReturn(project);
        when(projectDTO.getDeadLineDate()).thenReturn("2030-12-31");

        projectService.createProject(projectDTO);

        verify(projectMapper).map(projectDTO);
        verify(projectRepository).save(project);
    }

    @Test
    void isProjectWithIdExistsTest_shouldReturnTrueAndCallRepository() {
        when(projectRepository.existsById(ID)).thenReturn(true);

        assertTrue(projectService.isProjectWithIdExists(ID));
        verify(projectRepository).existsById(ID);
    }

    @Test
    void isProjectWithIdExists_whenNotExists() {
        when(projectRepository.existsById(ID)).thenReturn(false);

        assertFalse(projectService.isProjectWithIdExists(ID));
    }

    @Test
    void isProjectWithNameExists_shouldReturnTrueAndCallRepository() {
        when(projectRepository.existsByName(PROJECT_NAME)).thenReturn(true);

        assertTrue(projectService.isProjectWithNameExists(PROJECT_NAME));
        verify(projectRepository).existsByName(PROJECT_NAME);
    }

    @Test
    void updateProject_shouldCallMapperAndRepository() {
        when(projectDTO.getId()).thenReturn(ID);
        when(projectRepository.findById(ID)).thenReturn(Optional.of(project));

        projectService.updateProject(projectDTO);

        verify(projectMapper).updateProjectFromDto(projectDTO, project);
        verify(projectRepository).save(project);
    }




}