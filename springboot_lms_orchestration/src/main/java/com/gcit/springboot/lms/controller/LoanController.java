package com.gcit.springboot.lms.controller;

import java.io.IOException;
import java.sql.Timestamp;

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

import com.gcit.springboot.lms.entity.Loan;
/**
 * Handles requests for the loan routes.
 */
@RestController
public class LoanController {
	
	@Autowired
	RestTemplate restTemplate;
	
	private static final Logger logger = LoggerFactory.getLogger(LoanController.class);

	/* Read all loans */
	@RequestMapping(value = "/lms/loans", method = RequestMethod.GET)
	public Loan[] getAllLoans(HttpServletResponse response) throws IOException {
		logger.info("Getting all loans"); 
		try {
			ResponseEntity<Loan[]> responseEntity = restTemplate.getForEntity("http://localhost:8083/loans", Loan[].class);
			Loan[] loans = responseEntity.getBody();
			return loans;
		} catch (RestClientException e) {
			e.printStackTrace();
			response.sendError(404, "Invalid URL");
			return null;
		}
		
	}
	
	/* Get one loan */ 
	@RequestMapping(value = "/lms/loans/borrowers/{cardNo}/branches/{branchId}/books/{bookId}", method = RequestMethod.GET)
	public Loan getLoan(@PathVariable int cardNo, @PathVariable int branchId, @PathVariable int bookId, HttpServletResponse response ) throws IOException {
		//Timestamp di = new Timestamp(Long.parseLong(dateIn));
		logger.info("Getting loan with cardNo: " + cardNo + ", branchId: " + branchId + ", bookId: " + bookId);
		
		try {
			ResponseEntity<Loan> responseEntity = restTemplate.getForEntity("http://localhost:8083/loans/borrowers/" + cardNo 
					+ "/branches/" + branchId + "/books/" + bookId, Loan.class);
			Loan loan = responseEntity.getBody();
			return loan;
		} catch (RestClientException e) {
			response.sendError(404, "Invalid path parameters, loan does not exist in the database.");
			return null;
		}
	}
	
	/* Read all loans of by one borrower from cardNo */
	@RequestMapping(value = "/lms/loans/borrowers/{cardNo}", method = RequestMethod.GET)
	public Loan[] getPendingLoansByCardNo(@PathVariable int cardNo , HttpServletResponse response) throws IOException {
		logger.info("Getting all pending loans with cardNo: " + cardNo);
		try {
			ResponseEntity<Loan[]> responseEntity = restTemplate.getForEntity("http://localhost:8082/loans/borrowers/" + cardNo, Loan[].class);
			Loan[] loans = responseEntity.getBody();
			return loans;
		} catch (RestClientException e) {
			response.sendError(404, "Invalid card number, either borrower does not have any pending loans or borrower does not exist in the database.");
			return null;
		}
	}
	
	/* Borrow book with bookId and branchId */
	@RequestMapping(value = "/lms/loans/borrowers/{cardNo}/branches/{branchId}/books/{bookId}", method = RequestMethod.POST)
	public HttpHeaders borrowBook(@PathVariable int cardNo, @PathVariable int branchId, @PathVariable int bookId, HttpServletResponse response) throws IOException {
		
		try {
			ResponseEntity<Loan> responseEntity = restTemplate.postForEntity("http://localhost:8082/loans/borrowers/" + cardNo 
					+ "/branches/" + branchId + "/books/" + bookId, null, Loan.class);
			response.setStatus(201);
			return responseEntity.getHeaders();
		} catch (RestClientException e) {
			if (e.getMessage().equals("404 null")) {
				response.sendError(404, "Invalid request caused by invalid path parameters.");
			}
			if (e.getMessage().equals("400 null")) {
				response.sendError(400, "Book already borrowed, please return before borrowing again.");		
			}
			return null;
		}
	}
	
	/* Return book with bookId and branchId */
	@RequestMapping(value = "/lms/loans/borrowers/{cardNo}/branches/{branchId}/books/{bookId}/checkin", method = RequestMethod.PUT)
	public ResponseEntity<String> returnBook(@RequestHeader HttpHeaders headers, @PathVariable int cardNo, @PathVariable int branchId, @PathVariable int bookId, HttpServletResponse response) throws IOException {
		logger.info("Returning book of cardNo: " + cardNo + ", branch: " +  branchId + ", book: " + bookId);
		try {
			HttpEntity<String> requestUpdate = new HttpEntity<>(headers);
			ResponseEntity<String> responseEntity = restTemplate.exchange("http://localhost:8082/loans/borrowers/" + cardNo 
					+ "/branches/" + branchId + "/books/" + bookId, HttpMethod.PUT,requestUpdate,String.class);
			return responseEntity;
		} catch (RestClientException e) {
			e.printStackTrace();
			if (e.getMessage().equals("404 null")) {
				response.sendError(404, "Invalid request caused by invalid path parameters.");
			}
			return null;
		}
	}
	
	/* Update one loan with new due date */
	@RequestMapping(value = "/lms/loans/borrowers/{cardNo}/branches/{branchId}/books/{bookId}", method = RequestMethod.PUT)
	public HttpHeaders updateLoan(@RequestHeader HttpHeaders headers, @RequestBody Loan loan, @PathVariable int cardNo, @PathVariable int branchId, @PathVariable int bookId, HttpServletResponse response) throws IOException {
		logger.info("Updating loan with new due date: " + loan.getDueDate());
		try {
			HttpEntity<Loan> requestUpdate = new HttpEntity<>(loan, headers);
			ResponseEntity<Loan> responseEntity = restTemplate.exchange("http://localhost:8083/loans/borrowers/" + cardNo 
					+ "/branches/" + branchId + "/books/" + bookId, HttpMethod.PUT, requestUpdate, Loan.class);
			return responseEntity.getHeaders();
		} catch (NumberFormatException e) {
			if (e.getMessage().equals("404 null")) {
				response.sendError(404, "Invalid request caused by invalid path parameters.");
			}
			if (e.getMessage().equals("500 null")) {
				response.sendError(500, "Invalid request caused by invalid path parameters.");
			}
			if (e.getMessage().equals("400 null")) {
				response.sendError(400, "Number format exception");
			}
			return null;
		}
	}
}
