package com.strigalev.projectsservice.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.strigalev.projectsservice.dto.ProjectDTO;
import com.strigalev.projectsservice.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProjectEndpointTest {
    private static final String PATH = "/api/v1/projects";
    private static final long ID = 1L;
    private static final String NAME = "ProjectX";
    private static final String TITLE = "Asd asd asd";
    private static final String DESC = "Asd asd asd asd asd";
    private static final String CUSTOMER = "FEAR";
    private static final String DEAD_LINE_DATE = "2025-12-15";
    private static final String CREATION_DATE = "2022-07-7";
    private static final ProjectDTO PROJECT_DTO = ProjectDTO.builder()
            .name(NAME)
            .title(TITLE)
            .description(DESC)
            .id(ID)
            .customer(CUSTOMER)
            .deadLineDate(DEAD_LINE_DATE)
            .creationDate(CREATION_DATE)
            .build();

    @MockBean
    private ProjectService projectService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getProjectById() throws Exception {
        when(projectService.getProjectDtoById(ID)).thenReturn(PROJECT_DTO);

        mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/" + ID))
                .andExpect(jsonPath("$.id").value(ID))
                .andExpect(jsonPath("$.name").value(NAME))
                .andExpect(jsonPath("$.title").value(TITLE))
                .andExpect(jsonPath("$.description").value(DESC))
                .andExpect(jsonPath("$.customer").value(CUSTOMER))
                .andExpect(jsonPath("$.deadLineDate").value(DEAD_LINE_DATE))
                .andExpect(jsonPath("$.creationDate").value(CREATION_DATE));

    }

    @Test
    void createProject() throws Exception {
        when(projectService.createProject(PROJECT_DTO)).thenReturn(ID);

        mockMvc.perform(post(PATH)
                        .content(new ObjectMapper().writeValueAsString(PROJECT_DTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.objectId").value(ID));
    }

    @Test
    void createProject_whenInvalidRequestBody() throws Exception {
        when(projectService.createProject(PROJECT_DTO)).thenReturn(ID);

        mockMvc.perform(post(PATH)
                        .content(new ObjectMapper().writeValueAsString(new ProjectDTO()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProject_whenAlreadyExistsWithName() throws Exception {
        when(projectService.isProjectWithNameExists(PROJECT_DTO.getName())).thenReturn(true);

        mockMvc.perform(post(PATH)
                        .content(new ObjectMapper().writeValueAsString(PROJECT_DTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("name : " +
                        String.format("Project with name %s already exists", PROJECT_DTO.getName())));
    }

    @Test
    void getProjectsPage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(PATH))
                .andExpect(status().isOk());
    }

    @Test
    void deleteProject() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(PATH + "/" + ID))
                .andExpect(status().isOk());
        verify(projectService).deleteProject(ID);
    }


    @Test
    void updateProject() throws Exception {
        mockMvc.perform(put(PATH + "/" + ID)
                        .content(new ObjectMapper().writeValueAsString(PROJECT_DTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(projectService).updateProject(PROJECT_DTO);
    }

    @Test
    void updateProject_whenInvalidRequestBody() throws Exception {
        mockMvc.perform(put(PATH + "/" + ID)
                        .content(new ObjectMapper().writeValueAsString(new ProjectDTO()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void updateProject_whenAlreadyExistsWithName() throws Exception {
        when(projectService.isProjectWithNameExists(PROJECT_DTO.getName())).thenReturn(true);

        mockMvc.perform(put(PATH + "/" + ID)
                        .content(new ObjectMapper().writeValueAsString(PROJECT_DTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.message").value("name : " +
                                String.format("Project with name %s already exists", PROJECT_DTO.getName()))
                );
    }


}