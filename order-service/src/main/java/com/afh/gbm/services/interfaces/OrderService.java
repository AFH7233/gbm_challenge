package com.afh.gbm.services.interfaces;

import com.afh.gbm.dto.Issuer;
import com.afh.gbm.dto.Order;

import java.util.List;
import java.util.Set;

public interface OrderService {
    void createOrder( long accountId, Order order);

    List<Issuer> getSharesHeldByAccount(long accountId);
}
