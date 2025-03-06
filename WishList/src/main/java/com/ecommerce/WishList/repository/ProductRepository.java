package com.ecommerce.WishList.repository;

import com.ecommerce.WishList.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {

}