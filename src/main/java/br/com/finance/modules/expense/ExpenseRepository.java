package br.com.finance.modules.expense;

import br.com.finance.modules.expense.dto.ExpenseEntity;
import br.com.finance.modules.expense.dto.ExpensePaymentDto;
import br.com.finance.modules.summary.dto.SummaryExpenseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {

    @Query(
            value = """
                    SELECT
                        e.id as id,
                        e.due as due,
                        e.shared as shared,
                        e.name as name,
                        e.amount as amount,
                        e.detail as detail,
                        e.category as category,
                        ISNULL(t.id, 0) as integrated
            
                    FROM expense e
            
                    LEFT JOIN transaction_expense t
                        ON e.id = t.expense
            
                    WHERE
                        e.user_id = :userId
                        AND e.competence = :competence
            
                        AND (
                            :due IS NULL
                            OR e.due = :due
                        )
            
                        AND (
                            :categoryId IS NULL
                            OR e.category = :categoryId
                        )
            
                        AND (
                            :name IS NULL
                            OR :name = ''
                            OR LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%'))
                        )
                    """,

            countQuery = """
                    SELECT COUNT(*)
                    FROM expense e
                    WHERE
                        e.user_id = :userId
                        AND e.competence = :competence
                        AND (
                            :due IS NULL
                            OR e.due = :due
                        )
                        AND (
                            :categoryId IS NULL
                            OR e.category = :categoryId
                        )
                        AND (
                            :name IS NULL
                            OR :name = ''
                            OR LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%'))
                        )
                    """, nativeQuery = true)
    Page<ExpensePaymentDto> findAllByUserIdAndCompetence(
            @Param("userId")     String userId,
            @Param("competence") LocalDate competence,
            @Param("due")        LocalDate due,
            @Param("categoryId") Long categoryId,
            @Param("name")       String name,
            Pageable pageable
    );


    @Query("SELECT s FROM ExpenseEntity s WHERE s.id = :id AND s.userId = :userId AND s.competence = :competence")
    Optional<ExpenseEntity> findByIdUserIdAndCompetence(@Param("id") Long id, @Param("userId") String userId, @Param("competence") LocalDate competence);

    @Query("SELECT s FROM ExpenseEntity s WHERE s.id IN :ids AND s.userId = :userId AND s.competence = :competence")
    List<ExpenseEntity> findAllByDetailIdInAndUserId(@Param("ids") List<Long> ids, @Param("userId") String userId, @Param("competence") LocalDate competence);

    @Query(value = """
            SELECT COALESCE(SUM(amount), 0) as expense,
                   COALESCE(SUM(CASE WHEN t.id is not null THEN amount ELSE 0 END), 0) as expensePay
            FROM expense as e
            LEFT JOIN transaction_expense as t ON e.id = t.expense
            WHERE user_id = :userId
              AND competence = :competence
            """, nativeQuery = true)
    SummaryExpenseDto findSummaryExpense(
            @Param("userId") String userId,
            @Param("competence") LocalDate competence
    );

    @Query(value = "SELECT COUNT(*) " +
            "FROM expense as e " +
            "INNER JOIN transaction_expense as t ON e.id = t.expense " +
            "WHERE e.id = :id ", nativeQuery = true)
    Integer existsIntegratedById(@Param("id") Long id);

}
