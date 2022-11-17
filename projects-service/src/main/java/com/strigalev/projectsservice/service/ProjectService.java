package com.strigalev.projectsservice.service;

import com.strigalev.projectsservice.domain.Project;
import com.strigalev.projectsservice.domain.ProjectStatus;
import com.strigalev.projectsservice.dto.ProjectDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ProjectService {

    Project getProjectById(Long id);

    Project getProjectByName(String name);

    ProjectDTO getProjectDtoById(Long id);

    Long createProject(ProjectDTO projectDTO);

    void deleteProject(Long id);

    void setProjectStatus(Long projectId, ProjectStatus status);

    boolean isProjectWithIdExists(Long id);

    boolean isProjectWithNameExists(String projectName);

    void updateProject(ProjectDTO projectDTO);

    void addTaskToProject(Long projectId, Long taskId);

    Page<ProjectDTO> getAllProjectsPage(Pageable pageable);

    void addEmployeeToProject(Long projectId, Long userId);

    Page<ProjectDTO> getProjectsPageByStatus(ProjectStatus status, Pageable pageable);

}
