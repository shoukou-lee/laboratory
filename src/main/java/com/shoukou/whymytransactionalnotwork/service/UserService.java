package com.shoukou.whymytransactionalnotwork.service;

import com.shoukou.whymytransactionalnotwork.model.User;
import com.shoukou.whymytransactionalnotwork.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public void callMethod() {
        saveAndThrowRunTimeException();
    }

    public void callTxMethod() {
        txSaveAndThrowRunTimeException();
    }

    @Transactional
    public void txCallMethod() {
        saveAndThrowRunTimeException();
    }

    @Transactional
    public void txCallTxMethod() {
        txSaveAndThrowRunTimeException();
    }

    public void saveAndThrowRunTimeException() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            User u = new User("User" + String.valueOf(i));
            users.add(u);
        }
        userRepository.saveAll(users);

        throw new RuntimeException("throw runtime exception");
    }

    @Transactional
    public void txSaveAndThrowRunTimeException() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            User u = new User("User" + String.valueOf(i));
            users.add(u);
        }
        userRepository.saveAll(users);

        throw new RuntimeException("throw runtime exception");
    }

}
