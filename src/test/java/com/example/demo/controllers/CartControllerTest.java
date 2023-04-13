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
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class CartControllerTest {

    private CartController cartController;
    private final UserRepository userRepository = mock(UserRepository.class);

    private final CartRepository cartRepository = mock(CartRepository.class);

    private final ItemRepository itemRepository = mock(ItemRepository.class);

    private final Fixtures fixtures = new Fixtures();
    @Before
    public void setup() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
    }

    @Test
    public void addToCartShouldReturnUserNotFound() {
        ResponseEntity<Cart> cartResponseEntity = cartController.addTocart(fixtures.modifyCartRequest);
        assertEquals(404, cartResponseEntity.getStatusCodeValue());
    }

    @Test
    public void addToCartShouldReturnItemNotFound() {
        when(userRepository.findByUsername("test")).thenReturn(fixtures.user);
        ResponseEntity<Cart> cartResponseEntity = cartController.addTocart(fixtures.modifyCartRequest);
        assertEquals(404, cartResponseEntity.getStatusCodeValue());
    }

    @Test
    public void addToCartShouldReturnOK() {
        when(userRepository.findByUsername("test")).thenReturn(fixtures.user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(fixtures.item));
        ResponseEntity<Cart> cartResponseEntity = cartController.addTocart(fixtures.modifyCartRequest);
        assertNotEquals(404, cartResponseEntity.getStatusCodeValue());

        Cart cart = cartResponseEntity.getBody();
        assertNotNull(cart);
        assertEquals(cart, fixtures.cart);
        assertFalse(cart.getItems().isEmpty());
    }

    @Test
    public void removeFromCartShouldReturnUserNotFound() {
        ResponseEntity<Cart> cartResponseEntity = cartController.removeFromcart(fixtures.modifyCartRequest);
        assertEquals(404, cartResponseEntity.getStatusCodeValue());
    }

    @Test
    public void removeFromCartShouldReturnItemNotFound() {
        when(userRepository.findByUsername("test")).thenReturn(fixtures.user);
        ResponseEntity<Cart> cartResponseEntity = cartController.removeFromcart(fixtures.modifyCartRequest);
        assertEquals(404, cartResponseEntity.getStatusCodeValue());
    }

    @Test
    public void removeFromCartShouldReturnOK() {
        when(userRepository.findByUsername("test")).thenReturn(fixtures.user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(fixtures.item));
        ResponseEntity<Cart> cartResponseEntity = cartController.removeFromcart(fixtures.modifyCartRequest);
        assertEquals(200, cartResponseEntity.getStatusCodeValue());

        Cart cart = cartResponseEntity.getBody();
        assertNotNull(cart);
        assertTrue(cart.getItems().isEmpty());
    }

    static class Fixtures {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        User user = new User();
        Cart cart = new Cart();
        Item item = new Item();
        public Fixtures() {

            modifyCartRequest.setUsername("test");
            modifyCartRequest.setQuantity(2);
            modifyCartRequest.setItemId(1);
            item.setId(1L);
            item.setName("item");
            item.setPrice(BigDecimal.valueOf(2.99));
            cart.setId(1L);
            cart.addItem(item);

            user.setId(1);
            user.setCart(cart);

        }
    }
}
