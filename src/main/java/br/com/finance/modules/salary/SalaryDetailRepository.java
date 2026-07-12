package br.com.finance.modules.salary;

import br.com.finance.modules.salary.dto.SalaryDetailEntity;
import br.com.finance.modules.salary.dto.SalaryDetailItemEntity;
import br.com.finance.modules.salary.dto.SalaryDetailItemTypeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SalaryDetailRepository extends JpaRepository<SalaryDetailEntity, Long> {

    Page<SalaryDetailEntity> findAllByUserIdAndCompetenceDate(
            String userId, LocalDate competenceDate, Pageable pageable);

    @Query("SELECT s FROM SalaryDetailEntity s WHERE s.id = :id AND s.userId = :userId AND s.competenceDate = :competenceDate")
    Optional<SalaryDetailEntity> findByIdUserIdAndCompetenceDate(@Param("id") Long id, @Param("userId") String userId, @Param("competenceDate") LocalDate competenceDate);

    @Query("SELECT s FROM SalaryDetailEntity s WHERE s.id IN :ids AND s.userId = :userId AND s.competenceDate = :competenceDate")
    List<SalaryDetailEntity> findAllByDetailIdInAndUserId(@Param("ids") List<Long> ids, @Param("userId") String userId, @Param("competenceDate") LocalDate competenceDate);

    boolean existsByUserIdAndCompetenceDateAndItemTypeAndItem(
            String userId, LocalDate competenceDate,
            SalaryDetailItemTypeEntity itemType, SalaryDetailItemEntity item);
}
