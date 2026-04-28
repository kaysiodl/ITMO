package com.kaysiodl.rest;

import com.kaysiodl.service.AuthService;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthResourceTest {

    @Test
    void logout_shouldReturn204() {
        AuthService authService = mock(AuthService.class);

        AuthResource resource = new AuthResource();
        resource.authService = authService;

        Response response = resource.logout("test-session");

        assertEquals(204, response.getStatus());
        verify(authService).logout("test-session");
    }
}