package com.afh.gbm.dao;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.math.BigDecimal;
import java.util.List;

import com.afh.gbm.dto.Issuer;
import com.afh.gbm.dto.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

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

    public List<Issuer> getSharesHeldByAccount(long accountId) {
        String sql = "CALL get_shares_held_by_account(?)";
        List<Issuer> issuers = jdbcTemplate.query(sql, new Object[]{accountId}, issuerRowMapper);
        return issuers;
    }

    private final RowMapper<Issuer> issuerRowMapper = (rs, rowNum) -> {
        Issuer issuer = new Issuer();
        issuer.setIssuerName(rs.getString("issuer_name"));
        issuer.setTotalShares(rs.getInt("total_shares"));
        issuer.setSharePrice(rs.getBigDecimal("last_share_price"));
        return issuer;
    };
}