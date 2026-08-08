package br.com.finance.modules.transaction;

import br.com.finance.modules.transaction.dto.TransactionExpenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface TransactionExpenseRepository extends JpaRepository<TransactionExpenseEntity, Long> {

    @Query(value = """
            SELECT t.expense
            FROM transaction_expense t
            WHERE t.expense IN ( :expenses )
            """, nativeQuery = true)
    Set<Long> findIdsByExpense(@Param("expenses") List<Long> expenses);

    @Query(value = "SELECT t FROM TransactionExpenseEntity t WHERE t.expense IN ( :expenses )")
    List<TransactionExpenseEntity> findByExpense(@Param("expenses") List<Long> expenses);

}
