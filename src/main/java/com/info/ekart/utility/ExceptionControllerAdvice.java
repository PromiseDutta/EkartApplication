package com.info.ekart.utility;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import javax.naming.AuthenticationException;

import jakarta.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.info.ekart.exception.EKartException;

@RestControllerAdvice
public class ExceptionControllerAdvice {
	@Autowired
	Environment environment;

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorInfo> generalExceptionHandler(Exception exception) {
		ErrorInfo error = new ErrorInfo();
		error.setErrorMessage(environment.getProperty("General.EXCEPTION_MESSAGE") + exception.getMessage());
		error.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		error.setTimestamp(LocalDateTime.now());
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(EKartException.class)
	public ResponseEntity<ErrorInfo> ekartExceptionHandler(EKartException exception) {
		ErrorInfo error = new ErrorInfo();
		error.setErrorMessage(environment.getProperty(exception.getMessage()));
		error.setTimestamp(LocalDateTime.now());
		error.setErrorCode(HttpStatus.BAD_REQUEST.value());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<String> handleInvalidEnum(HttpMessageNotReadableException ex) {
	    return ResponseEntity.badRequest().body("Invalid payment type");
	}
	
	
	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ErrorInfo> handleAuthenticationException(AuthenticationException ex){
		
	ErrorInfo errorinfo=new  ErrorInfo();
	errorinfo.setErrorMessage("Invalid credeential");
	errorinfo.setErrorCode(HttpStatus.UNAUTHORIZED.value());	
	errorinfo.setTimestamp(LocalDateTime.now());
	
	return new ResponseEntity<ErrorInfo>(errorinfo, HttpStatus.UNAUTHORIZED);
	}
	
	

	@ExceptionHandler({ MethodArgumentNotValidException.class, ConstraintViolationException.class })
	public ResponseEntity<ErrorInfo> exceptionHandler(Exception exception) {
		ErrorInfo errorInfo = new ErrorInfo();
		errorInfo.setErrorCode(HttpStatus.BAD_REQUEST.value());
		String errorMsg = "";
		if (exception instanceof MethodArgumentNotValidException) {
			MethodArgumentNotValidException exception1 = (MethodArgumentNotValidException) exception;
			errorMsg = exception1.getBindingResult().getAllErrors().stream().map(x -> x.getDefaultMessage())
					.collect(Collectors.joining(", "));
		} else {
			ConstraintViolationException exception1 = (ConstraintViolationException) exception;
			errorMsg = exception1.getConstraintViolations().stream().map(x -> x.getMessage())
					.collect(Collectors.joining(", "));
		}
		errorInfo.setErrorMessage(errorMsg);
		errorInfo.setTimestamp(LocalDateTime.now());
		return new ResponseEntity<>(errorInfo, HttpStatus.BAD_REQUEST);
	}

}