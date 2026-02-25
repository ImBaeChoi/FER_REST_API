package com.graduation.fer.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    // 유저의 모든 리프레시 토큰 폐기/정리 등에 사용 가능
    long deleteByUser_UserId(String userId);
}
