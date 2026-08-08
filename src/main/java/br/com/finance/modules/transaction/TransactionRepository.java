package br.com.finance.modules.transaction;

import br.com.finance.modules.transaction.dto.TransactionDto;
import br.com.finance.modules.transaction.dto.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    @Query(value = """
                    SELECT
                        t.id as id,
                        t.method as method,
                        t.name as name,
                        t.debit as debit,
                        t.amount as amount,
                        t.date_transaction as date_transaction,
                        a.id as accountId,
                        a.name as accountName
                    FROM transaction_account as t
                   INNER JOIN account as a ON t.account = a.id
                    WHERE
                        a.user_id = :userId
                        AND a.competence = :competence
                        AND (
                            :dateTransaction IS NULL
                            OR t.date_transaction = :dateTransaction
                        )
                        AND (
                            :methodId IS NULL
                            OR t.method = :methodId
                        )
                        AND (
                            :name IS NULL
                            OR :name = ''
                            OR LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%'))
                        ) 
                        AND (
                            :accountName IS NULL
                            OR :accountName = ''
                            OR LOWER(a.name) LIKE LOWER(CONCAT('%', :accountName, '%'))
                        )
                    """,
    countQuery = """
                    SELECT COUNT(*)
                    FROM transaction_account as t
                    INNER JOIN account as a ON t.account = a.id
                    WHERE
                        a.user_id = :userId
                        AND a.competence = :competence
                        AND (
                            :dateTransaction IS NULL
                            OR t.date_transaction = :dateTransaction
                        )
                        AND (
                            :methodId IS NULL
                            OR t.method = :methodId
                        )
                        AND (
                            :name IS NULL
                            OR :name = ''
                            OR LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%'))
                        )
                        AND (
                            :accountName IS NULL
                            OR :accountName = ''
                            OR LOWER(a.name) LIKE LOWER(CONCAT('%', :accountName, '%'))
                        )
                    """, nativeQuery = true)
    Page<TransactionDto> findAllByUserIdAndCompetence(
            @Param("userId") String userId,
            @Param("competence") LocalDate competence,
            @Param("dateTransaction") LocalDate dateTransaction,
            @Param("methodId") Integer methodId,
            @Param("name") String name,
            @Param("accountName") String accountName,
            Pageable pageable);


    @Query(value = "SELECT t FROM TransactionEntity t WHERE t.id IN ( :ids )")
    List<TransactionEntity> findAllByIds(@Param("ids") List<Long> ids);

}
