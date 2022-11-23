package com.strigalev.projectsservice.service;

import com.strigalev.projectsservice.domain.User;
import com.strigalev.projectsservice.dto.SignUpRequest;
import com.strigalev.projectsservice.dto.UserStatisticDTO;
import com.strigalev.starter.dto.UserDTO;
import com.strigalev.starter.model.UserAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface UserService {
    User getUserById(Long id);

    void saveUser(SignUpRequest signUpRequest);

    void deleteUser(Long id);

    UserDTO getUserDtoById(Long id);

    UserDTO getUserDetailsByEmail(String email);

    boolean existsByEmail(String email);

    User getUserByEmail(String email);

    Page<UserDTO> getUsersPageByProjectId(Long projectId, Pageable pageable);

    Page<UserDTO> getUsersPageByTaskId(Long taskId, Pageable pageable);

    Page<UserDTO> getUsersPageByProjectName(String projectName, Pageable pageable);

    UserDTO getUserDto(Long id);

    boolean isPrincipalHaveTask(Long taskId);

    List<UserDTO> getUsersDtoByFullName(String firstName, String lastName);

    void sendUserTaskAction(UserAction action, Long taskId);

    void sendUserProjectAction(UserAction action, Long projectId);

    void sendManagerAction(UserAction action, Long projectId, Long taskId, Long actionedUserId);

    List<UserStatisticDTO> getUserStatisticBetween(Long[] ids, LocalDateTime from, LocalDateTime to);

}

