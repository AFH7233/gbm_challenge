package com.afh.gbm.responses;

import com.afh.gbm.dto.AccountBalance;
import com.afh.gbm.dto.BusinessErrorType;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
@Data
public class OrderResponse {
    @JsonIgnoreProperties(value = { "id" })
    private AccountBalance currentBalance;
    private List<BusinessErrorType> businessErrors;

    public OrderResponse(AccountBalance accountBalance){
        this.currentBalance = accountBalance;
        this.businessErrors = new ArrayList<>();
    }
    public void addError(BusinessErrorType businessError){
        this.businessErrors.add(businessError);
    }

}
