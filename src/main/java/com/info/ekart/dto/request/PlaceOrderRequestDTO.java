package com.info.ekart.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

import com.info.ekart.dto.PaymentThrough;

public class PlaceOrderRequestDTO {

 
    @NotNull(message = "{order.paymentthrough.absent}")
    private PaymentThrough paymentThrough;


    @NotNull(message = "{order.dateofdelivery.absent}")
    @Future(message = "{order.dateofdelivery.invalid}")
    private LocalDateTime dateOfDelivery;

    @NotNull(message = "{order.deliveryaddress.absent}")
    private String deliveryAddress;

   

    public PaymentThrough getPaymentThrough() {
		return paymentThrough;
	}

	public void setPaymentThrough(PaymentThrough paymentThrough) {
		this.paymentThrough = paymentThrough;
	}

	public LocalDateTime getDateOfDelivery() {
        return dateOfDelivery;
    }

    public void setDateOfDelivery(LocalDateTime dateOfDelivery) {
        this.dateOfDelivery = dateOfDelivery;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
}
