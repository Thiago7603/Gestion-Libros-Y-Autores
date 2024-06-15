package com.pliteratura.proyecto.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @Column(unique = true)
    private String title;
    private String author;
    private String language;
    private Double downloads;

    public Book() {
    }

    public Book(BookData bookData) {
        this.title = bookData.title();
        this.author = getFirstAuthor(bookData).getName();
        this.language = getFirstLanguage(bookData);
        this.downloads = bookData.downloads();
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Double getDownloads() {
        return downloads;
    }

    public void setDownloads(Double downloads) {
        this.downloads = downloads;
    }

    public Author getFirstAuthor(BookData bookData) {
        AuthorData authorData = bookData.author().getFirst();
        return new Author(authorData);
    }

    public String getFirstLanguage(BookData bookData) {
        return bookData.language().get(0);
    }

    @Override
    public String toString() {
        return "**** Book Info ****" +
                "\n\tTitle: " + title +
                "\n\tAuthor: " + author +
                "\n\tLanguage: " + language +
                "\n\tDownloads: " + downloads;
    }
}
