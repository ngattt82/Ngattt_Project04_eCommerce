package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CartControllerTest {
    private static final String USERNAME = "test";
    private static final long ITEM_ID = 0L;
    private static final BigDecimal ITEM_PRICE = new BigDecimal("2.99");
    private static final BigDecimal UPDATED_TOTAL = new BigDecimal("5.98");
    private static final BigDecimal EMPTY_CART_TOTAL = BigDecimal.ZERO;

    private CartController cartController;
    private final UserRepository userRepo = mock(UserRepository.class);
    private final CartRepository cartRepo = mock(CartRepository.class);
    private final ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "cartRepository", cartRepo);
        TestUtils.injectObjects(cartController, "userRepository", userRepo);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
    }

    private User createTestUser() {
        User user = new User();
        user.setUsername(USERNAME);
        return user;
    }

    private Cart createTestCart(User user, List<Item> items, BigDecimal total) {
        Cart cart = new Cart();
        cart.setId(ITEM_ID);
        cart.setItems(items);
        cart.setTotal(total);
        cart.setUser(user);
        user.setCart(cart);
        return cart;
    }

    private ModifyCartRequest createModifyCartRequest(long itemId, int quantity) {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(itemId);
        request.setQuantity(quantity);
        request.setUsername(USERNAME);
        return request;
    }

    @Test
    public void addToCart() {
        User user = createTestUser();
        Item item = TestUtils.getItem0();
        Cart cart = createTestCart(user, new ArrayList<>(List.of(item)), ITEM_PRICE);

        when(userRepo.findByUsername(USERNAME)).thenReturn(user);
        when(itemRepository.findById(ITEM_ID)).thenReturn(java.util.Optional.of(item));

        ModifyCartRequest request = createModifyCartRequest(ITEM_ID, 1);

        ResponseEntity<Cart> response = cartController.addToCart(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Cart retrievedCart = response.getBody();
        assertNotNull(retrievedCart);
        assertEquals(ITEM_ID, retrievedCart.getId().longValue());
        assertEquals(2, retrievedCart.getItems().size());
        assertEquals(UPDATED_TOTAL, retrievedCart.getTotal());
        assertEquals(user, retrievedCart.getUser());
    }

    @Test
    public void testAddToCartNullUser() {
        Item item = TestUtils.getItem0();

        when(userRepo.findByUsername(USERNAME)).thenReturn(null);
        when(itemRepository.findById(ITEM_ID)).thenReturn(java.util.Optional.of(item));

        ModifyCartRequest request = createModifyCartRequest(ITEM_ID, 1);

        ResponseEntity<Cart> response = cartController.addToCart(request);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }


    public void removeFromCart0() {
        User user = createTestUser();
        Item item = TestUtils.getItem0();
        Cart cart = createTestCart(user, new ArrayList<>(List.of(item)), ITEM_PRICE);

        when(userRepo.findByUsername(USERNAME)).thenReturn(user);
        when(itemRepository.findById(ITEM_ID)).thenReturn(java.util.Optional.of(item));

        ModifyCartRequest request = createModifyCartRequest(ITEM_ID, 1);

        ResponseEntity<Cart> response = cartController.removeFromCart(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Cart retrievedCart = response.getBody();
        assertNotNull(retrievedCart);
        assertEquals(ITEM_ID, retrievedCart.getId().longValue());
        assertEquals(0, retrievedCart.getItems().size());
        assertEquals(EMPTY_CART_TOTAL, retrievedCart.getTotal());
        assertEquals(user, retrievedCart.getUser());
    }

    @Test
    public void removeFromCart() {
        User user = createTestUser();
        Item item = TestUtils.getItem0();
        Cart cart = createTestCart(user, new ArrayList<>(List.of(item)), ITEM_PRICE);

        when(userRepo.findByUsername(USERNAME)).thenReturn(user);
        when(itemRepository.findById(ITEM_ID)).thenReturn(java.util.Optional.of(item));

        ModifyCartRequest request = createModifyCartRequest(ITEM_ID, 1);

        ResponseEntity<Cart> response = cartController.removeFromCart(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Cart retrievedCart = response.getBody();
        assertNotNull(retrievedCart);
        assertEquals(ITEM_ID, retrievedCart.getId().longValue());
        assertEquals(0, retrievedCart.getItems().size());

        // Use compareTo() to check for equality of BigDecimal values
        assertEquals(0, EMPTY_CART_TOTAL.compareTo(retrievedCart.getTotal()));
        assertEquals(user, retrievedCart.getUser());
    }

}
