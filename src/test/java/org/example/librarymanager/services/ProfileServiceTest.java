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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ProfileRepository profileRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ProfileService profileService;

    private User createUser(Long userId, String username, String password, Set<Role> roles) {
        User user = new User();
        user.setUserId(userId);
        user.setUsername(username);
        user.setPassword(password);
        user.setRoles(roles);
        return user;
    }

    private Profile createProfile(User user, String firstName) {
        Profile profile = new Profile();
        profile.setUser(user);
        profile.setFirstName(firstName);
        return profile;
    }

    private ProfileDto createProfileDto(Long userId, String username) {
        ProfileDto dto = new ProfileDto();
        dto.userId = userId;
        dto.username = username;
        return dto;
    }

    private Set<GrantedAuthority> createAuthorities(String... roles) {
        return java.util.Arrays.stream(roles)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    private UserDetails createUserDetails(String username, String... roles) {
        return new org.springframework.security.core.userdetails.User(
                username, "password", createAuthorities(roles)
        );
    }

    @Test
    void createProfile_UsernameAlreadyExists_ThrowsException() {
        try (MockedStatic<ProfileMapper> mockedStaticMapper = mockStatic(ProfileMapper.class)) {
            // Arrange
            ProfileInputDto inputDto = new ProfileInputDto();
            inputDto.username = "testuser";
            when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(new User()));

            // Act & Assert
            assertThrows(UsernameAlreadyExistsException.class, () -> profileService.createProfile(inputDto));
            verify(userRepository, times(1)).findByUsername("testuser");
            verify(userRepository, never()).save(any(User.class));
            mockedStaticMapper.verifyNoInteractions();
        }
    }

    @Test
    void createProfile_ValidInputWithRoles_CreatesProfile() {
        try (MockedStatic<ProfileMapper> mockedStaticMapper = mockStatic(ProfileMapper.class)) {
            // Arrange
            ProfileInputDto inputDto = new ProfileInputDto();
            inputDto.username = "testuser";
            inputDto.password = "password123";
            inputDto.roles = Collections.singleton("ADMIN");

            Role adminRole = new Role("ROLE_ADMIN");
            User newUser = createUser(1L, "testuser", "encodedPassword", Collections.singleton(adminRole));
            Profile newProfile = createProfile(newUser, "John");
            ProfileDto expectedDto = createProfileDto(1L, "testuser");

            when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(roleRepository.findById("ROLE_ADMIN")).thenReturn(Optional.of(adminRole));
            when(userRepository.save(any(User.class))).thenReturn(newUser);
            when(profileRepository.save(any(Profile.class))).thenReturn(newProfile);
            mockedStaticMapper.when(() -> ProfileMapper.toResponseDto(newProfile)).thenReturn(expectedDto);

            // Act
            ProfileDto result = profileService.createProfile(inputDto);

            // Assert
            assertNotNull(result);
            assertEquals(expectedDto, result);
            verify(userRepository).findByUsername("testuser");
            verify(userRepository).save(any(User.class));
            verify(profileRepository).save(any(Profile.class));
            verify(passwordEncoder).encode("password123");
            verify(roleRepository).findById("ROLE_ADMIN");
            mockedStaticMapper.verify(() -> ProfileMapper.toResponseDto(newProfile), times(1));
        }
    }

    @Test
    void createProfile_ValidInputWithoutRoles_AssignsMemberRole() {
        try (MockedStatic<ProfileMapper> mockedStaticMapper = mockStatic(ProfileMapper.class)) {
            // Arrange
            ProfileInputDto inputDto = new ProfileInputDto();
            inputDto.username = "testuser";
            inputDto.password = "password123";
            inputDto.roles = Collections.emptySet();

            Role memberRole = new Role("ROLE_MEMBER");
            User newUser = createUser(1L, "testuser", "encodedPassword", Collections.singleton(memberRole));
            Profile newProfile = createProfile(newUser, "John");
            ProfileDto expectedDto = createProfileDto(1L, "testuser");

            when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(roleRepository.findById("ROLE_MEMBER")).thenReturn(Optional.of(memberRole));
            when(userRepository.save(any(User.class))).thenReturn(newUser);
            when(profileRepository.save(any(Profile.class))).thenReturn(newProfile);
            mockedStaticMapper.when(() -> ProfileMapper.toResponseDto(newProfile)).thenReturn(expectedDto);

            // Act
            ProfileDto result = profileService.createProfile(inputDto);

            // Assert
            assertNotNull(result);
            assertEquals(expectedDto, result);
            verify(userRepository).findByUsername("testuser");
            verify(userRepository).save(any(User.class));
            verify(profileRepository).save(any(Profile.class));
            verify(roleRepository).findById("ROLE_MEMBER");
            mockedStaticMapper.verify(() -> ProfileMapper.toResponseDto(newProfile), times(1));
        }
    }

    @Test
    void createProfile_InvalidRole_ThrowsException() {
        try (MockedStatic<ProfileMapper> mockedStaticMapper = mockStatic(ProfileMapper.class)) {
            // Arrange
            ProfileInputDto inputDto = new ProfileInputDto();
            inputDto.username = "testuser";
            inputDto.roles = Collections.singleton("INVALID_ROLE");

            when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
            when(roleRepository.findById("ROLE_INVALID_ROLE")).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> profileService.createProfile(inputDto));
            verify(userRepository, never()).save(any(User.class));
            mockedStaticMapper.verifyNoInteractions();
        }
    }

    @Test
    void getProfileById_UserIsAdmin_ReturnsProfile() {
        try (MockedStatic<ProfileMapper> mockedStaticMapper = mockStatic(ProfileMapper.class)) {
            // Arrange
            UserDetails userDetails = createUserDetails("adminUser", "ROLE_ADMIN");
            Profile profile = createProfile(createUser(1L, "targetUser", "pass", null), "John");
            ProfileDto expectedDto = createProfileDto(1L, "targetUser");

            when(profileRepository.findByUser_UserId(1L)).thenReturn(Optional.of(profile));
            mockedStaticMapper.when(() -> ProfileMapper.toResponseDto(profile)).thenReturn(expectedDto);

            // Act
            ProfileDto result = profileService.getProfileById(1L, userDetails);

            // Assert
            assertNotNull(result);
            assertEquals(expectedDto, result);
            verify(profileRepository).findByUser_UserId(1L);
            mockedStaticMapper.verify(() -> ProfileMapper.toResponseDto(profile), times(1));
        }
    }

    @Test
    void getProfileById_UserIsLibrarian_ReturnsProfile() {
        try (MockedStatic<ProfileMapper> mockedStaticMapper = mockStatic(ProfileMapper.class)) {
            // Arrange
            UserDetails userDetails = createUserDetails("librarianUser", "ROLE_LIBRARIAN");
            Profile profile = createProfile(createUser(1L, "targetUser", "pass", null), "John");
            ProfileDto expectedDto = createProfileDto(1L, "targetUser");

            when(profileRepository.findByUser_UserId(1L)).thenReturn(Optional.of(profile));
            mockedStaticMapper.when(() -> ProfileMapper.toResponseDto(profile)).thenReturn(expectedDto);

            // Act
            ProfileDto result = profileService.getProfileById(1L, userDetails);

            // Assert
            assertNotNull(result);
            assertEquals(expectedDto, result);
            verify(profileRepository).findByUser_UserId(1L);
            mockedStaticMapper.verify(() -> ProfileMapper.toResponseDto(profile), times(1));
        }
    }

    @Test
    void getProfileById_UserMatchesProfile_ReturnsProfile() {
        try (MockedStatic<ProfileMapper> mockedStaticMapper = mockStatic(ProfileMapper.class)) {
            // Arrange
            UserDetails userDetails = createUserDetails("selfUser", "ROLE_MEMBER");
            Profile profile = createProfile(createUser(1L, "selfUser", "pass", null), "Self");
            ProfileDto expectedDto = createProfileDto(1L, "selfUser");

            when(profileRepository.findByUser_UserId(1L)).thenReturn(Optional.of(profile));
            mockedStaticMapper.when(() -> ProfileMapper.toResponseDto(profile)).thenReturn(expectedDto);

            // Act
            ProfileDto result = profileService.getProfileById(1L, userDetails);

            // Assert
            assertNotNull(result);
            assertEquals(expectedDto, result);
            verify(profileRepository).findByUser_UserId(1L);
            mockedStaticMapper.verify(() -> ProfileMapper.toResponseDto(profile), times(1));
        }
    }

    @Test
    void getProfileById_UserDoesNotMatchAndNoPrivileges_ThrowsException() {
        try (MockedStatic<ProfileMapper> mockedStaticMapper = mockStatic(ProfileMapper.class)) {
            // Arrange
            UserDetails userDetails = createUserDetails("otherUser", "ROLE_MEMBER");
            Profile profile = createProfile(createUser(1L, "targetUser", "pass", null), "Target");

            when(profileRepository.findByUser_UserId(1L)).thenReturn(Optional.of(profile));

            // Act & Assert
            assertThrows(AccessDeniedException.class, () -> profileService.getProfileById(1L, userDetails));
            verify(profileRepository).findByUser_UserId(1L);
            mockedStaticMapper.verifyNoInteractions();
        }
    }

    @Test
    void getProfileById_ProfileNotFound_ThrowsException() {
        try (MockedStatic<ProfileMapper> mockedStaticMapper = mockStatic(ProfileMapper.class)) {
            // Arrange
            UserDetails userDetails = createUserDetails("adminUser", "ROLE_ADMIN");
            when(profileRepository.findByUser_UserId(anyLong())).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> profileService.getProfileById(1L, userDetails));
            verify(profileRepository).findByUser_UserId(1L);
            mockedStaticMapper.verifyNoInteractions();
        }
    }

    @Test
    void getAllProfiles_ReturnsAllProfiles() {
        try (MockedStatic<ProfileMapper> mockedStaticMapper = mockStatic(ProfileMapper.class)) {
            // Arrange
            Profile profile1 = createProfile(createUser(1L, "user1", "pass", null), "User1");
            Profile profile2 = createProfile(createUser(2L, "user2", "pass", null), "User2");
            List<Profile> profiles = List.of(profile1, profile2);
            List<ProfileDto> expectedDtos = List.of(
                    createProfileDto(1L, "user1"),
                    createProfileDto(2L, "user2")
            );

            when(profileRepository.findAll()).thenReturn(profiles);
            mockedStaticMapper.when(() -> ProfileMapper.toResponseDtoList(profiles)).thenReturn(expectedDtos);

            // Act
            List<ProfileDto> result = profileService.getAllProfiles();

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            verify(profileRepository).findAll();
            mockedStaticMapper.verify(() -> ProfileMapper.toResponseDtoList(profiles), times(1));
        }
    }

    @Test
    void getProfileByUsername_UserIsAdmin_ReturnsProfile() {
        try (MockedStatic<ProfileMapper> mockedStaticMapper = mockStatic(ProfileMapper.class)) {
            // Arrange
            UserDetails userDetails = createUserDetails("adminUser", "ROLE_ADMIN");
            Profile profile = createProfile(createUser(1L, "targetUser", "pass", null), "Target");
            ProfileDto expectedDto = createProfileDto(1L, "targetUser");

            when(profileRepository.findByUser_UsernameIgnoreCase("targetUser")).thenReturn(Optional.of(profile));
            mockedStaticMapper.when(() -> ProfileMapper.toResponseDto(profile)).thenReturn(expectedDto);

            // Act
            ProfileDto result = profileService.getProfileByUsername("targetUser", userDetails);

            // Assert
            assertNotNull(result);
            assertEquals(expectedDto, result);
            verify(profileRepository).findByUser_UsernameIgnoreCase("targetUser");
            mockedStaticMapper.verify(() -> ProfileMapper.toResponseDto(profile), times(1));
        }
    }

    @Test
    void getProfileByUsername_UserMatchesUsername_ReturnsProfile() {
        try (MockedStatic<ProfileMapper> mockedStaticMapper = mockStatic(ProfileMapper.class)) {
            // Arrange
            UserDetails userDetails = createUserDetails("selfUser", "ROLE_MEMBER");
            Profile profile = createProfile(createUser(1L, "selfUser", "pass", null), "Self");
            ProfileDto expectedDto = createProfileDto(1L, "selfUser");

            when(profileRepository.findByUser_UsernameIgnoreCase("selfUser")).thenReturn(Optional.of(profile));
            mockedStaticMapper.when(() -> ProfileMapper.toResponseDto(profile)).thenReturn(expectedDto);

            // Act
            ProfileDto result = profileService.getProfileByUsername("selfUser", userDetails);

            // Assert
            assertNotNull(result);
            assertEquals(expectedDto, result);
            verify(profileRepository).findByUser_UsernameIgnoreCase("selfUser");
            mockedStaticMapper.verify(() -> ProfileMapper.toResponseDto(profile), times(1));
        }
    }

    @Test
    void getProfileByUsername_UserDoesNotMatchAndNoPrivileges_ThrowsException() {
        try (MockedStatic<ProfileMapper> mockedStaticMapper = mockStatic(ProfileMapper.class)) {
            // Arrange
            UserDetails userDetails = createUserDetails("otherUser", "ROLE_MEMBER");

            // Act & Assert
            assertThrows(AccessDeniedException.class, () -> profileService.getProfileByUsername("targetUser", userDetails));
            verify(profileRepository, never()).findByUser_UsernameIgnoreCase(anyString());
            mockedStaticMapper.verifyNoInteractions();
        }
    }

    @Test
    void getProfileByUsername_ProfileNotFound_ThrowsException() {
        try (MockedStatic<ProfileMapper> mockedStaticMapper = mockStatic(ProfileMapper.class)) {
            // Arrange
            UserDetails userDetails = createUserDetails("adminUser", "ROLE_ADMIN");
            when(profileRepository.findByUser_UsernameIgnoreCase(anyString())).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> profileService.getProfileByUsername("unknownUser", userDetails));
            verify(profileRepository).findByUser_UsernameIgnoreCase("unknownUser");
            mockedStaticMapper.verifyNoInteractions();
        }
    }

    @Test
    void getUserIdByUsername_UserFound_ReturnsUserId() {
        try (MockedStatic<ProfileMapper> mockedStaticMapper = mockStatic(ProfileMapper.class)) {
            // Arrange
            User user = createUser(1L, "testuser", "pass", null);
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

            // Act
            Long result = profileService.getUserIdByUsername("testuser");

            // Assert
            assertEquals(1L, result);
            verify(userRepository).findByUsername("testuser");
            mockedStaticMapper.verifyNoInteractions();
        }
    }

    @Test
    void getUserIdByUsername_UserNotFound_ThrowsException() {
        try (MockedStatic<ProfileMapper> mockedStaticMapper = mockStatic(ProfileMapper.class)) {
            // Arrange
            when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> profileService.getUserIdByUsername("unknownUser"));
            verify(userRepository).findByUsername("unknownUser");
            mockedStaticMapper.verifyNoInteractions();
        }
    }

    @Test
    void getMemberById_UserIsMember_ReturnsProfile() {
        try (MockedStatic<ProfileMapper> mockedStaticMapper = mockStatic(ProfileMapper.class)) {
            // Arrange
            Role memberRole = new Role("ROLE_MEMBER");
            User user = createUser(1L, "testuser", "pass", Collections.singleton(memberRole));
            Profile profile = createProfile(user, "John");
            ProfileDto expectedDto = createProfileDto(1L, "testuser");

            when(profileRepository.findByUser_UserId(1L)).thenReturn(Optional.of(profile));
            mockedStaticMapper.when(() -> ProfileMapper.toResponseDto(profile)).thenReturn(expectedDto);

            // Act
            ProfileDto result = profileService.getMemberById(1L);

            // Assert
            assertNotNull(result);
            assertEquals(expectedDto.userId, result.userId);
            verify(profileRepository).findByUser_UserId(1L);
            mockedStaticMapper.verify(() -> ProfileMapper.toResponseDto(profile), times(1));
        }
    }

    @Test
    void getMemberById_UserIsNotMember_ThrowsException() {
        try (MockedStatic<ProfileMapper> mockedStaticMapper = mockStatic(ProfileMapper.class)) {
            // Arrange
            Role librarianRole = new Role("ROLE_LIBRARIAN");
            User user = createUser(1L, "testuser", "pass", Collections.singleton(librarianRole));
            Profile profile = createProfile(user, "John");

            when(profileRepository.findByUser_UserId(1L)).thenReturn(Optional.of(profile));

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> profileService.getMemberById(1L));
            verify(profileRepository).findByUser_UserId(1L);
            mockedStaticMapper.verifyNoInteractions();
        }
    }

    @Test
    void getMemberById_ProfileNotFound_ThrowsException() {
        try (MockedStatic<ProfileMapper> mockedStaticMapper = mockStatic(ProfileMapper.class)) {
            // Arrange
            when(profileRepository.findByUser_UserId(anyLong())).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> profileService.getMemberById(1L));
            verify(profileRepository).findByUser_UserId(1L);
            mockedStaticMapper.verifyNoInteractions();
        }
    }

    @Test
    void getLibrarianById_UserIsLibrarian_ReturnsProfile() {
        try (MockedStatic<ProfileMapper> mockedStaticMapper = mockStatic(ProfileMapper.class)) {
            // Arrange
            Role librarianRole = new Role("ROLE_LIBRARIAN");
            User user = createUser(1L, "testuser", "pass", Collections.singleton(librarianRole));
            Profile profile = createProfile(user, "John");
            ProfileDto expectedDto = createProfileDto(1L, "testuser");

            when(profileRepository.findByUser_UserId(1L)).thenReturn(Optional.of(profile));
            mockedStaticMapper.when(() -> ProfileMapper.toResponseDto(profile)).thenReturn(expectedDto);

            // Act
            ProfileDto result = profileService.getLibrarianById(1L);

            // Assert
            assertNotNull(result);
            assertEquals(expectedDto.userId, result.userId);
            verify(profileRepository).findByUser_UserId(1L);
            mockedStaticMapper.verify(() -> ProfileMapper.toResponseDto(profile), times(1));
        }
    }

    @Test
    void getLibrarianById_UserIsNotLibrarian_ThrowsException() {
        try (MockedStatic<ProfileMapper> mockedStaticMapper = mockStatic(ProfileMapper.class)) {
            // Arrange
            Role memberRole = new Role("ROLE_MEMBER");
            User user = createUser(1L, "testuser", "pass", Collections.singleton(memberRole));
            Profile profile = createProfile(user, "John");

            when(profileRepository.findByUser_UserId(1L)).thenReturn(Optional.of(profile));

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> profileService.getLibrarianById(1L));
            verify(profileRepository).findByUser_UserId(1L);
            mockedStaticMapper.verifyNoInteractions();
        }
    }

    @Test
    void getLibrarianById_ProfileNotFound_ThrowsException() {
        try (MockedStatic<ProfileMapper> mockedStaticMapper = mockStatic(ProfileMapper.class)) {
            // Arrange
            when(profileRepository.findByUser_UserId(anyLong())).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> profileService.getLibrarianById(1L));
            verify(profileRepository).findByUser_UserId(1L);
            mockedStaticMapper.verifyNoInteractions();
        }
    }

    @Test
    void getAllMembers_ReturnsOnlyMembers() {
        try (MockedStatic<ProfileMapper> mockedStaticMapper = mockStatic(ProfileMapper.class)) {
            // Arrange
            Role memberRole = new Role("ROLE_MEMBER");
            Role librarianRole = new Role("ROLE_LIBRARIAN");
            User memberUser = createUser(1L, "member1", "pass", Collections.singleton(memberRole));
            User librarianUser = createUser(2L, "librarian1", "pass", Collections.singleton(librarianRole));
            Profile memberProfile = createProfile(memberUser, "Member1");
            Profile librarianProfile = createProfile(librarianUser, "Librarian1");
            List<Profile> allProfiles = List.of(memberProfile, librarianProfile);
            List<ProfileDto> expectedDtos = List.of(createProfileDto(1L, "member1"));

            when(profileRepository.findAll()).thenReturn(allProfiles);
            mockedStaticMapper.when(() -> ProfileMapper.toResponseDtoList(List.of(memberProfile))).thenReturn(expectedDtos);

            // Act
            List<ProfileDto> result = profileService.getAllMembers();

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("member1", result.get(0).username);
            verify(profileRepository).findAll();
            mockedStaticMapper.verify(() -> ProfileMapper.toResponseDtoList(List.of(memberProfile)), times(1));
        }
    }

    @Test
    void getAllLibrarians_ReturnsOnlyLibrarians() {
        try (MockedStatic<ProfileMapper> mockedStaticMapper = mockStatic(ProfileMapper.class)) {
            // Arrange
            Role memberRole = new Role("ROLE_MEMBER");
            Role librarianRole = new Role("ROLE_LIBRARIAN");
            User memberUser = createUser(1L, "member1", "pass", Collections.singleton(memberRole));
            User librarianUser = createUser(2L, "librarian1", "pass", Collections.singleton(librarianRole));
            Profile memberProfile = createProfile(memberUser, "Member1");
            Profile librarianProfile = createProfile(librarianUser, "Librarian1");
            List<Profile> allProfiles = List.of(memberProfile, librarianProfile);
            List<ProfileDto> expectedDtos = List.of(createProfileDto(2L, "librarian1"));

            when(profileRepository.findAll()).thenReturn(allProfiles);
            mockedStaticMapper.when(() -> ProfileMapper.toResponseDtoList(List.of(librarianProfile))).thenReturn(expectedDtos);

            // Act
            List<ProfileDto> result = profileService.getAllLibrarians();

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("librarian1", result.get(0).username);
            verify(profileRepository).findAll();
            mockedStaticMapper.verify(() -> ProfileMapper.toResponseDtoList(List.of(librarianProfile)), times(1));
        }
    }

    @Test
    void updateProfile_AllFieldsUpdated_UpdatesProfileAndUser() {
        try (MockedStatic<ProfileMapper> mockedStaticMapper = mockStatic(ProfileMapper.class)) {
            // Arrange
            User existingUser = createUser(1L, "oldUsername", "oldPass", null);
            Profile existingProfile = createProfile(existingUser, "OldName");
            existingProfile.setEmail("old.email@test.com");
            existingProfile.setProfilePictureUrl("old_url");
            existingProfile.setPhone("12345");
            existingUser.setProfile(existingProfile);

            ProfileInputDto inputDto = new ProfileInputDto();
            inputDto.username = "newUsername";
            inputDto.password = "newPass";
            inputDto.firstName = "NewName";
            inputDto.email = "new.email@test.com";
            inputDto.profilePictureUrl = "new_url";
            inputDto.phone = "98765";
            inputDto.roles = Collections.singleton("ADMIN");

            Role adminRole = new Role("ROLE_ADMIN");
            ProfileDto expectedDto = createProfileDto(1L, "newUsername");

            when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
            when(userRepository.findByUsername("newUsername")).thenReturn(Optional.empty());
            when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");
            when(roleRepository.findById("ROLE_ADMIN")).thenReturn(Optional.of(adminRole));
            when(profileRepository.save(any(Profile.class))).thenReturn(existingProfile);
            when(userRepository.save(any(User.class))).thenReturn(existingUser);
            mockedStaticMapper.when(() -> ProfileMapper.toResponseDto(existingProfile)).thenReturn(expectedDto);

            // Act
            ProfileDto result = profileService.updateProfile(1L, inputDto);

            // Assert
            assertEquals("newUsername", existingUser.getUsername());
            assertEquals("encodedNewPass", existingUser.getPassword());
            assertEquals("NewName", existingProfile.getFirstName());
            assertEquals("new.email@test.com", existingProfile.getEmail());
            assertEquals("new_url", existingProfile.getProfilePictureUrl());
            assertEquals("98765", existingProfile.getPhone());
            assertTrue(existingUser.getRoles().contains(adminRole));
            assertEquals(expectedDto, result);
            verify(userRepository).findById(1L);
            verify(userRepository).findByUsername("newUsername");
            verify(passwordEncoder).encode("newPass");
            verify(roleRepository).findById("ROLE_ADMIN");
            verify(profileRepository).save(existingProfile);
            verify(userRepository).save(existingUser);
            mockedStaticMapper.verify(() -> ProfileMapper.toResponseDto(existingProfile), times(1));
        }
    }

    @Test
    void updateProfile_SpecificFieldsUpdated_UpdatesProfile() {
        try (MockedStatic<ProfileMapper> mockedStaticMapper = mockStatic(ProfileMapper.class)) {
            // Arrange
            User existingUser = createUser(1L, "oldUsername", "oldPass", null);
            Profile existingProfile = createProfile(existingUser, "OldName");
            existingProfile.setProfilePictureUrl("old_url");
            existingUser.setProfile(existingProfile);

            ProfileInputDto inputDto = new ProfileInputDto();
            inputDto.email = "new.email@example.com";
            inputDto.phone = "1234567890";
            inputDto.profilePictureUrl = "new_url";

            ProfileDto expectedDto = createProfileDto(1L, "oldUsername");

            when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
            when(profileRepository.save(any(Profile.class))).thenReturn(existingProfile);
            when(userRepository.save(any(User.class))).thenReturn(existingUser);
            mockedStaticMapper.when(() -> ProfileMapper.toResponseDto(existingProfile)).thenReturn(expectedDto);

            // Act
            profileService.updateProfile(1L, inputDto);

            // Assert
            assertEquals("new.email@example.com", existingProfile.getEmail());
            assertEquals("1234567890", existingProfile.getPhone());
            assertEquals("new_url", existingProfile.getProfilePictureUrl());
            verify(profileRepository).save(existingProfile);
            verify(userRepository).save(existingUser);
        }
    }

    @Test
    void updateProfile_UsernameAlreadyExists_ThrowsException() {
        try (MockedStatic<ProfileMapper> mockedStaticMapper = mockStatic(ProfileMapper.class)) {
            // Arrange
            User existingUser = createUser(1L, "user1", "pass", null);
            Profile existingProfile = createProfile(existingUser, "John");
            existingUser.setProfile(existingProfile);
            User otherUser = createUser(2L, "otherUser", "pass", null);

            ProfileInputDto inputDto = new ProfileInputDto();
            inputDto.username = "otherUser";

            when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
            when(userRepository.findByUsername("otherUser")).thenReturn(Optional.of(otherUser));

            // Act & Assert
            assertThrows(UsernameAlreadyExistsException.class, () -> profileService.updateProfile(1L, inputDto));
            verify(userRepository).findById(1L);
            verify(userRepository, times(2)).findByUsername("otherUser");
            verify(userRepository, never()).save(any(User.class));
            mockedStaticMapper.verifyNoInteractions();
        }
    }

    @Test
    void updateProfile_UserNotFound_ThrowsException() {
        try (MockedStatic<ProfileMapper> mockedStaticMapper = mockStatic(ProfileMapper.class)) {
            // Arrange
            ProfileInputDto inputDto = new ProfileInputDto();
            when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> profileService.updateProfile(1L, inputDto));
            verify(userRepository).findById(1L);
            verify(userRepository, never()).save(any(User.class));
            mockedStaticMapper.verifyNoInteractions();
        }
    }

    @Test
    void updateProfile_UserInfoNotFound_ThrowsException() {
        // Arrange
        User userWithoutProfile = new User();
        userWithoutProfile.setUserId(1L);
        userWithoutProfile.setUsername("testuser");

        ProfileInputDto inputDto = new ProfileInputDto();

        when(userRepository.findById(1L)).thenReturn(Optional.of(userWithoutProfile));

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> profileService.updateProfile(1L, inputDto));
        verify(userRepository).findById(1L);
        verify(profileRepository, never()).save(any(Profile.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void patchProfile_AllFieldsUpdated_UpdatesProfileAndUser() {
        try (MockedStatic<ProfileMapper> mockedStaticMapper = mockStatic(ProfileMapper.class)) {
            // Arrange
            User existingUser = createUser(1L, "oldUsername", "oldPass", null);
            Profile existingProfile = createProfile(existingUser, "OldName");
            existingProfile.setLastName("OldLast");
            existingProfile.setStreet("OldStreet");
            existingProfile.setHouseNumber("1A");
            existingProfile.setPostalCode("1234AB");
            existingProfile.setCity("OldCity");
            existingProfile.setPhone("12345");
            existingProfile.setEmail("old@test.com");
            existingProfile.setProfilePictureUrl("old_url");
            existingUser.setProfile(existingProfile);

            ProfilePatchDto patchDto = new ProfilePatchDto();
            patchDto.setFirstName("NewFirst");
            patchDto.setLastName("NewLast");
            patchDto.setStreet("NewStreet");
            patchDto.setHouseNumber("2B");
            patchDto.setPostalCode("5678CD");
            patchDto.setCity("NewCity");
            patchDto.setPhone("98765");
            patchDto.setEmail("new@test.com");
            patchDto.setProfilePictureUrl("new_url");
            patchDto.setUsername("newUsername");
            patchDto.setPassword("newPass");
            patchDto.setRoles(Collections.singletonList("ADMIN"));

            Role adminRole = new Role("ROLE_ADMIN");
            ProfileDto expectedDto = createProfileDto(1L, "newUsername");

            when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
            when(userRepository.findByUsername("newUsername")).thenReturn(Optional.empty());
            when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");
            when(roleRepository.findById("ROLE_ADMIN")).thenReturn(Optional.of(adminRole));
            when(profileRepository.save(any(Profile.class))).thenReturn(existingProfile);
            when(userRepository.save(any(User.class))).thenReturn(existingUser);
            mockedStaticMapper.when(() -> ProfileMapper.toResponseDto(existingProfile)).thenReturn(expectedDto);

            // Act
            ProfileDto result = profileService.patchProfile(patchDto, 1L);

            // Assert
            assertEquals("NewFirst", existingProfile.getFirstName());
            assertEquals("NewLast", existingProfile.getLastName());
            assertEquals("NewStreet", existingProfile.getStreet());
            assertEquals("2B", existingProfile.getHouseNumber());
            assertEquals("5678CD", existingProfile.getPostalCode());
            assertEquals("NewCity", existingProfile.getCity());
            assertEquals("98765", existingProfile.getPhone());
            assertEquals("new@test.com", existingProfile.getEmail());
            assertEquals("new_url", existingProfile.getProfilePictureUrl());
            assertEquals("newUsername", existingUser.getUsername());
            assertEquals("encodedNewPass", existingUser.getPassword());
            assertTrue(existingUser.getRoles().contains(adminRole));
            assertEquals(expectedDto, result);
            verify(userRepository).findById(1L);
            verify(userRepository).findByUsername("newUsername");
            verify(passwordEncoder).encode("newPass");
            verify(profileRepository).save(existingProfile);
            verify(userRepository).save(existingUser);
            mockedStaticMapper.verify(() -> ProfileMapper.toResponseDto(existingProfile), times(1));
        }
    }

    @Test
    void patchProfile_PartialFieldsUpdated_UpdatesOnlyProvidedFields() {
        try (MockedStatic<ProfileMapper> mockedStaticMapper = mockStatic(ProfileMapper.class)) {
            // Arrange
            User existingUser = createUser(1L, "oldUser", "oldPass", null);
            Profile existingProfile = createProfile(existingUser, "OldFirst");
            existingProfile.setLastName("OldLast");
            existingProfile.setEmail("old@email.com");
            existingUser.setProfile(existingProfile);

            ProfilePatchDto patchDto = new ProfilePatchDto();
            patchDto.setLastName("NewLast");
            patchDto.setEmail("new@email.com");

            ProfileDto expectedDto = createProfileDto(1L, "oldUser");

            when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
            when(profileRepository.save(any(Profile.class))).thenReturn(existingProfile);
            when(userRepository.save(any(User.class))).thenReturn(existingUser);
            mockedStaticMapper.when(() -> ProfileMapper.toResponseDto(existingProfile)).thenReturn(expectedDto);

            // Act
            profileService.patchProfile(patchDto, 1L);

            // Assert
            assertEquals("OldFirst", existingProfile.getFirstName());
            assertEquals("NewLast", existingProfile.getLastName());
            assertEquals("new@email.com", existingProfile.getEmail());
            verify(profileRepository).save(existingProfile);
            verify(userRepository).save(existingUser);
        }
    }

    @Test
    void patchProfile_UsernameAlreadyExists_ThrowsException() {
        try (MockedStatic<ProfileMapper> mockedStaticMapper = mockStatic(ProfileMapper.class)) {
            // Arrange
            User existingUser = createUser(1L, "user1", "pass", null);
            Profile existingProfile = createProfile(existingUser, "John");
            existingUser.setProfile(existingProfile);
            User otherUser = createUser(2L, "otherUser", "pass", null);

            ProfilePatchDto patchDto = new ProfilePatchDto();
            patchDto.setUsername("otherUser");

            when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
            when(userRepository.findByUsername("otherUser")).thenReturn(Optional.of(otherUser));

            // Act & Assert
            assertThrows(UsernameAlreadyExistsException.class, () -> profileService.patchProfile(patchDto, 1L));
            verify(userRepository).findById(1L);
            verify(userRepository, times(2)).findByUsername("otherUser");
            verify(userRepository, never()).save(any(User.class));
            mockedStaticMapper.verifyNoInteractions();
        }
    }

    @Test
    void patchProfile_UserNotFound_ThrowsException() {
        try (MockedStatic<ProfileMapper> mockedStaticMapper = mockStatic(ProfileMapper.class)) {
            // Arrange
            ProfilePatchDto patchDto = new ProfilePatchDto();
            when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> profileService.patchProfile(patchDto, 1L));
            verify(userRepository).findById(1L);
            verify(userRepository, never()).save(any(User.class));
            mockedStaticMapper.verifyNoInteractions();
        }
    }

    @Test
    void patchProfile_UserInfoNotFound_ThrowsException() {
        try (MockedStatic<ProfileMapper> mockedStaticMapper = mockStatic(ProfileMapper.class)) {
            // Arrange
            User existingUser = new User();
            existingUser.setUserId(1L);
            existingUser.setUsername("test");

            ProfilePatchDto patchDto = new ProfilePatchDto();

            when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> profileService.patchProfile(patchDto, 1L));
            verify(userRepository).findById(1L);
            verify(profileRepository, never()).save(any(Profile.class));
            mockedStaticMapper.verifyNoInteractions();
        }
    }

    @Test
    void patchProfile_RolesUpdated_UpdatesUserRoles() {
        try (MockedStatic<ProfileMapper> mockedStaticMapper = mockStatic(ProfileMapper.class)) {
            // Arrange
            User existingUser = createUser(1L, "user1", "pass", new HashSet<>(Collections.singleton(new Role("ROLE_MEMBER"))));
            Profile existingProfile = createProfile(existingUser, "John");
            existingUser.setProfile(existingProfile);

            ProfilePatchDto patchDto = new ProfilePatchDto();
            patchDto.setRoles(Collections.singletonList("LIBRARIAN"));

            Role librarianRole = new Role("ROLE_LIBRARIAN");

            when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
            when(roleRepository.findById("ROLE_LIBRARIAN")).thenReturn(Optional.of(librarianRole));
            when(profileRepository.save(any(Profile.class))).thenReturn(existingProfile);
            when(userRepository.save(any(User.class))).thenReturn(existingUser);

            // Act
            profileService.patchProfile(patchDto, 1L);

            // Assert
            assertTrue(existingUser.getRoles().contains(librarianRole));
            assertFalse(existingUser.getRoles().stream().anyMatch(r -> r.getRolename().equals("ROLE_MEMBER")));
            verify(profileRepository).save(existingProfile);
            verify(userRepository).save(existingUser);
        }
    }

    @Test
    void deleteProfile_UserExists_DeletesUser() {
        try (MockedStatic<ProfileMapper> mockedStaticMapper = mockStatic(ProfileMapper.class)) {
            // Arrange
            User userToDelete = createUser(1L, "testuser", "pass", null);
            when(userRepository.findById(1L)).thenReturn(Optional.of(userToDelete));
            doNothing().when(userRepository).delete(userToDelete);

            // Act
            profileService.deleteProfile(1L);

            // Assert
            verify(userRepository).findById(1L);
            verify(userRepository).delete(userToDelete);
            mockedStaticMapper.verifyNoInteractions();
        }
    }

    @Test
    void deleteProfile_UserNotFound_ThrowsException() {
        try (MockedStatic<ProfileMapper> mockedStaticMapper = mockStatic(ProfileMapper.class)) {
            // Arrange
            when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> profileService.deleteProfile(1L));
            verify(userRepository).findById(1L);
            verify(userRepository, never()).delete(any(User.class));
            mockedStaticMapper.verifyNoInteractions();
        }
    }
}