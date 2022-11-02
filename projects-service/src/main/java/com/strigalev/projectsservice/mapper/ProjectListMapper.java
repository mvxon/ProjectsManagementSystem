package com.strigalev.projectsservice.mapper;

import com.strigalev.projectsservice.domain.Project;
import com.strigalev.projectsservice.dto.ProjectDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectListMapper {

    @Mappings({
            @Mapping(target = "title", ignore = true),
            @Mapping(target = "description", ignore = true),
            @Mapping(target = "deadLineDate", ignore = true),
            @Mapping(target = "creationDate", ignore = true)
    })
    ProjectDTO map(Project project);

    List<ProjectDTO> map(List<Project> projects);

}
