package com.strigalev.authenticationservice.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@Entity
@Table(name = "access_codes")
@AllArgsConstructor
@NoArgsConstructor
public class AccessCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String accessCodeValue;
    private String userEmail;
    private LocalDateTime creationDateTime;
}