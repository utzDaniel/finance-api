package br.com.finance.modules.transaction.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transaction_payroll", schema = "dbo")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionPayrollEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payroll")
    private Long payroll;

    @Column(name = "transaction_account", nullable = false)
    private Long transactionAccount;

}
