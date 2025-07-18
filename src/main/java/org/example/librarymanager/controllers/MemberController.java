package org.example.librarymanager.controllers;

import jakarta.validation.Valid;
import org.example.librarymanager.dtos.MemberDto;
import org.example.librarymanager.dtos.MemberInputDto;
import org.example.librarymanager.dtos.UserInputDto;
import org.example.librarymanager.mappers.MemberMapper;
import org.example.librarymanager.models.UserInformation;
import org.example.librarymanager.services.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;
    public MemberController(final MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public ResponseEntity<MemberDto> createMember(@Valid @RequestBody MemberInputDto memberInputDto, UserInputDto userInputDto) {
        UserInformation member = this.memberService.createMember(memberInputDto, userInputDto);
        MemberDto memberDto = MemberMapper.toResponseDto(member);

        URI uri = URI.create(
                ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/" + memberDto.userId)
                        .toUriString()
        );
        return ResponseEntity.created(uri).body(memberDto);
    }

    @GetMapping
    public ResponseEntity<List<MemberDto>> getAllMember() {
        List<UserInformation> allMembers = memberService.getAllMembers();
        List<MemberDto> allMemberDtos = MemberMapper.toResponseDtoList(allMembers);
        return ResponseEntity.ok(allMemberDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberDto> getMemberById(@PathVariable Long id) {
        return ResponseEntity.ok(MemberMapper.toResponseDto(memberService.getMemberById(id)));
    }

    @GetMapping("/firstName/{firstName}")
    public ResponseEntity<List<MemberDto>> getMemberByFirstName(@PathVariable String firstName) {
        return ResponseEntity.ok(MemberMapper.toResponseDtoList(memberService.getMemberByFirstName(firstName)));
    }

    @GetMapping("/lastName/{lastName}")
    public ResponseEntity<List<MemberDto>> getMemberByLastName(@PathVariable String lastName) {
        return ResponseEntity.ok(MemberMapper.toResponseDtoList(memberService.getMemberByLastName(lastName)));
    }

    @GetMapping("/fullName/{firstName}/{lastName}")
    public ResponseEntity<List<MemberDto>> getMemberByFullName(@PathVariable String firstName, @PathVariable String lastName) {
        return ResponseEntity.ok(MemberMapper.toResponseDtoList(memberService.getMemberByFirstNameAndLastName(firstName, lastName)));
    }

    @GetMapping("/userName/{userName}")
    public ResponseEntity<MemberDto> getMemberByUserName(@PathVariable String userName) {
        return ResponseEntity.ok(MemberMapper.toResponseDto(memberService.getMemberByUsername(userName)));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<List<MemberDto>> getMemberByEmail(@PathVariable String email) {
        return ResponseEntity.ok(MemberMapper.toResponseDtoList(memberService.getMemberByEmail(email)));
    }

    @GetMapping("/address/{postalCode}/{houseNumber}")
    public ResponseEntity<List<MemberDto>> getMemberByAddress(@PathVariable String postalCode, @PathVariable String houseNumber) {
        return ResponseEntity.ok(MemberMapper.toResponseDtoList(memberService.getMemberByPostalCodeAndHouseNumber(postalCode, houseNumber)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MemberDto> updateMember(@Valid @PathVariable Long id, @RequestBody MemberInputDto memberInputDto) {
        UserInformation member = this.memberService.updateMember(id, memberInputDto);
        MemberDto memberDto = MemberMapper.toResponseDto(member);
        return ResponseEntity.ok(memberDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MemberDto> patchMember(@Valid @PathVariable Long id, @RequestBody MemberInputDto memberInputDto) {
        UserInformation member = this.memberService.patchMember(memberInputDto, id);
        MemberDto memberDto = MemberMapper.toResponseDto(member);
        return ResponseEntity.ok(memberDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MemberDto> deleteMember(@PathVariable Long id) {
        this.memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }
}
