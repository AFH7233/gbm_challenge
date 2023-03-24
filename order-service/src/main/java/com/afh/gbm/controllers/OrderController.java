package com.afh.gbm.controllers;

import com.afh.gbm.compound.AccountBalance;
import com.afh.gbm.constants.AccountTransactionType;
import com.afh.gbm.constants.BusinessErrorType;
import com.afh.gbm.constants.OrderType;
import com.afh.gbm.dto.Account;
import com.afh.gbm.dto.AccountTransaction;
import com.afh.gbm.dto.Order;
import com.afh.gbm.exceptions.BrokerAccountNotFoundException;
import com.afh.gbm.responses.OrderResponse;
import com.afh.gbm.services.interfaces.BusinessValidatorService;
import com.afh.gbm.services.interfaces.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.*;

/**
 * REST controller for handling orders-related operations.
 *
 * @author Andres Fuentes Hernandez
 */
@RestController
@RequestMapping("/orders")
public class OrderController {

  private final WebClient accountServiceClient;

  private final BusinessValidatorService businessValidatorService;

  private final OrderService orderService;

  public OrderController(
      @Value("${account-service.base-url}") String accountServiceBaseUrl,
      @Autowired BusinessValidatorService businessValidatorService,
      @Autowired OrderService orderService) {
    this.accountServiceClient = WebClient.builder().baseUrl(accountServiceBaseUrl).build();
    this.businessValidatorService = businessValidatorService;
    this.orderService = orderService;
  }

  /**
   * Processes an order for the given account by validating the order against business rules,
   * updating the account's balance, and creating an order history entry.
   *
   * @param accountId The ID of the account for which the order is being processed.
   * @param order The order to be processed.
   * @return A {@link ResponseEntity} containing an {@link OrderResponse} object with the current
   *     account balance and any business errors found.
   */
  @PostMapping(value = "/{accountId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<OrderResponse> processOrder(
      @PathVariable("accountId") long accountId, @RequestBody Order order) {
    AccountBalance accountBalance = getAccountBalance(accountId);

    Set<BusinessErrorType> businessErrorTypes =
        businessValidatorService.validateBusinessRules(order, accountBalance);
    OrderResponse orderResponse = new OrderResponse(accountBalance);
    orderResponse.setBusinessErrors(businessErrorTypes);
    if (businessErrorTypes.isEmpty()) {
      orderService.createOrder(accountId, order);
      updateAccount(accountId, order);
      AccountBalance updatedAccountBalance = getAccountBalance(accountId);
      orderResponse.setCurrentBalance(updatedAccountBalance);
    }

    return new ResponseEntity<>(orderResponse, HttpStatus.OK);
  }

  private void updateAccount(long accountId, Order order) {
    if (order.getOperation().equals(OrderType.SELL.toString())) {
      AccountTransaction accountTransaction = new AccountTransaction();
      accountTransaction.setAccountId(accountId);
      accountTransaction.setTimestamp(order.getTimestamp());
      accountTransaction.setAmount(
          order.getSharePrice().multiply(new BigDecimal(order.getTotalShares())));
      accountTransaction.setTransactionType(AccountTransactionType.DEPOSIT);
      Mono<Account> depositCash =
          accountServiceClient
              .post()
              .uri("/accounts/transaction")
              .contentType(MediaType.APPLICATION_JSON)
              .body(BodyInserters.fromValue(accountTransaction))
              .retrieve()
              .bodyToMono(Account.class)
              .onErrorResume(
                  error -> {
                    if (error instanceof WebClientResponseException.NotFound) {
                      throw new BrokerAccountNotFoundException(accountId);
                    } else {
                      return Mono.error(error);
                    }
                  });
      depositCash.block();
    } else if (order.getOperation().equals(OrderType.BUY.toString())) {
      AccountTransaction accountTransaction = new AccountTransaction();
      accountTransaction.setAccountId(accountId);
      accountTransaction.setTimestamp(order.getTimestamp());
      accountTransaction.setAmount(
          order.getSharePrice().multiply(new BigDecimal(order.getTotalShares())));
      accountTransaction.setTransactionType(AccountTransactionType.RETIRE);
      Mono<Account> retrieveCash =
          accountServiceClient
              .post()
              .uri("/accounts/transaction")
              .contentType(MediaType.APPLICATION_JSON)
              .body(BodyInserters.fromValue(accountTransaction))
              .retrieve()
              .bodyToMono(Account.class)
              .onErrorResume(
                  error -> {
                    if (error instanceof WebClientResponseException.NotFound) {
                      throw new BrokerAccountNotFoundException(accountId);
                    } else {
                      return Mono.error(error);
                    }
                  });
      retrieveCash.block();
    }
  }

  private AccountBalance getAccountBalance(long accountId) {
    Mono<Account> accountMono =
        accountServiceClient
            .get()
            .uri("/accounts/{accountId}", accountId)
            .retrieve()
            .bodyToMono(Account.class)
            .onErrorResume(
                error -> {
                  if (error instanceof WebClientResponseException.NotFound) {
                    throw new BrokerAccountNotFoundException(accountId);
                  } else {
                    return Mono.error(error);
                  }
                });
    Account account = accountMono.block();

    AccountBalance accountBalance = new AccountBalance();
    accountBalance.setId(accountId);
    accountBalance.setCash(account.getCash());
    accountBalance.setIssuers(orderService.getSharesHeldByAccount(accountId));
    return accountBalance;
  }
}
