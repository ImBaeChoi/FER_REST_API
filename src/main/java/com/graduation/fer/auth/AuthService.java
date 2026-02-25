package com.graduation.fer.auth;

import com.graduation.fer.auth.dto.LoginRequest;
import com.graduation.fer.auth.dto.RefreshRequest;
import com.graduation.fer.auth.dto.SignupRequest;
import com.graduation.fer.auth.dto.TokenResponse;
import com.graduation.fer.domain.user.RefreshToken;
import com.graduation.fer.domain.user.RefreshTokenRepository;
import com.graduation.fer.domain.user.User;
import com.graduation.fer.domain.user.UserRepository;
import com.graduation.fer.security.JwtProvider;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public AuthService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtProvider jwtProvider
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    /** 로그인 */
    @Transactional
    public TokenResponse login(LoginRequest request) {

        User user = userRepository.findById(request.userId())
                 .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid userId or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid userId or password");
        }

        String accessToken = jwtProvider.createAccessToken(user);
        String refreshToken = jwtProvider.createRefreshToken(user);

        RefreshToken rt = new RefreshToken();
        rt.setUser(user);
        rt.setToken(refreshToken);
        rt.setExpiresAt(
                jwtProvider.parse(refreshToken)
                        .getPayload()
                        .getExpiration()
                        .toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDateTime()
        );
        rt.setRevoked(false);
        rt.setCreatedAt(LocalDateTime.now());

        refreshTokenRepository.save(rt);

        return new TokenResponse(accessToken, refreshToken);
    }

    /** Access Token 재발급 (Refresh Token Rotation) */
    @Transactional
    public TokenResponse refresh(RefreshRequest request) {

        String refreshToken = request.refreshToken();

        // 1) 토큰 타입 검증
        if (!jwtProvider.isType(refreshToken, "refresh")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        // 2) DB에 존재하는지 확인
        RefreshToken saved = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token not found"));

        // 3) 만료 / 폐기 체크
        if (saved.isRevoked() || saved.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expired or revoked");
        }

        User user = saved.getUser();

        // 4) 기존 refresh token 폐기
        saved.setRevoked(true);

        // 5) 새 토큰 발급
        String newAccess = jwtProvider.createAccessToken(user);
        String newRefresh = jwtProvider.createRefreshToken(user);

        RefreshToken newRt = new RefreshToken();
        newRt.setUser(user);
        newRt.setToken(newRefresh);
        newRt.setExpiresAt(
                jwtProvider.parse(newRefresh)
                        .getPayload()
                        .getExpiration()
                        .toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDateTime()
        );
        newRt.setRevoked(false);
        newRt.setCreatedAt(LocalDateTime.now());

        refreshTokenRepository.save(newRt);

        return new TokenResponse(newAccess, newRefresh);
    }

    /** 로그아웃 */
    @Transactional
    public void logout(RefreshRequest request) {
        refreshTokenRepository.findByToken(request.refreshToken())
                .ifPresent(rt -> rt.setRevoked(true));
    }

    /** 회원가입 */
    @Transactional
    public void signup(SignupRequest request) {

        // 1) userId 중복 체크
        if (userRepository.existsById(request.userId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "UserId already exists");
        }

        // 2) 비밀번호 해시
        String hashedPassword = passwordEncoder.encode(request.password());

        // 3) User 엔티티 생성
        User user = new User();
        user.setUserId(request.userId());
        user.setName(request.name());
        user.setPasswordHash(hashedPassword);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // 4) 저장
        userRepository.save(user);
    }

    /** 이름 수정 */
    @Transactional
    public void updateName(String userId, com.graduation.fer.auth.dto.UpdateProfileRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setName(request.name());
        user.setUpdatedAt(LocalDateTime.now());
        // save 호출 없어도 @Transactional + JPA dirty checking으로 반영됨
    }

    /** 비밀번호 수정 */
    @Transactional
    public void updatePassword(String userId, com.graduation.fer.auth.dto.UpdatePasswordRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        user.setUpdatedAt(LocalDateTime.now());

        // 보안상 권장: 비밀번호 변경 시 기존 refresh token 전부 무효화
        refreshTokenRepository.deleteByUser_UserId(userId);
    }

    /** 회원탈퇴*/
    @Transactional
    public void deleteAccount(String userId, com.graduation.fer.auth.dto.DeleteAccountRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Password is incorrect");
        }

        // 토큰 먼저 제거(명시적으로) → FK CASCADE가 있어도 안전
        refreshTokenRepository.deleteByUser_UserId(userId);

        // 유저 삭제
        userRepository.delete(user);
    }

    /**내 정보 조회*/
    @Transactional
    public com.graduation.fer.auth.dto.UserMeResponse getMe(String userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new org.springframework.web.server.ResponseStatusException(
                                org.springframework.http.HttpStatus.NOT_FOUND,
                                "User not found"
                        )
                );

        return new com.graduation.fer.auth.dto.UserMeResponse(
                user.getUserId(),
                user.getName(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }


}
