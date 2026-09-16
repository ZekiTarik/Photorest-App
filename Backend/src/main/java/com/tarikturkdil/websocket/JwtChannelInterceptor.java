package com.tarikturkdil.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor; // YENİ IMPORT
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import com.tarikturkdil.jwt.JwtService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtChannelInterceptor implements ChannelInterceptor {

    @Autowired
    private JwtService jwtService;

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {

        // DEĞİŞTİ: wrap() yerine getAccessor() — mesajın kendi mutable accessor'ını alıyoruz,
        // aksi hâlde setUser() çağrısı kalıcı olmuyor.
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {

            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("WebSocket CONNECT reddedildi: Authorization header eksik veya hatalı.");
                throw new IllegalArgumentException("Token bulunamadı");
            }

            String token = authHeader.substring(7);

            try {
                String email = jwtService.getUsernameByToken(token);

                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(email, null, java.util.List.of());

                accessor.setUser(authentication);

                log.info("WebSocket bağlantısı doğrulandı: {}", email);

            } catch (Exception e) {
                log.warn("WebSocket CONNECT reddedildi: geçersiz token.", e);
                throw new IllegalArgumentException("Geçersiz token");
            }
        }

        return message;
    }
}