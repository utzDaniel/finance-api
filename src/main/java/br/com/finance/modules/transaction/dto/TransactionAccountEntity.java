package br.com.finance.modules.transaction.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transaction_account_transfer", schema = "dbo")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionAccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_origin")
    private Integer accountOrigin;

    @Column(name = "account_destination")
    private Integer accountDestination;

    @Column(name = "transaction_account", nullable = false)
    private Long transactionAccount;

}
