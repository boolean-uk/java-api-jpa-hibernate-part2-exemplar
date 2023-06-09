package com.booleanuk.api.controller;

import com.booleanuk.api.model.Author;
import com.booleanuk.api.model.Book;
import com.booleanuk.api.model.Publisher;
import com.booleanuk.api.repository.AuthorRepository;
import com.booleanuk.api.repository.BookRepository;
import com.booleanuk.api.repository.PublisherRepository;
import com.booleanuk.api.response.ResponseData;
import com.booleanuk.api.response.ResponseList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("books")
public class BookController {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private PublisherRepository publisherRepository;

    @GetMapping
    public ResponseList<Book> getAll() {
        return new ResponseList<>(this.bookRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<Book>> getOne(@PathVariable int id) {
        Book book = this.bookRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No Book with that ID was found"));
        return ResponseEntity.ok(new ResponseData<>(book));
    }

    @PostMapping
    public ResponseEntity<ResponseData<Book>> create(@RequestBody Book book) {
        Author author = this.authorRepository.findById(book.getAuthor().getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No Author with that ID was found"));
        Publisher publisher = this.publisherRepository.findById(book.getPublisher().getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No Publisher with that ID was found"));
        Book createdBook = this.bookRepository.save(book);
        createdBook.setAuthor(author);
        createdBook.setPublisher(publisher);
        return new ResponseEntity<>(new ResponseData<>(createdBook), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseData<Book>> update(@PathVariable int id, @RequestBody Book book) {
        Book bookToBeUpdated = this.bookRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No Book with that ID was found"));
        if (book.getTitle() == null || book.getGenre() == null || book.getAuthor() == null || book.getPublisher() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Books must have the title, genre, author and publisher set.");
        }
        bookToBeUpdated.setTitle(book.getTitle());
        bookToBeUpdated.setGenre(book.getGenre());
        Author author = this.authorRepository.findById(book.getAuthor().getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No Author with that ID was found"));
        bookToBeUpdated.setAuthor(author);
        Publisher publisher = this.publisherRepository.findById(book.getPublisher().getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No Publisher with that ID was found"));
        bookToBeUpdated.setPublisher(publisher);

        return new ResponseEntity<>(new ResponseData<Book>(this.bookRepository.save(bookToBeUpdated)), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseData<Book>> delete(@PathVariable int id) {
        Book bookToBeDeleted = this.bookRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No Book with that ID was found"));
        this.bookRepository.delete(bookToBeDeleted);
        return ResponseEntity.ok(new ResponseData<>(bookToBeDeleted));
    }
}
