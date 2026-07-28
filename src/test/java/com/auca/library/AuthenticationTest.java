package com.auca.library;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.auca.library.domain.Location;
import com.auca.library.domain.User;

public class AuthenticationTest extends TestBase {

    private String username;
    private String password;

    @Before
    public void setUpUser() {
        Location village = createFullHierarchy("AU" + UUID.randomUUID().toString().substring(0, 4));
        username = "auth_" + UUID.randomUUID().toString().substring(0, 6);
        password = "secret123";
        createUser(username, password, village);
    }

    @Test
    public void authenticate_correctCredentials_returnsTrue() {
        assertTrue(userService.authenticate(username, password));
    }

    @Test
    public void authenticate_wrongPassword_returnsFalse() {
        assertFalse(userService.authenticate(username, "wrongpass"));
    }

    @Test
    public void authenticate_unknownUsername_returnsFalse() {
        assertFalse(userService.authenticate("unknown_user_xyz", password));
    }

    @Test
    public void authenticate_nullOrBlankInput_returnsFalse() {
        assertFalse(userService.authenticate(null, password));
        assertFalse(userService.authenticate("  ", password));
        assertFalse(userService.authenticate(username, null));
        assertFalse(userService.authenticate(username, "  "));
    }
}
