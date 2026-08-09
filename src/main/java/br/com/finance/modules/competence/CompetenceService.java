package br.com.finance.modules.competence;

import br.com.finance.config.ApiException;
import br.com.finance.modules.competence.dto.CompetenceEntity;
import br.com.finance.modules.competence.dto.CompetenceResponse;
import br.com.finance.modules.competence.dto.CompetenceStatus;
import br.com.finance.modules.competence.dto.CompetenceStatusResponse;
import br.com.finance.modules.event.EventProcessorEngine;
import br.com.finance.modules.event.dto.CompetencePayload;
import br.com.finance.modules.event.dto.EventAction;
import br.com.finance.modules.event.dto.EventPayload;
import br.com.finance.modules.event.dto.EventType;
import br.com.finance.modules.keycloak.KeycloakService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompetenceService {

    private final KeycloakService keycloakService;
    private final CompetenceRepository competenceRepository;
    private final EventProcessorEngine eventProcessorEngine;

    public CompetenceService(
            KeycloakService keycloakService,
            CompetenceRepository competenceRepository,
            EventProcessorEngine eventProcessorEngine
    ) {
        this.keycloakService = keycloakService;
        this.competenceRepository = competenceRepository;
        this.eventProcessorEngine = eventProcessorEngine;
    }

    public List<CompetenceResponse> getCompetences(Jwt jwt) {
        String userId = keycloakService.getIdUser(jwt);

        return competenceRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

    }

    public CompetenceResponse getCompetence(Jwt jwt, LocalDate competence) {
        String userId = keycloakService.getIdUser(jwt);

        CompetenceEntity competenceEntity = competenceRepository.findByUserIdAndCompetence(userId, competence)
                .orElseThrow(() -> ApiException.notFound("Competencia não inicializada"));

        return toResponse(competenceEntity);

    }

    @Transactional
    public List<CompetenceResponse> initializeCompetence(Jwt jwt, LocalDate competence) {
        String userId = keycloakService.getIdUser(jwt);

        List<CompetenceEntity> competences = competenceRepository.findByUserId(userId);

        if (!competences.isEmpty()) {
            throw ApiException.badRequest("Já existe um mês/ano inicializado");
        }

        CompetenceEntity entity = CompetenceEntity.builder()
                .userId(userId)
                .monthYear(competence)
                .status(CompetenceStatus.ABERTA.getId())
                .build();
        competenceRepository.save(entity);

        eventProcessorEngine.process(new EventPayload<>(EventType.COMPETENCE, EventAction.ADDED, userId, new CompetencePayload(competence, userId)));

        return getCompetences(jwt);
    }

    @Transactional
    public List<CompetenceResponse> closeCompetence(Jwt jwt, LocalDate competence) {
        String userId = keycloakService.getIdUser(jwt);

        CompetenceEntity competenceEntity = competenceRepository.findByUserIdAndCompetence(userId, competence)
                .orElseThrow(() -> ApiException.notFound("Competencia não inicializada"));

        if (competenceEntity.getStatus() != CompetenceStatus.ABERTA.getId()) {
            throw ApiException.badRequest("Mês já está fechado");
        }

        if (competenceRepository.existsPayrollByUserIdAndCompetence(userId, competence) > 0) {
            throw ApiException.badRequest("Existe lançamento(s) de salário não integrado(s)");
        }

        if (competenceRepository.existsExpenseByUserIdAndCompetence(userId, competence) > 0) {
            throw ApiException.badRequest("Existe despesa(s) que não foram paga(s)");
        }

        competenceEntity.setStatus(CompetenceStatus.FECHADO.getId());

        competenceRepository.save(competenceEntity);

        eventProcessorEngine.process(new EventPayload<>(EventType.COMPETENCE, EventAction.INTEGRATED, userId, new CompetencePayload(competence, userId)));

        return getCompetences(jwt);
    }

    private CompetenceResponse toResponse(CompetenceEntity entity) {
        CompetenceStatus status = CompetenceStatus.get(entity.getStatus());
        assert status != null;
        return new CompetenceResponse(
                entity.getMonthYear(),
                new CompetenceStatusResponse(status.getId(), status.getDescription())
        );
    }
}
