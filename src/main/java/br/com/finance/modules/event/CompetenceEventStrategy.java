package br.com.finance.modules.event;

import br.com.finance.modules.account.AccountRepository;
import br.com.finance.modules.account.dto.AccountEntity;
import br.com.finance.modules.competence.CompetenceRepository;
import br.com.finance.modules.competence.dto.CompetenceEntity;
import br.com.finance.modules.competence.dto.CompetenceStatus;
import br.com.finance.modules.event.dto.CompetencePayload;
import br.com.finance.modules.event.dto.EventAction;
import br.com.finance.modules.event.dto.EventType;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class CompetenceEventStrategy implements EventStrategy<CompetencePayload> {

    private final CompetenceRepository competenceRepository;
    private final AccountRepository accountRepository;

    public CompetenceEventStrategy(CompetenceRepository competenceRepository,
                                   AccountRepository accountRepository) {
        this.competenceRepository = competenceRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public void process(CompetencePayload payload, EventAction action) {
        if (payload.competence() == null) return;
        if (action != EventAction.INTEGRATED) return;

        LocalDate newCompetence = payload.competence().plusMonths(1);

        CompetenceEntity competenceEntity = CompetenceEntity.builder()
                .monthYear(newCompetence)
                .userId(payload.userID())
                .status(CompetenceStatus.ABERTA.getId())
                .build();
        competenceRepository.save(competenceEntity);

        List<AccountEntity> newAccount = new ArrayList<>();

        accountRepository.findAllByUserIdAndCompetence(payload.userID(), payload.competence())
                .forEach(a -> {
                    AccountEntity accountEntity = AccountEntity.builder()
                            .code(a.getCode())
                            .userId(a.getUserId())
                            .competence(newCompetence)
                            .name(a.getName())
                            .bank(a.getBank())
                            .type(a.getType())
                            .link(a.getLink())
                            .balance(a.getBalance())
                            .build();
                    newAccount.add(accountEntity);
                });

        if (newAccount.isEmpty()) return;

        accountRepository.saveAll(newAccount);

    }


    @Override
    public EventType getTypeHandler() {
        return EventType.COMPETENCE;
    }
}
