package com.strigalev.projectsservice.mapper;

import com.strigalev.projectsservice.domain.User;
import com.strigalev.projectsservice.dto.EmployeeDTO;
import com.strigalev.projectsservice.dto.SignUpRequest;
import com.strigalev.starter.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    UserDTO map(User user);

    @Mapping(target = "firstName", ignore = true)
    @Mapping(target = "lastName", ignore = true)
    UserDTO mapWithPassword(User user);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    User map(SignUpRequest signUpRequest);

    @Mapping(target = "workingProjectsIds", ignore = true)
    @Mapping(target = "workingTasksIds", ignore = true)
    EmployeeDTO mapToEmployeeDto(User user);

    List<EmployeeDTO> mapListToEmployeeDto(List<User> users);
}
