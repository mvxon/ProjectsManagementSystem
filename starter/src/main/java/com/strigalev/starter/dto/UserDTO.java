package com.strigalev.starter.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.strigalev.starter.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String firstName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String lastName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String email;
    private Role role;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String newAccessToken;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String password;
}
