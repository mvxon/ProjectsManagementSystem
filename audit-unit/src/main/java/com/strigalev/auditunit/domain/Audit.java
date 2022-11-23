package com.strigalev.auditunit.domain;

import com.strigalev.starter.model.Role;
import com.strigalev.starter.model.UserAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("audit")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Audit {

    @Id
    private String id;
    private UserAction action;
    private LocalDateTime date;
    private String userEmail;
    private Long actionedUserId;
    private Role userRole;
    private Long projectId;
    private Long taskId;


    private LocalDateTime dateOfDevStart;

}
