package org.example.librarymanager.mappers;


import org.example.librarymanager.dtos.ProfileDto;
import org.example.librarymanager.dtos.ProfileInputDto;
import org.example.librarymanager.models.Role;
import org.example.librarymanager.models.User;
import org.example.librarymanager.models.Profile;
import org.example.librarymanager.repositories.RoleRepository;
import org.springframework.stereotype.Component;


import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ProfileMapper {

    private final RoleRepository roleRepository;

    public ProfileMapper(RoleRepository roleRepository){
        this.roleRepository = roleRepository;
    }

    public Profile toEntity(ProfileInputDto profileInputDto) {
        User user = new User();
        user.setUsername(profileInputDto.username);

        if (profileInputDto.roles != null && !profileInputDto.roles.isEmpty()) {
            Set<Role> roles = profileInputDto.roles.stream()
                    .map(roleName -> roleRepository.findById("ROLE_" + roleName.toUpperCase()))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        } else {
            roleRepository.findById("ROLE_MEMBER").ifPresent(role -> {
                Set<Role> defaultRoles = new HashSet<>();
                defaultRoles.add(role);
                user.setRoles(defaultRoles);
            });
        }

        Profile profile = new Profile();
        profile.setUser(user);

        profile.setFirstName(profileInputDto.firstName);
        profile.setLastName(profileInputDto.lastName);
        profile.setEmail(profileInputDto.email);
        profile.setPhone(profileInputDto.phone);
        profile.setStreet(profileInputDto.street);
        profile.setHouseNumber(profileInputDto.houseNumber);
        profile.setPostalCode(profileInputDto.postalCode);
        profile.setCity(profileInputDto.city);
        profile.setProfilePictureUrl(profileInputDto.profilePictureUrl);

        user.setProfile(profile);

        return profile;
    }

    public static ProfileDto toResponseDto(Profile profile) {
        ProfileDto profileDto = new ProfileDto();
        profileDto.profileId = profile.getId();
        profileDto.firstName = profile.getFirstName();
        profileDto.lastName = profile.getLastName();
        profileDto.email = profile.getEmail();
        profileDto.phone = profile.getPhone();
        profileDto.street = profile.getStreet();
        profileDto.houseNumber = profile.getHouseNumber();
        profileDto.postalCode = profile.getPostalCode();
        profileDto.city = profile.getCity();
        profileDto.profilePictureUrl = profile.getProfilePictureUrl();

        if (profile.getUser() != null) {
            profileDto.userId = profile.getUser().getUserId();
            profileDto.username = profile.getUser().getUsername();
        }

        if (profile.getUser().getRoles() != null && !profile.getUser().getRoles().isEmpty()) {
            profileDto.roles = profile.getUser().getRoles().stream()
                    .map(Role::getRolename)
                    .collect(Collectors.toList());
        }
        return profileDto;
    }

    public static List<ProfileDto> toResponseDtoList(List<Profile> profiles) {
        return profiles.stream()
                .map(ProfileMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}