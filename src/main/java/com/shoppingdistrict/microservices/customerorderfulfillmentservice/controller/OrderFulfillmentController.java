package com.shoppingdistrict.microservices.customerorderfulfillmentservice.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;
import javax.ws.rs.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.shoppingdistrict.microservices.customerorderfulfillmentservice.configuration.Configuration;
import com.shoppingdistrict.microservices.customerorderfulfillmentservice.repository.CartRepository;
import com.shoppingdistrict.microservices.customerorderfulfillmentservice.repository.LineRepository;
import com.shoppingdistrict.microservices.customerorderfulfillmentservice.repository.OrderFulfillmentRepository;
import com.shoppingdistrict.microservices.model.model.Cart;
import com.shoppingdistrict.microservices.model.model.Line;
import com.shoppingdistrict.microservices.model.model.Orders;
import com.shoppingdistrict.microservices.model.model.Users;

@RestController
@RequestMapping("/customer-order-fulfillment-service")
public class OrderFulfillmentController {

	private Logger logger = LoggerFactory.getLogger(OrderFulfillmentController.class);

	@Autowired
	private Environment environment;

	@Autowired
	private OrderFulfillmentRepository repository;
	
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private LineRepository lineRepository;
	
	
	@Autowired
	private Configuration configuration;

	@Context
	private SecurityContext sc;
	
	// Get /order
	// retrieveAllOrders
	@GetMapping("/orders")
	public List<Orders> retrieveAllOrders() {
		logger.info("Entry to retrieveAllOrders");
		List<Orders> orders = repository.findAll();
		logger.info("Size of all orders", orders.size());
		logger.info("Returning orders and exiting from retrieveAllOrders");
		return orders;
	}

	// retrieveOrder
	@GetMapping("/orders/{id}")
	@PostAuthorize("#username == authentication.name")
	//TODO: Need to look for this, having authentication.name is giving oauth2 keycloak user id instead of name
	public Orders retrieveOrderByCustomerId(@PathVariable Integer id) {
		logger.info("Entry to retrieveOrder");

		logger.info("Port used {}", environment.getProperty("local.server.port"));
//		logger.info("Minimum from configuration {}", configuration.getMinimum());
//		
		
		Orders order = repository.findById(id).get();
		order.getUser();	

		logger.info("Returning order {} and exiting from retrieveOrder", order);
		return order;
	}
	
	@GetMapping("/orders/{id}/customers")
	public Users retrieveCustomerOfOrder(@PathVariable Integer id) {
		logger.info("Entry to retrieveCustomerOfOrder");
		
		Users customer = repository.findById(id).get().getUser();
		
		logger.info("Returning Customer {} of order id {} and exiting from retrieveCustomerOfOrder", customer, id);
		return customer;
	}

	/**
	 * TODO: Adding @Cascade({CascadeType.ALL}) in Orders (for Cart) and Cart class (for line) work only
	 * for cart. For lines cart id is null though on log file insert for cart is before insert statement for lines.
	 * Hence manually persisting Order object and it's childs.
	 * @param order
	 * @return
	 */
	@PostMapping("/orders")
	public ResponseEntity<Object> createOrder(@Valid @RequestBody Orders order) {
		logger.info("Entry to createOrder");
		
		logger.info("Order to be created {}", order);
		
		
		Cart savedCart = cartRepository.saveAndFlush(order.getCart());		
		
		List<Line> lines = order.getCart().getLines();
		for(Line l: lines) {
			l.setCart(savedCart);
			lineRepository.saveAndFlush(l);
		}
		
		order.setCart(savedCart);
		Orders savedOrder = repository.saveAndFlush(order);
		
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").
			buildAndExpand(savedOrder.getId()).toUri();
		
		logger.info("Returning newly created order {} ", savedOrder.getId());
		
		return ResponseEntity.created(location).build();
		
		
	}
	
	//TODO: Shall try and catch here or let error handling component to handle
	@PutMapping("/orders/{id}")
	public ResponseEntity<Object> updateOrder(@Valid @RequestBody Orders order, @PathVariable Integer id) {
		logger.info("Entry to updateOrder");

		logger.info("Order to be updated {}", order.getId());
		
		Optional<Orders> existingOrder = repository.findById(id);
		existingOrder.get().setShipped(order.isShipped());
		
		
		Orders updatedOrder = repository.saveAndFlush(existingOrder.get());
		

		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(updatedOrder.getId()).toUri();

		logger.info("Returning newly updated order id {} and exiting from updateOrder", updatedOrder.getId(),
				updatedOrder);

		return ResponseEntity.created(location).build();

	}

	
	
	// retrieveOrder
		@GetMapping("/")
		public String defultRoute() {
			logger.info("Entry to defultRoute");

			logger.info("Port used {}", environment.getProperty("local.server.port"));
//			logger.info("Minimum from configuration {}", configuration.getMinimum());
//			
			
			String message = "This is default route of Order fulfillment service. Please see API documentation for other routes.";

			logger.info("Exiting from defultRoute with message {}", message);
			return message;
		}
		

		@GetMapping("/publicinfo")
		public String getPublicInfo() {
			return "this is public info, no security needed";
		}


}
