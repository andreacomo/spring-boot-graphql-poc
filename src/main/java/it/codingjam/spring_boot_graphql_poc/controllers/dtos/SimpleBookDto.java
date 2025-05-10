package it.codingjam.spring_boot_graphql_poc.controllers.dtos;

import it.codingjam.spring_boot_graphql_poc.models.Book;

import java.io.Serializable;
import java.util.UUID;

public class SimpleBookDto implements Serializable {

   private final Book book;

    public SimpleBookDto(Book book) {
        this.book = book;
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
}
