package org.example.librarymanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;

    @NotNull
    @Column(nullable = false)
    private String title;

    @NotNull
    @Column(nullable = false)
    private String authorFirstName;
    @NotNull
    @Column(nullable = false)
    private String authorLastName;

    @NotNull
    @Column(nullable = false)
    private String ISBN;

    private String publisher;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookCategory category;

    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<BookCopy> copies = new ArrayList<>();

    public Book(String title, String authorFirstName, String authorLastName, String ISBN, String publisher, BookCategory category) {
        this.title = title;
        this.authorFirstName = authorFirstName;
        this.authorLastName = authorLastName;
        this.ISBN = ISBN;
        this.publisher = publisher;
        this.category = category;
    }

    public Book() {}

    public Long getBookId() {
        return bookId;
    }

    public @NotNull String getTitle() {
        return title;
    }

    public void setTitle(@NotNull String title) {
        this.title = title;
    }

    public @NotNull String getAuthorFirstName() {
        return authorFirstName;
    }

    public void setAuthorFirstName(@NotNull String authorFirstName) {
        this.authorFirstName = authorFirstName;
    }

    public @NotNull String getAuthorLastName() {
        return authorLastName;
    }

    public void setAuthorLastName(@NotNull String authorLastName) {
        this.authorLastName = authorLastName;
    }

    public @NotNull String getISBN() {
        return ISBN;
    }

    public void setISBN(@NotNull String ISBN) {
        this.ISBN = ISBN;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public BookCategory getCategory() {
        return category;
    }

    public void setCategory(BookCategory category) {
        this.category = category;
    }

    public List<BookCopy> getCopies() {
        return copies;
    }

    public void addCopy(BookCopy copy) {
        if (copy != null) {
            if (this.copies == null) {
                this.copies = new ArrayList<>();
            }
            this.copies.add(copy);
            copy.setBook(this); // Stel de relatie aan de andere kant in
        }
    }

    public void removeCopy(BookCopy copy) {
        if (copy != null && this.copies != null) {
            this.copies.remove(copy);
            copy.setBook(null); // Verwijder de relatie aan de andere kant
        }
    }

}
