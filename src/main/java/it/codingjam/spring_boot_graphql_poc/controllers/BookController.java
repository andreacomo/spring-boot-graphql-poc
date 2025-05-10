package it.codingjam.spring_boot_graphql_poc.controllers;

import graphql.GraphQLContext;
import graphql.schema.DataFetchingEnvironment;
import it.codingjam.spring_boot_graphql_poc.controllers.dtos.AuthorDto;
import it.codingjam.spring_boot_graphql_poc.controllers.dtos.BookDto;
import it.codingjam.spring_boot_graphql_poc.controllers.dtos.SimpleBookDto;
import it.codingjam.spring_boot_graphql_poc.models.Author;
import it.codingjam.spring_boot_graphql_poc.models.Book;
import it.codingjam.spring_boot_graphql_poc.services.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@SchemaMapping(typeName = "BookQueries")
public class BookController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookController.class);

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @QueryMapping
    public BookQueries book() {
        return new BookQueries();
    }

    /**
     * {@link BookDto} exposes getAuthor(), so there's no need of {@link SchemaMapping}
     * @param id
     * @param context
     * @return
     */
    @SchemaMapping(typeName = "BookQueries")
    public BookDto bookById(@Argument UUID id, GraphQLContext context) {
        LOGGER.info("bookById: {}", id);
        LOGGER.info("context: {}", context);
        return bookService.findBookById(id)
                .map(BookDto::new)
                .orElse(null);
    }
//
//    @QueryMapping
//    public SimpleBookDto simpleBookById(@Argument UUID id, GraphQLContext context) {
//        LOGGER.info("simpleBookById: {}", id);
//        return bookService.findBookById(id)
//                .map(book -> {
//                    context.put("author", book.getAuthor());
//                    return new SimpleBookDto(book);
//                })
//                .orElse(null);
//    }
//
//    @SchemaMapping(typeName = "Book")
//    public AuthorDto author(SimpleBookDto book, GraphQLContext context) {
//        Author author = context.get("author");
//        LOGGER.info("getting author {} for book {}", author, book);
//        return new AuthorDto(author);
//    }

    public record BookQueries() {

    }
}