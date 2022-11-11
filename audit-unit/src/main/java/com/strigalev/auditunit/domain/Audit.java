package com.strigalev.auditunit.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document("audit")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Audit {
    @Id
    private String id;
    private String action;
    private Date date;
    private String userEmail;
}
