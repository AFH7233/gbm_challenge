package com.afh.gbm.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * Holds common exceptions through the service.
 *
 * @author Andres Fuentes Hernandez
 */
@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BrokerAccountNotFoundException.class)
  public ResponseEntity<?> handleAccountNotFoundException(
      BrokerAccountNotFoundException ex, WebRequest request) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
  }
}
