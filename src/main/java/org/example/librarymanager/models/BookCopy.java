package org.example.librarymanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "bookCopies")
public class BookCopy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookCopiesId;

    @NotNull
    @Column(unique = true, nullable = false)
    private Long followNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookCopyStatus status;

    @ManyToOne
    @JoinColumn(name = "book_id")
    Book book;

    public BookCopy(Long followNumber, BookCopyStatus status, Book book) {
        this.followNumber = followNumber;
        this.status = status;
        this.book = book;
    }

    public BookCopy() {
    }

    public Long getBookCopiesId() {
        return bookCopiesId;
    }

    public @NotNull Long getFollowNumber() {
        return followNumber;
    }

    public void setFollowNumber(@NotNull Long followNumber) {
        this.followNumber = followNumber;
    }

    public BookCopyStatus getStatus() {
        return status;
    }

    public void setStatus(BookCopyStatus status) {
        this.status = status;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
