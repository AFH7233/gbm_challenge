package com.afh.gbm.dao;

import com.afh.gbm.dto.Account;
import com.afh.gbm.dto.AccountTransaction;
import com.afh.gbm.exceptions.BrokerAccountNotFoundException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

/**
 * Handles CRUD operations on accounts and account transactions.
 *
 * @author Andres Fuentes Hernandez
 */
@SuppressWarnings("ALL")
@Repository
public class AccountDAO {

  @Autowired private JdbcTemplate jdbcTemplate;

  /**
   * Creates a new account and an initial transaction with the given amount.
   *
   * @param accountTransaction The initial transaction to be created with the account.
   * @return The created account with its initial cash value.
   */
  public Account createAccount(AccountTransaction accountTransaction) {
    String sql = "{call create_account()}";
    int accountId =
        jdbcTemplate.execute(
            sql,
            (CallableStatementCallback<Integer>)
                cs -> {
                  cs.execute();
                  int updateCount = cs.getUpdateCount();
                  if (updateCount > 0) {
                    ResultSet rs = cs.executeQuery("SELECT LAST_INSERT_ID()");
                    if (rs.next()) {
                      return rs.getInt(1);
                    }
                  }
                  return -1;
                });

    accountTransaction.setAccountId((long) accountId);
    createAccountTransaction(accountTransaction);

    Account account = new Account();
    account.setId(accountId);
    account.setCash(accountTransaction.getAmount());

    return account;
  }

  /**
   * Gets the total cash for a given account.
   *
   * To calculate the total cash in an account it adds all the DEPOSIT transaction and substracts all the
   * RETIRE transactions.
   *
   * @param accountId The account ID to retrieve the total cash for.
   * @return The total cash value of the account.
   * @throws BrokerAccountNotFoundException If the account is not found.
   */
  public BigDecimal getTotalCashForAccount(long accountId) {
    String sql = "{call get_total_cash_for_account(?)}";
    BigDecimal totalCash =
        jdbcTemplate.execute(
            sql,
            (CallableStatementCallback<BigDecimal>)
                cs -> {
                  cs.setLong(1, accountId);
                  cs.execute();
                  ResultSet rs = cs.getResultSet();
                  if (rs.next()) {
                    return rs.getBigDecimal("total_cash");
                  } else {
                    throw new BrokerAccountNotFoundException(accountId);
                  }
                });
    return totalCash;
  }

  /**
   * Retrieves an account and its cash value by account ID.
   *
   * @param accountId The ID of the account to retrieve.
   * @return The account with the given ID.
   * @throws BrokerAccountNotFoundException If the account is not found.
   */
  public Account getAccount(long accountId) {
    Account account = new Account();

    String sql = "{call get_account(?)}";
    jdbcTemplate.query(
        sql,
        (ResultSetExtractor<Void>)
            rs -> {
              if (rs.next()) {
                account.setId(accountId);
              } else {
                throw new BrokerAccountNotFoundException(accountId);
              }
              return null;
            },
        accountId);

    BigDecimal cash = this.getTotalCashForAccount(accountId);
    account.setCash(cash);
    return account;
  }

  /**
   * Creates a new account transaction with the given details.
   *
   * @param accountTransaction The details of the transaction to be created.
   */
  public void createAccountTransaction(AccountTransaction accountTransaction) {
    long timestamp =
        Objects.isNull(accountTransaction.getTimestamp())
            ? Instant.now().toEpochMilli()
            : accountTransaction.getTimestamp();
    String sql = "{call create_account_transaction(?, ?, ?, ?)}";
    jdbcTemplate.execute(
        sql,
        (CallableStatementCallback<Void>)
            cs -> {
              cs.setLong(1, accountTransaction.getAccountId());
              cs.setBigDecimal(2, accountTransaction.getAmount());
              cs.setString(3, accountTransaction.getTransactionType().name());
              cs.setTimestamp(4, new Timestamp(timestamp));
              cs.execute();
              return null;
            });
  }

  /**
   * Updates an existing account transaction with the given details.
   *
   * @param accountTransaction The details of the transaction to be updated.
   */
  public void updateAccountTransaction(AccountTransaction accountTransaction) {
    String sql = "{call update_account_transaction(?, ?, ?, ?, ?)}";
    jdbcTemplate.execute(
        sql,
        (CallableStatementCallback<Void>)
            cs -> {
              cs.setLong(1, accountTransaction.getTransactionId());
              cs.setLong(2, accountTransaction.getAccountId());
              cs.setBigDecimal(3, accountTransaction.getAmount());
              cs.setString(4, accountTransaction.getTransactionType().name());
              cs.setTimestamp(5, new Timestamp(accountTransaction.getTimestamp()));
              cs.execute();
              return null;
            });
  }
}
