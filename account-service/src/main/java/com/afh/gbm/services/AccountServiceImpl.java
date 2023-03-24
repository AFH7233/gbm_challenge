package com.afh.gbm.services;

import com.afh.gbm.constants.AccountTransactionType;
import com.afh.gbm.dao.AccountDAO;
import com.afh.gbm.dto.Account;
import com.afh.gbm.dto.AccountTransaction;
import com.afh.gbm.services.interfaces.AccountService;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Manages AccountDAO actions.
 *
 * @author Andres Fuentes Hernandez
 */
@Service
public class AccountServiceImpl implements AccountService {

  @Autowired private AccountDAO accountDAO;

  /**
   * Creates a new account with the given account transaction.
   *
   * @param accountTransaction The initial transaction to be associated with the new account.
   * @return The created account.
   */
  @Override
  public Account createAccount(AccountTransaction accountTransaction) {
    return accountDAO.createAccount(accountTransaction);
  }

  /**
   * Updates an account transaction or creates a new transaction if the transaction ID is not
   * present. If the transaction type is HOLD and a transaction ID is provided, the transaction will
   * be updated. Otherwise, a new transaction will be created.
   *
   * @param accountTransaction The details of the transaction to be updated or created.
   * @return The account associated with the transaction.
   */
  @Override
  public Account updateTransaction(AccountTransaction accountTransaction) {
    if (Objects.nonNull(accountTransaction.getTransactionId())
        && accountTransaction.getTransactionType() == AccountTransactionType.HOLD) {
      accountDAO.updateAccountTransaction(accountTransaction);
    } else {
      accountDAO.createAccountTransaction(accountTransaction);
    }

    return accountDAO.getAccount(accountTransaction.getAccountId());
  }

  /**
   * Retrieves an account by its ID.
   *
   * @param accountId The ID of the account to be retrieved.
   * @return The account with the given ID.
   */
  @Override
  public Account getAccount(long accountId) {
    return accountDAO.getAccount(accountId);
  }
}
