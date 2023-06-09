package com.booleanuk.api.controller;

import com.booleanuk.api.model.Author;
import com.booleanuk.api.model.Book;
import com.booleanuk.api.repository.AuthorRepository;
import com.booleanuk.api.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("authors")
public class AuthorController {
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private BookRepository bookRepository;

    @GetMapping
    public List<Author> getAll() {
        return this.authorRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Author> getOneById(@PathVariable int id) {
        Author author = this.authorRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No Author with that ID was found."));
        return ResponseEntity.ok(author);
    }

    @PostMapping
    public ResponseEntity<Author> create(@RequestBody Author author) {
        Author createdAuthor = this.authorRepository.save(author);
        List<Book> books = this.bookRepository.getBooksByAuthorId(createdAuthor.getId());
        createdAuthor.setBooks(books);
        return new ResponseEntity<Author>(createdAuthor, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Author> update(@PathVariable int id, @RequestBody Author author) {
        Author authorToUpdate = this.authorRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No Author with that ID was found."));
        if (author.getFirstName() == null || author.getLastName() == null || author.getEmail() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Authors must have a first name, last name and email defined.");
        }
        authorToUpdate.setFirstName(author.getFirstName());
        authorToUpdate.setLastName(author.getLastName());
        authorToUpdate.setEmail(author.getEmail());
        authorToUpdate.setAlive(author.isAlive());

        return new ResponseEntity<Author>(this.authorRepository.save(authorToUpdate), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Author> delete(@PathVariable int id) {
        Author authorToDelete = this.authorRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No Author with that ID was found."));
        if (authorToDelete.getBooks().size() > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete that Author as there are books associated with them.");
        }
        authorToDelete.setBooks(null);
        this.authorRepository.delete(authorToDelete);
        return ResponseEntity.ok(authorToDelete);
    }
}
