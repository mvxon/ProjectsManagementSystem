package com.strigalev.projectsservice.endpoint;


import com.strigalev.projectsservice.dto.SignUpRequest;
import com.strigalev.starter.dto.UserDTO;
import com.strigalev.projectsservice.service.UserService;
import com.strigalev.starter.dto.ApiResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserEndpoint {
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseEntity> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponseEntity.builder()
                .object(userService.getUserDtoById(id))
                .status(HttpStatus.OK)
                .build());
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<Page<UserDTO>> getUsersPageByProjectId(@PathVariable Long projectId, Pageable pageable) {
        return ResponseEntity.ok(userService.getUsersPageByProjectId(projectId, pageable));
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<Page<UserDTO>> getUsersPageByTaskId(@PathVariable Long taskId, Pageable pageable) {
        return ResponseEntity.ok(userService.getUsersPageByTaskId(taskId, pageable));
    }

    @GetMapping("/userDetails/{email}")
    public ResponseEntity<UserDTO> getUserDetailsByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserDetailsByEmail(email));
    }

    @PostMapping("/sign-up")
    public void signUp(@RequestBody @Valid SignUpRequest signUpRequest) {
        userService.saveUser(signUpRequest);
    }


}
