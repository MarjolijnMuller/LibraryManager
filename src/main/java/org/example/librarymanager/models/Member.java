package org.example.librarymanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table (name = "members")
@PrimaryKeyJoinColumn(name = "userId", referencedColumnName = "userId")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

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


    @Override
    public String getRole() {
        return "Member";
    }
}
