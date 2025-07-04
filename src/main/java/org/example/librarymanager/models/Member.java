package org.example.librarymanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Entity
@Table (name = "members")
@PrimaryKeyJoinColumn(name = "userId", referencedColumnName = "userId")

public class Member extends User{
    //TODO: automatisch laten ophogen
    private Long memberId;

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
    @Positive
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

    public Member(String username, String password, String profilePictureUrl, Long memberId, String firstName, String lastName, String street, String houseNumber, String postalCode, String city, String email, String phone) {
        super(username, password, profilePictureUrl);
        this.memberId = memberId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.street = street;
        this.houseNumber = houseNumber;
        this.postalCode = postalCode;
        this.city = city;
        this.email = email;
        this.phone = phone;
    }

    public Member() {}

    @Override
    public String getRole() {
        return "Member";
    }

    public Long getMemberId() {
        return memberId;
    }

    //TODO: automatisch ophogen
    public void setMemberId(Long memberId) {
        this.memberId = memberId;
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

    public @NotNull String getStreet() {
        return street;
    }

    public void setStreet(@NotNull String street) {
        this.street = street;
    }

    public @NotNull String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(@NotNull String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public @NotNull String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(@NotNull String postalCode) {
        this.postalCode = postalCode;
    }

    public @NotNull String getCity() {
        return city;
    }

    public void setCity(@NotNull String city) {
        this.city = city;
    }

    public @Email String getEmail() {
        return email;
    }

    public void setEmail(@Email String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
