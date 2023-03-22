package com.afh.gbm.controllers;


import com.afh.gbm.dto.Account;
import com.afh.gbm.dto.AccountBalance;
import com.afh.gbm.services.interfaces.AccountServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountServiceImpl accountService;

    @PostMapping
    public ResponseEntity<AccountBalance> createAccount(@RequestBody Account account) {
        AccountBalance accountBalance = accountService.createAccount(account);
        return new ResponseEntity<>(accountBalance, HttpStatus.CREATED);
    }

    @PostMapping("/{accountId}/balance")
    public ResponseEntity<AccountBalance> checkAccountBalanceAndIssuers(@PathVariable Long accountId) {
        AccountBalance accountBalance = accountService.checkAccountBalance(accountId);
        return ResponseEntity.ok(accountBalance);
    }

}