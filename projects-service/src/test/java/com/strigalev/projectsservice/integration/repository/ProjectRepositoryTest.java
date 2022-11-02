package com.strigalev.projectsservice.integration.repository;

import com.strigalev.projectsservice.domain.Project;
import com.strigalev.projectsservice.integration.IntegrationTestBase;
import com.strigalev.projectsservice.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectRepositoryTest extends IntegrationTestBase {

    private static final long ID = 1;
    private static final String NAME = "ProjectX";

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    void testFindById() {
        Optional<Project> project = projectRepository.findById(ID);
        assertTrue(project.isPresent());
        assertEquals(NAME, project.get().getName());
    }

    @Test
    void testFindAllByActiveIsTrue() {
        Page<Project> projectPage = projectRepository.findAllByActiveIsTrue(Pageable.unpaged());
        assertThat(projectPage.getContent(), hasSize(3));
    }

    @Test
    void testFindAll() {
        Page<Project> projectPage = projectRepository.findAll(Pageable.unpaged());
        assertThat(projectPage.getContent(), hasSize(4));
    }

    @Test
    void testExistsByName() {
        assertTrue(projectRepository.existsByName(NAME));
    }

}
