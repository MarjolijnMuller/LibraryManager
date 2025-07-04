package org.example.librarymanager.repositories;

import org.example.librarymanager.models.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
