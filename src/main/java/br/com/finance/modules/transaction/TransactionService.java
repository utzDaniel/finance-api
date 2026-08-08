package br.com.finance.modules.transaction;

import br.com.finance.config.ApiException;
import br.com.finance.config.Violacao;
import br.com.finance.modules.account.AccountRepository;
import br.com.finance.modules.account.dto.AccountEntity;
import br.com.finance.modules.account.dto.AccountUserResponse;
import br.com.finance.modules.event.EventProcessorEngine;
import br.com.finance.modules.event.dto.EventAction;
import br.com.finance.modules.event.dto.EventPayload;
import br.com.finance.modules.event.dto.EventType;
import br.com.finance.modules.keycloak.KeycloakService;
import br.com.finance.modules.transaction.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
public class TransactionService {

    private final KeycloakService keycloakService;
    private final TransactionRepository transactionRepository;
    private final TransactionAccountRepository transactionAccountRepository;
    private final AccountRepository accountRepository;
    private final EventProcessorEngine eventProcessorEngine;

    public TransactionService(
            KeycloakService keycloakService,
            TransactionRepository transactionRepository,
            TransactionAccountRepository transactionAccountRepository,
            AccountRepository accountRepository,
            EventProcessorEngine eventProcessorEngine
    ) {
        this.keycloakService = keycloakService;
        this.transactionRepository = transactionRepository;
        this.transactionAccountRepository = transactionAccountRepository;
        this.accountRepository = accountRepository;
        this.eventProcessorEngine = eventProcessorEngine;
    }

    public Page<TransactionResponse> getTransaction(Jwt jwt, LocalDate competence, Pageable pageable, TransactionFilter filter) {
        String userId = keycloakService.getIdUser(jwt);

        return transactionRepository
                .findAllByUserIdAndCompetence(
                        userId,
                        competence,
                        filter != null ? filter.normalizedDate() : null,
                        filter != null ? filter.methodId() : null,
                        filter != null ? filter.normalizedName() : null,
                        filter != null ? filter.normalizedaccountName() : null,
                        pageable)
                .map(this::toResponse);
    }

    @Transactional
    public Page<TransactionResponse> transferTransaction(Jwt jwt, LocalDate competence, TransferTransactionRequest request, Pageable pageable) {
        String userId = keycloakService.getIdUser(jwt);

        findMethodOrThrow(request.method());

        AccountEntity account = accountRepository.findByIdAndUserId(request.accountOrigin(), userId)
                .orElseThrow(() -> ApiException.notFound("Conta não encontrado"));

        if (!request.debit() && account.getBalance().compareTo(request.amount()) < 0) {
            throw ApiException.badRequest(List.of(
                    new Violacao("accountOrigin", "Saldo da conta insuficiente: " + account.getBalance())
            ));
        }

        if (request.accountDestination() != null) {
            AccountEntity accountDest = accountRepository.findById(request.accountDestination())
                    .orElseThrow(() -> ApiException.notFound("Conta destino não encontrado"));

            if (request.debit() && accountDest.getBalance().compareTo(request.amount()) < 0) {
                throw ApiException.badRequest(List.of(
                        new Violacao("accountDestination", "Saldo da conta destino insuficiente: " + accountDest.getBalance())
                ));
            }

            TransactionEntity entityDest = TransactionEntity.builder()
                    .account(request.accountDestination())
                    .method(request.method())
                    .name(request.name())
                    .debit(!request.debit())
                    .amount(request.amount())
                    .dateTransaction(request.dateTransaction())
                    .build();

            transactionRepository.save(entityDest);

            TransactionAccountEntity transactionAccount = TransactionAccountEntity.builder()
                    .accountOrigin(request.accountDestination())
                    .accountDestination(request.accountOrigin())
                    .transactionAccount(entityDest.getId())
                    .build();

            transactionAccountRepository.save(transactionAccount);

            if (request.debit())
                accountDest.setBalance(accountDest.getBalance().subtract(request.amount()));
            else
                accountDest.setBalance(accountDest.getBalance().add(request.amount()));

            accountRepository.save(accountDest);
        }

        TransactionEntity entity = TransactionEntity.builder()
                .account(request.accountOrigin())
                .method(request.method())
                .name(request.name())
                .debit(request.debit())
                .amount(request.amount())
                .dateTransaction(request.dateTransaction())
                .build();

        transactionRepository.save(entity);

        TransactionAccountEntity transactionAccount = TransactionAccountEntity.builder()
                .accountOrigin(request.accountOrigin())
                .accountDestination(request.accountDestination())
                .transactionAccount(entity.getId())
                .build();

        transactionAccountRepository.save(transactionAccount);

        if (request.debit())
            account.setBalance(account.getBalance().add(request.amount()));
        else
            account.setBalance(account.getBalance().subtract(request.amount()));

        accountRepository.save(account);

        eventProcessorEngine.process(new EventPayload<>(EventType.TRANSACTION, EventAction.INTEGRATED, userId, null));

        return getTransaction(jwt, competence, pageable, null);
    }

    private TransactionResponse toResponse(TransactionDto dto) {
        TransactionMethod method = TransactionMethod.get(dto.getMethod());
        assert method != null;
        return new TransactionResponse(
                dto.getId(),
                new AccountUserResponse(dto.getAccountId(), dto.getAccountName()),
                new TransactionMethodResponse(method.getId(), method.getDescription()),
                dto.getName(),
                dto.getDebit(),
                dto.getDateTransaction(),
                dto.getAmount()
        );
    }

    private void findMethodOrThrow(int method) {
        if (!TransactionMethod.isValid(method)) {
            throw ApiException.badRequest(List.of(
                    new Violacao("method", "Metodo não encontrado: " + method)
            ));
        }
    }

    public List<TransactionMethodResponse> getTransactionMethod() {
        return Arrays.stream(TransactionMethod.values()).map(v -> new TransactionMethodResponse(v.getId(), v.getDescription())).toList();
    }


}
