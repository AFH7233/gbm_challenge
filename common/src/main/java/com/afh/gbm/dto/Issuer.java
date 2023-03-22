package com.afh.gbm.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Issuer {
    private String issuerName;
    private int totalShares;
    private BigDecimal sharePrice;
}

