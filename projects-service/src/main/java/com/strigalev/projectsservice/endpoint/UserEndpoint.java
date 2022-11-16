package com.strigalev.projectsservice.endpoint;

import com.strigalev.projectsservice.dto.SignUpRequest;
import com.strigalev.projectsservice.service.UserService;
import com.strigalev.starter.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserEndpoint {
    private final UserService userService;

    @PostMapping("/sign-up")
    public void signUp(@RequestBody @Valid SignUpRequest signUpRequest) {
        userService.saveUser(signUpRequest);
    }

    @GetMapping("/userDetails/{email}")
    public ResponseEntity<UserDTO> getUserDetailsByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserDetailsByEmail(email));
    }
}
