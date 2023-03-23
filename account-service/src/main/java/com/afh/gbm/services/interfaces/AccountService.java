package com.afh.gbm.services.interfaces;

import com.afh.gbm.dto.Account;
import com.afh.gbm.compound.AccountBalance;
import com.afh.gbm.dto.AccountTransaction;

public interface AccountService {
    Account createAccount(AccountTransaction accountTransaction) ;

    Account updateTransaction(AccountTransaction accountTransaction);

    Account getAccount(long accountId);
}
