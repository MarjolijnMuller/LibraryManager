package org.example.librarymanager.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "bookCopies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

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
