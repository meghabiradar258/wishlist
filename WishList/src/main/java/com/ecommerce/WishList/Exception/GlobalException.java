package com.ecommerce.WishList.Exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalException {

    private static final Logger logger = LoggerFactory.getLogger(GlobalException.class);

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException ex) {
        logger.warn("User not found: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<String> handleProductNotFoundException(ProductNotFoundException ex) {
        logger.warn("Product not found: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ProductOutOfStockException.class)
    public ResponseEntity<String> handleProductOutOfStockException(ProductOutOfStockException ex) {
        logger.warn("Product out of stock: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateWishlistItemException.class)
    public ResponseEntity<String> handleDuplicateWishlistItemException(DuplicateWishlistItemException ex) {
        logger.warn("Duplicate wishlist item: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(WishlistNotFoundException.class)
    public ResponseEntity<String> handleWishlistNotFoundException(WishlistNotFoundException ex) {
        logger.warn("Wishlist not found: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        logger.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        return new ResponseEntity<>("An error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
