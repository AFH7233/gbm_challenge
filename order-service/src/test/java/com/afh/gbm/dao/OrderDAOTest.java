package com.afh.gbm.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import com.afh.gbm.configuration.OrderServiceApplication;

import com.afh.gbm.dto.Issuer;
import com.afh.gbm.dto.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = OrderServiceApplication.class)
@ActiveProfiles("test")
@Transactional
public class OrderDAOTest {

  @Autowired private OrderDAO orderDAO;

  private Order sampleOrder;
  private long sampleAccountId = 1L;

  @BeforeEach
  public void setUp() {
    sampleOrder = new Order();
    sampleOrder.setIssuerName("Test Issuer");
    sampleOrder.setOperation("BUY");
    sampleOrder.setSharePrice(new BigDecimal("100.00"));
    sampleOrder.setTimestamp(System.currentTimeMillis());
    sampleOrder.setTotalShares(10);
  }

  @Test
  public void testCreateOrder() {
    orderDAO.createOrder(sampleAccountId, sampleOrder);

    Optional<Order> lastOrder = orderDAO.getLastOrderByAccount(sampleAccountId);
    assertThat(lastOrder.isPresent()).isTrue();
    assertThat(lastOrder.get().getIssuerName()).isEqualTo(sampleOrder.getIssuerName());
    assertThat(lastOrder.get().getOperation()).isEqualTo(sampleOrder.getOperation());
    assertThat(lastOrder.get().getSharePrice()).isEqualByComparingTo(sampleOrder.getSharePrice());
    assertThat(lastOrder.get().getTotalShares()).isEqualTo(sampleOrder.getTotalShares());
  }

  @Test
  public void testGetSharesHeldByAccount() {
    orderDAO.createOrder(sampleAccountId, sampleOrder);

    List<Issuer> issuers = orderDAO.getSharesHeldByAccount(sampleAccountId);
    assertThat(issuers).isNotEmpty();
    assertThat(issuers.get(0).getIssuerName()).isEqualTo(sampleOrder.getIssuerName());
    assertThat(issuers.get(0).getTotalShares()).isEqualTo(sampleOrder.getTotalShares());
    assertThat(issuers.get(0).getSharePrice()).isEqualByComparingTo(sampleOrder.getSharePrice());
  }

  @Test
  public void testGetLastOrderByAccount() {
    orderDAO.createOrder(sampleAccountId, sampleOrder);

    Optional<Order> lastOrder = orderDAO.getLastOrderByAccount(sampleAccountId);
    assertThat(lastOrder.isPresent()).isTrue();
    assertThat(lastOrder.get().getIssuerName()).isEqualTo(sampleOrder.getIssuerName());
    assertThat(lastOrder.get().getOperation()).isEqualTo(sampleOrder.getOperation());
    assertThat(lastOrder.get().getSharePrice()).isEqualByComparingTo(sampleOrder.getSharePrice());
    assertThat(lastOrder.get().getTotalShares()).isEqualTo(sampleOrder.getTotalShares());
  }
}
