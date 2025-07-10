package org.example.librarymanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table (name = "members")
@PrimaryKeyJoinColumn(name = "userId", referencedColumnName = "userId")

public class Member extends User{

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


    private String phone;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Loan> loans = new ArrayList<>();

    public Member() {}

    @Override
    public String getRole() {
        return "Member";
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<Loan> getLoans() {
        return loans;
    }

    public void setLoans(List<Loan> loans) {
        this.loans = loans;
    }
}
