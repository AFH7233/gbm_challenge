package com.afh.gbm.dto;

import lombok.Data;

@Data
public class Order {
    private long timestamp;
    private String operation;
    private String issuerName;
    private int totalShares;
    private double sharePrice;

}