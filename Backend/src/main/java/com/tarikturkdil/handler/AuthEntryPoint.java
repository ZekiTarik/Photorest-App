package com.tarikturkdil.handler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.tarikturkdil.exception.MessageType;
import com.tarikturkdil.jwt.JwtAuthenticationFilter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

@Component
public class AuthEntryPoint implements AuthenticationEntryPoint{
	
	private final ObjectMapper objectMapper;

    public AuthEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            org.springframework.security.core.AuthenticationException authException)
            throws IOException, ServletException {

        MessageType messageType = (MessageType) request.getAttribute(JwtAuthenticationFilter.EXCEPTION_MESSAGE_TYPE_ATTRIBUTE);
        if (messageType == null) {
            messageType = MessageType.USERNAME_OR_PASSWORD_INVALID; // token hiç yoksa / genel auth hatası
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(messageType.getStatus().value());

        ExceptionDetails<String> details = new ExceptionDetails<>();
        details.setPath(request.getRequestURI());
        details.setCreateTime(LocalDateTime.now()); // new Date() yerine
        details.setMessage(messageType.getMessage());

        ApiError<String> apiError = new ApiError<>();
        apiError.setStatus(messageType.getStatus().value());
        apiError.setException(details);

        response.getWriter().write(objectMapper.writeValueAsString(apiError));
    }
    
}
