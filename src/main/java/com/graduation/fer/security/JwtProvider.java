package com.graduation.fer.security;

import com.graduation.fer.domain.user.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtProvider {

    private final String issuer;
    private final SecretKey key;
    private final long accessTtlSeconds;
    private final long refreshTtlSeconds;

    public JwtProvider(
            @Value("${security.jwt.issuer}") String issuer,
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.access-ttl-seconds}") long accessTtlSeconds,
            @Value("${security.jwt.refresh-ttl-seconds}") long refreshTtlSeconds
    ) {
        this.issuer = issuer;
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTtlSeconds = accessTtlSeconds;
        this.refreshTtlSeconds = refreshTtlSeconds;
    }

    /** Access Token 생성: 인증 필터가 사용하는 토큰 */
    public String createAccessToken(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .issuer(issuer)
                .subject(user.getUserId())                // ✅ userId를 subject로
                .claim("typ", "access")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessTtlSeconds)))
                .signWith(key)
                .compact();
    }

    /** Refresh Token 생성: 재발급용 토큰(서버는 DB에도 저장) */
    public String createRefreshToken(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .issuer(issuer)
                .subject(user.getUserId())               // ✅ userId를 subject로
                .claim("typ", "refresh")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(refreshTtlSeconds)))
                .signWith(key)
                .compact();
    }

    /** 토큰 파싱 (서명/issuer 검증 포함) */
    public Jws<Claims> parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token);
    }

    /** 토큰 타입 확인(access/refresh) */
    public boolean isType(String token, String type) {
        Claims claims = parse(token).getPayload();
        String typ = claims.get("typ", String.class);
        return type.equals(typ);
    }

    /** 토큰에서 userId(subject) 추출 */
    public String getUserId(String token) {
        return parse(token).getPayload().getSubject();
    }

    /** 만료 여부(파싱 성공 기준) */
    public boolean isExpired(String token) {
        Date exp = parse(token).getPayload().getExpiration();
        return exp.before(new Date());
    }
}
