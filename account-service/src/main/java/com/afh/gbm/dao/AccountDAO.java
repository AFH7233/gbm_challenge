package com.afh.gbm.dao;

import com.afh.gbm.dto.Account;
import com.afh.gbm.compound.AccountBalance;
import com.afh.gbm.dto.AccountTransaction;
import com.afh.gbm.dto.Issuer;
import com.afh.gbm.exceptions.BrokerAccountNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("ALL")
@Repository
public class AccountDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Account createAccount(AccountTransaction accountTransaction) {
        String sql = "{call create_account()}";
        int accountId = jdbcTemplate.execute(sql, (CallableStatementCallback<Integer>) cs -> {
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

    public BigDecimal getTotalCashForAccount(long accountId) {
        String sql = "{call get_total_cash_for_account(?)}";
        BigDecimal totalCash = jdbcTemplate.execute(sql, (CallableStatementCallback<BigDecimal>) cs -> {
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

    public Account getAccount(long accountId) {
        Account account = new Account();

        String sql = "{call get_account(?)}";
        jdbcTemplate.query(sql, (ResultSetExtractor<Void>) rs -> {
            if (rs.next()) {
                account.setId(accountId);
            } else {
                throw new BrokerAccountNotFoundException(accountId);
            }
            return null;
        }, accountId);

        BigDecimal cash = this.getTotalCashForAccount(accountId);
        account.setCash(cash);
        return account;
    }

    public void createAccountTransaction(AccountTransaction accountTransaction) {
        long timestamp = Objects.isNull(accountTransaction.getTimestamp()) ? Instant.now().toEpochMilli() : accountTransaction.getTimestamp();
        String sql = "{call create_account_transaction(?, ?, ?, ?)}";
        jdbcTemplate.execute(sql, (CallableStatementCallback<Void>) cs -> {
            cs.setLong(1, accountTransaction.getAccountId());
            cs.setBigDecimal(2, accountTransaction.getAmount());
            cs.setString(3, accountTransaction.getTransactionType().name());
            cs.setTimestamp(4, new Timestamp(timestamp));
            cs.execute();
            return null;
        });
    }

    public void updateAccountTransaction(AccountTransaction accountTransaction) {
        String sql = "{call update_account_transaction(?, ?, ?, ?, ?)}";
        jdbcTemplate.execute(sql, (CallableStatementCallback<Void>) cs -> {
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
