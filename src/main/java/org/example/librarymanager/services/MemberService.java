package org.example.librarymanager.services;

import org.example.librarymanager.dtos.MemberInputDto;
import org.example.librarymanager.dtos.UserInputDto;
import org.example.librarymanager.exceptions.ResourceNotFoundException;
import org.example.librarymanager.mappers.MemberMapper;
import org.example.librarymanager.models.UserInformation;
import org.example.librarymanager.repositories.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public UserInformation createMember(MemberInputDto memberInputDto, UserInputDto userInputDto) {
        return this.memberRepository.save(MemberMapper.toEntity(memberInputDto, userInputDto));
    }

    public List<UserInformation> getAllMembers() {
        return this.memberRepository.findAll();
    }

    public UserInformation getMemberById(Long userId) {
        return this.memberRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + userId));
    }

    public List<UserInformation> getMemberByFirstName(String firstName) {
        return this.memberRepository.findByFirstName(firstName);
    }

    public List<UserInformation> getMemberByLastName(String lastName) {
        return this.memberRepository.findByLastName(lastName);
    }

    public List<UserInformation> getMemberByFirstNameAndLastName(String firstName, String lastName) {
        return this.memberRepository.findByFirstNameAndLastName(firstName, lastName);
    }

    public List<UserInformation> getMemberByEmail(String email) {
        return this.memberRepository.findByEmail(email);
    }

    public List<UserInformation> getMemberByPostalCodeAndHouseNumber(String postalCode, String houseNumber) {
        return this.memberRepository.findByPostalCodeAndHouseNumber(postalCode, houseNumber);
    }

    public UserInformation getMemberByUsername(String username) {
        return this.memberRepository.findByUsername(username);
    }

    public UserInformation updateMember(Long userId, MemberInputDto memberInputDto) {
        UserInformation existingMember = memberRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + userId));

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

    public UserInformation patchMember(MemberInputDto memberInputDto, Long userId) {
        UserInformation existingMember = memberRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + userId));

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
