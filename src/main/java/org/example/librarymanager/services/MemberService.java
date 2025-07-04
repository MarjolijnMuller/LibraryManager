package org.example.librarymanager.services;

import org.example.librarymanager.dtos.MemberInputDto;
import org.example.librarymanager.dtos.UserInputDto;
import org.example.librarymanager.exceptions.ResourceNotFountException;
import org.example.librarymanager.mappers.MemberMapper;
import org.example.librarymanager.models.Member;
import org.example.librarymanager.repositories.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member createMember(MemberInputDto memberInputDto, UserInputDto userInputDto) {
        return this.memberRepository.save(MemberMapper.toEntity(memberInputDto, userInputDto));
    }

    public List<Member> getAllMembers() {
        return this.memberRepository.findAll();
    }

    public Member getMemberByMemberId(Long memberId) {
        return this.memberRepository.findById(memberId).orElseThrow(() -> new ResourceNotFountException("Member not found with ID: " + memberId));
    }

    public List<Member> getMemberByFirstName(String firstName) {
        return this.memberRepository.findByMemberFirstName(firstName);
    }

    public List<Member> getMemberByLastName(String lastName) {
        return this.memberRepository.findByMemberLastName(lastName);
    }

    public Member getMemberByFirstNameAndLastName(String firstName, String lastName) {
        return this.memberRepository.findByMemberFirstNameAndMemberLastName(firstName, lastName);
    }

    public List<Member> getMemberByEmail(String email) {
        return this.memberRepository.findByMemberEmail(email);
    }

    public List<Member> getMemberByPostalCodeAndHouseNumber(String postalCode, String houseNumber) {
        return this.memberRepository.findMemberByPostalCodeAndHouseNumber(postalCode, houseNumber);
    }

    public Member getMemberByUsername(String username) {
        return this.memberRepository.findByUsername(username);
    }

    public Member updateMember(Long memberId, MemberInputDto memberInputDto) {
        Member existingMember = memberRepository.findById(memberId).orElseThrow(() -> new ResourceNotFountException("Member not found with ID: " + memberId));

        existingMember.setLastName(memberInputDto.lastName);
        existingMember.setStreet(memberInputDto.street);
        existingMember.setHouseNumber(memberInputDto.houseNumber);
        existingMember.setPostalCode(memberInputDto.postalCode);
        existingMember.setCity(memberInputDto.city);

        if (memberInputDto.phone != null) {
            existingMember.setPhone(memberInputDto.phone);
        }

        existingMember.setUsername(memberInputDto.username);

        //TODO: wachtwoord beveiligen
        existingMember.setPassword(memberInputDto.password);

        if (memberInputDto.email != null) {
            existingMember.setEmail(memberInputDto.email);
        }
        if (memberInputDto.profilePictureUrl != null) {
            existingMember.setProfilePictureUrl(memberInputDto.profilePictureUrl);
        }

        return this.memberRepository.save(existingMember);
    }

    public Member patchMember(MemberInputDto memberInputDto, Long memberId) {
        Member existingMember = memberRepository.findById(memberId).orElseThrow(() -> new ResourceNotFountException("Member not found with ID: " + memberId));

        if (memberInputDto.lastName != null) {
            existingMember.setLastName(memberInputDto.lastName);
        }
        if (memberInputDto.street != null) {
            existingMember.setStreet(memberInputDto.street);
        }
        if (memberInputDto.houseNumber != null) {
            existingMember.setHouseNumber(memberInputDto.houseNumber);
        }
        if (memberInputDto.postalCode != null) {
            existingMember.setPostalCode(memberInputDto.postalCode);
        }
        if (memberInputDto.city != null) {
            existingMember.setCity(memberInputDto.city);
        }
        if (memberInputDto.phone != null) {
            existingMember.setPhone(memberInputDto.phone);
        }
        if (memberInputDto.username != null) {
            existingMember.setUsername(memberInputDto.username);
        }
        //TODO: wachtwoord beveiligen
        if (memberInputDto.password != null) {
            existingMember.setPassword(memberInputDto.password);
        }
        if (memberInputDto.email != null) {
            existingMember.setEmail(memberInputDto.email);
        }
        if (memberInputDto.profilePictureUrl != null) {
            existingMember.setProfilePictureUrl(memberInputDto.profilePictureUrl);
        }
        return this.memberRepository.save(existingMember);
    }

    public void deleteMember(Long memberId) {
        this.memberRepository.deleteById(memberId);
    }
}
