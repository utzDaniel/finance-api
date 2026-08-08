package br.com.finance.modules.transaction.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface TransactionDto {

    Long getId();

    Integer getMethod();

    String getName();

    Boolean getDebit();

    LocalDate getDateTransaction();

    BigDecimal getAmount();

    Integer getAccountId();

    String getAccountName();

}
