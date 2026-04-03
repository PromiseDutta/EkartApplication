package com.info.ekart.api;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.info.ekart.dto.request.ProductCreateRequestDTO;
import com.info.ekart.dto.response.ProductResponseDTO;
import com.info.ekart.entity.Product;
import com.info.ekart.exception.EKartException;
import com.info.ekart.service.CustomerProductService;

import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value = "/product-api")
public class ProductAPI {

    @Autowired
    private CustomerProductService customerProductService;

    @Autowired
    private Environment environment;

    Log logger = LogFactory.getLog(ProductAPI.class);

    // ================= GET ALL PRODUCTS =================

    @GetMapping(value = "/products")
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() throws EKartException {

        logger.info("Received request to fetch all products");

        List<ProductResponseDTO> products = customerProductService.getAllProducts();

        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // ================= GET PRODUCT BY ID =================

    @GetMapping(value = "/product/{productId}")
    public ResponseEntity<ProductResponseDTO> getProductById(
            @PathVariable Integer productId) throws EKartException {
    	
    	System.out.println(Thread.currentThread().getName());

        logger.info("Received request to fetch product details for productId: " + productId);

        ProductResponseDTO product = customerProductService.getProductById(productId);

        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    // ================= REDUCE QUANTITY =================

    @PutMapping(value = "/update/{productId}")
    public ResponseEntity<String> reduceAvailableQuantity(
            @PathVariable Integer productId,
            @RequestBody String quantity) throws EKartException {

        logger.info("Received request to reduce quantity for productId: " + productId);

        customerProductService.reduceAvailableQuantity(productId, Integer.parseInt(quantity));

        return new ResponseEntity<>(
                environment.getProperty("ProductAPI.REDUCE_QUANTITY_SUCCESSFULL"),
                HttpStatus.OK);
    }
    
    
    //=======================ADD PRODUCT =====================================
    
    @PostMapping("/product")
    public ResponseEntity<ProductResponseDTO> addProduct(@Valid @RequestBody ProductCreateRequestDTO request)  throws EKartException {
    	
    	logger.info("Received request to add new product: " + request.getName());

    	    ProductResponseDTO response =
    	            customerProductService.addProduct(request);

    	    logger.info("Product added successfully with id: " + response.getProductId());

    	    return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    
    //=========================DELETE PRODUCT================================
    //if product id is available in other tables referencing product table then will not be able to delete
    @DeleteMapping("/delete-product/{productId}")
    
    public ResponseEntity<String> deleteProduct( @PathVariable Integer productId) throws EKartException {

        logger.info("Deleting product: " + productId);

        customerProductService.deleteProduct(productId);

        return new ResponseEntity<>(
                environment.getProperty("ProductAPI.DELETE_SUCCESSFULL"),
                HttpStatus.OK);
    }
}
