package com.info.ekart.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{

	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		// ==========================================================
	    // 1️⃣ Try to extract the Authorization header from request
	    // ==========================================================
	    // Example header:
	    // Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
	    String authHeader = request.getHeader("Authorization");

	    String token = null;
	    String username = null;

	    // ==========================================================
	    // 2️⃣ Check if header exists and starts with "Bearer "
	    // ==========================================================
	    // If not present, request may be public OR unauthenticated.
	    if (authHeader != null && authHeader.startsWith("Bearer ")) {

	        // Remove "Bearer " prefix to get actual JWT token
	        token = authHeader.substring(7);

	        // Extract username from token (this also validates signature internally)
	        username = jwtService.extractUsername(token);
	    }

	    // ==========================================================
	    // 3️⃣ Continue only if:
	    //     - Username extracted from token
	    //     - User is not already authenticated
	    // ==========================================================
	    // The second condition prevents overriding an already authenticated user.
	    if (username != null &&
	        SecurityContextHolder.getContext().getAuthentication() == null) {

	        // ======================================================
	        // 4️⃣ Load user details from database
	        // ======================================================
	        // Even though JWT contains username,
	        // we re-check DB to confirm:
	        // - User still exists
	        // - User role is current
	        UserDetails userDetails =
	                userDetailsService.loadUserByUsername(username);

	        // ======================================================
	        // 5️⃣ Validate token
	        // ======================================================
	        // This checks:
	        // - Username matches
	        // - Token not expired
	        // - Signature valid
	        if (jwtService.isTokenValid(token, userDetails.getUsername())) {

	            // ==================================================
	            // 6️⃣ Create Authentication object
	            // ==================================================
	            // We create a Spring Security authentication object
	            // containing:
	            // - user details
	            // - null password (because JWT already validated)
	            // - authorities (roles)
	            UsernamePasswordAuthenticationToken authToken =
	                    new UsernamePasswordAuthenticationToken(
	                            userDetails,
	                            null,
	                            userDetails.getAuthorities());

	            // Attach request details (IP, session info etc.)
	            authToken.setDetails(
	                    new WebAuthenticationDetailsSource()
	                            .buildDetails(request));

	            // ==================================================
	            // 7️⃣ Store authentication in SecurityContext
	            // ==================================================
	            // This is THE MOST IMPORTANT STEP.
	            // Now Spring Security treats this request as authenticated.
	            SecurityContextHolder.getContext()
	                    .setAuthentication(authToken);
	        }
	    }

	    // ==========================================================
	    // 8️⃣ Continue filter chain
	    // ==========================================================
	    // Without this line, request would stop here.
	    // This passes control to next filter / controller.
	    filterChain.doFilter(request, response);
	}
	
	
}
