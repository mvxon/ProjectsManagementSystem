package com.strigalev.projectsservice.mapper;

import com.strigalev.projectsservice.domain.User;
import com.strigalev.projectsservice.dto.SignUpRequest;
import com.strigalev.starter.dto.UserDTO;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-11-10T18:51:04+0300",
    comments = "version: 1.5.3.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-7.5.1.jar, environment: Java 17.0.4.1 (Amazon.com Inc.)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDTO map(User user) {
        if ( user == null ) {
            return null;
        }

        UserDTO.UserDTOBuilder userDTO = UserDTO.builder();

        userDTO.id( user.getId() );
        userDTO.firstName( user.getFirstName() );
        userDTO.lastName( user.getLastName() );
        userDTO.email( user.getEmail() );
        userDTO.role( user.getRole() );

        return userDTO.build();
    }

    @Override
    public UserDTO mapWithPassword(User user) {
        if ( user == null ) {
            return null;
        }

        UserDTO.UserDTOBuilder userDTO = UserDTO.builder();

        userDTO.id( user.getId() );
        userDTO.email( user.getEmail() );
        userDTO.role( user.getRole() );
        userDTO.password( user.getPassword() );

        return userDTO.build();
    }

    @Override
    public User map(SignUpRequest signUpRequest) {
        if ( signUpRequest == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.firstName( signUpRequest.getFirstName() );
        user.lastName( signUpRequest.getLastName() );
        user.email( signUpRequest.getEmail() );

        return user.build();
    }
}
