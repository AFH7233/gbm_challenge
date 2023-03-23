package com.afh.gbm.controllers;

import com.afh.gbm.dto.*;
import com.afh.gbm.responses.OrderResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RestController
@RequestMapping("/accounts")
public class BrokerController {
    private final WebClient accountServiceClient;

    public BrokerController(@Value("${account-service.base-url}") String accountServiceBaseUrl) {
        this.accountServiceClient = WebClient.builder()
                .baseUrl(accountServiceBaseUrl)
                .build();
    }

    @PostMapping
    public Mono<ResponseEntity<AccountBalance>> createAccount(@RequestBody Account account) {
        return accountServiceClient.post()
                .uri("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(account)
                .retrieve()
                .toEntity(AccountBalance.class)
                .map(responseEntity -> ResponseEntity.status(responseEntity.getStatusCode())
                        .headers(responseEntity.getHeaders())
                        .body(responseEntity.getBody()));
    }

    @PostMapping(value = "/{accountId}/orders",  produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderResponse> processOrder(
            @PathVariable("accountId") long accountId,
            @RequestBody Order order) {

        long timestamp = order.getTimestamp();
        String operationType = order.getOperation();
        String issuerName = order.getIssuerName();
        int totalShares = order.getTotalShares();
        BigDecimal unitPrice = order.getSharePrice();

        Issuer issuer = new Issuer();
        issuer.setIssuerName(issuerName);
        issuer.setSharePrice(unitPrice);
        issuer.setTotalShares(totalShares);

        AccountBalance accountBalance = new AccountBalance();
        accountBalance.setCash(new BigDecimal(100.0));
        accountBalance.addIssuer(issuer);
        accountBalance.setId(accountId);

        OrderResponse orderResponse = new OrderResponse(accountBalance);
        orderResponse.addError(BusinessErrorType.CLOSED_MARKET);

        // Implement order processing logic here
        return ResponseEntity.ok(orderResponse);
    }
}
