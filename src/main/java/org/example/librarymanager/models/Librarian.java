package org.example.librarymanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "librarians")
@PrimaryKeyJoinColumn(name = "userId", referencedColumnName = "userId")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Librarian extends User {

    @NotNull
    @Column(nullable = false)
    @Size(min = 3, max = 250)
    private String firstName;
    @NotNull
    @Column(nullable = false)
    @Size(min = 3, max = 250)
    private String lastName;


    @Override
    public String getRole() {
        return "Librarian";
    }

}
