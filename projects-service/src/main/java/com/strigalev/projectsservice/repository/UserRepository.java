package com.strigalev.projectsservice.repository;

import com.strigalev.projectsservice.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value =
            "SELECT * FROM users usr " +
                    "JOIN projects_employees pe ON usr.id = pe.employee_id " +
                    "WHERE project_id = :id",
            nativeQuery = true)
    Page<User> findAllByProjectId(Pageable pageable, @Param("id") Long projectId);

    @Query(value =
            "SELECT * FROM users usr " +
                    "JOIN tasks_employees te ON usr.id = te.employee_id " +
                    "WHERE task_id = :id",
            nativeQuery = true)
    Page<User> findAllByTaskId(Pageable pageable, @Param("id") Long taskId);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query(value = "SELECT project_id FROM projects_employees WHERE employee_id = :id", nativeQuery = true)
    List<Long> getProjectsIdsByUserId(@Param("id") Long id);

    @Query(value = "SELECT task_id FROM tasks_employees WHERE employee_id = :id", nativeQuery = true)
    List<Long> getTasksIdsByUserId(@Param("id") Long id);

    List<User> findAllByFirstNameAndLastName(String firstName, String lastName);


}
