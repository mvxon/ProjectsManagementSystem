package com.strigalev.projectsservice.repository;

import com.strigalev.projectsservice.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value =
            "select * from users usr " +
                    "join projects_employees pe on usr.id = pe.employee_id " +
                    "where project_id = :id",
            nativeQuery = true)
    Page<User> findAllByProjectId(Pageable pageable, @Param("id") Long projectId);

    @Query(value =
            "select * from users usr " +
                    "join tasks_employees te on usr.id = te.employee_id " +
                    "where task_id = :id",
            nativeQuery = true)
    Page<User> findAllByTaskId(Pageable pageable, @Param("id") Long taskId);

}
