package com.afh.gbm.exceptions;

public class BrokerAccountNotFoundException extends RuntimeException {

    public BrokerAccountNotFoundException(Long accountId) {
        super("Account not found with ID: " + accountId);
    }
}
