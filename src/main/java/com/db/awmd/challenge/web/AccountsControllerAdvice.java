package com.db.awmd.challenge.web;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.db.awmd.challenge.exception.InvalidAccountIdException;
import com.db.awmd.challenge.exception.OverdraftNotSupportedException;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class AccountsControllerAdvice {

	@ExceptionHandler({ InterruptedException.class })
	public ResponseEntity<Object> handleInterruptedException(Exception ex) throws IOException {
		log.error("Transaction failed. Please contact administrator.", ex.getMessage());
		return new ResponseEntity<Object>("Transaction failed. Please contact administrator.",
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler({ OverdraftNotSupportedException.class })
	public ResponseEntity<Object> handleOverdraftNotSupportedException(Exception ex, HttpServletResponse response)
			throws IOException {
		log.error("Not enough balance for fund transfer", ex.getMessage());
		return new ResponseEntity<Object>("Not enough balance for fund transfer", HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler({ InvalidAccountIdException.class })
	public ResponseEntity<Object> handleInvalidAccountIdException(Exception ex, HttpServletResponse response)
			throws IOException {
		log.error(ex.getMessage());
		return new ResponseEntity<Object>(ex.getMessage(), HttpStatus.NOT_ACCEPTABLE);
	}
}
