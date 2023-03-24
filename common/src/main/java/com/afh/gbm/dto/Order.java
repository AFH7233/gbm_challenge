package com.afh.gbm.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Represents a BUY/SELL order.
 *
 * @property timestamp The timestamp (in milliseconds since epoch) when the order was created.
 * @property operation The type of operation (e.g., buy, sell) represented by the order.
 * @property issuerName The name of the issuer of the financial instrument (e.g., a company for
 *     stocks).
 * @property totalShares The total number of shares involved in the order.
 * @property sharePrice The price per share for the order.
 * @author Andres Fuentes Hernandez
 */
@Data
public class Order {
  private long timestamp;
  private String operation;
  private String issuerName;
  private int totalShares;
  private BigDecimal sharePrice;
}
