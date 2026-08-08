package br.com.finance.modules.payroll;

import br.com.finance.modules.payroll.dto.PayrollDto;
import br.com.finance.modules.payroll.dto.PayrollEntity;
import br.com.finance.modules.summary.dto.SummaryPayrollDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PayrollRepository extends JpaRepository<PayrollEntity, Long> {

    @Query(value = "SELECT " +
            "p.id as id, " +
            "p.quantity as quantity, " +
            "p.amount as amount, " +
            "p.type as type, " +
            "p.entry as entry, " +
            "p.event as event, " +
            "ISNULL(t.id, 0) as integrated " +
            "FROM payroll as p " +
            "LEFT JOIN transaction_payroll as t ON p.id = t.payroll " +
            "WHERE " +
            "p.user_id = :userId AND " +
            "p.competence = :competence ",
            countQuery = "SELECT count(*) FROM payroll as p " +
                    "WHERE p.user_id = :userId AND p.competence = :competence", nativeQuery = true)
    Page<PayrollDto> findAllByUserIdAndCompetence(
            @Param("userId") String userId,
            @Param("competence") LocalDate competence,
            Pageable pageable);

    @Query("SELECT p FROM PayrollEntity p WHERE p.id = :id AND p.userId = :userId AND p.competence = :competence")
    Optional<PayrollEntity> findByIdUserIdAndCompetence(@Param("id") Long id, @Param("userId") String userId, @Param("competence") LocalDate competence);

    @Query("SELECT p FROM PayrollEntity p WHERE p.id IN (:ids) AND p.userId = :userId AND p.competence = :competence")
    List<PayrollEntity> findAllByUserIdAndCompetence(@Param("ids") List<Long> ids, @Param("userId") String userId, @Param("competence") LocalDate competence);

    @Query(value = """
            SELECT COALESCE(SUM(CASE WHEN type != 3 THEN quantity * amount ELSE 0 END), 0) as grossSalary,
                   COALESCE(SUM(CASE WHEN type = 2 THEN quantity * amount ELSE 0 END), 0) as netSalary
            FROM payroll as p
            LEFT JOIN transaction_payroll as t ON p.id = t.payroll
            WHERE user_id = :userId
              AND competence = :competence
              AND (t.id is not null OR type = 1)
            """, nativeQuery = true)
    SummaryPayrollDto findSummaryPayroll(
            @Param("userId") String userId,
            @Param("competence") LocalDate competence
    );

    @Query(value = "SELECT COUNT(*) " +
            "FROM payroll as p " +
            "INNER JOIN transaction_payroll as t ON p.id = t.payroll " +
            "WHERE p.id = :id ", nativeQuery = true)
    Integer existsIntegratedById(@Param("id") Long id);
}
