package com.afh.gbm.services;

import com.afh.gbm.dao.OrderDAO;
import com.afh.gbm.dto.Issuer;
import com.afh.gbm.dto.Order;
import com.afh.gbm.services.interfaces.OrderService;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Manages Order CRUD operations.
 *
 * @author Andres Fuentes Hernandez
 */
@Service
public class OrderServiceImpl implements OrderService {
  @Autowired private OrderDAO orderDAO;

  @Transactional
  public void createOrder(long accountId, Order order) {
    orderDAO.createOrder(accountId, order);
  }

  @Override
  public List<Issuer> getSharesHeldByAccount(long accountId) {
    return orderDAO.getSharesHeldByAccount(accountId);
  }
}
