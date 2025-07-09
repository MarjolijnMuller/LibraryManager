package org.example.librarymanager.models;

import jakarta.persistence.*;

@Entity
@Table(name = "bookCopies")
public class BookCopy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookCopyId;

    @Column
    private Long followNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookCopyStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    Book book;

    public BookCopy(Long followNumber, BookCopyStatus status, Book book) {
        this.followNumber = followNumber;
        this.status = status;
        this.book = book;
    }

    public BookCopy() {
    }

    public Long getBookCopyId() {
        return bookCopyId;
    }

    public Long getFollowNumber() {
        return followNumber;
    }

    public void setFollowNumber(Long followNumber) {
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
        if (this.book != null && !this.book.equals(book)) {
            this.book.getCopies().remove(this);
        }
        this.book = book;
        if (book != null && !book.getCopies().contains(this)) {
            book.getCopies().add(this);
        }
    }
}
