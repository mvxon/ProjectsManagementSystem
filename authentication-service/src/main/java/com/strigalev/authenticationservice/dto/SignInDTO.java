package com.strigalev.authenticationservice.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
public class SignInDTO {

    @Email(message = "Invalid email")
    @NotEmpty(message = "Email should not be empty")
    @Size(max = 50, min = 10, message = "Email length should be between {min} and {max} chars")
    private String email;

    @NotEmpty(message = "Password should not be empty")
    @Size(max = 30, min = 3, message = "Email length should be between {min} and {max} chars")
    private String password;
}
