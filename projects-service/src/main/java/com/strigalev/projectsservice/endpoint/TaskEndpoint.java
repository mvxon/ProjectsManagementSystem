package com.strigalev.projectsservice.endpoint;

import com.strigalev.projectsservice.domain.TaskStatus;
import com.strigalev.projectsservice.dto.TaskDTO;
import com.strigalev.projectsservice.service.ProjectService;
import com.strigalev.projectsservice.service.TaskService;
import com.strigalev.starter.dto.ApiResponseEntity;
import com.strigalev.starter.dto.DateIntervalDTO;
import com.strigalev.starter.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

import static com.strigalev.starter.util.MethodsUtil.getProjectNotExistsMessage;
import static org.springframework.http.HttpStatus.CREATED;

@Tag(name = "Task", description = "Endpoints for tasks managing")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tasks")
public class TaskEndpoint {
    private final TaskService taskService;
    private final ProjectService projectService;

    @GetMapping("/{id}")
    @Operation(summary = "Get task by id", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "SUCCESS",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content),
            @ApiResponse(responseCode = "500", description = "INTERNAL ERROR", content = @Content)
    })
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskDtoById(id));
    }

    @GetMapping("/byProjectId/{projectId}")
    @Operation(summary = "Get project tasks page", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "SUCCESS",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
            ),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content),
            @ApiResponse(responseCode = "500", description = "INTERNAL ERROR", content = @Content)
    })
    public ResponseEntity<Page<TaskDTO>> getProjectTasks(@PathVariable Long projectId, Pageable pageable) {
        if (!projectService.isProjectWithIdExists(projectId)) {
            throw new ResourceNotFoundException(getProjectNotExistsMessage(projectId));
        }
        return ResponseEntity.ok(taskService.getTasksPageByProjectId(pageable, projectId));
    }

    @GetMapping("/byProjectId/{projectId}/byStatus")
    public ResponseEntity<Page<TaskDTO>> getProjectTasksByStatus(
            @PathVariable Long projectId,
            @RequestParam("status") String status,
            Pageable pageable
    ) {
        if (!projectService.isProjectWithIdExists(projectId)) {
            throw new ResourceNotFoundException(getProjectNotExistsMessage(projectId));
        }
        return ResponseEntity.ok(taskService.getTasksPageByProjectIdAndStatus(pageable, projectId,
                TaskStatus.valueOf(status.toUpperCase())));
    }

    @GetMapping("/byProjectId/{projectId}/byCreationDate")
    public ResponseEntity<Page<TaskDTO>> getProjectTasks(
            @PathVariable Long projectId,
            @RequestParam(name = "from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(name = "to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            Pageable pageable
    ) {
        return ResponseEntity.ok(taskService.getTasksPageByProjectIdAndCreationDate(
                pageable,
                DateIntervalDTO.builder()
                        .from(from)
                        .to(to)
                        .build(),
                projectId));
    }

    @PostMapping("/{projectId}")
    @Operation(summary = "Create task in project",
            responses = {
                    @ApiResponse(responseCode = "201",
                            description = "SUCCESSFULLY CREATED",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponseEntity.class))),
                    @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content),
                    @ApiResponse(responseCode = "500", description = "INTERNAL ERROR", content = @Content)
            })
    public ResponseEntity<ApiResponseEntity> createTaskInProject(
            @PathVariable Long projectId,
            @RequestBody @Valid TaskDTO taskDTO
    ) {
        if (!projectService.isProjectWithIdExists(projectId)) {
            throw new ResourceNotFoundException(getProjectNotExistsMessage(projectId));
        }

        Long taskId = projectService.addTaskToProject(projectId, taskService.createTask(taskDTO));

        return new ResponseEntity<>(
                ApiResponseEntity.builder()
                        .object(taskId)
                        .build(),
                CREATED
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Delete task by id", responses = {
            @ApiResponse(responseCode = "200", description = "SUCCESS"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL ERROR")
    })
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update task by id", responses = {
            @ApiResponse(responseCode = "200", description = "SUCCESS"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL ERROR")
    })
    public void updateTask(@PathVariable Long id, @RequestBody @Valid TaskDTO taskDTO) {
        taskDTO.setId(id);
        taskService.updateTask(taskDTO);
    }

    @PatchMapping("/setOpen/{taskId}")
    public void openTask(@PathVariable Long taskId) {
        taskService.openTask(taskId);
    }

    @PatchMapping("/assignToUser/{taskId}")
    public void assignTaskToUser(@PathVariable Long taskId, @RequestParam(value = "userId") Long userId) {
        taskService.assignTaskToUser(taskId, userId);
    }

    @PatchMapping("/unAssignToUser/{taskId}")
    public void unAssignTaskToUser(@PathVariable Long taskId, @RequestParam("userId") Long userId) {
        taskService.unAssignTaskToUser(taskId, userId);
    }


    @PatchMapping("/setDeveloping/{taskId}")
    public ResponseEntity<ApiResponseEntity> takeTaskForDeveloping(@PathVariable Long taskId) {
        return ResponseEntity.ok(ApiResponseEntity.builder()
                .object(taskService.setTaskDeveloping(taskId))
                .status(HttpStatus.OK)
                .build());
    }

    @PatchMapping("/setCompleted/{taskId}")
    public void setTaskCompleted(@PathVariable Long taskId) {
        taskService.setTaskCompleted(taskId);
    }

    @PatchMapping("/setTesting/{taskId}")
    public ResponseEntity<ApiResponseEntity> takeTaskForTesting(@PathVariable Long taskId) {
        return ResponseEntity.ok(ApiResponseEntity.builder()
                .object(taskService.setTaskTesting(taskId))
                .status(HttpStatus.OK)
                .build());
    }

    @PatchMapping("/setTested/{taskId}")
    public void setTaskTested(@PathVariable Long taskId) {
        taskService.setTaskTested(taskId);
    }

    @PatchMapping("/setDocumented/{taskId}")
    public void setTaskDocumented(@PathVariable Long taskId) {
        taskService.setTaskDocumented(taskId);
    }

}
