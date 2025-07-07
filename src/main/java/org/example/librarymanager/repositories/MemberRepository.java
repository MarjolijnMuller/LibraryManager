package org.example.librarymanager.repositories;

import org.example.librarymanager.models.Librarian;
import org.example.librarymanager.models.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Librarian findByUserId(Long userId);
        List<Member> findByFirstName(String memberFirstName);
    List<Member> findByLastName(String memberLastName);
    List<Member> findByFirstNameAndLastName(String memberFirstName, String memberLastName);

    List<Member> findByEmail(String memberEmail);

    List<Member> findByPostalCodeAndHouseNumber(String postalCode, String houseNumber);

    Member findByUsername(String username);
}
