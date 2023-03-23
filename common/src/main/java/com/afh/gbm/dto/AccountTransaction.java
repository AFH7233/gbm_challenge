package com.afh.gbm.dto;

import com.afh.gbm.constants.AccountTransactionType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountTransaction {
    @JsonProperty("transaction_id")
    private Long transactionId;

    @JsonProperty("account_id")
    private Long accountId;

    @JsonProperty("timestamp")
    private Long timestamp;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("transaction_type")
    private AccountTransactionType transactionType;
}
