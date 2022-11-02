package com.strigalev.projectsservice.integration.IT;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.strigalev.projectsservice.domain.Project;
import com.strigalev.projectsservice.domain.Task;
import com.strigalev.projectsservice.dto.TaskDTO;
import com.strigalev.projectsservice.integration.IntegrationTestBase;
import com.strigalev.projectsservice.mapper.TaskMapper;
import com.strigalev.projectsservice.repository.TaskRepository;
import com.strigalev.projectsservice.service.ProjectService;
import com.strigalev.projectsservice.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class TaskIntegrationTest extends IntegrationTestBase {
    private static final String PATH = "/api/v1/tasks";
    private static final long ID = 1L;
    private static final TaskDTO TASK_DTO = TaskDTO.builder()
            .title("Developing")
            .description("Description description")
            .deadLineDate("2023-12-20")
            .build();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskMapper taskMapper;

    @Test
    void testGetTaskById() throws Exception {
        mockMvc.perform(get(PATH + "/" + ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(taskService.getTaskDtoById(ID)));
    }

    @Test
    void testCreateTaskInProject() throws Exception {
        Project project = projectService.getProjectById(ID);
        final long oldProjectTasksCount = project.getTasks().size();
        final long oldTasksCount = taskRepository.count();

        mockMvc.perform(post(PATH + "/" + ID)
                        .content(new ObjectMapper().writeValueAsString(TASK_DTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.objectId").exists());

        assertEquals(oldProjectTasksCount + 1, project.getTasks().size());
        assertEquals(oldTasksCount + 1, taskRepository.count());

        Task task = taskService.getTaskById(oldTasksCount + 1);
        assertThat(taskMapper.map(task)).isEqualToIgnoringNullFields(TASK_DTO);
    }

    @Test
    void testGetTasksPage() throws Exception {
        mockMvc.perform(get(PATH + "/project/" + ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(5));
    }

    @Test
    void testUpdateTask() throws Exception {
        final TaskDTO oldTask = taskService.getTaskDtoById(ID);

        mockMvc.perform(put(PATH + "/" + ID)
                        .content(new ObjectMapper().writeValueAsString(TASK_DTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertNotEquals(oldTask, taskService.getTaskDtoById(ID));
    }

    @Test
    void testDeleteTask() throws Exception {
        mockMvc.perform(delete(PATH + "/" + ID))
                .andExpect(status().isOk());

        assertFalse(taskService.getTaskById(ID).isActive());
    }

}
