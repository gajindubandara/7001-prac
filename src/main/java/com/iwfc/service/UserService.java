package com.iwfc.service;

import com.iwfc.exceptions.DuplicateDataException;
import com.iwfc.repository.Repository;
import com.iwfc.users.User;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class UserService {

    private final Repository<User> userRepository;

    public UserService(Repository<User> userRepository) {
        this.userRepository = Objects.requireNonNull(userRepository, "userRepository must not be null");
    }

    public void registerUser(User user) throws DuplicateDataException {
        userRepository.add(user);
    }

    public Optional<User> findById(String userId) {
        return userRepository.findById(userId);
    }

    public List<User> listAll() {
        return userRepository.findAll();
    }
}
