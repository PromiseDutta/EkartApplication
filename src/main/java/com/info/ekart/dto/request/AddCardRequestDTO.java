package com.info.ekart.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AddCardRequestDTO {

    @NotNull(message = "{order.paymentthrough.absent}")
    @Pattern(regexp = "(DEBIT_CARD|CREDIT_CARD)",
            message = "{order.paymentthrough.invalid}")
    private String cardType;

    @NotNull(message = "{card.number.absent}")
    @Pattern(regexp = "\\d{16}",
            message = "{card.invalid.number}")
    private String cardNumber;

    @NotNull(message = "{card.name.absent}")
    @Pattern(regexp = "([A-Za-z])+(\\s[A-Za-z]+)*",
            message = "{card.invalid.name}")
    private String nameOnCard;

    @NotNull(message = "{transaction.cvv.notpresent}")
    @Pattern(regexp = "\\d{3}",
            message = "{card.invalid.cvv}")
    private String cvv;

    @NotNull(message = "{card.expiry.absent}")
    @Future(message = "{card.invalid.expiry}")
    private LocalDateTime expiryDate;

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getNameOnCard() {
        return nameOnCard;
    }

    public void setNameOnCard(String nameOnCard) {
        this.nameOnCard = nameOnCard;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

	public LocalDateTime getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(LocalDateTime expiryDate) {
		this.expiryDate = expiryDate;
	}

   
}
