package com.ecommerce.WishList.service;

import com.ecommerce.WishList.Exception.*;
import com.ecommerce.WishList.entity.Product;
import com.ecommerce.WishList.entity.User;
import com.ecommerce.WishList.entity.WishList;
import com.ecommerce.WishList.repository.ProductRepository;
import com.ecommerce.WishList.repository.UserRepository;
import com.ecommerce.WishList.repository.WishlistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WishlistService {

    private static final Logger logger = LoggerFactory.getLogger(WishlistService.class);

    @Autowired
    private WishlistRepository wishlistRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Cacheable(value = "wishlistCache", key = "#userId")
    public Page<WishList> getWishlistItems(Integer userId, int page, int size) {
        logger.info("Fetching wishlist items for userId: {}", userId);
        Page<WishList> wishlist = wishlistRepository.findByUserId(userId, PageRequest.of(page, size));
        if (wishlist.isEmpty()) {
            logger.warn("Wishlist is empty for userId: {}", userId);
            throw new WishlistNotFoundException("Wishlist not found for user: " + userId);
        }
        logger.info("Successfully fetched wishlist items for userId: {}", userId);
        return wishlist;
    }

    @CacheEvict(value = "wishlistCache", key = "#userId + '-*'")
    public WishList addToWishlist(Integer userId, Integer productId) {
        logger.info("Attempting to add productId: {} to wishlist for userId: {}", productId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", userId);
                    return new UserNotFoundException("User not found with ID: " + userId);
                });

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    logger.error("Product not found with ID: {}", productId);
                    return new ProductNotFoundException("Product not found with ID: " + productId);
                });

        if (product.getStock() <= 0) {
            logger.warn("Product ID {} is out of stock", productId);
            throw new ProductOutOfStockException("Product ID " + productId + " is out of stock");
        }

        if (wishlistRepository.existsByUserAndProduct(user, product)) {
            logger.warn("Product ID {} already exists in wishlist for userId: {}", productId, userId);
            throw new DuplicateWishlistItemException("Product already exists in wishlist");
        }

        WishList wishlist = new WishList();
        wishlist.setUser(user);
        wishlist.setProduct(product);

        WishList savedWishlist = wishlistRepository.save(wishlist);
        logger.info("Successfully added productId: {} to wishlist for userId: {}", productId, userId);

        return savedWishlist;
    }


    //  Remove Item from Wishlist
    @CacheEvict(value = "wishlistCache", key = "#userId + '-*'")
    public void removeFromWishlist(Integer userId, Integer productId) {
        logger.info("Attempting to remove productId: {} from wishlist for userId: {}", productId, userId);

        WishList wishlist = wishlistRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> {
                    logger.warn("Wishlist item not found for productId: {} and userId: {}", productId, userId);
                    return new WishlistNotFoundException("Wishlist item not found for product ID: " + productId);
                });

        wishlistRepository.delete(wishlist);
        logger.info("Successfully removed productId: {} from wishlist for userId: {}", productId, userId);
    }
}