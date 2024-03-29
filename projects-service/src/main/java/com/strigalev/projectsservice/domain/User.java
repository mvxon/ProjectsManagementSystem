package com.strigalev.projectsservice.domain;

import com.strigalev.starter.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
    private boolean active;

    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;
}
