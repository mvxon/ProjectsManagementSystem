package com.strigalev.projectsservice.mapper;

import com.strigalev.projectsservice.domain.Task;
import com.strigalev.projectsservice.dto.TaskDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface TaskListMapper {

    @Mappings({
            @Mapping(target = "description", ignore = true),
            @Mapping(target = "deadLineDate", ignore = true),
            @Mapping(target = "creationDate", ignore = true)
    })
    TaskDTO map(Task task);

    List<TaskDTO> map(Set<Task> tasks);

}
