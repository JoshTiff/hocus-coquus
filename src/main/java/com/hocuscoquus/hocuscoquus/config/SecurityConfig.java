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
						"/search",
						"/edit/recipe",
						"/view-recipe",
						// TODO - delete account endpoints below.
						// This is for the sake of seeing account
						// pages before login is implemented.
						"/account",
						"/account/liked",
						"/account/tagged").permitAll()
						.anyRequest().authenticated())
				.formLogin(form -> form.defaultSuccessUrl("/", true));
		return http.build();
	}
}