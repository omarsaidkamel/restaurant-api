package com.restaurant.repository;

import com.restaurant.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
   List<Product> findByStockLessThanEqual(Integer stock);

   Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

   Page<Product> findByCategoryIgnoreCase(String category, Pageable pageable);

   Page<Product> findByNameContainingIgnoreCaseAndCategoryIgnoreCase(
           String name,
           String category,
           Pageable pageable
   );
}