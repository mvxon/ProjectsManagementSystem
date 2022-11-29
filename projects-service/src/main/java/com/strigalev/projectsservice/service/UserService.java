package com.strigalev.projectsservice.service;

import com.strigalev.projectsservice.domain.Project;
import com.strigalev.projectsservice.domain.Task;
import com.strigalev.projectsservice.domain.User;
import com.strigalev.projectsservice.dto.UserStatisticDTO;
import com.strigalev.starter.dto.UserDTO;
import com.strigalev.starter.model.UserAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface UserService {
    User getUserById(Long id);

    void deleteUser(Long id);

    UserDTO getUserDtoById(Long id);

    User getUserByEmail(String email);

    Page<UserDTO> getUsersPageByProjectId(Long projectId, Pageable pageable);

    Page<UserDTO> getUsersPageByTaskId(Long taskId, Pageable pageable);

    Page<UserDTO> getUsersPageByProjectName(String projectName, Pageable pageable);

    UserDTO getUserDto(Long id);

    boolean isPrincipalHaveTask(Long taskId);

    List<UserDTO> getUsersDtoByFullName(String firstName, String lastName);

    void sendUserTaskAction(UserAction action, Task task);

    void sendManagerTaskAction(UserAction action, Task task);

    void sendManagerProjectAction(UserAction action, Project project, Task task);

    void sendManagerAction(UserAction action, Project project, Task task, Long actionedUserId);

    List<UserStatisticDTO> getUserStatisticBetween(Long[] usersIds, LocalDateTime from, LocalDateTime to);

    User getPrincipal();

}