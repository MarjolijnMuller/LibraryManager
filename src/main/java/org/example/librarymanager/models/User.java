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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotNull
    @Column(unique = true, nullable = false)
    @Size(min = 5, max = 50)
    private String username;


    @NotNull
    @Column(nullable = false)
    @Size(min = 5, max = 250)
    private String password;



    @ManyToMany(fetch = FetchType.EAGER)
            @JoinTable(
                    name = "user_roles",
                    joinColumns = @JoinColumn(name = "user_id"),
                    inverseJoinColumns = @JoinColumn(name = "rolename")
            )
    private Set<Role> roles = new HashSet<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserInformation userInformation;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Loan> loans = new ArrayList<>();


    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
