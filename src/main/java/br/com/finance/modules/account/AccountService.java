package br.com.finance.modules.account;

import br.com.finance.config.ApiException;
import br.com.finance.config.Violacao;
import br.com.finance.modules.account.dto.*;
import br.com.finance.modules.competence.CompetenceService;
import br.com.finance.modules.competence.dto.CompetenceResponse;
import br.com.finance.modules.competence.dto.CompetenceStatus;
import br.com.finance.modules.event.EventProcessorEngine;
import br.com.finance.modules.event.dto.AccountPayload;
import br.com.finance.modules.event.dto.EventAction;
import br.com.finance.modules.event.dto.EventPayload;
import br.com.finance.modules.event.dto.EventType;
import br.com.finance.modules.keycloak.KeycloakService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final KeycloakService keycloakService;
    private final EventProcessorEngine eventProcessorEngine;
    private final CompetenceService competenceService;

    public AccountService(
            AccountRepository accountRepository,
            KeycloakService keycloakService,
            EventProcessorEngine eventProcessorEngine,
            CompetenceService competenceService
    ) {
        this.accountRepository = accountRepository;
        this.keycloakService = keycloakService;
        this.eventProcessorEngine = eventProcessorEngine;
        this.competenceService = competenceService;
    }

    public Page<AccountResponse> getAccount(Jwt jwt, LocalDate competence, Pageable pageable) {
        String userId = keycloakService.getIdUser(jwt);
        return accountRepository.findAllByUserId(userId, competence, pageable)
                .map(this::toResponse);
    }

    @Transactional
    public Page<AccountResponse> addAccount(Jwt jwt, LocalDate competence, AddAccountRequest request, Pageable pageable) {
        String userId = keycloakService.getIdUser(jwt);

        CompetenceResponse competenceResponse =  competenceService.getCompetence(jwt, competence);
        if (competenceResponse.status().id() != CompetenceStatus.ABERTA.getId()) {
            throw ApiException.badRequest("Mês já está fechado");
        }

        findTypeOrThrow(request.type());
        findBankOrThrow(request.bank());
        findLinkOrThrow(request.link());

        if (request.link() != AccountLink.NENHUM.getId()) {
            if (accountRepository.existsByLink(userId, competence, request.link()) != 0) {
                throw ApiException.badRequest(List.of(
                        new Violacao("link", "Só deve existir uma conta com esse vinculo")
                ));
            }
        }

        UUID uuid = UUID.randomUUID();

        while (accountRepository.existsByCode(uuid) != 0) {
            uuid = UUID.randomUUID();
        }

        AccountEntity entity = AccountEntity.builder()
                .userId(userId)
                .code(uuid)
                .competence(competence)
                .name(request.name())
                .bank(request.bank())
                .type(request.type())
                .link(request.link())
                .balance(BigDecimal.ZERO)
                .build();

        accountRepository.save(entity);
        eventProcessorEngine.process(new EventPayload<>(EventType.ACCOUNT, EventAction.ADDED, userId, new AccountPayload(List.of(entity.getId()))));

        return getAccount(jwt, competence, pageable);
    }

    private void findTypeOrThrow(int type) {
        if (!AccountType.isValid(type)) {
            throw ApiException.badRequest(List.of(
                    new Violacao("type", "Tipo não encontrado: " + type)
            ));
        }
    }

    private void findBankOrThrow(int bank) {
        if (!AccountBank.isValid(bank)) {
            throw ApiException.badRequest(List.of(
                    new Violacao("bank", "Banco não encontrado: " + bank)
            ));
        }
    }

    private void findLinkOrThrow(int link) {
        if (!AccountLink.isValid(link)) {
            throw ApiException.badRequest(List.of(
                    new Violacao("link", "Vinculo não encontrado: " + link)
            ));
        }
    }

    private AccountResponse toResponse(AccountDto dto) {
        AccountType type = AccountType.get(dto.getType());
        assert type != null;
        AccountBank bank = AccountBank.get(dto.getBank());
        assert bank != null;
        AccountLink link = AccountLink.get(dto.getLink());
        assert link != null;
        return new AccountResponse(
                dto.getId(),
                dto.getName(),
                new AccountBankResponse(type.getId(), type.getDescription()),
                new AccountTypeResponse(bank.getId(), bank.getDescription()),
                new AccountLinkResponse(link.getId(), link.getDescription()),
                dto.getBalance()
        );
    }

    public List<AccountUserResponse> getAccountUser(Jwt jwt, LocalDate competence) {
        String userId = keycloakService.getIdUser(jwt);
        return accountRepository.findAllByUserId(userId, competence)
                .stream()
                .map(v -> new AccountUserResponse(v.getId(), v.getName()))
                .toList();
    }

    public List<AccountUserResponse> getAccountFamily(Jwt jwt, LocalDate competence) {
        List<String> usersId = keycloakService.getIdUsers(jwt);
        if (usersId.isEmpty()) {
            return Collections.emptyList();
        }

        return accountRepository.findAllByUsersId(usersId, competence)
                .stream()
                .map(v -> new AccountUserResponse(v.getId(), v.getName()))
                .toList();
    }

    public List<AccountTypeResponse> getAccountType() {
        return Arrays.stream(AccountType.values()).map(v -> new AccountTypeResponse(v.getId(), v.getDescription())).toList();
    }

    public List<AccountBankResponse> getAccountBank() {
        return Arrays.stream(AccountBank.values()).map(v -> new AccountBankResponse(v.getId(), v.getDescription())).toList();
    }

    public List<AccountLinkResponse> getAccountLink() {
        return Arrays.stream(AccountLink.values()).map(v -> new AccountLinkResponse(v.getId(), v.getDescription())).toList();
    }

}
