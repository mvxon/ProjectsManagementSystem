package com.strigalev.projectsservice.mapper;

import com.strigalev.projectsservice.domain.User;
import com.strigalev.starter.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "workingProjectsIds", ignore = true)
    @Mapping(target = "workingTasksIds", ignore = true)
    UserDTO map(User user);

}