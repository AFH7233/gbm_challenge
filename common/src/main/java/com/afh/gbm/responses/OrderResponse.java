package com.afh.gbm.responses;

import com.afh.gbm.compound.AccountBalance;
import com.afh.gbm.constants.BusinessErrorType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * Response after an order request.
 *
 * @property currentBalance The current balance of the account after processing the order as well as
 *     owned shares.
 * @property businessErrors A set of business errors that may have occurred during the processing of
 *     the order.
 * @author Andres Fuentes Hernandez
 */
@Data
public class OrderResponse {
  @JsonIgnoreProperties(value = {"id"})
  private AccountBalance currentBalance;

  private Set<BusinessErrorType> businessErrors;

  public OrderResponse(AccountBalance accountBalance) {
    this.currentBalance = accountBalance;
    this.businessErrors = new HashSet<>();
  }

  public void addError(BusinessErrorType businessError) {
    this.businessErrors.add(businessError);
  }
}
