package org.example.librarymanager.controllers;

import jakarta.validation.Valid;
import org.example.librarymanager.dtos.LibrarianDto;
import org.example.librarymanager.dtos.LibrarianInputDto;

import org.example.librarymanager.dtos.UserInputDto;
import org.example.librarymanager.mappers.LibrarianMapper;
import org.example.librarymanager.models.Librarian;
import org.example.librarymanager.services.LibrarianService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/librarians")
public class LibrarianController {
    private final LibrarianService librarianService;

    public LibrarianController(final LibrarianService librarianService) {
        this.librarianService = librarianService;
    }

    @PostMapping
    public ResponseEntity<LibrarianDto> createLibrarian(@Valid @RequestBody LibrarianInputDto librarianInputDto, UserInputDto userInputDto) {
        Librarian librarian = this.librarianService.createLibrarian(librarianInputDto, userInputDto);
        LibrarianDto librarianDto = LibrarianMapper.toResponseDto(librarian);

        URI uri = URI.create(
                ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/" + librarianDto.userId).toUriString());
        return ResponseEntity.created(uri).body(librarianDto);

    }

    @GetMapping
    public ResponseEntity<List<LibrarianDto>> getAllLibrarians() {
        List<Librarian> allLibrarians = librarianService.getAllLibrarians();
        List<LibrarianDto> allLibrarianDtos = LibrarianMapper.toResponseDtoList(allLibrarians);
        return ResponseEntity.ok(allLibrarianDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LibrarianDto> getLibrarianById(@PathVariable long id) {
        return ResponseEntity.ok(LibrarianMapper.toResponseDto(librarianService.getLibrarianById(id)));
    }

    @GetMapping("/firstName/{firstName}")
    public ResponseEntity<List<LibrarianDto>> getLibrarianByFirstName(@PathVariable String firstName) {
        return ResponseEntity.ok(LibrarianMapper.toResponseDtoList(librarianService.getLibrarianByFirstName(firstName)));
    }

    @GetMapping("/lastName/{lastName}")
    public ResponseEntity<List<LibrarianDto>> getLibrarianByLastName(@PathVariable String lastName) {
        return ResponseEntity.ok(LibrarianMapper.toResponseDtoList(librarianService.getLibrarianByLastName(lastName)));
    }

    @GetMapping("/fullName/{firstName}/{lastName}")
    public ResponseEntity<List<LibrarianDto>> getLibrarianByFullName(@PathVariable String firstName, @PathVariable String lastName) {
        return ResponseEntity.ok(LibrarianMapper.toResponseDtoList(librarianService.getLibrarianByFirstNameAndLastName(firstName, lastName)));
    }

    @GetMapping("/userName/{userName}")
    public ResponseEntity<LibrarianDto> getLibrarianByUserName(@PathVariable String userName) {
        return ResponseEntity.ok(LibrarianMapper.toResponseDto(librarianService.getLibrarianByUserName(userName)));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<List<LibrarianDto>> getLibrarianByEmail(@PathVariable String email) {
        return ResponseEntity.ok(LibrarianMapper.toResponseDtoList(librarianService.getLibrarianByEmail(email)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LibrarianDto> updateLibrarian(@Valid @PathVariable long id, @RequestBody LibrarianInputDto librarianInputDto) {
        Librarian librarian = this.librarianService.updateLibrarian(librarianInputDto, id);
        LibrarianDto librarianDto = LibrarianMapper.toResponseDto(librarian);
        return ResponseEntity.ok(librarianDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<LibrarianDto> patchLibrarian(@Valid @PathVariable long id, @RequestBody LibrarianInputDto librarianInputDto) {
        Librarian librarian = this.librarianService.patchLibrarian(librarianInputDto, id);
        LibrarianDto librarianDto = LibrarianMapper.toResponseDto(librarian);
        return ResponseEntity.ok(librarianDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<LibrarianDto> deleteLibrarian(@PathVariable long id) {
        this.librarianService.deleteLibrarian(id);
        return ResponseEntity.noContent().build();
    }
}
