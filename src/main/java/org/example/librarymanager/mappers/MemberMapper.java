package org.example.librarymanager.mappers;


import org.example.librarymanager.dtos.MemberDto;
import org.example.librarymanager.dtos.MemberInputDto;
import org.example.librarymanager.dtos.UserInputDto;
import org.example.librarymanager.models.Member;


import java.util.List;
import java.util.stream.Collectors;

public class MemberMapper {
        public static Member toEntity(MemberInputDto memberInputDto, UserInputDto userInputDto) {
            Member member = new Member();
            member.setFirstName(memberInputDto.firstName);
            member.setLastName(memberInputDto.lastName);
            member.setUsername(memberInputDto.username);
            member.setPassword(memberInputDto.password);
            member.setProfilePictureUrl(memberInputDto.profilePictureUrl);
            return member;
        }

        public static MemberDto toResponseDto(Member member){
            MemberDto memberDto = new MemberDto();
            memberDto.memberId = member.getMemberId();
            memberDto.firstName = member.getFirstName();
            memberDto.lastName = member.getLastName();
            memberDto.username = member.getUsername();
            memberDto.password = member.getPassword();
            memberDto.profilePictureUrl = member.getProfilePictureUrl();
            return memberDto;
        }

        public static List<MemberDto> toResponseDtoList(List<Member> members){
            return members.stream()
                    .map(org.example.librarymanager.mappers.MemberMapper::toResponseDto)
                    .collect(Collectors.toList());
        }
}
