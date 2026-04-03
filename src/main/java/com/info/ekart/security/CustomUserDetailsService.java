package com.info.ekart.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.info.ekart.entity.Customer;
import com.info.ekart.exception.EKartException;
import com.info.ekart.repository.CustomerRepository;
@Service
public class CustomUserDetailsService  implements UserDetailsService{

	
	@Autowired
	private CustomerRepository customerRepository;

	/**
	 * This method is called by Spring Security during authentication.
	 *
	 * Its responsibility:
	 * 1. Fetch user details from database using the provided username (email in our case).
	 * 2. If user is not found, throw UsernameNotFoundException.
	 * 3. Convert our application's Customer entity into Spring Security's UserDetails object.
	 * 4. Provide username, encrypted password, and authorities (roles) to Spring Security.
	 *
	 * Spring Security then:
	 * - Compares raw password with encoded password using PasswordEncoder.
	 * - Creates an Authentication object if credentials are valid.
	 */
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

	    // Fetch user from database (email converted to lowercase to avoid case mismatch issues)
	    Customer customer = customerRepository.findById(email.toLowerCase())
	            .orElseThrow(() -> new UsernameNotFoundException("User Not Found."));

	    // Convert our Customer entity into Spring Security's User object
	    // This object is used internally by Spring Security for authentication & authorization
	    return new User(
	            customer.getEmailId(),          // Username (principal)
	            customer.getPassword(),         // Encrypted password from DB
	            List.of(
	                    // Assigning role as authority (must be prefixed with ROLE_)
	                    new SimpleGrantedAuthority("ROLE_" + customer.getRole().name())
	            )
	    );
	}
}
