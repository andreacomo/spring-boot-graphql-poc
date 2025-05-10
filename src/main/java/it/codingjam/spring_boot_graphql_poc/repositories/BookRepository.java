package it.codingjam.spring_boot_graphql_poc.repositories;

import it.codingjam.spring_boot_graphql_poc.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {
}
