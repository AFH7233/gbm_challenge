package com.afh.gbm.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Represents the issuer of the financial instrument.
 *
 * @property issuerName The name of the issuer of the financial instrument (e.g., a company for
 *     stocks).
 * @property totalShares The total number of shares issued by the issuer.
 * @property sharePrice The current price per share for the issuer's financial instrument.
 * @author Andres Fuentes Hernandez
 */
@Data
public class Issuer {
  private String issuerName;
  private int totalShares;
  private BigDecimal sharePrice;
}
