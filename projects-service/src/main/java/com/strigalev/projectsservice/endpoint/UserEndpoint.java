package com.strigalev.projectsservice.endpoint;

import com.strigalev.projectsservice.service.UserService;
import com.strigalev.starter.dto.ApiResponseEntity;
import com.strigalev.starter.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserEndpoint {
    private final UserService userService;
    
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseEntity> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponseEntity.builder()
                .object(userService.getUserDto(id))
                .status(HttpStatus.OK)
                .build());
    }

    @GetMapping("/byProjectId/{projectId}")
    public ResponseEntity<Page<UserDTO>> getEmployeesByProjectId(@PathVariable Long projectId, Pageable pageable) {
        return ResponseEntity.ok(userService.getUsersPageByProjectId(projectId, pageable));
    }

    @GetMapping("/byProjectName/{projectName}")
    public ResponseEntity<Page<UserDTO>> getEmployeesByProjectName(@PathVariable String projectName, Pageable pageable) {
        return ResponseEntity.ok(userService.getUsersPageByProjectName(projectName, pageable));
    }

    @GetMapping("/byTaskId/{taskId}")
    public ResponseEntity<Page<UserDTO>> getEmployeesByTaskId(@PathVariable Long taskId, Pageable pageable) {
        return ResponseEntity.ok(userService.getUsersPageByTaskId(taskId, pageable));
    }

    @GetMapping("/byFullName")
    public ResponseEntity<ApiResponseEntity> getEmployeesByFullName(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName
    ) {
        return ResponseEntity.ok(ApiResponseEntity.builder()
                .object(userService.getUsersDtoByFullName(firstName, lastName))
                .status(HttpStatus.OK)
                .build());
    }
}