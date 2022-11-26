package com.strigalev.projectsservice.mapper;

import com.strigalev.projectsservice.domain.Task;
import com.strigalev.projectsservice.dto.TaskDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-11-26T18:39:36+0300",
    comments = "version: 1.5.3.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-7.5.1.jar, environment: Java 17.0.4.1 (Amazon.com Inc.)"
)
@Component
public class TaskListMapperImpl implements TaskListMapper {

    @Override
    public TaskDTO map(Task task) {
        if ( task == null ) {
            return null;
        }

        TaskDTO.TaskDTOBuilder taskDTO = TaskDTO.builder();

        taskDTO.id( task.getId() );
        taskDTO.status( task.getStatus() );
        taskDTO.title( task.getTitle() );

        return taskDTO.build();
    }

    @Override
    public List<TaskDTO> map(Set<Task> tasks) {
        if ( tasks == null ) {
            return null;
        }

        List<TaskDTO> list = new ArrayList<TaskDTO>( tasks.size() );
        for ( Task task : tasks ) {
            list.add( map( task ) );
        }

        return list;
    }
}
