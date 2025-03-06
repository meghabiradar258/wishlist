package com.ecommerce.WishList.repository;


import com.ecommerce.WishList.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

}
