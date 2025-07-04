package org.example.librarymanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")

@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotNull
    @Column(unique = true, nullable = false)
    @Size(min = 5, max = 50)
    private String username;

    @NotNull
    @Column(nullable = false)
    @Size(min = 5, max = 50)
    private String password;

    private String profilePictureUrl;

    public User(String username, String password, String profilePictureUrl) {
        this.username = username;
        this.password = password;
        this.profilePictureUrl = profilePictureUrl;
    }
    public User() {}

    public Long getUserId() {
        return userId;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(@NotNull String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public abstract String getRole();
}
