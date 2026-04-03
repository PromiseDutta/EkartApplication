package com.info.ekart.dto.request;

import jakarta.validation.constraints.*;

public class MakePaymentRequestDTO {

    @NotNull(message = "{transaction.cardId.notpresent}")
    private Integer cardId;

    @NotNull(message = "{transaction.cvv.notpresent}")
    private String cvv;
    
    public Integer getCardId() {
        return cardId;
    }

    public void setCardId(Integer cardId) {
        this.cardId = cardId;
    }

	public String getCvv() {
		return cvv;
	}

	public void setCvv(String cvv) {
		this.cvv = cvv;
	}

	

    
}
