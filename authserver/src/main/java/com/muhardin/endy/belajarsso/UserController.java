package com.muhardin.endy.belajarsso;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @RequestMapping({"/api/user", "/api/me"})
    public Authentication user(Authentication auth) {
        return auth;
    }
}
