package com.strigalev.projectsservice.service.impl;

import com.strigalev.projectsservice.domain.User;
import com.strigalev.projectsservice.dto.UserDTO;
import com.strigalev.projectsservice.exception.ResourceNotFoundException;
import com.strigalev.projectsservice.mapper.UserMapper;
import com.strigalev.projectsservice.repository.UserRepository;
import com.strigalev.projectsservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.strigalev.projectsservice.util.MethodsUtil.getProjectNotExistsMessage;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(getProjectNotExistsMessage(id))
                );
    }

    @Override
    @Transactional
    public Long saveUser(UserDTO userDTO) {
        User mappedUser = userMapper.map(userDTO);
        mappedUser.setPassword(passwordEncoder.encode(mappedUser.getPassword()));
        return userRepository.save(mappedUser).getId();
    }

    @Override
    @Transactional
    public void updateUser(UserDTO userDTO) {

    }

    @Override
    @Transactional
    public void deleteUser(Long id) {

    }

    @Override
    public UserDTO getUserDtoById(Long id) {
        return userMapper.map(getUserById(id));
    }

    @Override
    public Page<UserDTO> getUsersPageByProjectId(Long projectId, Pageable pageable) {
        Page<User> users = userRepository.findAllByProjectId(pageable, projectId);
        if (users.getContent().isEmpty()) {
            throw new ResourceNotFoundException("Page not found");
        }
        return users.map(userMapper::map);
    }

    @Override
    public Page<UserDTO> getUsersPageByTaskId(Long taskId, Pageable pageable) {
        Page<User> users = userRepository.findAllByTaskId(pageable, taskId);
        if (users.getContent().isEmpty()) {
            throw new ResourceNotFoundException("Page not found");
        }
        return users.map(userMapper::map);
    }
}
