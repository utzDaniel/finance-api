package br.com.finance.modules.summary.dto;

import java.math.BigDecimal;

public interface SummaryPayrollDto {

    BigDecimal getGrossSalary();

    BigDecimal getNetSalary();
}
