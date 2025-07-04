package org.example.librarymanager.dtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class MemberInputDto {
    @NotNull
    @Size(min = 3, max = 250)
    private String firstName;
    @NotNull
    @Size(min = 3, max = 250)
    private String lastName;

    @NotNull
    @Size(min = 1, max = 250)
    private String street;
    @NotNull
    @Size(min=1)
    @Positive
    private String houseNumber;
    @NotNull
    @Size(min = 3, max = 100)
    private String postalCode;
    @NotNull
    @Size(min = 1, max = 100)
    private String city;

    @Email
    @Size(min = 3, max = 250)
    private String email;
    private String phone;
}
