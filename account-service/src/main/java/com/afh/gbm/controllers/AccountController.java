package com.afh.gbm.controllers;


import com.afh.gbm.compound.AccountBalance;
import com.afh.gbm.constants.AccountTransactionType;
import com.afh.gbm.dto.Account;

import com.afh.gbm.dto.AccountTransaction;
import com.afh.gbm.dto.InitialDeposit;
import com.afh.gbm.services.interfaces.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountBalance> createAccount(@RequestBody InitialDeposit deposit) {
        AccountTransaction accountTransaction = new AccountTransaction();
        accountTransaction.setAmount(deposit.getCash());
        accountTransaction.setTransactionType(AccountTransactionType.DEPOSIT);

        Account account = accountService.createAccount(accountTransaction);
        AccountBalance accountBalance = new AccountBalance();
        accountBalance.setId(account.getId());
        accountBalance.setCash(account.getCash());
        return new ResponseEntity<>(accountBalance, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{accountId}")
    public ResponseEntity<Account> getAccount(@PathVariable long accountId) {
        Account account = accountService.getAccount(accountId);
        return new ResponseEntity<>(account, HttpStatus.CREATED);
    }

    @PostMapping("/transaction")
    public ResponseEntity<Account> updateTransaction(@RequestBody AccountTransaction accountTransaction) {
        Account account = accountService.updateTransaction(accountTransaction);
        return ResponseEntity.ok(account);
    }

}