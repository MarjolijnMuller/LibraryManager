package org.example.librarymanager.repositories;

import org.example.librarymanager.models.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
        List<Member> findByMemberFirstName(String memberFirstName);
    List<Member> findByMemberLastName(String memberLastName);
    Member findByMemberFirstNameAndMemberLastName(String memberFirstName, String memberLastName);

    List<Member> findByMemberEmail(String memberEmail);

    List<Member> findMemberByPostalCodeAndHouseNumber(String postalCode, String houseNumber);

    Member findByUsername(String username);
}
