package com.strigalev.projectsservice.repository;

import com.strigalev.projectsservice.domain.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query(value = "SELECT * FROM tasks WHERE project_id = :id AND status = :status", nativeQuery = true)
    Page<Task> findAllByProjectIdAndStatus(Pageable pageable, @Param("id") Long projectId, @Param("status") String status);

    @Query(value = "SELECT * FROM tasks WHERE project_id = :id", nativeQuery = true)
    Page<Task> findAllByProjectId(Pageable pageable, @Param("id") Long projectId);

    @Query(value = "SELECT task_id FROM tasks_employees WHERE employee_id = :id", nativeQuery = true)
    List<Long> getTasksIdsByUserId(@Param("id") Long id);

}
