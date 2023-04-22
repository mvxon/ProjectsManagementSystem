package com.strigalev.auditunit.domain;

import com.strigalev.starter.dto.UserDTO;
import com.strigalev.starter.model.UserAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
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
    private UserDTO actionUser;
    private Long actionedUserId;
    private Long projectId;
    private Long taskId;


    @Transient
    private LocalDateTime dateOfDevStart;

}
