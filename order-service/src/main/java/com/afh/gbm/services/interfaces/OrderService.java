package com.afh.gbm.services.interfaces;

import com.afh.gbm.dto.Order;

public interface OrderService {
    void createOrder( long accountId, Order order);
}
