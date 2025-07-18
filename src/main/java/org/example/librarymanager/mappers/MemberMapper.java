package org.example.librarymanager.mappers;


import org.example.librarymanager.dtos.MemberDto;
import org.example.librarymanager.dtos.MemberInputDto;
import org.example.librarymanager.dtos.UserInputDto;
import org.example.librarymanager.models.UserInformation;


import java.util.List;
import java.util.stream.Collectors;

public class MemberMapper {
        public static UserInformation toEntity(MemberInputDto memberInputDto, UserInputDto userInputDto) {
            UserInformation member = new UserInformation();
            member.setFirstName(memberInputDto.firstName);
            member.setLastName(memberInputDto.lastName);
            member.setUsername(memberInputDto.username);
            member.setPassword(memberInputDto.password);
            member.setProfilePictureUrl(memberInputDto.profilePictureUrl);
            member.setEmail(memberInputDto.email);
            member.setPhone(memberInputDto.phone);
            member.setCity(memberInputDto.city);
            member.setPostalCode(memberInputDto.postalCode);
            member.setStreet(memberInputDto.street);
            member.setHouseNumber(memberInputDto.houseNumber);
            return member;
        }

        public static MemberDto toResponseDto(UserInformation member){
            MemberDto memberDto = new MemberDto();
            memberDto.userId = member.getUserId();
            memberDto.firstName = member.getFirstName();
            memberDto.lastName = member.getLastName();
            memberDto.username = member.getUsername();
            memberDto.password = member.getPassword();
            memberDto.profilePictureUrl = member.getProfilePictureUrl();
            memberDto.email = member.getEmail();
            memberDto.phone = member.getPhone();
            memberDto.city = member.getCity();
            memberDto.postalCode = member.getPostalCode();
            memberDto.street = member.getStreet();
            memberDto.houseNumber = member.getHouseNumber();
            return memberDto;
        }

        public static List<MemberDto> toResponseDtoList(List<UserInformation> members){
            return members.stream()
                    .map(org.example.librarymanager.mappers.MemberMapper::toResponseDto)
                    .collect(Collectors.toList());
        }
}
