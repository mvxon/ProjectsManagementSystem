package com.strigalev.authenticationservice.mapper;

import com.strigalev.authenticationservice.domain.User;
import com.strigalev.authenticationservice.dto.SignUpRequest;
import com.strigalev.starter.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    User map(SignUpRequest signUpRequest);

    UserDTO map(User user);
}
