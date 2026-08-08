package br.com.finance.modules.event.dto;

import br.com.finance.modules.payroll.dto.PayrollEntity;

import java.util.List;

public record PayrollPayload(List<PayrollEntity> entities) {
}
