package com.booleanuk.api.repository;

import com.booleanuk.api.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Integer> {
    List<Book> getBooksByPublisherId(int publisher_id);
    List<Book> getBooksByAuthorId(int author_id);
}
