package com.ecommerce.WishList.Exception;

public class WishlistNotFoundException extends RuntimeException {
    public WishlistNotFoundException(String message) {
        super(message);
    }
}