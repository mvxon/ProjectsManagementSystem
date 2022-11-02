package com.strigalev.projectsservice.service.impl;

import com.strigalev.projectsservice.domain.Project;
import com.strigalev.projectsservice.dto.ProjectDTO;
import com.strigalev.projectsservice.exception.ResourceNotFoundException;
import com.strigalev.projectsservice.mapper.ProjectListMapper;
import com.strigalev.projectsservice.mapper.ProjectMapper;
import com.strigalev.projectsservice.repository.ProjectRepository;
import com.strigalev.projectsservice.service.ProjectService;
import com.strigalev.projectsservice.service.TaskService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.strigalev.projectsservice.util.MethodsUtil.getProjectNotExistsMessage;


@Service
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final ProjectListMapper projectListMapper;
    private final TaskService taskService;

    public ProjectServiceImpl(ProjectRepository projectRepository,
                              ProjectMapper projectMapper,
                              ProjectListMapper projectListMapper,
                              @Lazy TaskService taskService
    ) {
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
        this.projectListMapper = projectListMapper;
        this.taskService = taskService;
    }

    @Override
    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(getProjectNotExistsMessage(id))
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
        project.setCreationDate(LocalDate.now());
        project.setDeadLineDate(LocalDate.parse(projectDTO.getDeadLineDate()));
        project.setActive(true);
        projectRepository.save(project);
        return project.getId();
    }

    @Override
    public List<ProjectDTO> getAllProjects() {
        return projectListMapper.map(projectRepository.findAll());
    }

    @Override
    @Transactional
    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
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
    public void softDeleteProject(Long id) {
        Project project = getProjectById(id);
        project.setActive(false);
        taskService.softDeleteAllTasksByProjectId(id);
        projectRepository.save(project);
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
        if(projects.getContent().isEmpty()) {
            throw new ResourceNotFoundException("Page not found");
        }
        return projects.map(projectListMapper::map);
    }

    @Override
    public Page<ProjectDTO> getActiveProjectsPage(Pageable pageable) {
        Page<Project> projects = projectRepository.findAllByActiveIsTrue(pageable);
        if(projects.getContent().isEmpty()) {
            throw new ResourceNotFoundException("Page not found");
        }
        return projects.map(projectListMapper::map);
    }
}
