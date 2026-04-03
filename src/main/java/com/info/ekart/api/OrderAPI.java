package com.info.ekart.api;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.info.ekart.dto.request.PlaceOrderRequestDTO;
import com.info.ekart.dto.response.OrderResponseDTO;
import com.info.ekart.exception.EKartException;
import com.info.ekart.service.CustomerOrderService;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value = "/order-api")
@Validated
public class OrderAPI {

    @Autowired
    private CustomerOrderService orderService;

    @Autowired
    private Environment environment;

    // ================= PLACE ORDER =================

    @PostMapping("/customer/{customerEmailId:.+}/place-order")
    public ResponseEntity<String> placeOrder(
            @PathVariable @Email( message = "{invalid.customeremail.format}")  String customerEmailId,
            @Valid @RequestBody PlaceOrderRequestDTO request)
            throws EKartException {

        Long orderId = orderService.placeOrder(customerEmailId, request);

        String message =
                environment.getProperty("OrderAPI.ORDERED_PLACE_SUCCESSFULLY")
                + orderId;

        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    // ================= GET ORDER BY ID =================

    @GetMapping("/order/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrderDetails(
            @NotNull(message = "{orderId.absent}")
            @PathVariable Long orderId)
            throws EKartException {

        OrderResponseDTO response =
                orderService.getOrderDetails(orderId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // ================= GET ORDERS OF CUSTOMER =================

    @GetMapping("/customer/{customerEmailId:.+}/orders")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersOfCustomer(
            @NotNull(message = "{email.absent}")
            @PathVariable String customerEmailId)
            throws EKartException {

        List<OrderResponseDTO> responses =
                orderService.findOrdersByCustomerEmailId(customerEmailId);

        return new ResponseEntity<>(responses, HttpStatus.OK);
    }
    
    //===================== CANCLE ORDER ==========================
    
    /*
     * Steps:

1️⃣ Fetch order
2️⃣ Check if already cancelled
3️⃣ Check if delivered (if delivered → maybe not allowed)
4️⃣ If payment already confirmed → maybe initiate refund logic
5️⃣ Restore product stock
6️⃣ Set status to CANCELLED
7️⃣ Save order*/
    
}
