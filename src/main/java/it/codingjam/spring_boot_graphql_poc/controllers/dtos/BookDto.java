package it.codingjam.spring_boot_graphql_poc.controllers.dtos;

import it.codingjam.spring_boot_graphql_poc.models.Author;
import it.codingjam.spring_boot_graphql_poc.models.Book;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class BookDto implements Serializable {

   private final Book book;

    public BookDto(Book book) {
        this.book = book;
    }

    public Author getAuthor() {
        return book.getAuthor();
    }

    public Integer getPageCount() {
        return book.getPageCount();
    }

    public String getName() {
        return book.getName();
    }

    public UUID getId() {
        return book.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BookDto bookDto = (BookDto) o;
        return Objects.equals(book, bookDto.book);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(book);
    }
}
