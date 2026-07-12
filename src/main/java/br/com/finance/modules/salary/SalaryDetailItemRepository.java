package br.com.finance.modules.salary;

import br.com.finance.modules.salary.dto.SalaryDetailItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SalaryDetailItemRepository extends JpaRepository<SalaryDetailItemEntity, Integer> {

    Optional<SalaryDetailItemEntity> findByCodeAndName(int code, String name);
}
