package br.com.finance.modules.transaction;

import br.com.finance.modules.transaction.dto.TransactionAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionAccountRepository extends JpaRepository<TransactionAccountEntity, Long> {

}
