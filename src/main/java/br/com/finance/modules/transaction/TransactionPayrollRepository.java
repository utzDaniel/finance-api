package br.com.finance.modules.transaction;

import br.com.finance.modules.transaction.dto.TransactionPayrollEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface TransactionPayrollRepository extends JpaRepository<TransactionPayrollEntity, Long> {

    @Query(value = """
            SELECT t.payroll
            FROM transaction_payroll t
            WHERE t.payroll IN ( :payrolls )
            """, nativeQuery = true)
    Set<Long> findIdsByPayroll(@Param("payrolls") List<Long> payrolls);

    @Query(value = "SELECT t FROM TransactionPayrollEntity t WHERE t.payroll IN ( :payrolls )")
    List<TransactionPayrollEntity> findByPayroll(@Param("payrolls") List<Long> payrolls);

}
