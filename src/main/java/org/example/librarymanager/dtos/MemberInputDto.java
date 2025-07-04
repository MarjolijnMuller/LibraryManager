package org.example.librarymanager.dtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class MemberInputDto extends UserInputDto {
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
    @Positive
    public String houseNumber;
    @NotNull
    @Size(min = 3, max = 100)
    public String postalCode;
    @NotNull
    @Size(min = 1, max = 100)
    public String city;

    public String phone;
}
