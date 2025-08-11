package org.example.librarymanager.services;

import org.example.librarymanager.dtos.UserDto;
import org.example.librarymanager.dtos.UserInputDto;
import org.example.librarymanager.mappers.UserMapper;
import org.example.librarymanager.models.Role;
import org.example.librarymanager.models.User;
import org.example.librarymanager.repositories.RoleRepository;
import org.example.librarymanager.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public UserDto createUser(UserInputDto userInputDto) {
        User newUser = new User();
        newUser.setUsername(userInputDto.username);
        newUser.setPassword(passwordEncoder.encode(userInputDto.password));

        Set<Role> assignedRoles = new HashSet<>();
        if (userInputDto.roles != null) {
            for (String rolename : userInputDto.roles) {
                Optional<Role> optionalRole = roleRepository.findById("ROLE_" + rolename.toUpperCase());
                optionalRole.ifPresent(assignedRoles::add);
            }
        }

        if (assignedRoles.isEmpty()) {
            roleRepository.findById("ROLE_USER").ifPresent(assignedRoles::add);
        }
        newUser.setRoles(assignedRoles);

        User savedUser = userRepository.save(newUser);

        return userMapper.toResponseDto(savedUser);
    }
}
