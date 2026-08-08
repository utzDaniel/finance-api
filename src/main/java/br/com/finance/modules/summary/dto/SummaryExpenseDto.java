package br.com.finance.modules.summary.dto;

import java.math.BigDecimal;

public interface SummaryExpenseDto {

    BigDecimal getExpense();

    BigDecimal getExpensePay();
}
