package com.afh.gbm.controllers;


import com.afh.gbm.compound.AccountBalance;
import com.afh.gbm.constants.BusinessErrorType;
import com.afh.gbm.constants.OrderType;
import com.afh.gbm.dto.Issuer;
import com.afh.gbm.dto.Order;
import com.afh.gbm.responses.OrderResponse;
import com.afh.gbm.services.interfaces.BusinessValidatorService;
import com.afh.gbm.services.interfaces.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/accounts")
public class OrderController {

    private final WebClient accountServiceClient;

    private final BusinessValidatorService businessValidatorService;

    private final OrderService orderService;

    public OrderController(@Value("${account-service.base-url}") String accountServiceBaseUrl,
                           @Autowired BusinessValidatorService businessValidatorService,
                           @Autowired OrderService orderService) {
        this.accountServiceClient = WebClient.builder()
                .baseUrl(accountServiceBaseUrl)
                .build();
        this.businessValidatorService = businessValidatorService;
        this.orderService = orderService;
    }
    @PostMapping(value = "/{accountId}/orders",  produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderResponse> processOrder(
            @PathVariable("accountId") long accountId,
            @RequestBody Order order) {

        Mono<AccountBalance> accountBalanceMono = accountServiceClient.post()
                .uri("/accounts/{accountId}/balance", accountId)
                .retrieve()
                .bodyToMono(AccountBalance.class);

        AccountBalance accountBalance = accountBalanceMono.block();
        Set<BusinessErrorType> businessErrorTypes = businessValidatorService.validateBusinessRules(order, accountBalance);
        OrderResponse orderResponse = new OrderResponse(accountBalance);
        orderResponse.setBusinessErrors(businessErrorTypes);
        if(businessErrorTypes.isEmpty()){
            orderService.createOrder(accountId, order);
            if(order.getOperation().equals(OrderType.SELL.toString())){
                AccountBalance updatedBalance = updateAfterSellAccountBalance(order, accountBalance);
                orderResponse.setCurrentBalance(updatedBalance);
            } else if(order.getOperation().equals(OrderType.BUY.toString())){
                AccountBalance updatedBalance = updateAfterBuyAccountBalance(order, accountBalance);
                orderResponse.setCurrentBalance(updatedBalance);
            }
        }

        return new ResponseEntity<>(orderResponse, HttpStatus.OK);
    }

    private AccountBalance updateAfterBuyAccountBalance(Order order, AccountBalance accountBalance){
        Map<String, Issuer> issuersMap = new HashMap<>(accountBalance.getIssuersMap());
        BigDecimal remaining = accountBalance.getCash().subtract( order.getSharePrice().multiply(new BigDecimal(order.getTotalShares())));
        if(issuersMap.containsKey(order.getIssuerName())){
            Issuer issuer = issuersMap.get(order.getIssuerName());
            Issuer updatedIssuer = new Issuer();
            updatedIssuer.setIssuerName(issuer.getIssuerName());
            updatedIssuer.setSharePrice(issuer.getSharePrice());
            updatedIssuer.setTotalShares(issuer.getTotalShares() + order.getTotalShares());
            issuersMap.put(updatedIssuer.getIssuerName(), updatedIssuer);
        } else {
            Issuer issuer = new Issuer();
            issuer.setIssuerName(order.getIssuerName());
            issuer.setSharePrice(order.getSharePrice());
            issuer.setTotalShares(order.getTotalShares());
            issuersMap.put(issuer.getIssuerName(), issuer);
        }
        AccountBalance updatedBalance = new AccountBalance();
        updatedBalance.setId(accountBalance.getId());
        updatedBalance.setCash(remaining);
        updatedBalance.setIssuers(new ArrayList<>(issuersMap.values()));
        return updatedBalance;
    }

    private AccountBalance updateAfterSellAccountBalance(Order order, AccountBalance accountBalance){
        Map<String, Issuer> issuersMap = new HashMap<>(accountBalance.getIssuersMap());
        Issuer issuer = issuersMap.get(order.getIssuerName());
        BigDecimal cash = accountBalance.getCash().add( order.getSharePrice().multiply(new BigDecimal(order.getTotalShares())));

        if (issuer.getTotalShares() == order.getTotalShares())  {
            issuersMap.remove(issuer.getIssuerName());

        } else {
            Issuer updatedIssuer = new Issuer();
            updatedIssuer.setIssuerName(issuer.getIssuerName());
            updatedIssuer.setSharePrice(issuer.getSharePrice());
            updatedIssuer.setTotalShares(issuer.getTotalShares() - order.getTotalShares());
            issuersMap.put(updatedIssuer.getIssuerName(), updatedIssuer);
        }
        AccountBalance updatedBalance = new AccountBalance();
        updatedBalance.setId(accountBalance.getId());
        updatedBalance.setCash(cash);
        updatedBalance.setIssuers(new ArrayList<>(issuersMap.values()));
        return updatedBalance;
    }
}