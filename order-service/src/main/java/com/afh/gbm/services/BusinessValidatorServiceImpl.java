package com.afh.gbm.services;

import com.afh.gbm.compound.AccountBalance;
import com.afh.gbm.constants.BusinessErrorType;
import com.afh.gbm.constants.OrderType;
import com.afh.gbm.dao.OrderDAO;
import com.afh.gbm.dto.Issuer;
import com.afh.gbm.dto.Order;
import com.afh.gbm.services.interfaces.BusinessValidatorService;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Validates business rules related to stock trading operations. It validates the following business
 * rules:
 *
 * <ul>
 *   <li>Insufficient Balance: When buying stocks, you must have enough cash to fulfill the order.
 *   <li>Insufficient Stocks: When selling stocks, you must have enough stocks to fulfill the order.
 *   <li>Duplicated Operation: No operations for the same stock at the same amount can occur within
 *       a 5-minute interval, as they are considered duplicates.
 *   <li>Closed Market: All operations must happen between 6am and 3pm.
 *   <li>Invalid Operation: Any other invalidity must be prevented.
 * </ul>
 *
 * @author Andres Fuentes Hernandez
 */
@Service
public class BusinessValidatorServiceImpl implements BusinessValidatorService {

  @Autowired private OrderDAO orderDAO;

  @Value("${market.open.time:06:00}")
  private String marketOpenTime;

  @Value("${market.close.time:15:00}")
  private String marketCloseTime;

  @Value("${allowed.minutes.between.operations:5}")
  private int allowedMinutesBetweenOperations;

  /**
   * Validates the given order against the business rules and returns a set of error types if any
   * are found.
   *
   * @param order The order to be validated.
   * @param accountBalance The account balance associated with the order.
   * @return A set of {@link BusinessErrorType} representing the errors found during validation.
   */
  public Set<BusinessErrorType> validateBusinessRules(Order order, AccountBalance accountBalance) {
    Set<BusinessErrorType> businessErrorTypes = new HashSet<>();
    boolean isMarketOpen = checkMarketOpen(order.getTimestamp());

    if (!isMarketOpen) {
      businessErrorTypes.add(BusinessErrorType.CLOSED_MARKET);
    }

    if (checkDuplicateOperation(accountBalance.getId(), order)) {
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

  private boolean checkDuplicateOperation(long accountId, Order order) {
    Optional<Order> latestOrderOptional = orderDAO.getLastOrderByAccount(accountId);

    if (latestOrderOptional.isPresent()
        && latestOrderOptional.get().getIssuerName().equals(order.getIssuerName())) {
      Order latestOrder = latestOrderOptional.get();
      Instant latestOrderInstant = Instant.ofEpochMilli(latestOrder.getTimestamp());
      Instant newOrderInstant = Instant.ofEpochMilli(order.getTimestamp());
      Duration timeDifference = Duration.between(latestOrderInstant, newOrderInstant);

      return (Math.abs(timeDifference.toMinutes()) < allowedMinutesBetweenOperations);
    }
    return false;
  }

  private boolean checkSufficientBalance(Order order, AccountBalance accountBalance) {
    BigDecimal totalOrderCost =
        order.getSharePrice().multiply(new BigDecimal(order.getTotalShares()));
    return accountBalance.getCash().compareTo(totalOrderCost) >= 0;
  }

  private boolean checkSufficientStocks(Order order, AccountBalance accountBalance) {
    int totalSharesToSell = order.getTotalShares();
    String issuerName = order.getIssuerName();
    if (accountBalance.getIssuersMap().containsKey(issuerName)) {
      Issuer issuer = accountBalance.getIssuersMap().get(issuerName);
      return issuer.getTotalShares() >= totalSharesToSell;
    }

    return false;
  }
}
