package br.com.finance.modules.summary;

import br.com.finance.modules.summary.dto.SalaryEntity;
import br.com.finance.modules.summary.dto.SumSalaryDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SalaryRepository extends JpaRepository<SalaryEntity, Long> {

    @Query(value = """
            SELECT *
            FROM dbo.salary
            WHERE user_id = :userId
              AND competence_date = :competenceDate
            """, nativeQuery = true)
    Optional<SalaryEntity> findByUserIdAndCompetenceDate(
            @Param("userId") String userId,
            @Param("competenceDate") LocalDate competenceDate
    );

    @Query(value = """
            SELECT COALESCE(SUM(gross_salary), 0) as grossSalary,
                   COALESCE(SUM(net_salary), 0) as netSalary
            FROM dbo.salary
            WHERE user_id IN (:userIds)
              AND competence_date = :competenceDate
            """, nativeQuery = true)
    SumSalaryDto sumSalaryByUserIdInAndCompetenceDate(
            @Param("userIds") List<String> userIds,
            @Param("competenceDate") LocalDate competenceDate
    );
}