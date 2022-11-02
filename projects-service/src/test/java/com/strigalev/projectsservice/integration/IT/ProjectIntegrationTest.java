package com.strigalev.projectsservice.integration.IT;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.strigalev.projectsservice.domain.Project;
import com.strigalev.projectsservice.dto.ProjectDTO;
import com.strigalev.projectsservice.integration.IntegrationTestBase;
import com.strigalev.projectsservice.mapper.ProjectMapper;
import com.strigalev.projectsservice.repository.ProjectRepository;
import com.strigalev.projectsservice.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class ProjectIntegrationTest extends IntegrationTestBase {
    private static final String PATH = "/api/v1/projects";
    private static final long ID = 1L;
    private static final ProjectDTO PROJECT_DTO = ProjectDTO.builder()
            .name("Baeldung")
            .title("Web application")
            .description("Description description")
            .customer("Oracle")
            .deadLineDate("2023-12-20")
            .build();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectMapper projectMapper;


    @Test
    void testGetProjectById() throws Exception {
        mockMvc.perform(get(PATH + "/" + ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(projectService.getProjectDtoById(ID)));
    }

    @Test
    void testCreateProject() throws Exception {
        final long projectsCount = projectRepository.count();

        mockMvc.perform(post(PATH)
                        .content(new ObjectMapper().writeValueAsString(PROJECT_DTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.objectId").exists());

        assertEquals(projectsCount + 1, projectRepository.count());
        Project project = projectService.getProjectById(projectsCount + 1);
        assertThat(projectMapper.map(project)).isEqualToIgnoringNullFields(PROJECT_DTO);
    }


    @Test
    void testGetProjectsPage() throws Exception {
        mockMvc.perform(get(PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(3));
    }

    @Test
    void testUpdateProject() throws Exception {
        final ProjectDTO oldProject = projectService.getProjectDtoById(ID);

        mockMvc.perform(put(PATH + "/" + ID)
                        .content(new ObjectMapper().writeValueAsString(PROJECT_DTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertNotEquals(oldProject, projectService.getProjectDtoById(ID));
    }

    @Test
    void testDeleteProject() throws Exception {
        mockMvc.perform(delete(PATH + "/" + ID))
                .andExpect(status().isOk());

        assertFalse(projectService.getProjectById(ID).isActive());
    }

}
