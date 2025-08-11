package org.example.librarymanager.repositories;

import org.example.librarymanager.models.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    List<Profile> findByFirstNameIgnoreCase(String firstName);
    List<Profile> findByLastNameIgnoreCase(String lastName);
    List<Profile> findByFirstNameAndLastNameIgnoreCase(String firstName, String lastName);
    List<Profile> findByEmailIgnoreCase(String email);
    List<Profile> findByPostalCodeAndHouseNumberIgnoreCase(String postalCode, String houseNumber);
    Optional<Profile> findByUser_UserId(Long userId);

    Optional<Profile> findByUser_UsernameIgnoreCase(String username);
}
