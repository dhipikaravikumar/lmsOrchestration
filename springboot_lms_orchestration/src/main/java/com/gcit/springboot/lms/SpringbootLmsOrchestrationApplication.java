package com.gcit.springboot.lms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class SpringbootLmsOrchestrationApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootLmsOrchestrationApplication.class, args);
		System.out.println("Hello LMS!");
	}
	
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}
	
}


/*package com.gcit.springboot.lms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringbootLmsOrchestrationApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootLmsOrchestrationApplication.class, args);
		
	}
}
*/