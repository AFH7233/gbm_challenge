package com.afh.gbm.dao;

import com.afh.gbm.configuration.AccountServiceApplication;
import com.afh.gbm.constants.AccountTransactionType;
import com.afh.gbm.dto.Account;
import com.afh.gbm.dto.AccountTransaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = AccountServiceApplication.class)
@ActiveProfiles("test")
public class AccountDAOTest {

  @Autowired private AccountDAO accountDAO;

  @Test
  public void testCreateAccount() {
    AccountTransaction accountTransaction = new AccountTransaction();
    accountTransaction.setAmount(new BigDecimal("100.00"));
    accountTransaction.setTransactionType(AccountTransactionType.DEPOSIT);

    Account account = accountDAO.createAccount(accountTransaction);

    assertNotNull(account);
    assertNotNull(account.getId());
    assertEquals(new BigDecimal("100.00"), account.getCash());

    BigDecimal totalCash = accountDAO.getTotalCashForAccount(account.getId());
    assertEquals(new BigDecimal("100.00"), totalCash);
  }

  @Test
  public void testCreateAccountTransaction() {
    AccountTransaction deposit = new AccountTransaction();
    deposit.setAmount(new BigDecimal(100.00));
    deposit.setTransactionType(AccountTransactionType.DEPOSIT);

    Account account = accountDAO.createAccount(deposit);

    AccountTransaction withdrawal = new AccountTransaction();
    withdrawal.setAccountId(account.getId());
    withdrawal.setAmount(new BigDecimal(50.00));
    withdrawal.setTransactionType(AccountTransactionType.WITHDRAWAL);

    accountDAO.createAccountTransaction(withdrawal);

    BigDecimal totalCash = accountDAO.getTotalCashForAccount(account.getId());
    BigDecimal error = new BigDecimal(0.0001);

    // This line subtracts the real value and expected value, then it expects the subtraction to be
    // less than the
    // allowed error.
    assertTrue(totalCash.subtract(new BigDecimal(50.00)).abs().compareTo(error) < 0);
  }
}
