package com.strigalev.projectsservice.mapper;

import com.strigalev.projectsservice.domain.Project;
import com.strigalev.projectsservice.domain.ProjectStatus;
import com.strigalev.projectsservice.dto.ProjectDTO;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-11-23T17:10:25+0300",
    comments = "version: 1.5.3.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-7.5.1.jar, environment: Java 17.0.4.1 (Amazon.com Inc.)"
)
@Component
public class ProjectListMapperImpl implements ProjectListMapper {

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
        projectDTO.customer( project.getCustomer() );

        return projectDTO.build();
    }

    @Override
    public List<ProjectDTO> map(List<Project> projects) {
        if ( projects == null ) {
            return null;
        }

        List<ProjectDTO> list = new ArrayList<ProjectDTO>( projects.size() );
        for ( Project project : projects ) {
            list.add( map( project ) );
        }

        return list;
    }
}
