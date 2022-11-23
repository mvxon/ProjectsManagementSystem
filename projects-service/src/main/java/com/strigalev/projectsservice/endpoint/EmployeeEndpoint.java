package com.strigalev.projectsservice.endpoint;


import com.strigalev.projectsservice.dto.UserStatisticDTO;
import com.strigalev.projectsservice.service.UserService;
import com.strigalev.starter.dto.ApiResponseEntity;
import com.strigalev.starter.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/employees")
public class EmployeeEndpoint {
    private final UserService userService;

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

    @GetMapping("/userStatistic/{ids}")
    public ResponseEntity<List<UserStatisticDTO>> getUserStatisticBetween(
            @PathVariable Long[] ids,
            @RequestParam(name = "from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(name = "to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return ResponseEntity.ok(userService.getUserStatisticBetween(ids, from, to));
    }

}
