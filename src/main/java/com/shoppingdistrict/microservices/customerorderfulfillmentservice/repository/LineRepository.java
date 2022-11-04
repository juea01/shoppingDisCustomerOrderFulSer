package com.shoppingdistrict.microservices.customerorderfulfillmentservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import com.shoppingdistrict.microservices.model.model.Cart;
import com.shoppingdistrict.microservices.model.model.Line;
import com.shoppingdistrict.microservices.model.model.Orders;

public interface LineRepository extends JpaRepository<Line, Integer> {

	
}
