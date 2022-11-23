package com.strigalev.projectsservice.repository;

import com.strigalev.projectsservice.domain.Task;
import com.strigalev.projectsservice.domain.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {


    Optional<Task> findTaskByIdAndDeletedIsFalse(Long id);

    Page<Task> findAllByProjectIdAndDeletedIsFalse(Pageable pageable, Long projectId);

    Page<Task> findAllByProjectIdAndStatusesContainingAndDeletedIsFalse(Pageable pageable, Long projectId, TaskStatus status);

    Page<Task> findAllByCreationDateBetweenAndProjectIdAndDeletedIsFalse(Pageable pageable,
                                                                         LocalDate startDate,
                                                                         LocalDate endDate,
                                                                         Long projectId);

    Page<Task> findAllByCreationDateAndProjectIdAndDeletedIsFalse(Pageable pageable,
                                                                  LocalDate creationDate,
                                                                  Long projectId
    );

    @Query(value = "SELECT projectId FROM Task WHERE id = :id")
    Long findProjectIdById(@Param("id") Long id);

}
