package com.strigalev.projectsservice.endpoint;


import com.strigalev.projectsservice.dto.EmployeeDTO;
import com.strigalev.projectsservice.service.UserService;
import com.strigalev.starter.dto.ApiResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/employees")
public class EmployeeEndpoint {
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseEntity> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponseEntity.builder()
                .object(userService.getEmployeeDto(id))
                .status(HttpStatus.OK)
                .build());
    }

    @GetMapping("/byProjectId/{projectId}")
    public ResponseEntity<Page<EmployeeDTO>> getEmployeesPageByProjectId(@PathVariable Long projectId, Pageable pageable) {
        return ResponseEntity.ok(userService.getUsersPageByProjectId(projectId, pageable));
    }

    @GetMapping("/byProjectName/{projectName}")
    public ResponseEntity<Page<EmployeeDTO>> getEmployeesPageByProjectName(@PathVariable String projectName, Pageable pageable) {
        return ResponseEntity.ok(userService.getUsersPageByProjectName(projectName, pageable));
    }
    @GetMapping("/task/{taskId}")
    public ResponseEntity<Page<EmployeeDTO>> getEmployeesPageByTaskId(@PathVariable Long taskId, Pageable pageable) {
        return ResponseEntity.ok(userService.getUsersPageByTaskId(taskId, pageable));
    }

    @GetMapping("/byFullName")
    public ResponseEntity<ApiResponseEntity> getEmployeesByFullName(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName
    ) {
        return ResponseEntity.ok(ApiResponseEntity.builder()
                .object(userService.getEmployeesDtoByFullName(firstName, lastName))
                .status(HttpStatus.OK)
                .build());
    }


}
