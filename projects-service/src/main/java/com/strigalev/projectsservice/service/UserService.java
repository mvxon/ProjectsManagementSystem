package com.strigalev.projectsservice.service;

import com.strigalev.projectsservice.domain.User;
import com.strigalev.projectsservice.dto.SignUpRequest;
import com.strigalev.starter.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    User getUserById(Long id);

    void saveUser(SignUpRequest signUpRequest);

    void deleteUser(Long id);

    UserDTO getUserDtoById(Long id);

    Page<UserDTO> getUsersPageByProjectId(Long projectId, Pageable pageable);

    Page<UserDTO> getUsersPageByTaskId(Long taskId, Pageable pageable);

    UserDTO getUserDetailsByEmail(String email);

    boolean existsByEmail(String email);

    UserDTO getUserDetailsById(Long id);
}
