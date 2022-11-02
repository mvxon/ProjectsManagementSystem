package com.strigalev.projectsservice.endpoint;


import com.strigalev.projectsservice.dto.UserDTO;
import com.strigalev.projectsservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserEndpoint {
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserDtoById(id));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<Page<UserDTO>> getUsersPageByProjectId(@PathVariable Long projectId, Pageable pageable) {
        return ResponseEntity.ok(userService.getUsersPageByProjectId(projectId, pageable));
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<Page<UserDTO>> getUsersPageByTaskId(@PathVariable Long taskId, Pageable pageable) {
        return ResponseEntity.ok(userService.getUsersPageByTaskId(taskId, pageable));
    }


}
