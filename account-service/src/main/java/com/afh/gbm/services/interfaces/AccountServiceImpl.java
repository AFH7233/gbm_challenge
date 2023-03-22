package com.afh.gbm.services.interfaces;

import com.afh.gbm.dto.Account;
import com.afh.gbm.dto.AccountBalance;

public interface AccountServiceImpl {
    AccountBalance createAccount(Account account) ;

    AccountBalance checkAccountBalance(Long accountId);
}
