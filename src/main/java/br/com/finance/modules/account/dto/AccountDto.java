package br.com.finance.modules.account.dto;

import java.math.BigDecimal;

public interface AccountDto {

    Integer getId();

    String getName();

    Integer getBank();

    Integer getType();

    Integer getLink();

    BigDecimal getBalance();

}
