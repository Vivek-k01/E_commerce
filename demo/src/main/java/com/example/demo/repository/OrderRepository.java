package com.example.demo.repository;

import com.example.demo.model.OrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderDetails, Long> {

    // Step 1: Search Logic for Name or Mobile Number
    List<OrderDetails> findByCustomerNameContainingIgnoreCaseOrMobileNumberContaining(String name, String mobile);
    
}