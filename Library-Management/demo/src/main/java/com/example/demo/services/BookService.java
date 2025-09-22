package com.example.demo.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Book;
import com.example.demo.repositories.BookRepository;

@Service
public class BookService {

	    @Autowired
	    private BookRepository bookRepository;

	    

	    public ResponseEntity<List<Book>> getAllBooks() {
	        try {
	            
	            List<Book> books = bookRepository.findAll();

	            
	            return ResponseEntity.status(HttpStatus.OK).body(books);
	        } catch (Exception e) {
	            
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	        }
	    }
	    
	    
	    public ResponseEntity<Map<String, Object>> addBook(Book book) {
	        try {

	            Book savedBook = bookRepository.save(book);

	            Map<String, Object> response = new HashMap<>();
	            response.put("message", "Book added successfully.");
	            response.put("bookDetails", Map.of(
	                    "bookId", savedBook.getBookId(),
	                    "title", savedBook.getTitle(),
	                    "author", savedBook.getAuthor(),
	                    "category", savedBook.getCategory(),
	                    "availableCopies", savedBook.getAvailableCopies()
	            ));


	            return ResponseEntity.status(HttpStatus.CREATED).body(response);
	        } catch (Exception e) {

	            Map<String, Object> errorResponse = new HashMap<>();
	            errorResponse.put("message", "An error occurred while adding the book: " + e.getMessage());
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	        }
	    }
	
}
