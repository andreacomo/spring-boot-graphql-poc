package it.codingjam.spring_boot_graphql_poc.controllers.dtos;

import it.codingjam.spring_boot_graphql_poc.models.Author;

import java.io.Serializable;
import java.util.UUID;

public class AuthorDto implements Serializable {

    private final Author author;

    public AuthorDto(Author author) {
        this.author = author;
    }

    public UUID getId() {
        return author.getId();
    }

    public String getFirstName() {
        return author.getFirstName();
    }

    public String getLastName() {
        return author.getLastName();
    }
}
