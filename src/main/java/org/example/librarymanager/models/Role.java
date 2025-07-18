package org.example.librarymanager.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="roles")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Role {
    @Id
    @Column(nullable = false, unique = true)
    private String rolename;
}
