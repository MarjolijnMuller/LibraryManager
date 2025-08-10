package org.example.librarymanager.repositories;

import org.example.librarymanager.models.UserInformation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserInformationRepository extends JpaRepository<UserInformation, Long> {
    List<UserInformation> findByFirstNameIgnoreCase(String firstName);
    List<UserInformation> findByLastNameIgnoreCase(String lastName);
    List<UserInformation> findByFirstNameAndLastNameIgnoreCase(String firstName, String lastName);
    List<UserInformation> findByEmailIgnoreCase(String email);
    List<UserInformation> findByPostalCodeAndHouseNumberIgnoreCase(String postalCode, String houseNumber);
    Optional<UserInformation> findByUser_UserId(Long userId);

    Optional<UserInformation> findByUser_UsernameIgnoreCase(String username);
}
