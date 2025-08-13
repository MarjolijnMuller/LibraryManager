package org.example.librarymanager.controllers;

import jakarta.validation.Valid;
import org.example.librarymanager.dtos.ProfileDto;
import org.example.librarymanager.dtos.ProfileInputDto;
import org.example.librarymanager.dtos.ProfilePatchDto;
import org.example.librarymanager.services.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/profiles")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping
    public ResponseEntity<ProfileDto> createProfile(@Valid @RequestBody ProfileInputDto profileInputDto) {
        ProfileDto createdProfileDto = profileService.createProfile(profileInputDto);

        URI uri = URI.create(
                ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/" + createdProfileDto.profileId)
                        .toUriString());
        return ResponseEntity.created(uri).body(createdProfileDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ProfileDto> getProfileById(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        ProfileDto profileDto = profileService.getProfileById(userId, userDetails);
        return ResponseEntity.ok(profileDto);
    }

    @GetMapping
    public ResponseEntity<List<ProfileDto>> getAllProfiles() {
        List<ProfileDto> profile = profileService.getAllProfiles();
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<ProfileDto> getProfileByUsername(
            @PathVariable String username,
            @AuthenticationPrincipal UserDetails userDetails) {

        ProfileDto profileDto = profileService.getProfileByUsername(username, userDetails);
        return ResponseEntity.ok(profileDto);
    }

    @GetMapping("/members/{userId}")
    public ResponseEntity<ProfileDto> getMemberById(@PathVariable Long userId) {
        ProfileDto memberDto = profileService.getMemberById(userId);
        return ResponseEntity.ok(memberDto);
    }

    @GetMapping("/members")
    public ResponseEntity<List<ProfileDto>> getAllMembers() {
        List<ProfileDto> members = profileService.getAllMembers();
        return ResponseEntity.ok(members);
    }

    @GetMapping("/librarians/{userId}")
    public ResponseEntity<ProfileDto> getLibrarianById(@PathVariable Long userId) {
        ProfileDto librarian = profileService.getLibrarianById(userId);
        return ResponseEntity.ok(librarian);
    }

    @GetMapping("/librarians")
    public ResponseEntity<List<ProfileDto>> getAllLibrarians() {
        List<ProfileDto> librarians = profileService.getAllLibrarians();
        return ResponseEntity.ok(librarians);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ProfileDto> updateProfile(@PathVariable Long userId, @Valid @RequestBody ProfileInputDto profileInputDto) {
        ProfileDto updatedProfile = profileService.updateProfile(userId, profileInputDto);
        return ResponseEntity.ok(updatedProfile);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<ProfileDto> patchProfile(@PathVariable Long userId, @Valid @RequestBody ProfilePatchDto profilePatchDto) {
        ProfileDto patchedProfile = profileService.patchProfile(profilePatchDto, userId);
        return ResponseEntity.ok(patchedProfile);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long userId) {
        profileService.deleteProfile(userId);
        return ResponseEntity.noContent().build();
    }
}
