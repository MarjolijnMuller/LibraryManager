package org.example.librarymanager.repositories;

import org.example.librarymanager.models.Librarian;
import org.example.librarymanager.models.UserInformation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<UserInformation, Long> {
    Librarian findByUserId(Long userId);
        List<UserInformation> findByFirstName(String memberFirstName);
    List<UserInformation> findByLastName(String memberLastName);
    List<UserInformation> findByFirstNameAndLastName(String memberFirstName, String memberLastName);

    List<UserInformation> findByEmail(String memberEmail);

    List<UserInformation> findByPostalCodeAndHouseNumber(String postalCode, String houseNumber);

    UserInformation findByUsername(String username);
}
