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

import com.gcit.springboot.lms.entity.Copy;
/**
 * Handles requests for the copy routes.
 */
@RestController
public class CopyController {
	
	@Autowired
	RestTemplate restTemplate;
	
	private static final Logger logger = LoggerFactory.getLogger(CopyController.class);

	/* Read all copies */
	@RequestMapping(value = "/lms/copies", method = RequestMethod.GET)
	public Copy[] getAllCopys(HttpServletResponse response) throws IOException {
		logger.info("Getting all copies"); 
		try {
			ResponseEntity<Copy[]> responseEntity = restTemplate.getForEntity("http://localhost:8083/copies", Copy[].class);
			Copy[] copies = responseEntity.getBody();
			return copies;
		} catch (RestClientException e) {
			response.sendError(404, "Invalid URL");
			return null;
		}	
		
	}
	
	/* Read one copy by branchId and bookId */
	@RequestMapping(value = "/lms/copies/branches/{branchId}/books/{bookId}", method = RequestMethod.GET)
	public Copy getCopyById(@PathVariable int branchId, @PathVariable int bookId, HttpServletResponse response) throws IOException {
		logger.info("Getting copy with branchId: " + branchId + ", bookId: " + bookId);
		try {
			ResponseEntity<Copy> responseEntity = restTemplate.getForEntity("http://localhost:8083/copies/branches/" + branchId + "/books/" + bookId, Copy.class);
			Copy copy = responseEntity.getBody();
			return copy;
		} catch (RestClientException e) {
			response.sendError(404, "Invalid branch id and/or book id, copy entry does not exist in the database.");
			return null;
		}
	}

	/* Update one copy with the given branchId and bookId */
	@RequestMapping(value = "/lms/copies/branches/{branchId}/books/{bookId}", method = RequestMethod.PUT)
	public HttpHeaders updateCopy(@RequestHeader HttpHeaders headers, @RequestBody Copy copy, @PathVariable int branchId, @PathVariable int bookId, HttpServletResponse response) throws IOException {
		logger.info("Updating book (id: " + bookId + ") at branch (id: " + branchId + ") with " + copy.getNoCopies() + " copies");
		
		try {
			HttpEntity<Copy> requestUpdate = new HttpEntity<>(copy, headers);
			ResponseEntity<Copy> responseEntity = restTemplate.exchange("http://localhost:8081/copies/branches/" + branchId 
					+ "/books/" + bookId , HttpMethod.PUT, requestUpdate, Copy.class);
			return responseEntity.getHeaders();
		} catch (RestClientException e) {
			if (e.getMessage().equals("404 null")){
				response.sendError(404, "Invalid request caused by invalid path parameters.");
			}
			if (e.getMessage().equals("400 null")) {
				response.sendError(400, "Invalid request caused by invalid body parameters.");
			}
			return null;
		}
	}
	
}
