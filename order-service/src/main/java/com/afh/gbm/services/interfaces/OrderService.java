package com.afh.gbm.services.interfaces;

import com.afh.gbm.dto.Issuer;
import com.afh.gbm.dto.Order;
import java.util.List;

/**
 * Query interface for Orders.
 *
 * @author Andres Fuentes Hernandez
 */
public interface OrderService {
  void createOrder(long accountId, Order order);

  List<Issuer> getSharesHeldByAccount(long accountId);
}
