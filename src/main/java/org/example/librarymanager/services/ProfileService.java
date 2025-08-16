package org.example.librarymanager.services;

import org.example.librarymanager.dtos.ProfileDto;
import org.example.librarymanager.dtos.ProfileInputDto;
import org.example.librarymanager.dtos.ProfilePatchDto;
import org.example.librarymanager.exceptions.AccessDeniedException;
import org.example.librarymanager.exceptions.ResourceNotFoundException;
import org.example.librarymanager.exceptions.UsernameAlreadyExistsException;
import org.example.librarymanager.mappers.ProfileMapper;
import org.example.librarymanager.models.Role;
import org.example.librarymanager.models.User;
import org.example.librarymanager.models.Profile;
import org.example.librarymanager.repositories.RoleRepository;
import org.example.librarymanager.repositories.ProfileRepository;
import org.example.librarymanager.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class ProfileService {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final StorageService storageService;

    public ProfileService(UserRepository userRepository, ProfileRepository profileRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, StorageService storageService) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.storageService = storageService;
    }

    public ProfileDto createProfile(ProfileInputDto profileInputDto) {
        if (userRepository.findByUsername(profileInputDto.username).isPresent()) {
            throw new UsernameAlreadyExistsException("Username " + profileInputDto.username + " already exists.");
        }

        User newUser = new User();
        newUser.setUsername(profileInputDto.username);
        newUser.setPassword(passwordEncoder.encode(profileInputDto.password));

        Set<Role> assignedRoles = new HashSet<>();
        if (profileInputDto.roles != null && !profileInputDto.roles.isEmpty()) {
            for (String roleName : profileInputDto.roles) {
                Optional<Role> optionalRole = roleRepository.findById("ROLE_" + roleName.toUpperCase());
                if (optionalRole.isPresent()) {
                    assignedRoles.add(optionalRole.get());
                } else {
                    throw new IllegalArgumentException("Invalid role specified: " + roleName);
                }
            }
        }

        if (assignedRoles.isEmpty()) {
            roleRepository.findById("ROLE_MEMBER").ifPresent(assignedRoles::add);
        }
        newUser.setRoles(assignedRoles);

        newUser = userRepository.save(newUser);

        Profile newUserInfo = new Profile(
                newUser,
                profileInputDto.firstName,
                profileInputDto.lastName,
                profileInputDto.street,
                profileInputDto.houseNumber,
                profileInputDto.postalCode,
                profileInputDto.city,
                profileInputDto.phone,
                profileInputDto.email
        );

        Profile savedUserInfo = profileRepository.save(newUserInfo);

        return ProfileMapper.toResponseDto(savedUserInfo);
    }

    public ProfileDto getProfileById(Long userId, UserDetails userDetails){
        boolean hasPrivilegedRole = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(auth -> auth.equals("ROLE_ADMIN") || auth.equals("ROLE_LIBRARIAN"));

        Profile profile = profileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));

        User profileUser = profile.getUser();

        if (hasPrivilegedRole || userDetails.getUsername().equals(profileUser.getUsername())) {
            return ProfileMapper.toResponseDto(profile);
        } else {
            throw new AccessDeniedException("You do not have permission to view this profile.");
        }
    }

    public List<ProfileDto> getAllProfiles() {
        List<Profile> profiles = profileRepository.findAll();
        return ProfileMapper.toResponseDtoList(profiles);
    }

    public ProfileDto getProfileByUsername(String username, UserDetails userDetails) {
        boolean hasAdminOrLibrarianRole = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(auth -> auth.equals("ROLE_ADMIN") || auth.equals("ROLE_LIBRARIAN"));

        if (userDetails.getUsername().equals(username) || hasAdminOrLibrarianRole) {
            Profile profile = profileRepository.findByUser_UsernameIgnoreCase(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Profile not found for username: " + username));

            return ProfileMapper.toResponseDto(profile);
        } else {
            throw new AccessDeniedException("You do not have permission to view this profile.");
        }
    }

    public Long getUserIdByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return user.getUserId();
    }

    public ProfileDto getMemberById(Long userId) {
        Profile profile = profileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id " + userId));

        if (!profile.getUser().getRoles().stream().anyMatch(role -> role.getRolename().equals("ROLE_MEMBER"))) {
            throw new ResourceNotFoundException("User with id " + userId + " is not a member.");
        }
        return ProfileMapper.toResponseDto(profile);
    }

    public ProfileDto getLibrarianById(Long userId) {
        Profile profile = profileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Librarian not found with id " + userId));

        if (!profile.getUser().getRoles().stream().anyMatch(role -> role.getRolename().equals("ROLE_LIBRARIAN"))) {
            throw new ResourceNotFoundException("User with id " + userId + " is not a librarian.");
        }
        return ProfileMapper.toResponseDto(profile);
    }

    public List<ProfileDto> getAllMembers() {
        List<Profile> members = profileRepository.findAll().stream()
                .filter(ui -> ui.getUser().getRoles().stream().anyMatch(role -> role.getRolename().equals("ROLE_MEMBER")))
                .collect(Collectors.toList());
        return ProfileMapper.toResponseDtoList(members);
    }

    public List<ProfileDto> getAllLibrarians() {
        List<Profile> librarians = profileRepository.findAll().stream()
                .filter(ui -> ui.getUser().getRoles().stream().anyMatch(role -> role.getRolename().equals("ROLE_LIBRARIAN")))
                .collect(Collectors.toList());
        return ProfileMapper.toResponseDtoList(librarians);
    }

    public ProfileDto updateProfile(Long userId, ProfileInputDto profileInputDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        Profile existingUserInfo = user.getProfile();

        if (existingUserInfo == null) {
            throw new ResourceNotFoundException("User information not found for User with ID: " + userId);
        }

        if (profileInputDto.getFirstName() != null) {
            existingUserInfo.setFirstName(profileInputDto.getFirstName());
        }
        existingUserInfo.setLastName(profileInputDto.lastName);
        existingUserInfo.setStreet(profileInputDto.street);
        existingUserInfo.setHouseNumber(profileInputDto.houseNumber);
        existingUserInfo.setPostalCode(profileInputDto.postalCode);
        existingUserInfo.setCity(profileInputDto.city);

        if (profileInputDto.email != null) {
            existingUserInfo.setEmail(profileInputDto.email);
        }

        if (profileInputDto.phone != null) {
            existingUserInfo.setPhone(profileInputDto.phone);
        }

        if (profileInputDto.username != null && !profileInputDto.username.equals(user.getUsername())) {
            if (userRepository.findByUsername(profileInputDto.username).isPresent() &&
                    !userRepository.findByUsername(profileInputDto.username).get().getUserId().equals(user.getUserId())) {
                throw new UsernameAlreadyExistsException("Username " + profileInputDto.username + " already exists.");
            }
            user.setUsername(profileInputDto.getUsername());
        }

        if (profileInputDto.password != null && !profileInputDto.password.isEmpty()) {
            user.setPassword(passwordEncoder.encode(profileInputDto.password));
        }

        if (profileInputDto.roles != null) {
            Set<Role> newRoles = new HashSet<>();
            for (String roleName : profileInputDto.roles) {
                roleRepository.findById("ROLE_" + roleName.toUpperCase()).ifPresent(newRoles::add);
            }
            user.setRoles(newRoles);
        }

        Profile updatedUserInfo = profileRepository.save(existingUserInfo);
        userRepository.save(user);

        return ProfileMapper.toResponseDto(updatedUserInfo);
    }

    public ProfileDto patchProfile(ProfilePatchDto profilePatchDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        Profile existingProfile = user.getProfile();

        if (existingProfile == null) {
            throw new ResourceNotFoundException("User Information not found for User with ID: " + userId);
        }

        if (profilePatchDto.getFirstName() != null) {
            existingProfile.setFirstName(profilePatchDto.getFirstName());
        }
        if (profilePatchDto.getLastName() != null) {
            existingProfile.setLastName(profilePatchDto.getLastName());
        }
        if (profilePatchDto.getStreet() != null) {
            existingProfile.setStreet(profilePatchDto.getStreet());
        }
        if (profilePatchDto.getHouseNumber() != null) {
            existingProfile.setHouseNumber(profilePatchDto.getHouseNumber());
        }
        if (profilePatchDto.getPostalCode() != null) {
            existingProfile.setPostalCode(profilePatchDto.getPostalCode());
        }
        if (profilePatchDto.getCity() != null) {
            existingProfile.setCity(profilePatchDto.getCity());
        }
        if (profilePatchDto.getPhone() != null) {
            existingProfile.setPhone(profilePatchDto.getPhone());
        }
        if (profilePatchDto.getEmail() != null) {
            existingProfile.setEmail(profilePatchDto.getEmail());
        }

        if (profilePatchDto.getUsername() != null && !profilePatchDto.getUsername().equals(user.getUsername())) {
            if (userRepository.findByUsername(profilePatchDto.getUsername()).isPresent() &&
                    !userRepository.findByUsername(profilePatchDto.getUsername()).get().getUserId().equals(user.getUserId())) {
                throw new UsernameAlreadyExistsException("Username " + profilePatchDto.getUsername() + " already exists.");
            }
            user.setUsername(profilePatchDto.getUsername());
        }

        if (profilePatchDto.getPassword() != null && !profilePatchDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(profilePatchDto.getPassword()));
        }

        if (profilePatchDto.getRoles() != null) {
            Set<Role> newRoles = new HashSet<>();
            for (String roleName : profilePatchDto.getRoles()) {
                roleRepository.findById("ROLE_" + roleName.toUpperCase()).ifPresent(newRoles::add);
            }
            user.setRoles(newRoles);
        }

        Profile patchedProfile = this.profileRepository.save(existingProfile);
        userRepository.save(user);

        return ProfileMapper.toResponseDto(patchedProfile);
    }

    public ProfileDto uploadProfilePhoto(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        Profile existingProfile = user.getProfile();
        String newUrl = storageService.store(file);
        existingProfile.setProfilePictureFile(newUrl);

        Profile updatedProfile = this.profileRepository.save(existingProfile);

        return ProfileMapper.toResponseDto(updatedProfile);
    }

    public void deleteProfile(Long userId) {
        User userToDelete = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        userRepository.delete(userToDelete);
    }
}
