package com.ecommerce.WishList.controller;

import com.ecommerce.WishList.entity.WishList;
import com.ecommerce.WishList.service.WishlistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wishlist")
public class WishlistController {

    private static final Logger logger = LoggerFactory.getLogger(WishlistController.class);

    @Autowired
    private WishlistService wishlistService;

    // Fetch Wishlist Items for a User
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<WishList>> getUserWishlist(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        logger.info("Fetching wishlist for userId: {}, page: {}, size: {}", userId, page, size);

        Page<WishList> wishlistPage = wishlistService.getWishlistItems(userId, page, size);

        logger.info("Successfully fetched wishlist for userId: {}", userId);
        List<WishList> wishlist = wishlistPage.getContent();
        return ResponseEntity.ok(wishlist);
    }

    // Add Product to Wishlist
    @PostMapping("/add/{userId}/{productId}")
    public ResponseEntity<WishList> addToWishlist(@PathVariable Integer userId, @PathVariable Integer productId) {

        logger.info("Adding productId: {} to wishlist for userId: {}", productId, userId);

        WishList wishlistItem = wishlistService.addToWishlist(userId, productId);

        logger.info("Successfully added productId: {} to wishlist for userId: {}", productId, userId);
        return ResponseEntity.ok(wishlistItem);
    }

    // Remove Product from Wishlist
    @DeleteMapping("/remove/{userId}/{productId}")
    public ResponseEntity<String> removeFromWishlist(@PathVariable Integer userId, @PathVariable Integer productId) {

        logger.info("Removing productId: {} from wishlist for userId: {}", productId, userId);

        wishlistService.removeFromWishlist(userId, productId);

        logger.info("Successfully removed productId: {} from wishlist for userId: {}", productId, userId);
        return ResponseEntity.ok("Product removed from wishlist successfully");
    }
}
