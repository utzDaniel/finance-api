package br.com.finance.modules.account;

import br.com.finance.modules.account.dto.AccountDto;
import br.com.finance.modules.account.dto.AccountEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<AccountEntity, Integer> {

    @Query(value = "SELECT " +
            "a.id as id, " +
            "a.name as name, " +
            "a.bank as bank, " +
            "a.type as type, " +
            "a.link as link, " +
            "a.balance as balance " +
            "FROM account as a " +
            "WHERE " +
            "a.user_id = :userId AND a.competence = :competence ",
            countQuery = "SELECT count(*) FROM account as a " +
                    "WHERE a.user_id = :userId AND a.competence = :competence", nativeQuery = true)
    Page<AccountDto> findAllByUserId(
            @Param("userId") String userId,
            @Param("competence") LocalDate competence,
            Pageable pageable);

    @Query(value = "SELECT " +
            "a.id as id, " +
            "a.name as name " +
            "FROM account as a " +
            "WHERE " +
            "a.user_id = :userId AND a.competence = :competence ", nativeQuery = true)
    List<AccountDto> findAllByUserId(
            @Param("userId") String userId,
            @Param("competence") LocalDate competence);

    @Query(value = "SELECT " +
            "a.id as id, " +
            "a.name as name " +
            "FROM account as a " +
            "WHERE " +
            "a.user_id IN (:usersId) AND a.competence = :competence ", nativeQuery = true)
    List<AccountDto> findAllByUsersId(
            @Param("usersId") List<String> usersId,
            @Param("competence") LocalDate competence);

    @Query("SELECT a FROM AccountEntity a WHERE a.userId = :userId AND a.id = :account")
    Optional<AccountEntity> findByIdAndUserId(
            @Param("account") int account,
            @Param("userId") String userId
    );

    @Query(value = """
            SELECT COUNT(*)
            FROM account
            WHERE user_id = :userId
              AND competence = :competence
              AND link = :link
            """, nativeQuery = true)
    Integer existsByLink(
            @Param("userId") String userId,
            @Param("competence") LocalDate competence,
            @Param("link") int link
    );

    @Query("SELECT COUNT(*) FROM AccountEntity a WHERE a.code = :uuid")
    Integer existsByCode(@Param("uuid") UUID uuid);

    @Query("SELECT a FROM AccountEntity a WHERE a.userId = :userId AND a.competence = :competence AND a.link IN (2,3,4)")
    List<AccountEntity> findIdByLink(
            @Param("userId") String userId,
            @Param("competence") LocalDate competence
    );

    @Query("SELECT a FROM AccountEntity a WHERE a.userId = :userId AND a.competence = :competence AND a.id = :account")
    Optional<AccountEntity> findByUserId(
            @Param("userId") String userId,
            @Param("competence") LocalDate competence,
            @Param("account") Long account
    );

    @Query("SELECT a FROM AccountEntity a WHERE a.userId = :userId AND a.competence = :competence")
    List<AccountEntity> findAllByUserIdAndCompetence(
            @Param("userId") String userId,
            @Param("competence") LocalDate competence
    );
}
