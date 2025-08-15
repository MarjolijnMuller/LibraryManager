package org.example.librarymanager.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name="roles")

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class Role {
    @Id
    @NonNull
    private String rolename;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

}
