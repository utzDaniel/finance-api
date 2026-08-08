package br.com.finance.modules.expense.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ExpensePaymentDto {

    Long getId();

    LocalDate getDue();

    boolean getShared();

    String getName();

    BigDecimal getAmount();

    int getCategory();

    String getDetail();

    Integer getIntegrated();

}
