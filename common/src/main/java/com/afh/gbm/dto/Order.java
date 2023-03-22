package com.afh.gbm.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Order {
    private long timestamp;
    private String operation;
    private String issuerName;
    private int totalShares;
    private BigDecimal sharePrice;

}