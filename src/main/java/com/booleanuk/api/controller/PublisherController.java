package com.booleanuk.api.controller;

import com.booleanuk.api.model.Book;
import com.booleanuk.api.model.Publisher;
import com.booleanuk.api.repository.BookRepository;
import com.booleanuk.api.repository.PublisherRepository;
import com.booleanuk.api.response.ResponseData;
import com.booleanuk.api.response.ResponseList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("publishers")
public class PublisherController {
    @Autowired
    private PublisherRepository publisherRepository;
    @Autowired
    private BookRepository bookRepository;

    @GetMapping
    public ResponseList<Publisher> getAll() {
        return new ResponseList<>(this.publisherRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<Publisher>> getOne(@PathVariable int id) {
        Publisher publisher = this.publisherRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No Publisher with that ID was found"));
        return ResponseEntity.ok(new ResponseData<>(publisher));
    }

    @PostMapping
    public ResponseEntity<ResponseData<Publisher>> create(@RequestBody Publisher publisher) {
        Publisher createdPublisher = this.publisherRepository.save(publisher);
        List<Book> books = this.bookRepository.getBooksByPublisherId(createdPublisher.getId());
        createdPublisher.setBooks(books);
        return new ResponseEntity<>(new ResponseData<>(createdPublisher), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseData<Publisher>> update(@PathVariable int id, @RequestBody Publisher publisher) {
        Publisher publisherToUpdate = this.publisherRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No Publisher with that ID was found"));
        if (publisher.getName() == null || publisher.getLocation() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Publishers must have both a name and a location set.");
        }
        publisherToUpdate.setName(publisher.getName());
        publisherToUpdate.setLocation(publisher.getLocation());
        return new ResponseEntity<>(new ResponseData<>(this.publisherRepository.save(publisherToUpdate)), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseData<Publisher>> delete(@PathVariable int id) {
        Publisher publisherToDelete = this.publisherRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No Publisher with that ID was found"));
        if (publisherToDelete.getBooks().size() > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete that Publisher as there are books associated with them.");
        }
        publisherToDelete.setBooks(null);
        this.publisherRepository.delete(publisherToDelete);
        return ResponseEntity.ok(new ResponseData<>(publisherToDelete));
    }
}
