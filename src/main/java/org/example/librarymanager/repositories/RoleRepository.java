package org.example.librarymanager.repositories;

import org.example.librarymanager.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, String> {
}
