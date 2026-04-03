package com.info.ekart.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.info.ekart.dto.request.LoginRequestDTO;
import com.info.ekart.dto.request.RegisterRequestDTO;
import com.info.ekart.dto.request.UpdateAddressRequestDTO;
import com.info.ekart.dto.request.ChangePasswordRequestDTO;
import com.info.ekart.dto.response.CustomerResponseDTO;
import com.info.ekart.exception.EKartException;
import com.info.ekart.service.CustomerService;
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/customer-api")
public class CustomerAPI {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private Environment environment;

    static Log logger = LogFactory.getLog(CustomerAPI.class);

    // ================= LOGIN =================

    @PostMapping("/login")
    public ResponseEntity<String> login(
            @Valid @RequestBody LoginRequestDTO request)
            throws EKartException {

        logger.info("CUSTOMER TRYING TO LOGIN: " + request.getEmailId());

        String token  = customerService.login(request);

        logger.info("CUSTOMER LOGIN SUCCESS: ");

        return new ResponseEntity<>(token , HttpStatus.OK);
    }

    // ================= REGISTER =================

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @Valid @RequestBody RegisterRequestDTO request)
            throws EKartException {

        logger.info("CUSTOMER TRYING TO REGISTER: " + request.getEmailId());

        String registeredEmail = customerService.register(request);

        String message = environment.getProperty("CustomerAPI.CUSTOMER_REGISTRATION_SUCCESS")
                + registeredEmail;

        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    // ================= CHANGE PASSWORD =================

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @Valid @RequestBody ChangePasswordRequestDTO request)
            throws EKartException {

        customerService.changePassword(request);

        String message = environment.getProperty("CustomerAPI.PASSWORD_UPDATE_SUCCESS");

        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    // ================= UPDATE ADDRESS =================

    @PutMapping("/change-address")
    public ResponseEntity<String> updateShippingAddress(@Valid @RequestBody UpdateAddressRequestDTO updateAddress )
            throws EKartException {
    	//logger.info("CUSTOMER TRYING TO Update Address");
        customerService.updateShippingAddress(updateAddress);

        String message = environment.getProperty("CustomerAPI.UPDATE_ADDRESS_SUCCESS");

        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    // ================= DELETE ADDRESS =================

    
    //✅ With :.+  customerEmailId:.+
//    @PutMapping("/customer/{customerEmailId:.+}/address")
//    Now Spring understands:
//
//    Take everything including dots (.)
//
//    So full email is captured
//
//    ✔ test@gmail.com → correctly mapped  //better use separate dto
    
    @DeleteMapping("/customer/{customerEmailId:.+}")
    public ResponseEntity<String> deleteShippingAddress(
            @Pattern(
                regexp = "[a-zA-Z0-9._]+@[a-zA-Z]{2,}\\.[a-zA-Z][a-zA-Z.]+",
                message = "{invalid.email.format}"
            )
            @PathVariable String customerEmailId)
            throws EKartException {

        customerService.deleteShippingAddress(customerEmailId);

        String message = environment.getProperty("CustomerAPI.CUSTOMER_ADDRESS_DELETED_SUCCESS");

        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}
