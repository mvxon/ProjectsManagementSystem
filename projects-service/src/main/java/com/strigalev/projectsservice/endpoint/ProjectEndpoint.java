package com.strigalev.projectsservice.endpoint;


import com.strigalev.projectsservice.dto.ApiResponseEntity;
import com.strigalev.projectsservice.dto.ProjectDTO;
import com.strigalev.projectsservice.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.CREATED;

@Tag(name = "Project", description = "Endpoints for projects managing")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/projects")
public class ProjectEndpoint {
    private final ProjectService projectService;

    @GetMapping
    @Operation(summary = "Get projects page", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "SUCCESS",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
            ),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content),
            @ApiResponse(responseCode = "500", description = "INTERNAL ERROR", content = @Content)
    })
    public ResponseEntity<Page<ProjectDTO>> getProjectsPage(Pageable pageable) {
        return new ResponseEntity<>(projectService.getActiveProjectsPage(pageable), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get project by id", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "SUCCESS",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProjectDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content),
            @ApiResponse(responseCode = "500", description = "INTERNAL ERROR", content = @Content)
    })
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectDtoById(id));
    }

    @PostMapping
    @Operation(summary = "Create project", responses = {
            @ApiResponse(responseCode = "201",
                    description = "SUCCESSFULLY CREATED",
                    content =  @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseEntity.class))
            ),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content),
            @ApiResponse(responseCode = "500", description = "INTERNAL ERROR", content = @Content)
    })
    public ResponseEntity<ApiResponseEntity> createProject(@RequestBody @Valid ProjectDTO projectDTO) {
        return new ResponseEntity<>(
                ApiResponseEntity.builder()
                        .objectId(projectService.createProject(projectDTO))
                        .build(),
                CREATED
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Delete project by id", responses = {
            @ApiResponse(responseCode = "200", description = "SUCCESS"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL ERROR")
    })
    public void deleteProject(@PathVariable Long id) {
        projectService.softDeleteProject(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update project by id", responses = {
            @ApiResponse(responseCode = "200", description = "SUCCESS"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL ERROR")
    })
    public void updateProject(@PathVariable Long id, @RequestBody @Valid ProjectDTO projectDTO) {
        projectDTO.setId(id);
        projectService.updateProject(projectDTO);
    }
}
