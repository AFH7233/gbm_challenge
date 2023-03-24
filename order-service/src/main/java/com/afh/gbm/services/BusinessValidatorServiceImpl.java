package com.afh.gbm.services;

import com.afh.gbm.compound.AccountBalance;
import com.afh.gbm.constants.BusinessErrorType;
import com.afh.gbm.constants.OrderType;
import com.afh.gbm.dto.Issuer;
import com.afh.gbm.dto.Order;
import com.afh.gbm.services.interfaces.BusinessValidatorService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;
@Service
public class BusinessValidatorServiceImpl implements BusinessValidatorService {

    @Value("${market.open.time:06:00}")
    private String marketOpenTime;

    @Value("${market.close.time:15:00}")
    private String marketCloseTime;
    public Set<BusinessErrorType> validateBusinessRules(Order order, AccountBalance accountBalance){
        Set<BusinessErrorType> businessErrorTypes = new HashSet<>();
        boolean isMarketOpen = checkMarketOpen(order.getTimestamp());

        if (!isMarketOpen) {
            businessErrorTypes.add(BusinessErrorType.CLOSED_MARKET);
        }

        if (checkDuplicateOperation(order)) {
            businessErrorTypes.add(BusinessErrorType.DUPLICATED_OPERATION);
        }

        if (order.getOperation().equalsIgnoreCase(OrderType.BUY.toString())) {
            if (!checkSufficientBalance(order, accountBalance)) {
                businessErrorTypes.add(BusinessErrorType.INSUFFICIENT_BALANCE);
            }
        } else if (order.getOperation().equalsIgnoreCase(OrderType.SELL.toString())) {
            if (!checkSufficientStocks(order, accountBalance)) {
                businessErrorTypes.add(BusinessErrorType.INSUFFICIENT_STOCKS);
            }
        } else {
            businessErrorTypes.add(BusinessErrorType.INVALID_OPERATION);
        }

        return businessErrorTypes;
    }

    private boolean checkMarketOpen(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        LocalTime currentTime = LocalTime.ofInstant(instant, ZoneId.systemDefault());
        LocalTime marketOpen = LocalTime.parse(marketOpenTime);
        LocalTime marketClose = LocalTime.parse(marketCloseTime);
        return (currentTime.isAfter(marketOpen) && currentTime.isBefore(marketClose));
    }

    private boolean checkDuplicateOperation(Order order) {
        // Implement a check for duplicate orders within the last 5 minutes here
        return false;
    }

    private boolean checkSufficientBalance(Order order, AccountBalance accountBalance) {
        BigDecimal totalOrderCost = order.getSharePrice().multiply(new BigDecimal(order.getTotalShares()));
        return accountBalance.getCash().compareTo(totalOrderCost) >= 0;
    }

    private boolean checkSufficientStocks(Order order, AccountBalance accountBalance) {
        int totalSharesToSell = order.getTotalShares();
        String issuerName = order.getIssuerName();
        if(accountBalance.getIssuersMap().containsKey(issuerName)) {
            Issuer issuer = accountBalance.getIssuersMap().get(issuerName);
            return issuer.getTotalShares() >= totalSharesToSell;
        }

        return false;
    }
}
