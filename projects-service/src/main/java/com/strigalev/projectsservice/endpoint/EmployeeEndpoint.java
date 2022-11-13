package com.strigalev.projectsservice.endpoint;


import com.strigalev.projectsservice.service.UserService;
import com.strigalev.starter.dto.ApiResponseEntity;
import com.strigalev.starter.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/employees")
public class EmployeeEndpoint {
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseEntity> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponseEntity.builder()
                .object(userService.getUserDtoById(id))
                .status(HttpStatus.OK)
                .build());
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<Page<UserDTO>> getEmployeesPageByProjectId(@PathVariable Long projectId, Pageable pageable) {
        return ResponseEntity.ok(userService.getUsersPageByProjectId(projectId, pageable));
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<Page<UserDTO>> getEmployeesPageByTaskId(@PathVariable Long taskId, Pageable pageable) {
        return ResponseEntity.ok(userService.getUsersPageByTaskId(taskId, pageable));
    }

}
