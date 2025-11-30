package com.app.authjwt;

import com.app.authjwt.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class JwtUtilsTest {
    @InjectMocks
    private JwtUtils jwtUtils;

    private UserDetails userDetails;

    @BeforeEach
    public void setUp() {
        String secret = "b5f3860a89d08d3db83258a0509186d527b38ad627852a79";
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", secret);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 3600000L);
        userDetails = new User("testuser", "password", new ArrayList<>());
    }
    @Test
    void generateToken_ShouldReturnTokenString() {
        String token = jwtUtils.generateToken(userDetails);
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
    }
    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        String token = jwtUtils.generateToken(userDetails);
        String username = jwtUtils.extractUsername(token);

        assertThat(username).isEqualTo("testuser");
    }

    @Test
    void isTokenValid_ShouldReturnTrue_WhenTokenIsValid() {
        String token = jwtUtils.generateToken(userDetails);
        boolean isValid = jwtUtils.isTokenValid(token, userDetails);

        assertThat(isValid).isTrue();
    }

    @Test
    void isTokenValid_ShouldReturnFalse_WhenUsernameDoesNotMatch() {
        String token = jwtUtils.generateToken(userDetails);
        UserDetails otherUser = new User("otherUser", "password", new ArrayList<>());

        boolean isValid = jwtUtils.isTokenValid(token, otherUser);

        assertThat(isValid).isFalse();
    }
}
