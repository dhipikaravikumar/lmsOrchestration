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

import com.gcit.springboot.lms.entity.Branch;
/**
 * Handles requests for the branch routes.
 */
@RestController
public class BranchController {
	
	@Autowired
	RestTemplate restTemplate;
	
	private static final Logger logger = LoggerFactory.getLogger(BranchController.class);

	/* Read all branches */
	@RequestMapping(value = "/lms/branches", method = RequestMethod.GET)
	public Branch[] getAllBranches(HttpServletResponse response) throws IOException {
		logger.info("Getting all branches"); 
		try {
			ResponseEntity<Branch[]> responseEntity = restTemplate.getForEntity("http://localhost:8081/branches", Branch[].class);
			Branch[] branches = responseEntity.getBody();
			return branches;
		} catch (RestClientException e) {
			response.sendError(404, "Invalid URL");
			return null;
		}
		
	}
	
	/* Read one branch by id */
	@RequestMapping(value = "/lms/branches/{branchId}", method = RequestMethod.GET)
	public Branch getBranchById(@PathVariable int branchId, HttpServletResponse response) throws IOException {
		logger.info("Getting branch with id: " + branchId);
		try {
			ResponseEntity<Branch> responseEntity = restTemplate.getForEntity("http://localhost:8081/branches/" + branchId, Branch.class);
			Branch branch = responseEntity.getBody();
			return branch;
		} catch (RestClientException e) {
			response.sendError(404, "Invalid branch id, branch does not exist in database.");
			return null;
		}
	}
	
	/* Save one branch and returns the auto-generated id */
	@RequestMapping(value = "/lms/branch", method = RequestMethod.POST)
	public HttpHeaders saveBranchWithId(@RequestBody Branch branch, HttpServletResponse response) throws IOException {
		logger.info("Saving branch with name: " + branch.getBranchName());
		try {
			ResponseEntity<Branch> responseEntity = restTemplate.postForEntity("http://localhost:8083/branch/", branch, Branch.class);
			response.setStatus(201);
			return responseEntity.getHeaders();
		} catch (RestClientException e) {
			response.sendError(400, "Invalid request caused by invalid body parameters.");
			return null;
		}
	}
	
	/* Delete one branch with the given id */
	@RequestMapping(value = "/lms/branches/{branchId}", method = RequestMethod.DELETE)
	public void deleteBranch(@PathVariable int branchId, HttpServletResponse response) throws IOException {
		logger.info("Deleting branch with id " + branchId);
		try {
			restTemplate.delete(("http://localhost:8083/branches/" +  branchId));
			response.setStatus(204);
		} catch (RestClientException e) {
			response.sendError(404, "Invalid branch id, branch does not exist in database.");
		}
	}
	
	/* Update one branch with the given id */
	@RequestMapping(value = "/lms/branches/{branchId}", method = RequestMethod.PUT)
	public HttpHeaders updateBranch(@RequestBody Branch branch, @RequestHeader HttpHeaders headers, @PathVariable int branchId, HttpServletResponse response) throws IOException {
		logger.info("Updating branch (id: " + branchId + ") with new name: " + branch.getBranchName() + ", new address: " + branch.getBranchAddress() );
		try {
			HttpEntity<Branch> requestUpdate = new HttpEntity<>(branch, headers);
			ResponseEntity<Branch> responseEntity = restTemplate.exchange("http://localhost:8081/branches/" + branchId, HttpMethod.PUT, requestUpdate, Branch.class);
			return responseEntity.getHeaders();
		} catch (RestClientException e) {
			if (e.getMessage().equals("404 null")) {
				response.sendError(404, "Invalid branch id, branch does not exist in the database.");
			}
			if (e.getMessage().equals("400 null")) {
				response.sendError(400, "Invalid request caused by invalid body parameters.");		
			}
			return null;
		}
	}
	
}
