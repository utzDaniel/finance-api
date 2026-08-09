package br.com.finance.modules.competence;

import br.com.finance.modules.competence.dto.CompetenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CompetenceRepository extends JpaRepository<CompetenceEntity, Integer> {

    @Query("SELECT c FROM CompetenceEntity c WHERE c.userId = :userId AND c.monthYear = :competence ")
    Optional<CompetenceEntity> findByUserIdAndCompetence(
            @Param("userId") String userId,
            @Param("competence") LocalDate competence
    );

    @Query("SELECT c FROM CompetenceEntity c WHERE c.userId = :userId ")
    List<CompetenceEntity> findByUserId(
            @Param("userId") String userId
    );

    @Query(value = """
           SELECT COUNT(*)
             FROM payroll as p
        LEFT JOIN transaction_payroll as t ON p.id = t.payroll
            WHERE p.user_id = :userId
              AND p.competence = :competence
              AND type != 1
              AND t.id is null
                """, nativeQuery = true)
    Integer existsPayrollByUserIdAndCompetence(
            @Param("userId") String userId,
            @Param("competence") LocalDate competence
    );

    @Query(value = """
           SELECT COUNT(*)
             FROM expense as e
        LEFT JOIN transaction_expense as t ON e.id = t.expense
            WHERE e.user_id = :userId
              AND e.competence = :competence
              AND t.id is null
                """, nativeQuery = true)
    Integer existsExpenseByUserIdAndCompetence(
            @Param("userId") String userId,
            @Param("competence") LocalDate competence
    );
}
