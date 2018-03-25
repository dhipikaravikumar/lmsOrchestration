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

import com.gcit.springboot.lms.entity.Author;





/**
 * Handles requests for the author routes.
 */
@RestController
public class AuthorController {
	
	@Autowired
	RestTemplate restTemplate;
	
	private static final Logger logger = LoggerFactory.getLogger(AuthorController.class);

	/* Read all authors */
	@RequestMapping(value = "/lms/authors", method = RequestMethod.GET)
	public Author[] getAllAuthors(HttpServletResponse response) throws IOException {
		logger.info("Getting all authors"); 
		try {
			ResponseEntity<Author[]> responseEntity = restTemplate.getForEntity("http://localhost:8083/authors", Author[].class);
			Author[] authors = responseEntity.getBody();
			return authors;
		} catch (RestClientException e) {
			response.sendError(404, "Invalid URL");
			return null;
		}

	}
	
	/* Read one author by id */
	@RequestMapping(value = "/lms/authors/{authorId}", method = RequestMethod.GET)
	public Author getAuthorById(@PathVariable int authorId, HttpServletResponse response) throws IOException {
		logger.info("Getting author with id: " + authorId);
		try {
			ResponseEntity<Author> responseEntity = restTemplate.getForEntity("http://localhost:8083/authors/" + authorId , Author.class);
			Author author = responseEntity.getBody();
			return author;
		} catch (RestClientException e) {
			response.sendError(404, "Invalid id, author does not exist in the database.");
			return null;
		}
	}
	
	/* Save one author and returns the auto-generated id */
	@RequestMapping(value = "/lms/author", method = RequestMethod.POST)
	public HttpHeaders saveAuthorWithId(@RequestBody Author author, HttpServletResponse response) throws IOException {
		logger.info("Saving author with name: " + author.getAuthorName());
		try {
			ResponseEntity<Author> responseEntity = restTemplate.postForEntity("http://localhost:8083/author/", author, Author.class);
			response.setStatus(201);
			return responseEntity.getHeaders();
		} catch (RestClientException e) {
			response.sendError(400, "Invalid request caused by invalid body parameters.");
			return null;
		}
	}
	
	/* Delete one author with the given id */
	@RequestMapping(value = "/lms/authors/{authorId}", method = RequestMethod.DELETE)
	public void deleteAuthor(@PathVariable int authorId , HttpServletResponse response) throws IOException {
		logger.info("Deleting author with id " + authorId);
		try {
			restTemplate.delete(("http://localhost:8083/authors/" +  authorId));
			response.setStatus(204);
		} catch (RestClientException e) {
			response.sendError(404, "Invalid id, author does not exist in database.");
		}
		
	}
	
	/* Update one author with the given id */
	@RequestMapping(value = "/lms/authors/{authorId}", method = RequestMethod.PUT, consumes = {"application/xml", "application/json"})
	public HttpHeaders updateAuthor(@RequestBody Author author, @PathVariable int authorId, @RequestHeader HttpHeaders headers, HttpServletResponse response) throws IOException {
		logger.info("Updating author (id: " + authorId + ") with new name: " + author.getAuthorName());
		try {
			HttpEntity<Author> requestUpdate = new HttpEntity<>(author, headers);
			ResponseEntity<Author> responseEntity = restTemplate.exchange("http://localhost:8083/authors/" + authorId, HttpMethod.PUT, requestUpdate, Author.class);
			return responseEntity.getHeaders();
		} catch (RestClientException e) {
			if (e.getMessage().equals("404 null")) {
				response.sendError(404, "Invalid id, author does not exist in database.");
			}
			if (e.getMessage().equals("400 null")) {
				response.sendError(400, "Invalid request caused by invalid body parameters.");		
			}
			return null;
		}
	}
	
}
