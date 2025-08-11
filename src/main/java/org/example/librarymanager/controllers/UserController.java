package org.example.librarymanager.controllers;

import org.example.librarymanager.dtos.UserDto;
import org.example.librarymanager.dtos.UserInputDto;
import org.example.librarymanager.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserInputDto userInputDto) {
        UserDto createdUserDto = userService.createUser(userInputDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdUserDto.getUserId())
                .toUri();

        return ResponseEntity.created(location).body(createdUserDto);
    }
}