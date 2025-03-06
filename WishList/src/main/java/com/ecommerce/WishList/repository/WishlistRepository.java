package com.ecommerce.WishList.repository;

import com.ecommerce.WishList.entity.Product;
import com.ecommerce.WishList.entity.User;
import com.ecommerce.WishList.entity.WishList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<WishList, Integer> {

    //  Find wishlist items by user id
    //List<WishList> findByUserId(Integer userId);
    Page<WishList> findByUserId(Integer userId, Pageable pageable);

    //  Find a specific wishlist item by user and product
    Optional<WishList> findByUserIdAndProductId(Integer userId, Integer productId);

    //  Check if a product already exists in the users wishlist
    boolean existsByUserAndProduct(User user, Product product);

}