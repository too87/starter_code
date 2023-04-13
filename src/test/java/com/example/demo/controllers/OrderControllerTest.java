package com.example.demo.controllers;


import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;
    private final UserRepository userRepository = mock(UserRepository.class);

    private final OrderRepository orderRepository = mock(OrderRepository.class);

    private final Fixtures fixtures = new Fixtures();
    @Before
    public void setup() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
    }

    @Test
    public void submitOrderShouldReturnNotFoundWhenNoUser() {
        ResponseEntity<UserOrder> response = orderController.submit("test");
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void submitOrderShouldReturnOK() {
        when(userRepository.findByUsername("test")).thenReturn(fixtures.user);
        ResponseEntity<UserOrder> response = orderController.submit("test");
        assertNotEquals(404, response.getStatusCodeValue());

        UserOrder userOrder = response.getBody();
        assertNotNull(userOrder);
        assertEquals(userOrder.getUser(), fixtures.user);
        assertEquals(userOrder.getItems().size(), 1);
    }

    @Test
    public void getOrdersForUserShouldReturnNotFoundWhenNoUser() {
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("test");
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void getOrdersForUserShouldReturnOK() {
        when(userRepository.findByUsername("test")).thenReturn(fixtures.user);
        when(orderRepository.findByUser(any())).thenReturn(Collections.emptyList());
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("test");
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    static class Fixtures {
        User user = new User();
        Cart cart = new Cart();
        Item item = new Item();
        public Fixtures() {

            item.setId(1L);
            item.setName("item");
            item.setPrice(BigDecimal.valueOf(2.99));

            cart.setId(1L);
            cart.addItem(item);
            cart.setUser(user);

            user.setId(1);
            user.setCart(cart);

        }
    }
}
