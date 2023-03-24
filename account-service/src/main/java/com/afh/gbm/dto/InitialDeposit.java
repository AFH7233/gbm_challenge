package com.afh.gbm.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Represents the initial deposit when creating an account.
 *
 * @author Andres Fuentes Hernandez
 */
@Data
public class InitialDeposit {
  private BigDecimal cash;
}
