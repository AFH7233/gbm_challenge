package com.afh.gbm.dao;

import com.afh.gbm.dto.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public class OrderDAO{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void createOrder(long accountId, Order order) {
        Timestamp orderTimestamp = new Timestamp(order.getTimestamp());
        jdbcTemplate.update(
                "CALL create_order_history_entry(?, ?, ?, ?, ?, ?)",
                accountId,
                order.getOperation().toString(),
                order.getIssuerName(),
                order.getTotalShares(),
                order.getSharePrice(),
                orderTimestamp
        );
    }
}