package com.strigalev.authenticationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordDTO {

    @NotEmpty(message = "Access code should not be empty")
    private String email;

    @NotEmpty(message = "Access code should not be empty")
    private String accessCode;

    @NotEmpty(message = "Password should not be empty")
    @Size(max = 30, min = 3, message = "Email length should be between {min} and {max} chars")
    private String password;
}
