package org.example.librarymanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="loans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loanId;

    @NotNull
    @Column(nullable = false)
    private LocalDate loanDate;

    @NotNull
    @Column(nullable = false)
    private LocalDate returnDate;

    private LocalDate actualReturnDate;

    @Column(nullable = false)
    private Boolean isReturned = false;

    @ManyToOne
    @JoinColumn(name = "book_copy_id", nullable = false)
    @NotNull
    private BookCopy bookCopy;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL)
    private List<Fine> fines = new ArrayList<>();

}
