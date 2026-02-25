package com.graduation.fer.auth;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthTestController {

    @GetMapping("/api/me")
    public String me(Authentication authentication) {
        // 인증 성공 시 principal = userId
        return authentication.getName();
    }
}
