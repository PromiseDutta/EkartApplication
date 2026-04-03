package com.info.ekart.service;

import java.beans.Encoder;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.info.ekart.api.CustomerAPI;
import com.info.ekart.dto.Role;
import com.info.ekart.dto.request.ChangePasswordRequestDTO;
import com.info.ekart.dto.request.LoginRequestDTO;
import com.info.ekart.dto.request.RegisterRequestDTO;
import com.info.ekart.dto.request.UpdateAddressRequestDTO;
import com.info.ekart.dto.response.CustomerResponseDTO;
import com.info.ekart.entity.Customer;
import com.info.ekart.exception.EKartException;
import com.info.ekart.repository.CustomerRepository;
import com.info.ekart.security.JwtService;

@Service
public class CustomerServiceImpl implements CustomerService {

	static Log logger = LogFactory.getLog(CustomerServiceImpl.class);

	private final CustomerRepository customerRepository;

	private final PasswordEncoder passwordEncoder;

	private final AuthenticationManager authenticationManager;

	private final JwtService jwtService;

	public CustomerServiceImpl(CustomerRepository customerRepository, PasswordEncoder passwordEncoder,
			AuthenticationManager authenticationManager, JwtService jwtService) {
		super();
		this.customerRepository = customerRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
	}

	// 🔐 Login
	@Override
	public String login(LoginRequestDTO request) throws EKartException {

		// 🔐 Step 1: Authenticate user using Spring Security AuthenticationManager
		// This internally calls UserDetailsService.loadUserByUsername()
		// and verifies password using PasswordEncoder.matches() if match return auth object
		//  else BadCredentialException
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getEmailId(), request.getPassword()));

		
		
		// SecurityContextHolder.getContext().setAuthentication(authentication);

		// 🔎 Step 2: Fetch customer details from DB
	    // We fetch again because we need role and other details for JWT generation
		Customer customer = customerRepository.findById(request.getEmailId().toLowerCase())
				.orElseThrow(() -> new EKartException("CustomerService.CUSTOMER_NOT_FOUND"));

		// 🔥 Step 3: Generate JWT Token
		String token = jwtService.generateToken(customer.getEmailId(), customer.getRole().name());

		
		// 📤 Return JWT token instead of returning user object
	    // Client will use this token in Authorization header for future requests
	    return token;


	    // =================== PREVIOUS IMPLEMENTATION (Before JWT) ===================

	    // ❌ Earlier we were doing manual password comparison
	    // This is not recommended when using Spring Security

	    // if (!customer.getPassword().equals(request.getPassword())) {
	    //     throw new EKartException("CustomerService.INVALID_CREDENTIALS");
	    // }

	    // ❌ This was manual passwordEncoder check
	    // Now AuthenticationManager already handles this internally
	    // So this becomes redundant

	    // if(!passwordEncoder.matches(request.getPassword(), customer.getPassword())) {
	    //     throw new EKartException("CustomerService.INVALID_CREDENTIALS");
	    // }

	    // ❌ Earlier we were returning Customer Response DTO
	    // Now instead of returning user details,
	    // we return JWT token for stateless authentication

	    // return convertToResponseDTO(customer);
	}

	// 📝 Register
	@Override
	public String register(RegisterRequestDTO request) throws EKartException {

		boolean emailAvailable = customerRepository.findById(request.getEmailId().toLowerCase()).isEmpty();

		boolean phoneAvailable = customerRepository.findByPhoneNumber(request.getPhoneNumber()).isEmpty();

		if (!emailAvailable) {
			throw new EKartException("CustomerService.EMAIL_ID_ALREADY_IN_USE");
		}

		if (!phoneAvailable) {
			throw new EKartException("CustomerService.PHONE_NUMBER_ALREADY_IN_USE");
		}

		Customer customer = new Customer();
		customer.setEmailId(request.getEmailId().toLowerCase());
		customer.setName(request.getName());
		// customer.setPassword(request.getPassword());
		customer.setRole(Role.USER);// default role will be user only, even though we have set that in db also,still
									// setting here as well

		customer.setPassword(passwordEncoder.encode(request.getPassword()));
		customer.setPhoneNumber(request.getPhoneNumber());
		customer.setAddress(request.getAddress());

		customerRepository.save(customer);

		return customer.getEmailId();
	}

	// 🔑 Change Password
	@Override
	public void changePassword(ChangePasswordRequestDTO request) throws EKartException {

		Customer customer = customerRepository.findById(request.getEmailId().toLowerCase())
				.orElseThrow(() -> new EKartException("CustomerService.CUSTOMER_NOT_FOUND"));

		if (!passwordEncoder.matches(request.getOldPassword(), customer.getPassword())) {
			throw new EKartException("CustomerService.INVALID_CREDENTIALS");
		}

		// customer.setPassword(request.getNewPassword());
		customer.setPassword(passwordEncoder.encode(request.getNewPassword()));
		customerRepository.save(customer);
	}

	// 🏠 Update Address
	@Override
	public void updateShippingAddress(UpdateAddressRequestDTO updateAddress) throws EKartException {

		logger.info("inside updateShippingAddress");
		Customer customer = customerRepository.findById(updateAddress.getEmail().toLowerCase())
				.orElseThrow(() -> new EKartException("CustomerService.CUSTOMER_NOT_FOUND"));
		logger.info("Updating address to new address ");
		customer.setAddress(updateAddress.getAddress());
		customerRepository.save(customer);
	}

	// ❌ Delete Address
	@Override
	public void deleteShippingAddress(String customerEmailId) throws EKartException {

		Customer customer = customerRepository.findById(customerEmailId.toLowerCase())
				.orElseThrow(() -> new EKartException("CustomerService.CUSTOMER_NOT_FOUND"));

		customer.setAddress(null);
		customerRepository.save(customer);
	}

	// 👤 Get Customer
	@Override
	public CustomerResponseDTO getCustomerByEmailId(String emailId) throws EKartException {

		Customer customer = customerRepository.findById(emailId.toLowerCase())
				.orElseThrow(() -> new EKartException("CustomerService.CUSTOMER_NOT_FOUND"));

		return convertToResponseDTO(customer);
	}

	// 🔄 Mapper
	private CustomerResponseDTO convertToResponseDTO(Customer customer) {

		CustomerResponseDTO dto = new CustomerResponseDTO();

		dto.setEmailId(customer.getEmailId());
		dto.setName(customer.getName());
		dto.setPhoneNumber(customer.getPhoneNumber());
		dto.setAddress(customer.getAddress());
		dto.setRole(customer.getRole());
		return dto;
	}
}
