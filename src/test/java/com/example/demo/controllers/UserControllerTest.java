package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;


    private final UserRepository userRepository = mock(UserRepository.class);

    private final CartRepository cartRepository = mock(CartRepository.class);
    private final BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setup() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
    }

    @Test
    public void createUserShouldPass(){
        when(bCryptPasswordEncoder.encode("password")).thenReturn("password");
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("password");
        request.setConfirmPassword("password");

        ResponseEntity<User> response = userController.createUser(request);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("test", user.getUsername());
        assertEquals("password", user.getPassword());
    }

    @Test
    public void createUserWithPasswordLessThanSevenShouldReturnBadRequest(){

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("1234");
        request.setConfirmPassword("1234");

        ResponseEntity<User> response = userController.createUser(request);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());

        User user = response.getBody();
        assertNull(user);
    }

    @Test
    public void findUserByUserNameShouldReturnNotFoundUser() {
        ResponseEntity<User> response = userController.findByUserName("test");
        assertNotEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void findUserByUserNameShouldReturnFoundUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("test");
        when(userRepository.findByUsername(anyString())).thenReturn(user);
        ResponseEntity<User> response = userController.findByUserName("test");
        User foundUser = response.getBody();
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(foundUser);
    }

    @Test
    public void findUserByIdShouldReturnNotFoundUser() {
        ResponseEntity<User> response = userController.findById(1L);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void findUserByIdShouldReturnFoundUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("test");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        ResponseEntity<User> response = userController.findById(1L);
        User foundUser = response.getBody();
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(foundUser);
    }
}
