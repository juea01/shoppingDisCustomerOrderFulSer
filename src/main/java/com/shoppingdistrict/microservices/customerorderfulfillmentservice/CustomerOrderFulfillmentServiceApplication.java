package com.shoppingdistrict.microservices.customerorderfulfillmentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(
		{"com.shoppingdistrict.microservices.exceptionshandling",
		"com.shoppingdistrict.microservices.customerorderfulfillmentservice"})
@EntityScan(basePackages="com.shoppingdistrict.microservices.model.model")
public class CustomerOrderFulfillmentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomerOrderFulfillmentServiceApplication.class, args);
	}

}
