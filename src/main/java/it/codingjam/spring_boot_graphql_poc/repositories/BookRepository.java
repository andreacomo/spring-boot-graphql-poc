package it.codingjam.spring_boot_graphql_poc.repositories;

import it.codingjam.spring_boot_graphql_poc.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {

    @Query("select b from Book b left join fetch b.author where b.id = :id")
    Optional<Book> findByIdWithAuthor(UUID id);
}
