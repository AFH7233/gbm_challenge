package com.afh.gbm.dao;

import com.afh.gbm.constants.OrderType;
import com.afh.gbm.dto.Issuer;
import com.afh.gbm.dto.Order;
import java.sql.Timestamp;
import java.util.List;import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * Creates abd retrieves information about the Orders.
 *
 * @author Andres Fuentes Hernandez
 */
@Repository
public class OrderDAO {
  @Autowired private JdbcTemplate jdbcTemplate;

  /**
   * Creates an order history entry in the database.
   *
   * @param accountId The account ID associated with the order.
   * @param order The order containing details such as operation, issuer name, total shares, share
   *     price, and timestamp.
   */
  public void createOrder(long accountId, Order order) {
    Timestamp orderTimestamp = new Timestamp(order.getTimestamp());
    jdbcTemplate.update(
        "CALL create_order_history_entry(?, ?, ?, ?, ?, ?)",
        accountId,
        order.getOperation().toString(),
        order.getIssuerName(),
        order.getTotalShares(),
        order.getSharePrice(),
        orderTimestamp);
  }

  /**
   * Retrieves the list of shares held by an account.
   *
   * @param accountId The account ID to retrieve the shares for.
   * @return A list of Issuer objects containing the issuer name, total shares, and share price.
   */
  public List<Issuer> getSharesHeldByAccount(long accountId) {
    String sql = "CALL get_shares_held_by_account(?)";
    List<Issuer> issuers = jdbcTemplate.query(sql, new Object[] {accountId}, issuerRowMapper);
    return issuers;
  }

  /**
   * Retrieves the last order for a given account based on the timestamp.
   *
   * @param accountId The account ID to retrieve the last order for.
   * @return The last Order object for the account.
   */
  public Optional<Order> getLastOrderByAccount(long accountId) {
    String sql = "CALL get_last_order_by_account(?)";
    List<Order> orders = jdbcTemplate.query(sql, new Object[] {accountId}, orderRowMapper);
    return orders.isEmpty() ? Optional.empty() : Optional.of(orders.get(0));
  }

  private final RowMapper<Issuer> issuerRowMapper =
      (rs, rowNum) -> {
        Issuer issuer = new Issuer();
        issuer.setIssuerName(rs.getString("issuer_name"));
        issuer.setTotalShares(rs.getInt("total_shares"));
        issuer.setSharePrice(rs.getBigDecimal("last_share_price"));
        return issuer;
      };

  private final RowMapper<Order> orderRowMapper =
      (rs, rowNum) -> {
        Order order = new Order();
        order.setIssuerName(rs.getString("issuer_name"));
        order.setOperation(rs.getString("operation"));
        order.setSharePrice(rs.getBigDecimal("share_price"));
        order.setTimestamp(rs.getTimestamp("timestamp").getTime());
        order.setTotalShares(rs.getInt("total_shares"));
        return order;
      };
}
