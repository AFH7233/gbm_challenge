package com.afh.gbm.dto;

import com.afh.gbm.constants.AccountTransactionType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Represents the issuer of the financial instrument.
 *
 * @property transactionId The unique identifier of the transaction.
 * @property accountId The unique identifier of the account associated with the transaction.
 * @property timestamp The timestamp (in milliseconds since epoch) when the transaction occurred.
 * @property amount The amount of the transaction.
 * @property transactionType The type of the transaction (e.g., DEPOSIT, WITHDRAW, HOLD).
 * @author Andres Fuentes Hernandez
 */
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
