package org.example.librarymanager.repositories;

import org.example.librarymanager.models.FineConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FineConfigurationRepository extends JpaRepository<FineConfiguration, Long> {
}
