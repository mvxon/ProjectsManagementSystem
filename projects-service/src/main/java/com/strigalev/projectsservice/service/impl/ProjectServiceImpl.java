package com.strigalev.projectsservice.service.impl;

import com.strigalev.projectsservice.domain.Project;
import com.strigalev.projectsservice.domain.ProjectStatus;
import com.strigalev.projectsservice.domain.Task;
import com.strigalev.projectsservice.domain.User;
import com.strigalev.projectsservice.dto.ProjectDTO;
import com.strigalev.projectsservice.exception.EmployeeException;
import com.strigalev.projectsservice.mapper.ProjectListMapper;
import com.strigalev.projectsservice.mapper.ProjectMapper;
import com.strigalev.projectsservice.repository.ProjectRepository;
import com.strigalev.projectsservice.service.ProjectService;
import com.strigalev.projectsservice.service.UserService;
import com.strigalev.starter.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.strigalev.projectsservice.domain.ProjectStatus.CREATED;
import static com.strigalev.starter.model.UserAction.*;
import static com.strigalev.starter.util.MethodsUtil.getProjectNotExistsMessage;
import static com.strigalev.starter.util.MethodsUtil.getProjectWithNameNotExistsMessage;


@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final ProjectListMapper projectListMapper;
    private final UserService userService;

    @Override
    public Project getProjectById(Long id) {
        return projectRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(getProjectNotExistsMessage(id))
                );
    }

    @Override
    public Project getProjectByTask(Task task) {
        return projectRepository.findByTasksContainingAndDeletedIsFalse(task);
    }

    @Override
    public Project getProjectByName(String name) {
        return projectRepository.findByNameAndDeletedIsFalse(name)
                .orElseThrow(
                        () -> new ResourceNotFoundException(getProjectWithNameNotExistsMessage(name))
                );
    }

    @Override
    public ProjectDTO getProjectDtoById(Long id) {
        return projectMapper.map(getProjectById(id));
    }

    @Override
    @Transactional
    public Long addTaskToProject(Long projectId, Task task) {
        Project project = getProjectById(projectId);
        project.getTasks().add(task);

        userService.sendManagerProjectAction(ADD_TASK_TO_PROJECT, project, task);

        return task.getId();
    }

    @Override
    @Transactional
    public Long createProject(ProjectDTO projectDTO) {
        Project project = projectMapper.map(projectDTO);
        project.setStatus(CREATED);

        userService.sendManagerProjectAction(CREATE_PROJECT, projectRepository.save(project), null);

        return project.getId();
    }

    @Override
    @Transactional
    public void deleteProject(Long id) {
        Project project = getProjectById(id);
        project.setDeleted(true);
        project.getTasks().forEach(task -> task.setDeleted(true));

        userService.sendManagerProjectAction(DELETE_PROJECT, project, null);
    }

    @Override
    @Transactional
    public void setProjectStatus(Long projectId, ProjectStatus status) {
        Project project = getProjectById(projectId);
        project.setStatus(status);
    }

    @Override
    @Transactional
    public boolean isProjectWithIdExists(Long id) {
        return projectRepository.existsById(id);
    }

    @Override
    public boolean isProjectWithNameExists(String projectName) {
        return projectRepository.existsByName(projectName);
    }

    @Override
    @Transactional
    public void updateProject(ProjectDTO projectDTO) {
        Project savedProject = getProjectById(projectDTO.getId());
        projectMapper.updateProjectFromDto(projectDTO, savedProject);

        userService.sendManagerProjectAction(UPDATE_PROJECT, savedProject, null);
    }

    @Override
    public Page<ProjectDTO> getAllProjectsPage(Pageable pageable) {
        Page<Project> projects = projectRepository.findAll(pageable);
        if (projects.getContent().isEmpty()) {
            throw new ResourceNotFoundException("Page not found");
        }
        return projects.map(projectListMapper::map);
    }

    @Override
    @Transactional
    public void addUserToProject(Long projectId, Long userId) {
        Project project = getProjectById(projectId);

        if (!project.getEmployees().add(userService.getUserById(userId))) {
            throw new EmployeeException(
                    String.format("User with %oid is already working at %oid project", userId, projectId));

        }

        userService.sendManagerAction(ADD_USER_TO_PROJECT, project, null, userId);
    }

    @Override
    public boolean isUserAndTaskMatchesOnProject(Task task, User user) {
        if (projectRepository.existsByTasksContainingAndEmployeesContaining(task, user)) {
            return true;
        }
        throw new EmployeeException(String.format("Task %oid and user %oid are not matching on project",
                task.getId(),
                user.getId()));
    }

    @Override
    public Page<ProjectDTO> getProjectsPageByStatus(ProjectStatus status, Pageable pageable) {
        Page<Project> projects = projectRepository.findAllByStatusAndDeletedIsFalse(pageable, status);
        if (projects.getContent().isEmpty()) {
            throw new ResourceNotFoundException("Page not found");
        }

        return projects.map(projectListMapper::map);
    }
}