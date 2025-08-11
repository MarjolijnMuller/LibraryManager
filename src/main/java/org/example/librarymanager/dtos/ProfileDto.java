package org.example.librarymanager.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ProfileDto {
    public Long userId;
    public String username;
    public List<String> roles;

    public Long profileId;
    public String firstName;
    public String lastName;
    public String street;
    public String houseNumber;
    public String postalCode;
    public String city;

    public String email;
    public String phone;
    public String profilePictureUrl;
}
