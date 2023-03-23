package com.afh.gbm.services;

import com.afh.gbm.constants.AccountTransactionType;
import com.afh.gbm.dao.AccountDAO;
import com.afh.gbm.dto.Account;
import com.afh.gbm.compound.AccountBalance;
import com.afh.gbm.dto.AccountTransaction;
import com.afh.gbm.services.interfaces.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountDAO accountDAO;


    @Override
    public Account createAccount(AccountTransaction accountTransaction) {
        return accountDAO.createAccount(accountTransaction);
    }

    @Override
    public Account updateTransaction(AccountTransaction accountTransaction) {
        if(Objects.nonNull(accountTransaction.getTransactionId()) && accountTransaction.getTransactionType() == AccountTransactionType.HOLD){
            accountDAO.updateAccountTransaction(accountTransaction);
        } else {
            accountDAO.createAccountTransaction(accountTransaction);
        }

        return accountDAO.getAccount(accountTransaction.getAccountId());
    }

    @Override
    public Account getAccount(long accountId) {
        return accountDAO.getAccount(accountId);
    }
}