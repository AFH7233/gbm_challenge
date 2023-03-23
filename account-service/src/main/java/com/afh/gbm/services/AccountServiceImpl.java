package com.afh.gbm.services;

import com.afh.gbm.dao.AccountDAO;
import com.afh.gbm.dto.Account;
import com.afh.gbm.dto.AccountBalance;
import com.afh.gbm.services.interfaces.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountDAO accountDAO;

    @Override
    public AccountBalance createAccount(Account account) {
        return accountDAO.createAccount(account);
    }

    @Override
    public AccountBalance checkAccountBalance(long accountId) {
        return accountDAO.checkAccountBalance(accountId);
    }
}