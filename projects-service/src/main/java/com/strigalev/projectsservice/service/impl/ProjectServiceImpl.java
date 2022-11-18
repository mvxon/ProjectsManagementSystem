package com.strigalev.projectsservice.service.impl;

import com.strigalev.projectsservice.domain.Project;
import com.strigalev.projectsservice.domain.ProjectStatus;
import com.strigalev.projectsservice.dto.ProjectDTO;
import com.strigalev.projectsservice.exception.ResourceNotFoundException;
import com.strigalev.projectsservice.mapper.ProjectListMapper;
import com.strigalev.projectsservice.mapper.ProjectMapper;
import com.strigalev.projectsservice.repository.ProjectRepository;
import com.strigalev.projectsservice.service.ProjectService;
import com.strigalev.projectsservice.service.TaskService;
import com.strigalev.projectsservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static com.strigalev.starter.util.MethodsUtil.getProjectNotExistsMessage;
import static com.strigalev.starter.util.MethodsUtil.getProjectWithNameNotExistsMessage;


@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final ProjectListMapper projectListMapper;
    private final TaskService taskService;
    private final UserService userService;

    @Override
    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(getProjectNotExistsMessage(id))
                );
    }

    @Override
    public Project getProjectByName(String name) {
        return projectRepository.findByName(name)
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
    public Long createProject(ProjectDTO projectDTO) {
        Project project = projectMapper.map(projectDTO);
        project.setDeadLineDate(LocalDate.parse(projectDTO.getDeadLineDate()));
        project.setStatus(ProjectStatus.CREATED);

        projectRepository.save(project);
        return project.getId();
    }

    @Override
    @Transactional
    public void deleteProject(Long id) {
        Project project = getProjectById(id);
        project.setStatus(ProjectStatus.DELETED);
        project.getEmployees().clear();
    }

    @Override
    @Transactional
    public void setProjectStatus(Long projectId, ProjectStatus status) {
        Project project = getProjectById(projectId);
        project.setStatus(status);

        projectRepository.save(project);
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

        projectRepository.save(savedProject);
    }

    @Override
    @Transactional
    public void addTaskToProject(Long projectId, Long taskId) {
        Project project = getProjectById(projectId);
        project.getTasks().add(taskService.getTaskById(taskId));

        projectRepository.save(project);
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
    public void addEmployeeToProject(Long projectId, Long userId) {
        Project project = getProjectById(projectId);
        project.getEmployees().add(userService.getUserById(userId));

        projectRepository.save(project);
    }

    @Override
    public Page<ProjectDTO> getProjectsPageByStatus(ProjectStatus status, Pageable pageable) {
        Page<Project> projects = projectRepository.findAllByStatus(pageable, status);
        if (projects.getContent().isEmpty()) {
            throw new ResourceNotFoundException("Page not found");
        }

        return projects.map(projectListMapper::map);
    }
}
