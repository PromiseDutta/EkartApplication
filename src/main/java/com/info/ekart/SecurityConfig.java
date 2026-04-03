package com.info.ekart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.info.ekart.security.JwtAuthenticationFilter;
import com.info.ekart.security.RateLimitFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private RateLimitFilter rateLimitFilter;
	
	@Autowired
	private JwtAuthenticationFilter jwtFilter;
	// ==========================================================
	// 1️⃣ PASSWORD ENCODER BEAN
	// ==========================================================

	// 🔹 This bean tells Spring Security which algorithm to use
	// for encoding and validating passwords.
	//
	// We are using BCrypt because:
	// - It automatically handles salting
	// - It is slow (prevents brute-force attacks)
	// - It is recommended for password storage
	//
	// Spring will automatically use this bean internally when
	// validating credentials during authentication.
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// ==========================================================
	// 2️⃣ AUTHENTICATION MANAGER BEAN
	// ==========================================================

	// 🔹 AuthenticationManager is the main entry point into Spring Security’s
	// authentication engine.
	// When we call:
	// authenticationManager.authenticate(...)
	// It internally:
	// - Fetches user from DB - Uses PasswordEncoder to validate password - Returns
	// authenticated Authentication object
	// In Spring Boot 3+, AuthenticationManager is not exposed
	// automatically, so we explicitly expose it as a bean
	// so it can be injected into our service layer.
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {

		// This retrieves the fully configured AuthenticationManager
		// that Spring has already built internally.
		return config.getAuthenticationManager();
	}

	// ==========================================================
	// 3️⃣ SECURITY FILTER CHAIN CONFIGURATION
	// ==========================================================
	// 🔹 SecurityFilterChain defines how HTTP requests are secured.
	// Spring Security works using filters.
	// Every request passes through this filter chain before reaching controllers.
	//
	// Here we define:
	// - Which endpoints are public
	// - Which require authentication
	// - Disable default form login
	// - Disable CSRF (since we are building REST APIs)
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http

				// 🔹 Disable CSRF protection.
				// CSRF is mainly required for browser-based form submissions.
				// Since we are building REST APIs (stateless requests),
				// we disable it for simplicity.
				.csrf(csrf -> csrf.disable())

				// 🔥 IMPORTANT: Stateless
				/*
				 * Even though JWT is stateless, Spring Security by default uses session-based
				 * authentication. So we explicitly configure SessionCreationPolicy.STATELESS to
				 * ensure no session is created and each request is authenticated purely using
				 * the JWT token
				 */
				.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				// 🔹 Define authorization rules for endpoints.
				.authorizeHttpRequests(auth -> auth

						// =====================================
						// 🔓 PUBLIC ENDPOINTS
						// =====================================
						.requestMatchers("/customer-api/register", "/customer-api/login").permitAll()

						// =====================================
						// 👑 ADMIN ONLY ENDPOINTS
						// =====================================
						.requestMatchers("/product-api/product", // add new product
								"/product-api/update/**", // update product
								"/product-api/delete-product/**" // delete product
						).hasRole("ADMIN")

						// =====================================
						// 👤 BOTH USER & ADMIN CAN VIEW PRODUCTS
						// =====================================
						//.requestMatchers("/product-api/products", "/product-api/product/**").hasAnyRole("USER", "ADMIN")
						  .requestMatchers("/product-api/products", "/product-api/product/**").hasAnyRole("USER", "ADMIN")
						// =====================================
						// 👤 USER ONLY ENDPOINTS
						// =====================================
						.requestMatchers("/customer-api/change-address", "/customer-api/change-password",
								"/customer-api/customer/**", "/cart-api/**", "/order-api/**", "/payment-api/**")
						.hasRole("USER")

						// =====================================
						// 🔐 ANY OTHER REQUEST
						// =====================================
						.anyRequest().authenticated())
				// 🔥 Add JWT filter
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
				
				
				
				
				/// NEW LINE — rate limit runs right after JWT
	            .addFilterAfter(rateLimitFilter, JwtAuthenticationFilter.class)
	            // addFilterAfter means: place rateLimitFilter immediately
	            // after JwtAuthenticationFilter in the chain.
	            // Order matters because resolveIdentifier() reads
	            // SecurityContextHolder which JWT populates.
	            // If rate limit ran before JWT, auth would always be null
	            // and every user would be bucketed by IP instead of email.
	            
				
				/// 🔹 Disable default Spring Security login page.
				// By default, Spring shows a form-based login page.
				// Since we have our own custom login API,
				// we disable the built-in form login.
				.formLogin(form -> form.disable());

		// 🔹 Build and return the configured SecurityFilterChain.
		return http.build();
	}
}
