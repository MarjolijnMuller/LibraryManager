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
@Table(name = "profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profileId")
    private long id;

    @NotNull
    @Column(nullable = false)
    @Size(min = 3, max = 250)
    private String firstName;
    @NotNull
    @Column(nullable = false)
    @Size(min = 3, max = 250)
    private String lastName;

    @NotNull
    @Column(nullable = false)
    @Size(min = 1, max = 250)
    private String street;
    @NotNull
    @Column(nullable = false)
    private String houseNumber;
    @NotNull
    @Column(nullable = false)
    @Size(min = 3, max = 100)
    private String postalCode;
    @NotNull
    @Column(nullable = false)
    @Size(min = 1, max = 100)
    private String city;

    @Email
    private String email;

    private String phone;


    private String profilePictureFile;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "userId", unique = true, nullable = false)
    private User user;

    public Profile(User user, String firstName, String lastName, String street, String houseNumber, String postalCode, String city, String phone, String email) {
        this.user = user;
        this.firstName = firstName;
        this.lastName = lastName;
        this.street = street;
        this.houseNumber = houseNumber;
        this.postalCode = postalCode;
        this.city = city;
        this.phone = phone;
        this.email = email;
    }
}
