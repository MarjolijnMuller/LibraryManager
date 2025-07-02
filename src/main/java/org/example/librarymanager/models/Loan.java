package org.example.librarymanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
@Table(name="loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loanId;

    @NotNull
    @Column(nullable = false)
    private LocalDate loanDate;

    @NotNull
    @Column(nullable = false)
    private LocalDate dueDate;

    private LocalDate returnDate;

    @Column(nullable = false)
    private boolean isReturned = false;

    @ManyToOne
    @JoinColumn(name="bookCopyId", nullable=false)
    @NotNull
        private BookCopy bookCopy;

    @ManyToOne
    @JoinColumn(name="memberId")
    @NotNull
    private Member member;

    @OneToOne(mappedBy = "loan")
    private Fine fine;

    public Loan(LocalDate loanDate, LocalDate dueDate, LocalDate returnDate, boolean isReturned, BookCopy bookCopy, Member member, Fine fine) {
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.isReturned = isReturned;
        this.bookCopy = bookCopy;
        this.member = member;
        this.fine = fine;
    }

    public Loan() {}

    public Long getLoanId() {
        return loanId;
    }

    public @NotNull LocalDate getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(@NotNull LocalDate loanDate) {
        this.loanDate = loanDate;
    }

    public @NotNull LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(@NotNull LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public boolean isReturned() {
        return isReturned;
    }

    public void setReturned(boolean returned) {
        isReturned = returned;
    }

    public @NotNull BookCopy getBookCopy() {
        return bookCopy;
    }

    public void setBookCopy(@NotNull BookCopy bookCopy) {
        this.bookCopy = bookCopy;
    }

    public @NotNull Member getMember() {
        return member;
    }

    public void setMember(@NotNull Member member) {
        this.member = member;
    }

    public Fine getFine() {
        return fine;
    }

    public void setFine(Fine fine) {
        if (fine != null){
            fine.setLoan(this);
        }
        this.fine = fine;
    }
}
