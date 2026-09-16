package com.tarikturkdil.jwt;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.tarikturkdil.exception.MessageType;
import com.tarikturkdil.service.impl.AuthServiceImpl;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String EXCEPTION_MESSAGE_TYPE_ATTRIBUTE = "exceptionMessageType";

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            String username = jwtService.getUsernameByToken(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(username, null, userDetails.getAuthorities());

                    authenticationToken.setDetails(userDetails);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                } else {
                    // Token parse edildi (username okunabildi) ama geçersiz/expired sayıldı
                    request.setAttribute(EXCEPTION_MESSAGE_TYPE_ATTRIBUTE, MessageType.TOKEN_IS_EXPIRED);
                }
            }

        } catch (ExpiredJwtException ex) {
            request.setAttribute(EXCEPTION_MESSAGE_TYPE_ATTRIBUTE, MessageType.TOKEN_IS_EXPIRED);

        } catch (Exception e) {
            log.error("JWT doğrulama sırasında beklenmeyen hata", e); // bunu ekle
            request.setAttribute(EXCEPTION_MESSAGE_TYPE_ATTRIBUTE, MessageType.GENERAL_EXCEPTION);
        }

        filterChain.doFilter(request, response);
    }
}