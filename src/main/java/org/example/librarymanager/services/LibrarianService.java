package org.example.librarymanager.services;

import org.example.librarymanager.dtos.LibrarianInputDto;
import org.example.librarymanager.dtos.UserInputDto;
import org.example.librarymanager.exceptions.ResourceNotFountException;
import org.example.librarymanager.mappers.LibrarianMapper;
import org.example.librarymanager.models.Librarian;
import org.example.librarymanager.repositories.LibrarianRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LibrarianService {
    private final LibrarianRepository librarianRepository;

    public LibrarianService(LibrarianRepository librarianRepository) {
        this.librarianRepository = librarianRepository;
    }

    public Librarian createLibrarian(LibrarianInputDto librarianInputDto, UserInputDto userInputDto) {
        return this.librarianRepository.save(LibrarianMapper.toEntity(librarianInputDto, userInputDto));
    }

    public List<Librarian> getAllLibrarians() {
        return librarianRepository.findAll();
    }

    public Librarian getLibrarianById(Long userId) {
        return this.librarianRepository.findById(userId).orElseThrow(() -> new RuntimeException("Librarian not found with ID: " + userId));
    }

    public  List<Librarian> getLibrarianByFirstName(String firstName) {
        return this.librarianRepository.findByFirstName(firstName);
    }

    public  List<Librarian> getLibrarianByLastName(String lastName) {
        return this.librarianRepository.findByLastName(lastName);
    }

    public List<Librarian> getLibrarianByFirstNameAndLastName(String firstName, String lastName) {
        return this.librarianRepository.findByFirstNameAndLastName(firstName, lastName);
    }

    public Librarian getLibrarianByUserName(String userName) {
        return this.librarianRepository.findByUsername(userName);
    }

    public List<Librarian> getLibrarianByEmail(String email) {
        return this.librarianRepository.findByEmail(email);
    }

    public Librarian updateLibrarian(LibrarianInputDto librarianInputDto, Long userId) {

        Librarian existingLibrarian = librarianRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFountException("Librarian not found with ID: " + userId));

        if (librarianInputDto.profilePictureUrl != null) {
            existingLibrarian.setProfilePictureUrl(librarianInputDto.profilePictureUrl);
        }

        existingLibrarian.setFirstName(librarianInputDto.firstName);
        existingLibrarian.setLastName(librarianInputDto.lastName);
        existingLibrarian.setUsername(librarianInputDto.username);

        if (librarianInputDto.email != null) {
            existingLibrarian.setEmail(librarianInputDto.email);
        }

        //TODO: wachtwoord beveiligen
        existingLibrarian.setPassword(librarianInputDto.password);

        return this.librarianRepository.save(existingLibrarian);
    }

    public Librarian patchLibrarian(LibrarianInputDto updates, Long userId) {
        Librarian existingLibrarian = librarianRepository.findById(userId).orElseThrow(() -> new ResourceNotFountException("Librarian not found with ID: " + userId));

        if (updates.profilePictureUrl != null) {
            existingLibrarian.setProfilePictureUrl(updates.profilePictureUrl);
        }
        if (updates.lastName != null) {
            existingLibrarian.setLastName(updates.lastName);
        }
        if (updates.username != null) {
            existingLibrarian.setUsername(updates.username);
        }
        if (updates.email != null) {
            existingLibrarian.setEmail(updates.email);
        }
        if (updates.password != null) {
            //TODO: wachtwoord beveiligen
            existingLibrarian.setPassword(updates.password);
        }
        return this.librarianRepository.save(existingLibrarian);
    }

    public void deleteLibrarian(Long userId) {
        librarianRepository.deleteById(userId);
    }

}
