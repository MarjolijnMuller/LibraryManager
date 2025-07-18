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

    @GetMapping("/{id}")
    public ResponseEntity<UserInformationDto> getUserById(@PathVariable Long id) {
        UserInformationDto userInformationDto = userInformationService.getUserById(id);
        return ResponseEntity.ok(userInformationDto);
    }

    @GetMapping
    public ResponseEntity<List<UserInformationDto>> getAllUsers() {
        List<UserInformationDto> userInformations = userInformationService.getAllUsers();
        return ResponseEntity.ok(userInformations);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserInformationDto> getUserByUsername(@PathVariable String username) {
        UserInformationDto userInformationDto = userInformationService.getUserByUsername(username);
        return ResponseEntity.ok(userInformationDto);
    }

    @GetMapping("/members/{id}")
    public ResponseEntity<UserInformationDto> getMemberById(@PathVariable Long id) {
        UserInformationDto memberDto = userInformationService.getMemberById(id);
        return ResponseEntity.ok(memberDto);
    }

    @GetMapping("/members")
    public ResponseEntity<List<UserInformationDto>> getAllMembers() {
        List<UserInformationDto> members = userInformationService.getAllMembers();
        return ResponseEntity.ok(members);
    }

    @GetMapping("/members/username/{username}")
    public ResponseEntity<UserInformationDto> getMemberByUsername(@PathVariable String username) {
        UserInformationDto member = userInformationService.getMemberByUsername(username);
        return ResponseEntity.ok(member);
    }

    @GetMapping("/librarians/{id}")
    public ResponseEntity<UserInformationDto> getLibrarianById(@PathVariable Long id) {
        UserInformationDto librarian = userInformationService.getLibrarianById(id);
        return ResponseEntity.ok(librarian);
    }

    @GetMapping("/librarians")
    public ResponseEntity<List<UserInformationDto>> getAllLibrarians() {
        List<UserInformationDto> librarians = userInformationService.getAllLibrarians();
        return ResponseEntity.ok(librarians);
    }

    @GetMapping("/librarians/username/{username}")
    public ResponseEntity<UserInformationDto> getLibrarianByUsername(@PathVariable String username) {
        UserInformationDto librarian = userInformationService.getLibrarianByUsername(username);
        return ResponseEntity.ok(librarian);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserInformationDto> updateUserInformation(@PathVariable Long id, @Valid @RequestBody UserInformationInputDto userInformationInputDto) {
        UserInformationDto updatedUser = userInformationService.updateUserInformation(id, userInformationInputDto);
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserInformationDto> patchUser(@PathVariable Long id, @Valid @RequestBody UserInformationPatchDto userInformationPatchDto) {
        UserInformationDto patchedUser = userInformationService.patchUser(userInformationPatchDto, id);
        return ResponseEntity.ok(patchedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userInformationService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}