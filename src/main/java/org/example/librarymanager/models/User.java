package org.example.librarymanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotNull
    @Column(unique = true, nullable = false)
    @Size(min = 5, max = 50)
    private String username;

    @Email
    private String email;

    @NotNull
    @Column(nullable = false)
    @Size(min = 5, max = 250)
    private String password;

    private String profilePictureUrl;


    public abstract String getRole();

}
