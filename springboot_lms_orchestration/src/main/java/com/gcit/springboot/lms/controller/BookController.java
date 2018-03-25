package com.gcit.springboot.lms.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.gcit.springboot.lms.entity.Book;
/**
 * Handles requests for the book routes.
 */
@RestController
public class BookController {
	
	@Autowired
	RestTemplate restTemplate;
	
	private static final Logger logger = LoggerFactory.getLogger(BookController.class);

	/* Read all books */
	@RequestMapping(value = "/lms/books", method = RequestMethod.GET)
	public Book[] getAllBooks(HttpServletResponse response) throws IOException {
		logger.info("Getting all books"); 
		try {
			ResponseEntity<Book[]> responseEntity = restTemplate.getForEntity("http://localhost:8083/books", Book[].class);
			Book[] books = responseEntity.getBody();
			return books;
		} catch (RestClientException e) {
			response.sendError(404, "Invalid URL");
			return null;
		}
	}
	
	/* Read one book by id */
	@RequestMapping(value = "/lms/books/{bookId}", method = RequestMethod.GET)
	public Book getBookById(@PathVariable int bookId, HttpServletResponse response) throws IOException {
		logger.info("Getting book with id: " + bookId);
		try {
			ResponseEntity<Book> responseEntity = restTemplate.getForEntity("http://localhost:8083/books/" + bookId, Book.class);
			Book book = responseEntity.getBody();
			return book;
		} catch (RestClientException e) {
			response.sendError(404, "Invalid bookId, book does not exist in the database.");
			return null;
		}
	}
	
	/* Add one book with all its connections */
	@RequestMapping(value = "/lms/book", method = RequestMethod.POST, consumes = {"application/xml", "application/json"})
	public HttpHeaders addBook(@RequestBody Book book, HttpServletResponse response) throws IOException {
		logger.info("Adding book with title: " + book.getTitle() + ", pubId: " + book.getPublisher().getPubId());
		try {
			ResponseEntity<Book> responseEntity = restTemplate.postForEntity("http://localhost:8083/book/", book, Book.class);
			response.setStatus(201);
			return responseEntity.getHeaders();
		} catch (RestClientException e) {
			response.sendError(400, "Invalid request caused by invalid body parameters.");
			return null;
		}
	}
	
	/* Delete one book with the given id */
	@RequestMapping(value = "/lms/books/{bookId}", method = RequestMethod.DELETE)
	public void deleteBook(@PathVariable int bookId, HttpServletResponse response) throws IOException {
		logger.info("Deleting book with id " + bookId);
		try {
			restTemplate.delete(("http://localhost:8083/books/" +  bookId));
			response.setStatus(204);
		} catch (RestClientException e) {
			response.sendError(404, "Invalid id, book does not exist in database.");
		}
	}
	
	/* Update one book title with the given id */
	@RequestMapping(value = "/lms/books/{bookId}", method = RequestMethod.PUT, consumes = {"application/xml", "application/json"})
	public HttpHeaders updateBook(@RequestHeader HttpHeaders headers, @RequestBody Book book, @PathVariable int bookId, HttpServletResponse response) throws IOException {
		logger.info("Updating book (id: " + bookId + ") with new title: " + book.getTitle());
		try {
			HttpEntity<Book> requestUpdate = new HttpEntity<>(book, headers);
			ResponseEntity<Book> responseEntity = restTemplate.exchange("http://localhost:8083/books/" + bookId, HttpMethod.PUT, requestUpdate, Book.class);
			return responseEntity.getHeaders();
		} catch (RestClientException e) {
			if (e.getMessage().equals("404 null")) {
				response.sendError(404, "Invalid id, book does not exist in database.");
			}
			if (e.getMessage().equals("400 null")) {
				response.sendError(400, "Invalid request caused by invalid body parameters.");		
			}
			return null;
		}
	}
	
}
