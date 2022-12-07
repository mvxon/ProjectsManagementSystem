package com.strigalev.projectsservice.repository;

import com.strigalev.projectsservice.domain.Task;
import com.strigalev.projectsservice.domain.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {


    Optional<Task> findTaskByIdAndDeletedIsFalse(Long id);

    Page<Task> findAllByProjectIdAndDeletedIsFalse(Pageable pageable, Long projectId);

    Page<Task> findAllByProjectIdAndStatusAndDeletedIsFalse(Pageable pageable, Long projectId, TaskStatus status);

    Page<Task> findAllByCreationDateBetweenAndProjectIdAndDeletedIsFalse(Pageable pageable,
                                                                         LocalDateTime from,
                                                                         LocalDateTime to,
                                                                         Long projectId);

    @Modifying
    @Transactional
    @Query("UPDATE Task SET status = :status WHERE id = :id")
    void updateTaskStatus(Long id, TaskStatus status);
}
