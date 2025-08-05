package org.example.librarymanager.controllers;

import jakarta.validation.Valid;
import org.example.librarymanager.dtos.UserInformationDto;
import org.example.librarymanager.dtos.UserInformationInputDto;
import org.example.librarymanager.dtos.UserInformationPatchDto;
import org.example.librarymanager.services.UserInformationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserInformationController {

    private final UserInformationService userInformationService;

    public UserInformationController(UserInformationService userInformationService) {
        this.userInformationService = userInformationService;
    }

    @PostMapping
    public ResponseEntity<UserInformationDto> createUser(@Valid @RequestBody UserInformationInputDto userInformationInputDto) {
        UserInformationDto createdUserDto = userInformationService.createUser(userInformationInputDto);

        URI uri = URI.create(
                ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/" + createdUserDto.userInformationId)
                        .toUriString());
        return ResponseEntity.created(uri).body(createdUserDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserInformationDto> getUserById(@PathVariable Long userId) {
        UserInformationDto userInformationDto = userInformationService.getUserById(userId);
        return ResponseEntity.ok(userInformationDto);
    }

    @GetMapping
    public ResponseEntity<List<UserInformationDto>> getAllUsers() {
        List<UserInformationDto> userInformations = userInformationService.getAllUsers();
        if (userInformations.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(userInformations);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserInformationDto> getUserByUsername(@PathVariable String username) {
        UserInformationDto userInformationDto = userInformationService.getUserByUsername(username);
        return ResponseEntity.ok(userInformationDto);
    }

    @GetMapping("/members/{userId}")
    public ResponseEntity<UserInformationDto> getMemberById(@PathVariable Long userId) {
        UserInformationDto memberDto = userInformationService.getMemberById(userId);
        return ResponseEntity.ok(memberDto);
    }

    @GetMapping("/members")
    public ResponseEntity<List<UserInformationDto>> getAllMembers() {
        List<UserInformationDto> members = userInformationService.getAllMembers();
        if (members.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(members);
    }

    @GetMapping("/members/username/{username}")
    public ResponseEntity<UserInformationDto> getMemberByUsername(@PathVariable String username) {
        UserInformationDto member = userInformationService.getMemberByUsername(username);
        return ResponseEntity.ok(member);
    }

    @GetMapping("/librarians/{userId}")
    public ResponseEntity<UserInformationDto> getLibrarianById(@PathVariable Long userId) {
        UserInformationDto librarian = userInformationService.getLibrarianById(userId);
        return ResponseEntity.ok(librarian);
    }

    @GetMapping("/librarians")
    public ResponseEntity<List<UserInformationDto>> getAllLibrarians() {
        List<UserInformationDto> librarians = userInformationService.getAllLibrarians();
        if (librarians.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(librarians);
    }

    @GetMapping("/librarians/username/{username}")
    public ResponseEntity<UserInformationDto> getLibrarianByUsername(@PathVariable String username) {
        UserInformationDto librarian = userInformationService.getLibrarianByUsername(username);
        return ResponseEntity.ok(librarian);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserInformationDto> updateUserInformation(@PathVariable Long userId, @Valid @RequestBody UserInformationInputDto userInformationInputDto) {
        UserInformationDto updatedUser = userInformationService.updateUserInformation(userId, userInformationInputDto);
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserInformationDto> patchUser(@PathVariable Long userId, @Valid @RequestBody UserInformationPatchDto userInformationPatchDto) {
        UserInformationDto patchedUser = userInformationService.patchUser(userInformationPatchDto, userId);
        return ResponseEntity.ok(patchedUser);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userInformationService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}