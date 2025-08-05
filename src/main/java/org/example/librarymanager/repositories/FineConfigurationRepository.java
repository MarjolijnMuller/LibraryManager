package org.example.librarymanager.repositories;

import org.example.librarymanager.config.FineConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FineConfigurationRepository extends JpaRepository<FineConfiguration, Long> {
}
