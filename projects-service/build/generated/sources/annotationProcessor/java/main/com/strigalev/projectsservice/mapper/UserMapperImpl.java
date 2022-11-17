package com.strigalev.projectsservice.mapper;

import com.strigalev.projectsservice.domain.User;
import com.strigalev.projectsservice.dto.EmployeeDTO;
import com.strigalev.projectsservice.dto.SignUpRequest;
import com.strigalev.starter.dto.UserDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-11-17T19:39:30+0300",
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

    @Override
    public EmployeeDTO mapToEmployeeDto(User user) {
        if ( user == null ) {
            return null;
        }

        EmployeeDTO.EmployeeDTOBuilder employeeDTO = EmployeeDTO.builder();

        employeeDTO.id( user.getId() );
        employeeDTO.firstName( user.getFirstName() );
        employeeDTO.lastName( user.getLastName() );
        employeeDTO.email( user.getEmail() );
        employeeDTO.role( user.getRole() );

        return employeeDTO.build();
    }

    @Override
    public List<EmployeeDTO> mapListToEmployeeDto(List<User> users) {
        if ( users == null ) {
            return null;
        }

        List<EmployeeDTO> list = new ArrayList<EmployeeDTO>( users.size() );
        for ( User user : users ) {
            list.add( mapToEmployeeDto( user ) );
        }

        return list;
    }
}
