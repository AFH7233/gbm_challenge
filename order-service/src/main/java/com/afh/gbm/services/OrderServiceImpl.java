package com.afh.gbm.services;

import com.afh.gbm.dao.OrderDAO;
import com.afh.gbm.dto.Issuer;
import com.afh.gbm.services.interfaces.OrderService;
import org.springframework.stereotype.Service;
import com.afh.gbm.dto.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl  implements OrderService {
    @Autowired
    private OrderDAO orderDAO;

    @Transactional
    public void createOrder( long accountId, Order order) {
        orderDAO.createOrder(accountId, order);
    }

    @Override
    public List<Issuer> getSharesHeldByAccount(long accountId) {
        return orderDAO.getSharesHeldByAccount(accountId);
    }
}
