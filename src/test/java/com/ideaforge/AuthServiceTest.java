package com.ideaforge;

import com.ideaforge.dto.AuthRequest;
import com.ideaforge.dto.AuthResponse;
import com.ideaforge.dto.RegisterRequest;
import com.ideaforge.entity.Role;
import com.ideaforge.entity.User;
import com.ideaforge.exception.BadRequestException;
import com.ideaforge.repository.UserRepository;
import com.ideaforge.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testRegisterUserSuccessfully() {
        RegisterRequest req = new RegisterRequest("Alice Student", "alice@example.com", "Password@123");
        AuthResponse res = authService.register(req);

        assertNotNull(res);
        assertNotNull(res.getToken());
        assertEquals("alice@example.com", res.getEmail());
        assertEquals("Alice Student", res.getName());
        assertEquals(Role.ROLE_USER, res.getRole());

        User userInDb = userRepository.findByEmail("alice@example.com").orElse(null);
        assertNotNull(userInDb);
        assertTrue(passwordEncoder.matches("Password@123", userInDb.getPassword()));
        assertNotEquals("Password@123", userInDb.getPassword()); // BCrypt hashed
    }

    @Test
    void testRegisterDuplicateEmailThrowsBadRequest() {
        RegisterRequest req1 = new RegisterRequest("Bob", "bob@example.com", "Secret123");
        authService.register(req1);

        RegisterRequest req2 = new RegisterRequest("Bob Clone", "bob@example.com", "AnotherSecret");
        assertThrows(BadRequestException.class, () -> authService.register(req2));
    }

    @Test
    void testLoginSuccessfully() {
        RegisterRequest reg = new RegisterRequest("Charlie", "charlie@example.com", "MyPass123");
        authService.register(reg);

        AuthRequest loginReq = new AuthRequest("charlie@example.com", "MyPass123");
        AuthResponse loginRes = authService.login(loginReq);

        assertNotNull(loginRes);
        assertNotNull(loginRes.getToken());
        assertEquals("charlie@example.com", loginRes.getEmail());
    }
}
