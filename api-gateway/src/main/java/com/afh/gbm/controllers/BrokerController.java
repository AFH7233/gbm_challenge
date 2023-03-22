package com.afh.gbm.controllers;

import com.afh.gbm.dto.Account;
import com.afh.gbm.dto.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class BrokerController {
    @PostMapping
    public ResponseEntity<String> createAccount(@RequestBody Account account) {
        // Implement account creation logic here
        double initialBalance = account.getCash();
        return new ResponseEntity<>("Account created with initial balance: " + initialBalance, HttpStatus.CREATED);
    }

    @PostMapping("/{accountId}/orders")
    public ResponseEntity<String> processOrder(
            @PathVariable("accountId") String accountId,
            @RequestBody Order order) {

        long timestamp = order.getTimestamp();
        String operationType = order.getOperation();
        String issuerName = order.getIssuerName();
        int totalShares = order.getTotalShares();
        double unitPrice = order.getSharePrice();

        // Implement order processing logic here
        return new ResponseEntity<>("Order processed for account: " + accountId, HttpStatus.CREATED);
    }
}
