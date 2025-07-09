package org.example.librarymanager.services;

import org.example.librarymanager.dtos.MemberInputDto;
import org.example.librarymanager.dtos.UserInputDto;
import org.example.librarymanager.exceptions.ResourceNotFoundException;
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

    public Member getMemberById(Long userId) {
        return this.memberRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + userId));
    }

    public List<Member> getMemberByFirstName(String firstName) {
        return this.memberRepository.findByFirstName(firstName);
    }

    public List<Member> getMemberByLastName(String lastName) {
        return this.memberRepository.findByLastName(lastName);
    }

    public List<Member> getMemberByFirstNameAndLastName(String firstName, String lastName) {
        return this.memberRepository.findByFirstNameAndLastName(firstName, lastName);
    }

    public List<Member> getMemberByEmail(String email) {
        return this.memberRepository.findByEmail(email);
    }

    public List<Member> getMemberByPostalCodeAndHouseNumber(String postalCode, String houseNumber) {
        return this.memberRepository.findByPostalCodeAndHouseNumber(postalCode, houseNumber);
    }

    public Member getMemberByUsername(String username) {
        return this.memberRepository.findByUsername(username);
    }

    public Member updateMember(Long userId, MemberInputDto memberInputDto) {
        Member existingMember = memberRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + userId));

        existingMember.setFirstName(memberInputDto.firstName);
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

    public Member patchMember(MemberInputDto memberInputDto, Long userId) {
        Member existingMember = memberRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + userId));

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

    public void deleteMember(Long userId) {
        this.memberRepository.deleteById(userId);
    }
}
