package com.strigalev.projectsservice.repository;

import com.strigalev.projectsservice.domain.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<Task> findByIdAndActiveIsTrue(Long id);

    @Query(value = "SELECT * FROM tasks WHERE project_id = :id AND active = true", nativeQuery = true)
    Page<Task> findAllByProjectIdAndActiveIsTrue(Pageable pageable, @Param("id") Long projectId);

    @Query(value = "SELECT * FROM tasks WHERE project_id = :id", nativeQuery = true)
    Page<Task> findAllByProjectId(Pageable pageable, @Param("id") Long projectId);

    @Modifying
    @Query(value = "UPDATE tasks SET active = false WHERE project_id = :id", nativeQuery = true)
    void setActiveFalseAllTasksByProjectId(@Param("id") Long projectId);
}
