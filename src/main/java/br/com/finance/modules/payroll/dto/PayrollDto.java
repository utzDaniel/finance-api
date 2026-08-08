package br.com.finance.modules.payroll.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface PayrollDto {

    Long getId();

    Integer getQuantity();

    BigDecimal getAmount();

    Integer getType();

    LocalDate getEntry();

    Integer getEvent();

    Integer getIntegrated();

}
