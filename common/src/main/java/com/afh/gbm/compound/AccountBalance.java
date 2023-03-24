package com.afh.gbm.compound;

import com.afh.gbm.dto.Issuer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * Current balance of the user, total cash in wallet and stocks owned.
 *
 * @author Andres Fuentes Hernandez
 */
@Data
public class AccountBalance {
  private long id;
  private BigDecimal cash;

  @Setter(AccessLevel.NONE)
  @JsonIgnore
  private Map<String, Issuer> issuersMap;

  {
    this.issuersMap = new HashMap();
  }

  public void addIssuer(Issuer issuer) {
    issuersMap.putIfAbsent(issuer.getIssuerName(), issuer);
  }

  public List<Issuer> getIssuers() {
    return new ArrayList<>(issuersMap.values());
  }

  public void setIssuers(List<Issuer> issuersList) {
    this.issuersMap =
        issuersList.stream().collect(Collectors.toMap(Issuer::getIssuerName, Function.identity()));
  }
}
