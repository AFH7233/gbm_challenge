package com.afh.gbm.services.interfaces;

import com.afh.gbm.dto.Account;
import com.afh.gbm.dto.AccountTransaction;

/**
 * Service layer for accounts.
 *
 * @author Andres Fuentes Hernandez
 */
public interface AccountService {
  Account createAccount(AccountTransaction accountTransaction);

  Account updateTransaction(AccountTransaction accountTransaction);

  Account getAccount(long accountId);
}
