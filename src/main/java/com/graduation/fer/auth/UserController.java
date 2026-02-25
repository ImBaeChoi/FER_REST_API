package com.graduation.fer.auth;

import com.graduation.fer.auth.dto.DeleteAccountRequest;
import com.graduation.fer.auth.dto.UpdatePasswordRequest;
import com.graduation.fer.auth.dto.UpdateProfileRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.graduation.fer.common.response.ApiResponse;
import com.graduation.fer.auth.dto.UserMeResponse;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final AuthService authService;

    public UserController(AuthService authService) {
        this.authService = authService;
    }

    // 계정 정보 수정(이름)
    @PatchMapping("/me")
    public ApiResponse<Void> updateName(Authentication authentication,
                           @Valid @RequestBody UpdateProfileRequest request) {
        String userId = authentication.getName();
        authService.updateName(userId, request);
        return ApiResponse.success();
    }

    // 비밀번호 변경
    @PatchMapping("/me/password")
    public ApiResponse<Void> updatePassword(Authentication authentication,
                               @Valid @RequestBody UpdatePasswordRequest request) {
        String userId = authentication.getName();
        authService.updatePassword(userId, request);
        return ApiResponse.success();
    }

    // 회원 탈퇴
    @DeleteMapping("/me")
    public ApiResponse<Void> deleteAccount(Authentication authentication,
                              @Valid @RequestBody DeleteAccountRequest request) {
        String userId = authentication.getName();
        authService.deleteAccount(userId, request);
        return ApiResponse.success();
    }

    // 내 정보 조회
    @GetMapping("/me")
    public ApiResponse<UserMeResponse> me(Authentication authentication) {
        String userId = authentication.getName(); // JwtAuthFilter에서 세팅됨
        return ApiResponse.success(authService.getMe(userId));
    }

}
