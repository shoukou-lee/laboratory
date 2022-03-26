package com.shoukou.whymytransactionalnotwork.service;

import com.shoukou.whymytransactionalnotwork.model.User;
import com.shoukou.whymytransactionalnotwork.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void init() {

    }

    @BeforeEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void callSaveAndThrowRunTimeException() {
        // when
        assertThatThrownBy(() -> userService.saveAndThrowRunTimeException())
                .isInstanceOf(RuntimeException.class);

        // then
        List<User> users = userRepository.findAll();
        assertThat(users.isEmpty()).isEqualTo(false); // expected : non-rollback
    }

    @Test
    void callTxSaveAndThrowRunTimeException() {
        // when
        assertThatThrownBy(() -> userService.txSaveAndThrowRunTimeException())
                .isInstanceOf(RuntimeException.class);

        // then
        List<User> users = userRepository.findAll();
        assertThat(users.isEmpty()).isEqualTo(true); // expected : rollback
    }


    @Test
    void callMethod() {
        // when
        assertThatThrownBy(() -> userService.callMethod())
                .isInstanceOf(RuntimeException.class);

        // then
        List<User> users = userRepository.findAll();
        assertThat(users.isEmpty()).isEqualTo(false); // expected : non-rollback
    }

    @Test
    void callTxMethod() {
        // when
        assertThatThrownBy(() -> userService.callTxMethod())
                .isInstanceOf(RuntimeException.class);

        // then
        List<User> users = userRepository.findAll();
        assertThat(users.isEmpty()).isEqualTo(false); // expected : non-rollback
    }

    @Test
    void txCallMethod() {
        // when
        assertThatThrownBy(() -> userService.txCallMethod())
                .isInstanceOf(RuntimeException.class);

        // then
        List<User> users = userRepository.findAll();
        assertThat(users.isEmpty()).isEqualTo(true); // expected : rollback
    }

    @Test
    void txCallTxMethod() {
        // when
        assertThatThrownBy(() -> userService.txCallTxMethod())
                .isInstanceOf(RuntimeException.class);

        // then
        List<User> users = userRepository.findAll();
        assertThat(users.isEmpty()).isEqualTo(true); // expected : rollback
    }


}
