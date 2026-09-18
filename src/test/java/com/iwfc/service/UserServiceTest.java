package com.iwfc.service;

import com.iwfc.exceptions.DuplicateDataException;
import com.iwfc.repository.Repository;
import com.iwfc.users.Administrator;
import com.iwfc.users.Member;
import com.iwfc.users.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        Repository<User> repository = new Repository<>(User::getUserId);
        userService = new UserService(repository);
    }

    @Test
    void registerAndFindUser() throws DuplicateDataException {
        Member member = new Member("U-1", "Sam Member", "sam@iwfc.com");
        userService.registerUser(member);

        assertTrue(userService.findById("U-1").isPresent());
        assertEquals(member, userService.findById("U-1").get());
    }

    @Test
    void registerUserRejectsDuplicateId() throws DuplicateDataException {
        userService.registerUser(new Member("U-1", "Sam Member", "sam@iwfc.com"));

        assertThrows(DuplicateDataException.class,
                () -> userService.registerUser(new Administrator("U-1", "Other Admin", "other@iwfc.com")));
    }

    @Test
    void listAllReturnsEveryRegisteredUser() throws DuplicateDataException {
        userService.registerUser(new Administrator("U-1", "Alice Admin", "alice@iwfc.com"));
        userService.registerUser(new Member("U-2", "Sam Member", "sam@iwfc.com"));

        List<User> users = userService.listAll();

        assertEquals(2, users.size());
    }
}
