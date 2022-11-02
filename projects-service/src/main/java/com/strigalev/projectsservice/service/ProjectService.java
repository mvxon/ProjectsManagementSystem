package com.strigalev.projectsservice.service;

import com.strigalev.projectsservice.domain.Project;
import com.strigalev.projectsservice.dto.ProjectDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface ProjectService {

    Project getProjectById(Long id);

    ProjectDTO getProjectDtoById(Long id);

    Long createProject(ProjectDTO projectDTO);

    List<ProjectDTO> getAllProjects();

    void deleteProject(Long id);

    boolean isProjectWithIdExists(Long id);

    boolean isProjectWithNameExists(String projectName);

    void updateProject(ProjectDTO projectDTO);

    void softDeleteProject(Long id);

    void addTaskToProject(Long projectId, Long taskId);

    Page<ProjectDTO> getAllProjectsPage(Pageable pageable);

    Page<ProjectDTO> getActiveProjectsPage(Pageable pageable);

}
