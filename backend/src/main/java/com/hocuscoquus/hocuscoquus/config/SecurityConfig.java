package com.hocuscoquus.hocuscoquus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(
				auth -> auth.requestMatchers("/",
						"/index",
						"/css/**",
						"/images/**",
						"/404",
						"/error",
						"/calendar",
						// TODO - delete account endpoints below.
						// This is for the sake of seeing account
						// pages before login is implemented.
						"/account",
						"/account/liked",
						"/account/tagged").permitAll()
						// Authentication is not implemented yet; let unmatched URLs
						// reach Spring Boot's error handler and render error/404.html.
						.anyRequest().permitAll())
				.formLogin(form -> form.defaultSuccessUrl("/", true));
		return http.build();
	}
}
