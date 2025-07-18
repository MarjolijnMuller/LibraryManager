package org.example.librarymanager.mappers;


import org.example.librarymanager.dtos.UserInformationDto;
import org.example.librarymanager.dtos.UserInformationInputDto;
import org.example.librarymanager.models.Role;
import org.example.librarymanager.models.User;
import org.example.librarymanager.models.UserInformation;
import org.example.librarymanager.repositories.RoleRepository;
import org.springframework.stereotype.Component;


import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserInformationMapper {

    private final RoleRepository roleRepository;

    public UserInformationMapper(RoleRepository roleRepository){
        this.roleRepository = roleRepository;
    }

    public UserInformation toEntity(UserInformationInputDto userInformationInputDto) {
        User user = new User();
        user.setUsername(userInformationInputDto.username);

        if (userInformationInputDto.roles != null && !userInformationInputDto.roles.isEmpty()) {
            Set<Role> roles = userInformationInputDto.roles.stream()
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

        UserInformation userInformation = new UserInformation();
        userInformation.setUser(user);

        userInformation.setFirstName(userInformationInputDto.firstName);
        userInformation.setLastName(userInformationInputDto.lastName);
        userInformation.setEmail(userInformationInputDto.email);
        userInformation.setPhone(userInformationInputDto.phone);
        userInformation.setStreet(userInformationInputDto.street);
        userInformation.setHouseNumber(userInformationInputDto.houseNumber);
        userInformation.setPostalCode(userInformationInputDto.postalCode);
        userInformation.setCity(userInformationInputDto.city);
        userInformation.setProfilePictureUrl(userInformationInputDto.profilePictureUrl);

        user.setUserInformation(userInformation);

        return userInformation;
    }

    public static UserInformationDto toResponseDto(UserInformation userInformation) {
        UserInformationDto userInformationDto = new UserInformationDto();
        userInformationDto.userInformationId = userInformation.getId();
        userInformationDto.firstName = userInformation.getFirstName();
        userInformationDto.lastName = userInformation.getLastName();
        userInformationDto.email = userInformation.getEmail();
        userInformationDto.phone = userInformation.getPhone();
        userInformationDto.street = userInformation.getStreet();
        userInformationDto.houseNumber = userInformation.getHouseNumber();
        userInformationDto.postalCode = userInformation.getPostalCode();
        userInformationDto.city = userInformation.getCity();
        userInformationDto.profilePictureUrl = userInformation.getProfilePictureUrl();

        if (userInformation.getUser() != null) {
            userInformationDto.userId = userInformation.getUser().getUserId();
            userInformationDto.username = userInformation.getUser().getUsername();
        }

        if (userInformation.getUser().getRoles() != null && !userInformation.getUser().getRoles().isEmpty()) {
            userInformationDto.roles = userInformation.getUser().getRoles().stream()
                    .map(Role::getRolename)
                    .collect(Collectors.toList());
        }
        return userInformationDto;
    }

    public static List<UserInformationDto> toResponseDtoList(List<UserInformation> userInformations) {
        return userInformations.stream()
                .map(UserInformationMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}