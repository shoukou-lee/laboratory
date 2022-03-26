package com.shoukou.whymytransactionalnotwork.controller;

import com.shoukou.whymytransactionalnotwork.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @GetMapping("/launch")
    void callService() {
        userService.txSaveAndThrowRunTimeException();
    }

}
