package com.afh.gbm.services.interfaces;

import com.afh.gbm.compound.AccountBalance;
import com.afh.gbm.constants.BusinessErrorType;
import com.afh.gbm.dto.Order;

import java.util.Set;

/**
 * Validation layer for orders.
 *
 * @author Andres Fuentes Hernandez
 */
public interface BusinessValidatorService {
    Set<BusinessErrorType> validateBusinessRules(Order order, AccountBalance accountBalance);
}
