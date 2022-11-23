package com.strigalev.projectsservice.mapper;

import com.strigalev.projectsservice.domain.Project;
import com.strigalev.projectsservice.domain.ProjectStatus;
import com.strigalev.projectsservice.dto.ProjectDTO;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-11-23T17:10:25+0300",
    comments = "version: 1.5.3.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-7.5.1.jar, environment: Java 17.0.4.1 (Amazon.com Inc.)"
)
@Component
public class ProjectMapperImpl implements ProjectMapper {

    @Override
    public ProjectDTO map(Project project) {
        if ( project == null ) {
            return null;
        }

        ProjectDTO.ProjectDTOBuilder projectDTO = ProjectDTO.builder();

        projectDTO.id( project.getId() );
        Set<ProjectStatus> set = project.getStatuses();
        if ( set != null ) {
            projectDTO.statuses( new LinkedHashSet<ProjectStatus>( set ) );
        }
        projectDTO.name( project.getName() );
        projectDTO.title( project.getTitle() );
        projectDTO.customer( project.getCustomer() );
        projectDTO.description( project.getDescription() );
        if ( project.getCreationDate() != null ) {
            projectDTO.creationDate( DateTimeFormatter.ISO_LOCAL_DATE.format( project.getCreationDate() ) );
        }
        if ( project.getDeadLineDate() != null ) {
            projectDTO.deadLineDate( DateTimeFormatter.ISO_LOCAL_DATE.format( project.getDeadLineDate() ) );
        }

        return projectDTO.build();
    }

    @Override
    public Project map(ProjectDTO projectDTO) {
        if ( projectDTO == null ) {
            return null;
        }

        Project.ProjectBuilder project = Project.builder();

        project.name( projectDTO.getName() );
        project.title( projectDTO.getTitle() );
        project.description( projectDTO.getDescription() );
        project.customer( projectDTO.getCustomer() );
        if ( projectDTO.getCreationDate() != null ) {
            project.creationDate( LocalDate.parse( projectDTO.getCreationDate() ) );
        }
        if ( projectDTO.getDeadLineDate() != null ) {
            project.deadLineDate( LocalDate.parse( projectDTO.getDeadLineDate() ) );
        }
        Set<ProjectStatus> set = projectDTO.getStatuses();
        if ( set != null ) {
            project.statuses( new LinkedHashSet<ProjectStatus>( set ) );
        }

        return project.build();
    }

    @Override
    public void updateProjectFromDto(ProjectDTO projectDTO, Project project) {
        if ( projectDTO == null ) {
            return;
        }

        project.setId( projectDTO.getId() );
        project.setName( projectDTO.getName() );
        project.setTitle( projectDTO.getTitle() );
        project.setDescription( projectDTO.getDescription() );
        project.setCustomer( projectDTO.getCustomer() );
        if ( projectDTO.getCreationDate() != null ) {
            project.setCreationDate( LocalDate.parse( projectDTO.getCreationDate() ) );
        }
        else {
            project.setCreationDate( null );
        }
        if ( projectDTO.getDeadLineDate() != null ) {
            project.setDeadLineDate( LocalDate.parse( projectDTO.getDeadLineDate() ) );
        }
        else {
            project.setDeadLineDate( null );
        }
        if ( project.getStatuses() != null ) {
            Set<ProjectStatus> set = projectDTO.getStatuses();
            if ( set != null ) {
                project.getStatuses().clear();
                project.getStatuses().addAll( set );
            }
            else {
                project.setStatuses( null );
            }
        }
        else {
            Set<ProjectStatus> set = projectDTO.getStatuses();
            if ( set != null ) {
                project.setStatuses( new LinkedHashSet<ProjectStatus>( set ) );
            }
        }
    }
}
