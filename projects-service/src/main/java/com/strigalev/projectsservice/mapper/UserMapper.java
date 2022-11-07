package com.strigalev.projectsservice.mapper;

import com.strigalev.projectsservice.domain.User;
import com.strigalev.projectsservice.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO map(User user);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "workingProjects", ignore = true)
    @Mapping(target = "workingTasks", ignore = true)
    User map(UserDTO userDTO);

}
