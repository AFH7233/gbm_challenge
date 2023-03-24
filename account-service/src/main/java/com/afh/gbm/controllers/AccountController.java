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

/**
 * REST controller for handling account-related operations.
 *
 * @author Andres Fuentes Hernandez
 */
@RestController
@RequestMapping("/accounts")
public class AccountController {

  @Autowired private AccountService accountService;

  /**
   * Creates a new account with an initial deposit.
   *
   * @param deposit The initial deposit.
   * @return A ResponseEntity containing the created account's balance.
   */
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

  /**
   * Retrieves an account by its ID.
   *
   * @param accountId The ID of the account to be retrieved.
   * @return A ResponseEntity containing the account.
   */
  @GetMapping(value = "/{accountId}")
  public ResponseEntity<Account> getAccount(@PathVariable long accountId) {
    Account account = accountService.getAccount(accountId);
    return new ResponseEntity<>(account, HttpStatus.CREATED);
  }

  /**
   * Creates or Updates an account transaction.
   *
   * @param accountTransaction The details of the transaction to be updated.
   * @return A ResponseEntity containing the updated account and HTTP status.
   */
  @PostMapping("/transaction")
  public ResponseEntity<Account> updateTransaction(
      @RequestBody AccountTransaction accountTransaction) {
    Account account = accountService.updateTransaction(accountTransaction);
    return ResponseEntity.ok(account);
  }
}
