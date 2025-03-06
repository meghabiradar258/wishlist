package com.ecommerce.WishList.Exception;

public class DuplicateWishlistItemException extends RuntimeException {
    public DuplicateWishlistItemException(String message) {
        super(message);
    }
}