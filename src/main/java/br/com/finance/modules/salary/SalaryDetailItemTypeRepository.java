package br.com.finance.modules.salary;

import br.com.finance.modules.salary.dto.SalaryDetailItemTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalaryDetailItemTypeRepository extends JpaRepository<SalaryDetailItemTypeEntity, Integer> {
}
