package org.example.librarymanager.services;

import org.example.librarymanager.dtos.UserInformationDto;
import org.example.librarymanager.dtos.UserInformationInputDto;
import org.example.librarymanager.dtos.UserInformationPatchDto;
import org.example.librarymanager.exceptions.ResourceNotFoundException;
import org.example.librarymanager.exceptions.UsernameAlreadyExistsException;
import org.example.librarymanager.mappers.UserInformationMapper;
import org.example.librarymanager.models.Role;
import org.example.librarymanager.models.User;
import org.example.librarymanager.models.UserInformation;
import org.example.librarymanager.repositories.RoleRepository;
import org.example.librarymanager.repositories.UserInformationRepository;
import org.example.librarymanager.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserInformationService {
    private final UserRepository userRepository;
    private final UserInformationRepository userInformationRepository;
    private final RoleRepository roleRepository;
    private final UserInformationMapper userInformationMapper;

    public UserInformationService(UserRepository userRepository, UserInformationRepository userInformationRepository, RoleRepository roleRepository, UserInformationMapper userInformationMapper) {
        this.userRepository = userRepository;
        this.userInformationRepository = userInformationRepository;
        this.roleRepository = roleRepository;
        this.userInformationMapper = userInformationMapper;
    }

    public UserInformationDto createUser(UserInformationInputDto userInformationInputDto) {

        if (userRepository.findByUsername(userInformationInputDto.username).isPresent()) {
            throw new UsernameAlreadyExistsException("Username " + userInformationInputDto.username + " already exists.");
        }

        User newUser = new User();
        newUser.setUsername(userInformationInputDto.username);
        newUser.setPassword(userInformationInputDto.password);
        // TODO: wachtwoord beveiligen, hier moet een passwordEncoder komen

        Set<Role> assignedRoles = new HashSet<>();
        if (userInformationInputDto.roles != null && !userInformationInputDto.roles.isEmpty()) {
            for (String roleName : userInformationInputDto.roles) {
                Optional<Role> optionalRole = roleRepository.findById("ROLE_" + roleName.toUpperCase());
                if (optionalRole.isPresent()) {
                    assignedRoles.add(optionalRole.get());
                } else {

                }
            }
        }

        if (assignedRoles.isEmpty()) {
            roleRepository.findById("ROLE_MEMBER").ifPresent(assignedRoles::add);
        }
        newUser.setRoles(assignedRoles);

        newUser = userRepository.save(newUser);

        UserInformation newUserInfo = new UserInformation(
                newUser,
                userInformationInputDto.firstName,
                userInformationInputDto.lastName,
                userInformationInputDto.street,
                userInformationInputDto.houseNumber,
                userInformationInputDto.postalCode,
                userInformationInputDto.city,
                userInformationInputDto.phone,
                userInformationInputDto.email
        );
        newUserInfo.setProfilePictureUrl(userInformationInputDto.profilePictureUrl);

        UserInformation savedUserInfo = userInformationRepository.save(newUserInfo);

        savedUserInfo.setUser(newUser);
        newUser.setUserInformation(savedUserInfo);
        userRepository.save(newUser);

        return userInformationMapper.toResponseDto(savedUserInfo);

    }

    public UserInformationDto getUserById(Long id){
        UserInformation userInformation = userInformationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
        return userInformationMapper.toResponseDto(userInformation);
    }

    public List<UserInformationDto> getAllUsers() {
        List<UserInformation> userInformations = userInformationRepository.findAll();
        return userInformationMapper.toResponseDtoList(userInformations);
    }

    public UserInformationDto getUserByUsername(String username) {
        UserInformation userInformation = userInformationRepository.findByUser_Username(username).orElseThrow(() -> new ResourceNotFoundException("User not found with username " + username));
        return userInformationMapper.toResponseDto(userInformation);
    }

    public UserInformationDto getMemberById(Long id) {
        UserInformation userInformation = userInformationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id " + id));
        if (!userInformation.getUser().getRoles().stream().anyMatch(role -> role.getRolename().equals("ROLE_MEMBER"))) {
            throw new ResourceNotFoundException("User with id " + id + " is not a member.");
        }
        return userInformationMapper.toResponseDto(userInformation);
    }

    public List<UserInformationDto> getAllMembers() {
        List<UserInformation> members = userInformationRepository.findAll().stream()
                .filter(ui -> ui.getUser().getRoles().stream().anyMatch(role -> role.getRolename().equals("ROLE_MEMBER")))
                .collect(Collectors.toList());
        return userInformationMapper.toResponseDtoList(members);
    }

    public UserInformationDto getMemberByUsername(String username) {
        UserInformation userInformation = userInformationRepository.findByUser_Username(username)
                .orElseThrow(() -> new ResourceNotFoundException("Member with username " + username + " not found."));

        if (!userInformation.getUser().getRoles().stream().anyMatch(role -> role.getRolename().equals("ROLE_MEMBER"))) {
            throw new ResourceNotFoundException("User with username " + username + " is not a member.");
        }
        return userInformationMapper.toResponseDto(userInformation);
    }

    public UserInformationDto getLibrarianById(Long id) {
        UserInformation userInformation = userInformationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Librarian not found with id " + id));
        if (!userInformation.getUser().getRoles().stream().anyMatch(role -> role.getRolename().equals("ROLE_LIBRARIAN"))) {
            throw new ResourceNotFoundException("User with id " + id + " is not a librarian.");
        }
        return userInformationMapper.toResponseDto(userInformation);
    }

    public List<UserInformationDto> getAllLibrarians() {
        List<UserInformation> librarians = userInformationRepository.findAll().stream()
                .filter(ui -> ui.getUser().getRoles().stream().anyMatch(role -> role.getRolename().equals("ROLE_LIBRARIAN")))
                .collect(Collectors.toList());
        return userInformationMapper.toResponseDtoList(librarians);
    }

    public UserInformationDto getLibrarianByUsername(String username) {
        UserInformation userInformation = userInformationRepository.findByUser_Username(username)
                .orElseThrow(() -> new ResourceNotFoundException("Librarian with username " + username + " not found."));

        if (!userInformation.getUser().getRoles().stream().anyMatch(role -> role.getRolename().equals("ROLE_LIBRARIAN"))) {
            throw new ResourceNotFoundException("User with username " + username + " is not a librarian.");
        }
        return userInformationMapper.toResponseDto(userInformation);
    }

    public UserInformationDto updateUserInformation(Long userInformationId, UserInformationInputDto userInformationInputDto) {
        UserInformation existingUserInfo = userInformationRepository.findById(userInformationId)
                .orElseThrow(() -> new ResourceNotFoundException("User information not found with ID: " + userInformationId));

        User user = existingUserInfo.getUser();

        existingUserInfo.setFirstName(userInformationInputDto.firstName);
        existingUserInfo.setLastName(userInformationInputDto.lastName);
        existingUserInfo.setStreet(userInformationInputDto.street);
        existingUserInfo.setHouseNumber(userInformationInputDto.houseNumber);
        existingUserInfo.setPostalCode(userInformationInputDto.postalCode);
        existingUserInfo.setCity(userInformationInputDto.city);

        if (userInformationInputDto.email != null) {
            existingUserInfo.setEmail(userInformationInputDto.email);
        }
        if (userInformationInputDto.profilePictureUrl != null) {
            existingUserInfo.setProfilePictureUrl(userInformationInputDto.profilePictureUrl);
        }
        if (userInformationInputDto.phone != null) {
            existingUserInfo.setPhone(userInformationInputDto.phone);
        }

        if (userInformationInputDto.username != null && !userInformationInputDto.username.equals(user.getUsername())) {
            if (userRepository.findByUsername(userInformationInputDto.username).isPresent() &&
                    !userRepository.findByUsername(userInformationInputDto.username).get().getUserId().equals(user.getUserId())) {
                throw new UsernameAlreadyExistsException("Username " + userInformationInputDto.username + " already exists.");
            }
            user.setUsername(userInformationInputDto.username);
        }

        if (userInformationInputDto.password != null && !userInformationInputDto.password.isEmpty()) {
            user.setPassword(userInformationInputDto.password); // TODO: wachtwoord beveiligen
        }

        if (userInformationInputDto.roles != null) {
            Set<Role> newRoles = new HashSet<>();
            for (String roleName : userInformationInputDto.roles) {
                roleRepository.findById("ROLE_" + roleName.toUpperCase()).ifPresent(newRoles::add);
            }
            user.setRoles(newRoles);
        }

        UserInformation updatedUserInfo = userInformationRepository.save(existingUserInfo);
        userRepository.save(user);

        return userInformationMapper.toResponseDto(updatedUserInfo);
    }

    public UserInformationDto patchUser(UserInformationPatchDto userInformationPatchDto, Long userId) {
        UserInformation existingUserInformation = userInformationRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User information not found with ID: " + userId));

        User user = existingUserInformation.getUser();

        if (userInformationPatchDto.firstName != null) {
            existingUserInformation.setFirstName(userInformationPatchDto.firstName);
        }
        if (userInformationPatchDto.lastName != null) {
            existingUserInformation.setLastName(userInformationPatchDto.lastName);
        }
        if (userInformationPatchDto.street != null) {
            existingUserInformation.setStreet(userInformationPatchDto.street);
        }
        if (userInformationPatchDto.houseNumber != null) {
            existingUserInformation.setHouseNumber(userInformationPatchDto.houseNumber);
        }
        if (userInformationPatchDto.postalCode != null) {
            existingUserInformation.setPostalCode(userInformationPatchDto.postalCode);
        }
        if (userInformationPatchDto.city != null) {
            existingUserInformation.setCity(userInformationPatchDto.city);
        }
        if (userInformationPatchDto.phone != null) {
            existingUserInformation.setPhone(userInformationPatchDto.phone);
        }
        if (userInformationPatchDto.email != null) {
            existingUserInformation.setEmail(userInformationPatchDto.email);
        }
        if (userInformationPatchDto.profilePictureUrl != null) {
            existingUserInformation.setProfilePictureUrl(userInformationPatchDto.profilePictureUrl);
        }


        if (userInformationPatchDto.username != null && !userInformationPatchDto.username.equals(user.getUsername())) {
            if (userRepository.findByUsername(userInformationPatchDto.username).isPresent() &&
                    !userRepository.findByUsername(userInformationPatchDto.username).get().getUserId().equals(user.getUserId())) {
                throw new UsernameAlreadyExistsException("Username " + userInformationPatchDto.username + " already exists.");
            }
            user.setUsername(userInformationPatchDto.username);
        }

        if (userInformationPatchDto.password != null && !userInformationPatchDto.password.isEmpty()) {
            user.setPassword(userInformationPatchDto.password);
            // TODO: wachtwoord beveiligen
        }

        if (userInformationPatchDto.roles != null) {
            Set<Role> newRoles = new HashSet<>();
            for (String roleName : userInformationPatchDto.roles) {
                roleRepository.findById("ROLE_" + roleName.toUpperCase()).ifPresent(newRoles::add);
            }
            user.setRoles(newRoles);
        }

        UserInformation patchedUserInformation = this.userInformationRepository.save(existingUserInformation);
        userRepository.save(user);

        return userInformationMapper.toResponseDto(patchedUserInformation);
    }

    public void deleteUser(Long userId) {
        UserInformation userInformationToDelete = userInformationRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User Information not found with ID: " + userId));

        User userToDelete = userInformationToDelete.getUser();

        userInformationRepository.delete(userInformationToDelete);
        userRepository.delete(userToDelete);
    }
}