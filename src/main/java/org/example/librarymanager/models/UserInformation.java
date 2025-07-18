package org.example.librarymanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users_information")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class UserInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_information_id")
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


    private String profilePictureUrl;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId", unique = true, nullable = false)
    private User user;

    public UserInformation(User user, String firstName, String lastName, String street, String houseNumber, String postalCode, String city, String phone, String email) {
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
