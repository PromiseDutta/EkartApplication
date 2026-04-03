package com.info.ekart.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.info.ekart.dto.request.AddCartItemRequestDTO;
import com.info.ekart.dto.response.CartResponseDTO;
import com.info.ekart.exception.EKartException;
import com.info.ekart.service.CustomerCartService;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value = "/cart-api")
@Validated
public class CartAPI {

    @Autowired
    private CustomerCartService customerCartService;

    @Autowired
    private Environment environment;

    Log logger = LogFactory.getLog(CartAPI.class);

    // ================= ADD PRODUCT TO CART =================

    @PostMapping("/customer/{customerEmailId:.+}/product")
    public ResponseEntity<String> addProductToCart(
            @PathVariable
            @Email(message="{invalid.customeremail.format}")
                 
            String customerEmailId,

            @Valid @RequestBody AddCartItemRequestDTO request)
            throws EKartException {

        logger.info("Adding product to cart for: " + customerEmailId);

        Integer cartId = customerCartService.addProductToCart(customerEmailId, request);

        String message = environment.getProperty("CustomerCartAPI.PRODUCT_ADDED_TO_CART_SUCCESS")
                + cartId;

        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    // ================= GET CART =================

    @GetMapping("/customer/{customerEmailId:.+}/products")
    public ResponseEntity<CartResponseDTO> getProductsFromCart(
            @Email( message = "{invalid.customeremail.format}")
                   
            @PathVariable String customerEmailId)
            throws EKartException {

        logger.info("Fetching cart for: " + customerEmailId);

        CartResponseDTO response =
                customerCartService.getProductsFromCart(customerEmailId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // ================= DELETE SINGLE PRODUCT =================

    @DeleteMapping("/customer/{customerEmailId:.+}/product/{productId}")
    public ResponseEntity<String> deleteProductFromCart(
    		
            @PathVariable String customerEmailId,
            @PathVariable Integer productId)
            throws EKartException {

        customerCartService.deleteProductFromCart(customerEmailId, productId);

        String message = environment.getProperty(
                "CustomerCartAPI.PRODUCT_DELETED_FROM_CART_SUCCESS");

        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    // ================= MODIFY QUANTITY =================

    @PutMapping("/customer/{customerEmailId:.+}/product/{productId}")
    public ResponseEntity<String> modifyQuantityOfProductInCart(
            @PathVariable String customerEmailId,
            @PathVariable Integer productId,
            @RequestBody String quantity)
            throws EKartException {

        customerCartService.modifyQuantityOfProductInCart(
                customerEmailId,
                productId,
                Integer.parseInt(quantity)
        );

        String message = environment.getProperty(
                "CustomerCartAPI.PRODUCT_QUANTITY_UPDATE_FROM_CART_SUCCESS");

        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    // ================= DELETE ALL PRODUCTS =================

    @DeleteMapping("/customer/{customerEmailId:.+}/products")
    public ResponseEntity<String> deleteAllProductsFromCart(
            @PathVariable String customerEmailId)
            throws EKartException {

        customerCartService.deleteAllProductsFromCart(customerEmailId);

        String message =
                environment.getProperty("CustomerCartAPI.ALL_PRODUCTS_DELETED");

        return new ResponseEntity<>(message, HttpStatus.OK);
    }
    
    
}
