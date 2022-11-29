package com.strigalev.starter.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.strigalev.starter.model.UserAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserActionMailMessageDTO {
    private UserAction userAction;
    private String taskTittle;
    private String projectName;
    private String actionUserFnAndEmail;
    private String actionedUserFirstName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private MailMessageDTO mailMessageDTO;
}
