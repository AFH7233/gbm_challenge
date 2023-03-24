package com.afh.gbm.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Represents the user account.
 *
 * @property id The unique identifier of the account associated with the transaction.
 * @property cash The total in the user´s wallet.
 * @author Andres Fuentes Hernandez
 */
@Data
public class Account {

  private long id;
  private BigDecimal cash;
}
