package org.example.librarymanager.repositories;

import org.example.librarymanager.models.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUser_UserId(Long userId);

    Optional<Profile> findByUser_UsernameIgnoreCase(String username);
}
