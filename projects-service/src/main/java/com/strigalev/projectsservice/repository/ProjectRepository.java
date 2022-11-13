package com.strigalev.projectsservice.repository;

import com.strigalev.projectsservice.domain.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    boolean existsByName(String projectName);

    Optional<Project> findByIdAndActiveIsTrue(Long id);

    Page<Project> findAllByActiveIsTrue(Pageable pageable);

    List<Project> findAll();
}
