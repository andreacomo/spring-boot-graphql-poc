package it.codingjam.spring_boot_graphql_poc.controllers;

import graphql.GraphQLContext;
import it.codingjam.spring_boot_graphql_poc.controllers.dtos.BookDto;
import it.codingjam.spring_boot_graphql_poc.services.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
public class BookController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookController.class);

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * {@link BookDto} exposes getAuthor() <strong>always fetched</strong>,
     * so there's no need of {@link SchemaMapping}
     * -> this is not optimized because author is always queried even if not returned
     * @param id
     * @param context
     * @return
     */
    @QueryMapping
    public BookDto bookById(@Argument UUID id, GraphQLContext context) {
        LOGGER.info("bookById: {}", id);
        LOGGER.info("context: {}", context);
        return bookService.findBookById(id)
                .map(BookDto::new)
                .orElse(null);
    }
}