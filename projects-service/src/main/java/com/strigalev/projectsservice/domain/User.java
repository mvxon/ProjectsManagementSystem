package com.strigalev.projectsservice.domain;

import com.strigalev.starter.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String password;

    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;


    /*@PreRemove
    private void preRemove() {
        workingProjects.forEach(project -> {
            Set<User> employees = project.getEmployees().stream()
                    .filter(employee -> !project.equals(this))
                    .collect(Collectors.toSet());
            employee.setWorkingProjects(projects);
        });
    }*/
}
