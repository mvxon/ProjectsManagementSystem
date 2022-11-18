package com.strigalev.projectsservice.repository;

import com.strigalev.projectsservice.domain.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface TaskRepository extends JpaRepository<Task, Long> {


    Page<Task> findAllByProjectIdAndStatus(Pageable pageable, Long projectId, String status);

    Page<Task> findAllByProjectId(Pageable pageable, Long projectId);

    Page<Task> findAllByCreationDateBetweenAndProjectId(Pageable pageable, LocalDate startDate, LocalDate endDate, Long projectId);

    Page<Task> findAllByCreationDateAndProjectId(Pageable pageable, LocalDate creationDate, Long projectId);

}
