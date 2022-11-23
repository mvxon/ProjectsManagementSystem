package com.strigalev.projectsservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "tasks")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;

    @Column(name = "project_id")
    private Long projectId;

    @CreationTimestamp
    private LocalDate creationDate;
    private LocalDate deadLineDate;

    @UpdateTimestamp
    private LocalDateTime updateDate;

    @ElementCollection(targetClass = TaskStatus.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "tasks_statuses")
    @Enumerated(EnumType.STRING)
    private Set<TaskStatus> statuses;

    private boolean deleted;

    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    @JoinTable(
            name = "tasks_employees",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id"))
    Set<User> employees;
}
