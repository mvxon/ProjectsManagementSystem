package com.strigalev.projectsservice.mapper;

import com.strigalev.projectsservice.domain.Task;
import com.strigalev.projectsservice.dto.TaskDTO;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-11-26T18:39:36+0300",
    comments = "version: 1.5.3.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-7.5.1.jar, environment: Java 17.0.4.1 (Amazon.com Inc.)"
)
@Component
public class TaskMapperImpl implements TaskMapper {

    @Override
    public TaskDTO map(Task task) {
        if ( task == null ) {
            return null;
        }

        TaskDTO.TaskDTOBuilder taskDTO = TaskDTO.builder();

        taskDTO.id( task.getId() );
        taskDTO.status( task.getStatus() );
        taskDTO.title( task.getTitle() );
        taskDTO.description( task.getDescription() );
        if ( task.getCreationDate() != null ) {
            taskDTO.creationDate( DateTimeFormatter.ISO_LOCAL_DATE.format( task.getCreationDate() ) );
        }
        if ( task.getDeadLineDate() != null ) {
            taskDTO.deadLineDate( DateTimeFormatter.ISO_LOCAL_DATE.format( task.getDeadLineDate() ) );
        }

        return taskDTO.build();
    }

    @Override
    public List<TaskDTO> map(List<Task> tasks) {
        if ( tasks == null ) {
            return null;
        }

        List<TaskDTO> list = new ArrayList<TaskDTO>( tasks.size() );
        for ( Task task : tasks ) {
            list.add( map( task ) );
        }

        return list;
    }

    @Override
    public Task map(TaskDTO taskDTO) {
        if ( taskDTO == null ) {
            return null;
        }

        Task.TaskBuilder task = Task.builder();

        task.id( taskDTO.getId() );
        task.title( taskDTO.getTitle() );
        task.description( taskDTO.getDescription() );
        if ( taskDTO.getCreationDate() != null ) {
            task.creationDate( LocalDate.parse( taskDTO.getCreationDate() ) );
        }
        if ( taskDTO.getDeadLineDate() != null ) {
            task.deadLineDate( LocalDate.parse( taskDTO.getDeadLineDate() ) );
        }
        task.status( taskDTO.getStatus() );

        return task.build();
    }

    @Override
    public void updateTaskFromDto(TaskDTO taskDTO, Task task) {
        if ( taskDTO == null ) {
            return;
        }

        task.setId( taskDTO.getId() );
        task.setTitle( taskDTO.getTitle() );
        task.setDescription( taskDTO.getDescription() );
        if ( taskDTO.getCreationDate() != null ) {
            task.setCreationDate( LocalDate.parse( taskDTO.getCreationDate() ) );
        }
        else {
            task.setCreationDate( null );
        }
        if ( taskDTO.getDeadLineDate() != null ) {
            task.setDeadLineDate( LocalDate.parse( taskDTO.getDeadLineDate() ) );
        }
        else {
            task.setDeadLineDate( null );
        }
        task.setStatus( taskDTO.getStatus() );
    }
}
