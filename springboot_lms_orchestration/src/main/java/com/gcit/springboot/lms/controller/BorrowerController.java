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

import com.gcit.springboot.lms.entity.Borrower;
/**
 * Handles requests for the borrower routes.
 */
@RestController
public class BorrowerController {
	
	@Autowired
	RestTemplate restTemplate;
	
	private static final Logger logger = LoggerFactory.getLogger(BorrowerController.class);

	/* Read all borrowers */
	@RequestMapping(value = "/lms/borrowers", method = RequestMethod.GET)
	public Borrower[] getAllBorrowers(HttpServletResponse response) throws IOException {
		logger.info("Getting all borrowers"); 
		try {
			ResponseEntity<Borrower[]> responseEntity = restTemplate.getForEntity("http://localhost:8083/borrowers", Borrower[].class);
			Borrower[] borrowers = responseEntity.getBody();
			return borrowers;
		} catch (RestClientException e) {
			response.sendError(404, "Invalid URL");
			return null;
		}
	}
	
	/* Read one borrower by id */
	@RequestMapping(value = "/lms/borrowers/{cardNo}", method = RequestMethod.GET)
	public Borrower getBorrowerById(@PathVariable int cardNo, HttpServletResponse response) throws IOException {
		logger.info("Getting borrower with id: " + cardNo);
		try {
			ResponseEntity<Borrower> responseEntity = restTemplate.getForEntity("http://localhost:8083/borrowers/" + cardNo, Borrower.class);
			Borrower borrower = responseEntity.getBody();
			return borrower;
		} catch (RestClientException e) {
			response.sendError(404, "Invalid card number, borrower does not exist in the database.");
			return null;
		}
	}
	
	/* Save one borrower and returns the auto-generated id */
	@RequestMapping(value = "/lms/borrower", method = RequestMethod.POST , consumes = {"application/xml", "application/json"})
	public HttpHeaders saveBorrowerWithId(@RequestBody Borrower borrower, HttpServletResponse response) throws IOException {
		logger.info("Saving borrower with name: " + borrower.getName() + ", address: " + borrower.getAddress() + ", phone: " + borrower.getPhone());
		try {
			ResponseEntity<Borrower> responseEntity = restTemplate.postForEntity("http://localhost:8083/borrower", borrower, Borrower.class);
			response.setStatus(201);
			return responseEntity.getHeaders();
		} catch (RestClientException e) {
			response.sendError(400, "Invalid request caused by invalid body parameters.");
			return null;
		}
	}
	
	/* Delete one borrower with the given id */
	@RequestMapping(value = "/lms/borrowers/{cardNo}", method = RequestMethod.DELETE)
	public void deleteBorrower(@PathVariable int cardNo, HttpServletResponse response) throws IOException {
		logger.info("Deleting borrower with id " + cardNo);
		try {
			restTemplate.delete("http://localhost:8083/borrowers/" +  cardNo);
			response.setStatus(204);
		} catch (RestClientException e) {
			response.sendError(404, "Invalid card number, borrower does not exist in the database.");
		}
	}
	
	/* Update one borrower with the given id */
	@RequestMapping(value = "/lms/borrowers/{cardNo}", method = RequestMethod.PUT)
	public HttpHeaders updateAuthor(@RequestHeader HttpHeaders headers, @RequestBody Borrower borrower, @PathVariable int cardNo, HttpServletResponse response) throws IOException {
		logger.info("Updating borrower (id: " + cardNo + ") with new name: " + borrower.getName() + ", new address: " + borrower.getAddress() + ", new phone: " + borrower.getPhone());
		try {
			HttpEntity<Borrower> requestUpdate = new HttpEntity<>(borrower, headers);
			ResponseEntity<Borrower> responseEntity = restTemplate.exchange("http://localhost:8083/borrowers/" + cardNo, HttpMethod.PUT, requestUpdate, Borrower.class);
			return responseEntity.getHeaders();
		} catch (RestClientException e ) {
			if (e.getMessage().equals("404 null")) {
				response.sendError(404, "Invalid card number, borrower does not exist in the database.");
			}
			if (e.getMessage().equals("400 null")) {
				response.sendError(400, "Invalid request caused by invalid body parameters.");
			}
			return null;
		}
	}
	
}
