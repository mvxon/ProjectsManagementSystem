package com.strigalev.projectsservice.repository;

import com.strigalev.projectsservice.domain.Project;
import com.strigalev.projectsservice.domain.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    boolean existsByName(String projectName);

    Page<Project> findAllByStatus(Pageable pageable, ProjectStatus status);

    Optional<Project> findByName(String name);

    @Query(value = "SELECT project_id FROM projects_employees WHERE employee_id = :id", nativeQuery = true)
    List<Long> getProjectsIdsByUserId(@Param("id") Long id);
}
