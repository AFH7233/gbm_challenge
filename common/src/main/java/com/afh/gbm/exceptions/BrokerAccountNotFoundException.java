package com.afh.gbm.exceptions;

/**
 * Represents an exception when no account is found.
 *
 * @author Andres Fuentes Hernandez
 */
public class BrokerAccountNotFoundException extends RuntimeException {

  public BrokerAccountNotFoundException(Long accountId) {
    super("Account not found with ID: " + accountId);
  }
}
