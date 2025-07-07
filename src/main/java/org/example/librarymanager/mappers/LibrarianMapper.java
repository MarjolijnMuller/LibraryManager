package org.example.librarymanager.mappers;

import org.example.librarymanager.dtos.LibrarianDto;
import org.example.librarymanager.dtos.LibrarianInputDto;
import org.example.librarymanager.dtos.UserInputDto;
import org.example.librarymanager.models.Librarian;

import java.util.List;
import java.util.stream.Collectors;

public class LibrarianMapper {
    public static Librarian toEntity(LibrarianInputDto librarianInputDto, UserInputDto userInputDto) {
        Librarian librarian = new Librarian();
        librarian.setFirstName(librarianInputDto.firstName);
        librarian.setLastName(librarianInputDto.lastName);
        librarian.setUsername(librarianInputDto.username);
        librarian.setPassword(librarianInputDto.password);
        librarian.setProfilePictureUrl(librarianInputDto.profilePictureUrl);
        librarian.setEmail(librarianInputDto.email);
        return librarian;
    }

    public static LibrarianDto toResponseDto(Librarian librarian){
        LibrarianDto librarianDto = new LibrarianDto();
        librarianDto.userId = librarian.getUserId();
        librarianDto.firstName = librarian.getFirstName();
        librarianDto.lastName = librarian.getLastName();
        librarianDto.username = librarian.getUsername();
        librarianDto.password = librarian.getPassword();
        librarianDto.profilePictureUrl = librarian.getProfilePictureUrl();
        librarianDto.email = librarian.getEmail();
        return librarianDto;
    }

    public static List<LibrarianDto> toResponseDtoList(List<Librarian> librarians){
        return librarians.stream()
                .map(LibrarianMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
