package it.codingjam.spring_boot_graphql_poc.services;

import it.codingjam.spring_boot_graphql_poc.models.Book;
import it.codingjam.spring_boot_graphql_poc.repositories.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> findAllBooks() {
        return bookRepository.findAll();
    }

    public Optional<Book> findBookById(UUID id) {
        return bookRepository.findByIdWithAuthor(id);
    }

    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    public void deleteBookById(UUID id) {
        bookRepository.deleteById(id);
    }
}