package org.example.librarymanager.repositories;

import org.example.librarymanager.models.UserInformation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserInformationRepository extends JpaRepository<UserInformation, Long> {
    List<UserInformation> findByFirstName(String firstName);
    List<UserInformation> findByLastName(String lastName);
    List<UserInformation> findByFirstNameAndLastName(String firstName, String lastName);
    List<UserInformation> findByEmail(String email);
    List<UserInformation> findByPostalCodeAndHouseNumber(String postalCode, String houseNumber);

    Optional<UserInformation> findByUser_Username(String username);
}
