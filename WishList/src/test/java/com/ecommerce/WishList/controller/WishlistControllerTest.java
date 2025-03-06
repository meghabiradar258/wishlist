package com.ecommerce.WishList.controller;

import com.ecommerce.WishList.entity.WishList;
import com.ecommerce.WishList.service.WishlistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WishlistControllerTest {

    @Mock
    private WishlistService wishlistService;

    @InjectMocks
    private WishlistController wishlistController;

    private WishList wishListItem;

    @BeforeEach
    void setUp() {
        wishListItem = new WishList();
        wishListItem.setId(1);
    }

    // positive test case for fetching wishlist
    @Test
    void getWishlistSuccess() {
        int userId = 1;
        int page = 0;
        int size = 5;
        List<WishList> wishlist = Arrays.asList(wishListItem);
        Page<WishList> wishlistPage = new PageImpl<>(wishlist, PageRequest.of(page, size), wishlist.size());

        when(wishlistService.getWishlistItems(userId, page, size)).thenReturn(wishlistPage);

        ResponseEntity<List<WishList>> response = wishlistController.getUserWishlist(userId, page, size);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(wishlistService, times(1)).getWishlistItems(userId, page, size);
    }

    // negative test case for fetching wishlist with invalid user id
    @Test
    void getWishlistFailure() {
        int userId = -1;
        int page = 0;
        int size = 5;

        when(wishlistService.getWishlistItems(userId, page, size)).thenReturn(Page.empty());

        ResponseEntity<List<WishList>> response = wishlistController.getUserWishlist(userId, page, size);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isEmpty());
        verify(wishlistService, times(1)).getWishlistItems(userId, page, size);
    }

    // positive test case for adding item to wishlist
    @Test
    void addWishlistSuccess() {
        int userId = 1;
        int productId = 101;

        when(wishlistService.addToWishlist(userId, productId)).thenReturn(wishListItem);

        ResponseEntity<WishList> response = wishlistController.addToWishlist(userId, productId);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        verify(wishlistService, times(1)).addToWishlist(userId, productId);
    }

    // negative test case for adding item to wishlist with invalid user id
    @Test
    void addWishlistFailure() {
        int userId = -1;
        int productId = 101;

        when(wishlistService.addToWishlist(userId, productId)).thenThrow(new RuntimeException("Invalid user ID"));

        Exception exception = assertThrows(RuntimeException.class, () -> wishlistController.addToWishlist(userId, productId));

        assertEquals("Invalid user ID", exception.getMessage());
        verify(wishlistService, times(1)).addToWishlist(userId, productId);
    }

    // positive test case for removing item from wishlist
    @Test
    void removeWishlistSuccess() {
        int userId = 1;
        int productId = 101;

        doNothing().when(wishlistService).removeFromWishlist(userId, productId);

        ResponseEntity<String> response = wishlistController.removeFromWishlist(userId, productId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Product removed from wishlist successfully", response.getBody());
        verify(wishlistService, times(1)).removeFromWishlist(userId, productId);
    }

    // negative test case for removing item from wishlist with invalid product id
    @Test
    void removeWishlistFailure() {
        int userId = 1;
        int productId = -1;

        doThrow(new RuntimeException("Invalid product ID"))
                .when(wishlistService).removeFromWishlist(userId, productId);

        Exception exception = assertThrows(RuntimeException.class, () -> wishlistController.removeFromWishlist(userId, productId));

        assertEquals("Invalid product ID", exception.getMessage());
        verify(wishlistService, times(1)).removeFromWishlist(userId, productId);
    }
}
