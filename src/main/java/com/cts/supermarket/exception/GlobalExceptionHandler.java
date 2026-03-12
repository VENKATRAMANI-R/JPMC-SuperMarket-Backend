package com.cts.supermarket.exception;


import java.time.LocalDateTime;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {	

		private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	    private ResponseEntity<ApiError> build(HttpStatus status, String message, HttpServletRequest req, List<String> details) {
	        ApiError body = ApiError.builder()
	                .timestamp(LocalDateTime.now())
	                .status(status.value())
	                .error(status.getReasonPhrase())
	                .message(message)
	                .path(req.getRequestURI())
	                .details(details)
	                .build();
	        return new ResponseEntity<>(body, status);
	    }
	
	    @ExceptionHandler(ItemNotFoundException.class)
	    public ResponseEntity<ApiError> handleItemNotFound(ItemNotFoundException ex, HttpServletRequest req) {
	        log.warn("Item not found: {}", ex.getMessage());
	        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req, null);
	    }


		@ExceptionHandler(CartItemNotFoundException.class)
		public ResponseEntity<ApiError> handleCartItemNotFound(CartItemNotFoundException ex, HttpServletRequest req) {
	        log.warn("Cart Item not found: {}", ex.getMessage());
	        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req, null);
		}


		@ExceptionHandler(Exception.class)
		public ResponseEntity<ApiError> handleGenericException(Exception ex, HttpServletRequest req) {
		    log.error("Unexpected error", ex);
		    return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", req, List.of(ex.getMessage()));
		}


}
