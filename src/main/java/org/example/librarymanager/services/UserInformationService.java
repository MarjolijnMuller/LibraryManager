package org.example.librarymanager.services;

import org.example.librarymanager.dtos.UserInformationInputDto;
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

    public UserInformationService(UserRepository userRepository, UserInformationRepository userInformationRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.userInformationRepository = userInformationRepository;
        this.roleRepository = roleRepository;

    }

    public UserInformation createUser(UserInformationInputDto userInformationInputDto) {

        if (userRepository.findByUsername(userInformationInputDto.username).isPresent()) {
            throw new UsernameAlreadyExistsException("Username " + userInformationInputDto.username + " already exists.");
        }

        User newUser = new User();
        newUser.setUsername(userInformationInputDto.username);
        newUser.setPassword(userInformationInputDto.password);

        Set<Role> assignedRoles = new HashSet<>();
        if (userInformationInputDto.roles != null && !userInformationInputDto.roles.isEmpty()) {
            for (String roleName : userInformationInputDto.roles) {
                Optional<Role> optionalRole = roleRepository.findById("ROLE_" + roleName.toUpperCase());
                if (optionalRole.isPresent()) {
                    assignedRoles.add(optionalRole.get());
                } else {
                    // Handle invalid role (e.g., throw exception or log)
                }
            }
        }

        if (assignedRoles.isEmpty()) {
            roleRepository.findById("ROLE_MEMBER").ifPresent(assignedRoles::add);
        }
        newUser.setRoles(assignedRoles);

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

        newUser.setUserInformation(newUserInfo);
        userRepository.save(newUser);

        return newUserInfo;
    }

    public UserInformation getUserById(Long id){
        UserInformation userInformation = userInformationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
        return userInformation;
    }

    public UserInformation getAllUsers() {
        return userInformationRepository.findAll().stream().findFirst().get();
    }

    public UserInformation getUserByUsername(String username) {
        UserInformation userInformation = userInformationRepository.findByUser_Username(username).orElseThrow(() -> new ResourceNotFoundException("User not found with username " + username));
        return userInformation;
    }

    public UserInformation getMemberById(Long id) {
        UserInformation userInformation = userInformationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id " + id));
        if (!userInformation.getUser().getRoles().stream().anyMatch(role -> role.getRolename().equals("ROLE_MEMBER"))) {
            throw new ResourceNotFoundException("User with id " + id + " is not a member.");
        }
        return userInformation;
    }

    public List<UserInformation> getAllMembers() {
        return userInformationRepository.findAll().stream()
                .filter(ui -> ui.getUser().getRoles().stream().anyMatch(role -> role.getRolename().equals("ROLE_MEMBER")))
                .collect(Collectors.toList());
    }

    public UserInformation getMemberByUsername(String username) {
        UserInformation userInformation = userInformationRepository.findByUser_Username(username)
                .orElseThrow(() -> new ResourceNotFoundException("Member with username " + username + " not found."));

        if (!userInformation.getUser().getRoles().stream().anyMatch(role -> role.getRolename().equals("ROLE_MEMBER"))) {
            throw new ResourceNotFoundException("User with username " + username + " is not a member.");
        }
        return userInformation;
    }

    public UserInformation getLibrarianById(Long id) {
        UserInformation userInformation = userInformationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Librarian not found with id " + id));
        if (!userInformation.getUser().getRoles().stream().anyMatch(role -> role.getRolename().equals("ROLE_LIBRARIAN"))) {
            throw new ResourceNotFoundException("User with id " + id + " is not a librarian.");
        }
        return userInformation;
    }

    public List<UserInformation> getAllLibrarians() {
        return userInformationRepository.findAll().stream()
                .filter(ui -> ui.getUser().getRoles().stream().anyMatch(role -> role.getRolename().equals("ROLE_LIBRARIAN")))
                .collect(Collectors.toList());
    }

    public UserInformation getLibrarianByUsername(String username) {
        UserInformation userInformation = userInformationRepository.findByUser_Username(username)
                .orElseThrow(() -> new ResourceNotFoundException("Librarian with username " + username + " not found."));

        if (!userInformation.getUser().getRoles().stream().anyMatch(role -> role.getRolename().equals("ROLE_LIBRARIAN"))) {
            throw new ResourceNotFoundException("User with username " + username + " is not a librarian.");
        }
        return userInformation;
    }

    public UserInformation updateUserInformation(Long userInformationId, UserInformationInputDto userInformationInputDto) {
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
            user.setPassword(userInformationInputDto.password); // GEEN passwordEncoder.encode() meer
        }

        if (userInformationInputDto.roles != null) {
            Set<Role> newRoles = new HashSet<>();
            for (String roleName : userInformationInputDto.roles) {
                roleRepository.findById("ROLE_" + roleName.toUpperCase()).ifPresent(newRoles::add);
            }
            user.setRoles(newRoles);
        }

        return userInformationRepository.save(existingUserInfo);
    }

    public UserInformation patchUser(UserInformationInputDto userInformationInputDto, Long userId) {
        UserInformation existingUser = userInformationRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        if (userInformationInputDto.lastName != null) {
            existingUser.setLastName(userInformationInputDto.lastName);
        }
        if (userInformationInputDto.street != null) {
            existingUser.setStreet(userInformationInputDto.street);
        }
        if (userInformationInputDto.houseNumber != null) {
            existingUser.setHouseNumber(userInformationInputDto.houseNumber);
        }
        if (userInformationInputDto.postalCode != null) {
            existingUser.setPostalCode(userInformationInputDto.postalCode);
        }
        if (userInformationInputDto.city != null) {
            existingUser.setCity(userInformationInputDto.city);
        }
        if (userInformationInputDto.phone != null) {
            existingUser.setPhone(userInformationInputDto.phone);
        }
        if (userInformationInputDto.username != null) {
            existingUser.getUser().setUsername(userInformationInputDto.username);
        }
        //TODO: wachtwoord beveiligen
        if (userInformationInputDto.password != null) {
            existingUser.getUser().setPassword(userInformationInputDto.password);
        }
        if (userInformationInputDto.email != null) {
            existingUser.setEmail(userInformationInputDto.email);
        }
        if (userInformationInputDto.profilePictureUrl != null) {
            existingUser.setProfilePictureUrl(userInformationInputDto.profilePictureUrl);
        }
        return this.userInformationRepository.save(existingUser);
    }

    public void deleteUser(Long userId) {
        this.userInformationRepository.deleteById(userId);
    }
}
