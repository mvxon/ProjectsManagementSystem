package com.strigalev.projectsservice.mapper;

import com.strigalev.projectsservice.domain.User;
import com.strigalev.projectsservice.dto.UserDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO map(User user);

}
