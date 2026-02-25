package com.graduation.fer.security;

import com.graduation.fer.domain.user.User;
import com.graduation.fer.domain.user.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    public JwtAuthFilter(JwtProvider jwtProvider, UserRepository userRepository) {
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        // Authorization 헤더가 없거나 Bearer가 아니면 그냥 통과
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            // 1) access 토큰인지 확인 (refresh 토큰으로는 인증 불가)
            if (!jwtProvider.isType(token, "access")) {
                filterChain.doFilter(request, response);
                return;
            }

            // 2) userId(subject) 추출
            String userId = jwtProvider.getUserId(token);

            // 3) 이미 인증되어 있으면 중복 인증 방지
            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 4) 유저 존재 확인 (삭제된 유저 토큰 방지)
                User user = userRepository.findById(userId).orElse(null);
                if (user == null) {
                    filterChain.doFilter(request, response);
                    return;
                }

                // ※ 현재 Users 테이블에 role 컬럼이 없어서 일단 USER로 고정
                var authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

                var auth = new UsernamePasswordAuthenticationToken(
                        user.getUserId(),   // principal
                        null,               // credentials
                        authorities
                );

                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }

        } catch (Exception ignored) {
            // 토큰 파싱/검증 실패 시 인증 없이 통과 (보호 API에서 401)
        }

        filterChain.doFilter(request, response);
    }
}
