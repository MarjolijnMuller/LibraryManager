package org.example.librarymanager.config;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "fine_configurations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FineConfiguration {
    @Id
    private Long id = 1L;

    @NotNull
    @PositiveOrZero
    private Double dailyFine;

    @NotNull
    @PositiveOrZero
    private Double maxFineAmount;

    @Column(name = "last_updated_by")
    private String lastUpdatedBy;

    @Column(name = "last_updated_at")
    private LocalDateTime lastUpdatedAt;
}