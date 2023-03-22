package com.afh.gbm.dto;

import com.afh.gbm.dto.Issuer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
public class AccountBalance {
    private long id;
    private BigDecimal cash;
    private List<Issuer> issuers;

    {
        this.issuers = new ArrayList<>();
    }

    public void addIssuer(Issuer issuer) {
        issuers.add(issuer);
    }
}
