package com.ecommerce.WishList.service;

import com.ecommerce.WishList.Exception.*;
import com.ecommerce.WishList.entity.Product;
import com.ecommerce.WishList.entity.User;
import com.ecommerce.WishList.entity.WishList;
import com.ecommerce.WishList.repository.ProductRepository;
import com.ecommerce.WishList.repository.UserRepository;
import com.ecommerce.WishList.repository.WishlistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WishlistServiceTest {

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private WishlistService wishlistService;

    private User user;
    private Product product;
    private WishList wishList;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1);
        user.setFirstName("megha");
        user.setLastName("biradar");
        user.setEmail("megha.b@.com");

        product = new Product();
        product.setId(1);
        product.setName("Smartphone");
        product.setDescription("Latest model");
        product.setPrice(599.99);
        product.setStock(10);

        wishList = new WishList(user, product);
    }

    // +ve test case :get wishlist items Successfully
    @Test
    void getWishlistItems() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<WishList> mockPage = new PageImpl<>(List.of(wishList));
        when(wishlistRepository.findByUserId(user.getId(), pageable)).thenReturn(mockPage);
        Page<WishList> result = wishlistService.getWishlistItems(user.getId(), 0, 10);
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        verify(wishlistRepository, times(1)).findByUserId(user.getId(), pageable);
    }

    // -ve test case :get wishlist items when empty
    @Test
    void getWishlistItems_NotFound() {
        Pageable pageable = PageRequest.of(0, 10);
        when(wishlistRepository.findByUserId(user.getId(), pageable)).thenReturn(Page.empty());

        assertThrows(WishlistNotFoundException.class, () -> wishlistService.getWishlistItems(user.getId(), 0, 10));
        verify(wishlistRepository, times(1)).findByUserId(user.getId(), pageable);
    }

    // +ve test case : add product to wishlist Successfully
    @Test
    void addToWishlist() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(wishlistRepository.existsByUserAndProduct(user, product)).thenReturn(false);
        when(wishlistRepository.save(any(WishList.class))).thenReturn(wishList);

        WishList result = wishlistService.addToWishlist(user.getId(), product.getId());

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(product, result.getProduct());
        verify(wishlistRepository, times(1)).save(any(WishList.class));
    }

    // -ve test case :add product to wishlist - product already exists
    @Test
    void addToWishlist_Duplicate() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(wishlistRepository.existsByUserAndProduct(user, product)).thenReturn(true);

        assertThrows(DuplicateWishlistItemException.class, () -> wishlistService.addToWishlist(user.getId(), product.getId()));
        verify(wishlistRepository, never()).save(any(WishList.class));
    }

    // -ve test case :add product to wishlist - User Not Found
    @Test
    void addToWishlist_UserNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> wishlistService.addToWishlist(user.getId(), product.getId()));
        verify(productRepository, never()).findById(anyInt());
        verify(wishlistRepository, never()).save(any(WishList.class));
    }

    // -ve test case :add product to wishlist -  Product Not Found
    @Test
    void addToWishlist_ProductNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(productRepository.findById(product.getId())).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> wishlistService.addToWishlist(user.getId(), product.getId()));
        verify(wishlistRepository, never()).save(any(WishList.class));
    }

    // -ve test case :add product to wishlist -  Product Out of Stock
    @Test
    void addToWishlist_ProductOutOfStock() {
        product.setStock(0);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        assertThrows(ProductOutOfStockException.class, () -> wishlistService.addToWishlist(user.getId(), product.getId()));
        verify(wishlistRepository, never()).save(any(WishList.class));
    }

    // +ve test case   remove item from wishlist Successfully
    @Test
    void removeFromWishlist() {
        when(wishlistRepository.findByUserIdAndProductId(user.getId(), product.getId()))
                .thenReturn(Optional.of(wishList));

        assertDoesNotThrow(() -> wishlistService.removeFromWishlist(user.getId(), product.getId()));

        verify(wishlistRepository, times(1)).delete(wishList);
    }

    // -ve test case   remove item from wishlist - Item Not Found
    @Test
    void removeFromWishlist_NotFound() {
        when(wishlistRepository.findByUserIdAndProductId(user.getId(), product.getId())).thenReturn(Optional.empty());

        assertThrows(WishlistNotFoundException.class, () -> wishlistService.removeFromWishlist(user.getId(), product.getId()));
        verify(wishlistRepository, never()).delete(any(WishList.class));
    }
}
