package com.strigalev.projectsservice.service;

import com.strigalev.projectsservice.domain.User;
import com.strigalev.projectsservice.dto.EmployeeDTO;
import com.strigalev.projectsservice.dto.SignUpRequest;
import com.strigalev.starter.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    User getUserById(Long id);

    void saveUser(SignUpRequest signUpRequest);

    void deleteUser(Long id);

    UserDTO getUserDtoById(Long id);

    UserDTO getUserDetailsByEmail(String email);

    boolean existsByEmail(String email);

    User getUserByEmail(String email);

    Long getPrincipalId();

    Page<EmployeeDTO> getUsersPageByProjectId(Long projectId, Pageable pageable);

    Page<EmployeeDTO> getUsersPageByTaskId(Long taskId, Pageable pageable);

    Page<EmployeeDTO> getUsersPageByProjectName(String projectName, Pageable pageable);

    EmployeeDTO getEmployeeDto(Long id);

    boolean isPrincipalHaveTask(Long taskId);

    boolean isUserHaveTask(Long userId, Long taskId);

    List<EmployeeDTO> getEmployeesDtoByFullName(String firstName, String lastName);


}

