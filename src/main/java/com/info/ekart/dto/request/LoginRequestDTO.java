package com.info.ekart.dto.request;



import jakarta.validation.constraints.NotNull;




public class LoginRequestDTO {

    @NotNull(message = "{email.absent}")
    private String emailId;

    @NotNull(message = "{password.absent}")
    private String password;

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

    // getters & setters
    
    
}


    
    


