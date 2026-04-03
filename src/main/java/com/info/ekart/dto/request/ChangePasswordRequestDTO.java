package com.info.ekart.dto.request;

import jakarta.validation.constraints.NotNull;

public class ChangePasswordRequestDTO {

    @NotNull(message = "{email.absent}")
    private String emailId;

    @NotNull(message = "{password.absent}")
    private String oldPassword;

    @NotNull(message = "{password.absent}")
    private String newPassword;

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

    // getters & setters
    
    
    
}
