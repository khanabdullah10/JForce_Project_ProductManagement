package com.productmanagement.repository;

import com.productmanagement.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryId(Long categoryId);
    List<Product> findByEnabledTrue();
    List<Product> findByCategoryIdAndEnabledTrue(Long categoryId);
}

