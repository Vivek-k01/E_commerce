package com.example.demo.repository;

import com.example.demo.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // Ye line database se naam dhoondne mein madad karegi
    List<Product> findByNameContainingIgnoreCase(String name);
}