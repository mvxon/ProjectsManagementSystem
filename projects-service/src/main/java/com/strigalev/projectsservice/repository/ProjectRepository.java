package com.strigalev.projectsservice.repository;

import com.strigalev.projectsservice.domain.Project;
import com.strigalev.projectsservice.domain.ProjectStatus;
import com.strigalev.projectsservice.domain.Task;
import com.strigalev.projectsservice.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    boolean existsByName(String projectName);

    boolean existsByTasksContainingAndEmployeesContaining(Task task, User employee);

    Page<Project> findAllByStatusAndDeletedIsFalse(Pageable pageable, ProjectStatus status);

    Optional<Project> findByNameAndDeletedIsFalse(String name);

    Optional<Project> findByIdAndDeletedIsFalse(Long id);

}
