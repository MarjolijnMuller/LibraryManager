package org.example.librarymanager.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public class UserInformationInputDto {
    @NotNull
    @Size(min = 5, max = 50)
    public String username;

    @NotNull
    @Size(min = 5, max = 250)
    public String password;

    public Set<String> roles;

    @NotNull
    @Size(min = 3, max = 250)
    public String firstName;
    @NotNull
    @Size(min = 3, max = 250)
    public String lastName;

    @NotNull
    @Size(min = 1, max = 250)
    public String street;
    @NotNull
    @Size(min=1)
    public String houseNumber;
    @NotNull
    @Size(min = 3, max = 100)
    public String postalCode;
    @NotNull
    @Size(min = 1, max = 100)
    public String city;

    @Email
    public String email;
    public String phone;
    public String profilePictureUrl;
}
