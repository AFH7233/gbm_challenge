package com.afh.gbm.dao;

import com.afh.gbm.dto.Account;
import com.afh.gbm.dto.AccountBalance;
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
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
@Repository
public class AccountDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public AccountBalance createAccount(Account account) {
        BigDecimal cash = account.getCash();

        String sql = "{call create_account(?)}";
        int accountId = jdbcTemplate.execute(sql, (CallableStatementCallback<Integer>) cs -> {
            cs.setBigDecimal(1, cash);
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

        AccountBalance accountBalance = new AccountBalance();
        accountBalance.setId(accountId);
        accountBalance.setCash(cash);

        return accountBalance;
    }

    public AccountBalance checkAccountBalance(long accountId) {
        AccountBalance accountBalance = new AccountBalance();

        String sql = "{call get_account(?)}";
        jdbcTemplate.query(sql, (ResultSetExtractor<Void>) rs -> {
            if (rs.next()) {
                accountBalance.setId(accountId);
                accountBalance.setCash(rs.getBigDecimal("cash"));
            } else {
                throw new BrokerAccountNotFoundException(accountId);
            }
            return null;
        }, accountId);

        String issuersSql = "{call get_issuers_from_account(?)}";
        List<Issuer> issuers = new ArrayList<>();
        jdbcTemplate.execute(issuersSql, (CallableStatementCallback<Void>) cs -> {
            cs.setLong(1, accountId);
            cs.execute();
            ResultSet rs = cs.getResultSet();
            while (rs.next()) {
                Issuer issuer = issuerRowMapper.mapRow(rs, rs.getRow());
                issuers.add(issuer);
            }
            return null;
        });

        accountBalance.setIssuers(issuers);

        return accountBalance;
    }

    private final RowMapper<Issuer> issuerRowMapper = (rs, rowNum) -> {
        Issuer issuer = new Issuer();
        issuer.setIssuerName(rs.getString("symbol"));
        issuer.setTotalShares(rs.getInt("shares"));
        issuer.setSharePrice(rs.getBigDecimal("share_price"));
        return issuer;
    };
}
