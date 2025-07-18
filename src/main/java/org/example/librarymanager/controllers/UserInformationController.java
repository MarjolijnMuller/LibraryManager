package org.example.librarymanager.controllers;

import jakarta.validation.Valid;
import org.example.librarymanager.dtos.UserInformationDto;
import org.example.librarymanager.dtos.UserInformationInputDto;
import org.example.librarymanager.mappers.UserInformationMapper;
import org.example.librarymanager.models.UserInformation;
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
    private final UserInformationMapper userInformationMapper;

    public UserInformationController(UserInformationService userInformationService, UserInformationMapper userInformationMapper) {
        this.userInformationService = userInformationService;
        this.userInformationMapper = userInformationMapper;
    }

    @PostMapping
    public ResponseEntity<UserInformationDto> createUserInformation(@Valid @RequestBody UserInformationInputDto userInformationInputDto) {
        UserInformation newUserInfo = this.userInformationService.createUser(userInformationInputDto);
        UserInformationDto userInformationDto = userInformationMapper.toResponseDto(newUserInfo);

        URI uri = URI.create(
                ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/" + userInformationDto.userInformationId)
                        .toUriString()
        );
        return ResponseEntity.created(uri).body(userInformationDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserInformationDto> getUserInformationById(@PathVariable Long id) {
        UserInformation userInformation = userInformationService.getUserById(id);
        return ResponseEntity.ok(UserInformationMapper.toResponseDto(userInformation));
    }

    @GetMapping
    public ResponseEntity<List<UserInformationDto>> getAllUsers() {
        UserInformation firstUser = userInformationService.getAllUsers();
        return ResponseEntity.ok(List.of(UserInformationMapper.toResponseDto(firstUser)));
    }


    @GetMapping("/username/{username}")
    public ResponseEntity<UserInformationDto> getUserByUsername(@PathVariable String username) {
        UserInformation userInformation = userInformationService.getUserByUsername(username);
        return ResponseEntity.ok(UserInformationMapper.toResponseDto(userInformation));
    }

    @GetMapping("/members/{id}")
    public ResponseEntity<UserInformationDto> getMemberById(@PathVariable Long id) {
        UserInformation userInformation = userInformationService.getMemberById(id);
        return ResponseEntity.ok(UserInformationMapper.toResponseDto(userInformation));
    }

    @GetMapping("/members")
    public ResponseEntity<List<UserInformationDto>> getAllMembers() {
        List<UserInformation> userInformations = userInformationService.getAllMembers();
        return ResponseEntity.ok(UserInformationMapper.toResponseDtoList(userInformations));
    }

    @GetMapping("/members/username/{username}")
    public ResponseEntity<UserInformationDto> getMemberByUsername(@PathVariable String username) {
        UserInformation userInformation = userInformationService.getMemberByUsername(username);
        return ResponseEntity.ok(UserInformationMapper.toResponseDto(userInformation));
    }

    @GetMapping("/librarians/{id}")
    public ResponseEntity<UserInformationDto> getLibrarianById(@PathVariable Long id) {
        UserInformation userInformation = userInformationService.getLibrarianById(id);
        return ResponseEntity.ok(UserInformationMapper.toResponseDto(userInformation));
    }

    @GetMapping("/librarians")
    public ResponseEntity<List<UserInformationDto>> getAllLibrarians() {
        List<UserInformation> userInformations = userInformationService.getAllLibrarians();
        return ResponseEntity.ok(UserInformationMapper.toResponseDtoList(userInformations));
    }

    @GetMapping("/librarians/username/{username}")
    public ResponseEntity<UserInformationDto> getLibrarianByUsername(@PathVariable String username) {
        UserInformation userInformation = userInformationService.getLibrarianByUsername(username);
        return ResponseEntity.ok(UserInformationMapper.toResponseDto(userInformation));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserInformationDto> updateUserInformation(@Valid @PathVariable Long id, @RequestBody UserInformationInputDto userInformationInputDto) {
        UserInformation updatedUserInfo = this.userInformationService.updateUserInformation(id, userInformationInputDto);
        UserInformationDto userInformationDto = userInformationMapper.toResponseDto(updatedUserInfo);
        return ResponseEntity.ok(userInformationDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserInformationDto> patchUserInformation(@Valid @PathVariable Long id, @RequestBody UserInformationInputDto userInformationInputDto) {
        UserInformation patchedUserInfo = this.userInformationService.patchUser(userInformationInputDto, id); // Note: service method parameter order
        UserInformationDto userInformationDto = userInformationMapper.toResponseDto(patchedUserInfo);
        return ResponseEntity.ok(userInformationDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        this.userInformationService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}