package com.strigalev.authenticationservice.dto;

import com.strigalev.authenticationservice.validation.EmailCheck;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {

    @Email(message = "Invalid email")
    @EmailCheck
    @NotEmpty(message = "Email should not be empty")
    @Size(max = 50, min = 10, message = "Email length should be between {min} and {max} chars")
    private String email;

    @NotEmpty(message = "Password should not be empty")
    @Size(max = 30, min = 3, message = "Email length should be between {min} and {max} chars")
    private String password;

    @NotEmpty(message = "Firstname should not be empty")
    @Size(max = 30, min = 2, message = "Firstname length should be between {min} and {max} chars")
    private String firstName;

    @NotEmpty(message = "Lastname should not be empty")
    @Size(max = 30, min = 2, message = "Lastname length should be between {min} and {max} chars")
    private String lastName;

    @NotEmpty(message = "Role should not be empty")
    private String role;
}