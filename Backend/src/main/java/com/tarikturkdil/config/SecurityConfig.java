package com.tarikturkdil.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.tarikturkdil.handler.AuthEntryPoint;
import com.tarikturkdil.jwt.JwtAuthenticationFilter;
import com.tarikturkdil.jwt.RateLimitingFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private AuthenticationProvider authenticationProvider;

	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@Autowired
	private AuthEntryPoint authEntryPoint;

	@Autowired
	private RateLimitingFilter rateLimitingFilter;

	private static final String REGISTER = "/register";
	private static final String AUTHENTICATE = "/authenticate";
	private static final String REFRESH_TOKEN = "/refreshToken";
	private static final String WS_ENDPOINT = "/ws/**";

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		http
	    .csrf(csrf -> csrf.disable())
	    .logout(logout -> logout.disable())
	    .authorizeHttpRequests(request -> request
	            .requestMatchers(REGISTER, AUTHENTICATE, REFRESH_TOKEN, WS_ENDPOINT).permitAll()
	            .anyRequest()
	            .authenticated())
	    .exceptionHandling(exception -> exception.authenticationEntryPoint(authEntryPoint))
	    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	    .authenticationProvider(authenticationProvider)
	    .addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class)
	    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

	return http.build();
	}
}