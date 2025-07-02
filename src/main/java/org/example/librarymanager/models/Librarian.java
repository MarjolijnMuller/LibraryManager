package org.example.librarymanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "librarians")
@PrimaryKeyJoinColumn(name = "userId", referencedColumnName = "userId")

public class Librarian extends User {

    //TODO: automatisch laten ophogen
    private Long librarianId;

    @NotNull
    @Column(nullable = false)
    private String firstName;
    @NotNull
    @Column(nullable = false)
    private String lastName;

    public Librarian(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Librarian() {
    }

    @Override
    public String getRole() {
        return "Librarian";
    }

    public Long getLibrarianId() {
        return librarianId;
    }

    //TODO: automatisch ophogen
    public void setLibrarianId(Long librarianId) {
        this.librarianId = librarianId;
    }

    public @NotNull String getFirstName() {
        return firstName;
    }

    public void setFirstName(@NotNull String firstName) {
        this.firstName = firstName;
    }

    public @NotNull String getLastName() {
        return lastName;
    }

    public void setLastName(@NotNull String lastName) {
        this.lastName = lastName;
    }
}
