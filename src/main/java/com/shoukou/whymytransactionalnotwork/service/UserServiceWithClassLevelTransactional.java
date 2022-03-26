package com.shoukou.whymytransactionalnotwork.service;

import com.shoukou.whymytransactionalnotwork.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
public class UserServiceWithClassLevelTransactional {

    private final UserRepository userRepository;

    private void doPrivateTransactionalMethod() {

    }

}
