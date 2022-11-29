package com.strigalev.projectsservice.service;

import com.strigalev.projectsservice.domain.Project;
import com.strigalev.projectsservice.domain.ProjectStatus;
import com.strigalev.projectsservice.domain.Task;
import com.strigalev.projectsservice.domain.User;
import com.strigalev.projectsservice.dto.ProjectDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ProjectService {

    Project getProjectById(Long id);

    Project getProjectByTask(Task task);

    Project getProjectByName(String name);

    ProjectDTO getProjectDtoById(Long id);

    Long addTaskToProject(Long projectId, Task task);

    Long createProject(ProjectDTO projectDTO);

    void deleteProject(Long id);

    void setProjectStatus(Long projectId, ProjectStatus status);

    boolean isProjectWithIdExists(Long id);

    boolean isProjectWithNameExists(String projectName);

    void updateProject(ProjectDTO projectDTO);

    Page<ProjectDTO> getAllProjectsPage(Pageable pageable);

    void addUserToProject(Long projectId, Long userId);

    boolean isUserAndTaskMatchesOnProject(Task task, User user);

    Page<ProjectDTO> getProjectsPageByStatus(ProjectStatus status, Pageable pageable);

}