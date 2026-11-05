package com.hocuscoquus.hocuscoquus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.ignoringRequestMatchers( // TODO - delete after login implemented
				PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.DELETE, "/api/account/**")));
		http.authorizeHttpRequests(
				auth -> auth.requestMatchers("/",
						"/index",
						"/signup",
						"/account/signup",
						"/css/**",
						"/images/**",
						"/search",
						"/404",
						"/calendar",
						"/edit/recipe",
						"/view-recipe",
						// TODO - delete account endpoints below.
						// This is for the sake of seeing account
						// pages before login is implemented.
						"/account",
						"/account/liked",
						"/account/tagged",
						"/error").permitAll()
						.requestMatchers("/api/**").authenticated()
						.requestMatchers(HttpMethod.DELETE, "/api/account/**").permitAll()
						.anyRequest().permitAll())
				.formLogin(form -> form
						.loginPage("/login")
						.usernameParameter("email")
						.defaultSuccessUrl("/index")
						.permitAll()
				)
				.logout((logout) -> logout
						.logoutSuccessUrl("/index")
				);
		return http.build();
	}
}
