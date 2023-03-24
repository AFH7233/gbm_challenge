package com.afh.gbm.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.afh.gbm.compound.AccountBalance;
import com.afh.gbm.constants.BusinessErrorType;
import com.afh.gbm.constants.OrderType;
import com.afh.gbm.dao.OrderDAO;
import com.afh.gbm.dto.Issuer;
import com.afh.gbm.dto.Order;
import java.math.BigDecimal;
import java.time.*;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class BusinessValidatorServiceImplTest {

  @InjectMocks private BusinessValidatorServiceImpl businessValidatorService;

  @Mock private OrderDAO orderDAO;

  private Order sampleOrder;
  private AccountBalance accountBalance;
  private long accountId = 1L;

  @BeforeEach
  public void setUp() {
    ReflectionTestUtils.setField(businessValidatorService, "marketOpenTime", "06:00");
    ReflectionTestUtils.setField(businessValidatorService, "marketCloseTime", "15:00");
    ReflectionTestUtils.setField(businessValidatorService, "allowedMinutesBetweenOperations", 5);

    sampleOrder = new Order();
    sampleOrder.setIssuerName("Test Issuer");
    sampleOrder.setOperation(OrderType.BUY.toString());
    sampleOrder.setSharePrice(new BigDecimal(100.00));
    sampleOrder.setTimestamp(getTimestamp(LocalTime.parse("07:00")));
    sampleOrder.setTotalShares(10);

    accountBalance = new AccountBalance();
    accountBalance.setId(accountId);
    accountBalance.setCash(new BigDecimal("5000.00"));
    Issuer issuer = new Issuer();
    issuer.setIssuerName("Test Issuer");
    issuer.setTotalShares(20);
    issuer.setSharePrice(new BigDecimal(100.00));
    accountBalance.getIssuersMap().put(issuer.getIssuerName(), issuer);
  }

  @Test
  public void testValidateBusinessRules() {
    when(orderDAO.getLastOrderByAccount(accountId)).thenReturn(Optional.empty());

    Set<BusinessErrorType> validationResult =
        businessValidatorService.validateBusinessRules(sampleOrder, accountBalance);
    assertThat(validationResult).isEmpty();
  }

  @Test
  public void testMarketClosed() {

    when(orderDAO.getLastOrderByAccount(accountId)).thenReturn(Optional.empty());
    sampleOrder.setTimestamp(getTimestamp(LocalTime.parse("18:00")));

    Set<BusinessErrorType> validationResult =
        businessValidatorService.validateBusinessRules(sampleOrder, accountBalance);
    assertThat(validationResult).contains(BusinessErrorType.CLOSED_MARKET);
  }

  @Test
  public void testDuplicatedOperation() {
    Order order = new Order();
    order.setTotalShares(sampleOrder.getTotalShares());
    order.setOperation(OrderType.BUY.toString());
    order.setIssuerName(sampleOrder.getIssuerName());
    order.setTimestamp(getTimestamp(LocalTime.parse("08:02")));

    when(orderDAO.getLastOrderByAccount(accountId)).thenReturn(Optional.of(order));
    sampleOrder.setTimestamp(getTimestamp(LocalTime.parse("08:00")));

    Set<BusinessErrorType> validationResult =
        businessValidatorService.validateBusinessRules(sampleOrder, accountBalance);
    assertThat(validationResult).contains(BusinessErrorType.DUPLICATED_OPERATION);
  }

  @Test
  public void testNotDuplicatedOperation() {
    Order order = new Order();
    order.setTotalShares(sampleOrder.getTotalShares());
    order.setOperation(OrderType.BUY.toString());
    order.setIssuerName(sampleOrder.getIssuerName());
    order.setTimestamp(getTimestamp(LocalTime.parse("08:06")));

    when(orderDAO.getLastOrderByAccount(accountId)).thenReturn(Optional.of(order));
    sampleOrder.setTimestamp(getTimestamp(LocalTime.parse("08:00")));

    Set<BusinessErrorType> validationResult =
        businessValidatorService.validateBusinessRules(sampleOrder, accountBalance);
    assertThat(validationResult).isEmpty();
  }

  private long getTimestamp(LocalTime localTime) {
    LocalDate localDate = LocalDate.now();
    LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
    Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
    return instant.toEpochMilli();
  }
}
