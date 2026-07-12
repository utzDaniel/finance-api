package br.com.finance.modules.salary;

import br.com.finance.config.ApiException;
import br.com.finance.config.Violacao;
import br.com.finance.modules.event.EventPublisher;
import br.com.finance.modules.event.EventType;
import br.com.finance.modules.keycloak.KeycloakService;
import br.com.finance.modules.keycloak.dto.KeycloakUserFamilyRecord;
import br.com.finance.modules.salary.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class SalaryService {

    private final SalaryDetailRepository salaryDetailRepository;
    private final SalaryDetailItemRepository salaryDetailItemRepository;
    private final SalaryDetailItemTypeRepository salaryDetailItemTypeRepository;
    private final KeycloakService keycloakService;
    private final EventPublisher eventPublisher;

    public SalaryService(
            SalaryDetailRepository salaryDetailRepository,
            SalaryDetailItemRepository salaryDetailItemRepository,
            SalaryDetailItemTypeRepository salaryDetailItemTypeRepository,
            KeycloakService keycloakService,
            EventPublisher eventPublisher
    ) {
        this.salaryDetailRepository = salaryDetailRepository;
        this.salaryDetailItemRepository = salaryDetailItemRepository;
        this.salaryDetailItemTypeRepository = salaryDetailItemTypeRepository;
        this.keycloakService = keycloakService;
        this.eventPublisher = eventPublisher;
    }

    public Page<SalaryDetailResponse> getSalaryDetail(Jwt jwt, LocalDate competenceDate, Pageable pageable) {
        KeycloakUserFamilyRecord userFamily = keycloakService.getUserFamily(jwt);
        return salaryDetailRepository
                .findAllByUserIdAndCompetenceDate(userFamily.userId(), competenceDate, pageable)
                .map(this::toResponse);
    }

    @Transactional
    public Page<SalaryDetailResponse> addSalaryDetail(Jwt jwt, LocalDate competenceDate, AddSalaryDetailRequest request, Pageable pageable) {
        KeycloakUserFamilyRecord userFamily = keycloakService.getUserFamily(jwt);

        SalaryDetailItemTypeEntity itemType = findItemTypeOrThrow(request.idType());
        SalaryDetailItemEntity item = upsertItem(request.code(), request.name());

        if (salaryDetailRepository.existsByUserIdAndCompetenceDateAndItemTypeAndItem(
                userFamily.userId(), competenceDate, itemType, item)) {
            throw ApiException.badRequest(List.of(
                    new Violacao("idType", "Já existe essa combinação de tipo, código e nome"),
                    new Violacao("code", "Já existe essa combinação de tipo, código e nome"),
                    new Violacao("name", "Já existe essa combinação de tipo, código e nome")
            ));
        }

        SalaryDetailEntity entity = SalaryDetailEntity.builder()
                .userId(userFamily.userId())
                .competenceDate(competenceDate)
                .itemType(itemType)
                .item(item)
                .quantity(request.quantity())
                .amount(request.amount())
                .build();

        salaryDetailRepository.save(entity);
        eventPublisher.publish(EventType.SALARY_DETAIL_ADDED, userFamily.userId(), request);

        return getSalaryDetail(jwt, competenceDate, pageable);
    }

    @Transactional
    public Page<SalaryDetailResponse> updateSalaryDetail(Jwt jwt, LocalDate competenceDate, UpdateSalaryDetailRequest request, Pageable pageable) {
        KeycloakUserFamilyRecord userFamily = keycloakService.getUserFamily(jwt);

        SalaryDetailEntity entity = salaryDetailRepository.findByIdUserIdAndCompetenceDate(request.id(), userFamily.userId(), competenceDate)
                .orElseThrow(() -> ApiException.notFound("Detalhe salarial não encontrado"));

        SalaryDetailItemTypeEntity itemType = findItemTypeOrThrow(request.idType());
        SalaryDetailItemEntity item = upsertItem(request.code(), request.name());

        boolean mudouItemOuTipo = entity.getItemType().getId() != itemType.getId()
                || entity.getItem().getId() != item.getId();

        if (mudouItemOuTipo && salaryDetailRepository.existsByUserIdAndCompetenceDateAndItemTypeAndItem(
                userFamily.userId(), competenceDate, itemType, item)) {
            throw ApiException.badRequest(List.of(
                    new Violacao("idType", "Já existe essa combinação de tipo, código e nome"),
                    new Violacao("code", "Já existe essa combinação de tipo, código e nome"),
                    new Violacao("name", "Já existe essa combinação de tipo, código e nome")
            ));
        }

        entity.setItemType(itemType);
        entity.setItem(item);
        entity.setQuantity(request.quantity());
        entity.setAmount(request.amount());

        salaryDetailRepository.save(entity);
        eventPublisher.publish(EventType.SALARY_DETAIL_UPDATED, userFamily.userId(), request);

        return getSalaryDetail(jwt, competenceDate, pageable);
    }

    @Transactional
    public Page<SalaryDetailResponse> deleteSalaryDetail(Jwt jwt, LocalDate competenceDate, DeleteSalaryDetailRequest request, Pageable pageable) {
        KeycloakUserFamilyRecord userFamily = keycloakService.getUserFamily(jwt);

        List<SalaryDetailEntity> entities = salaryDetailRepository.findAllByDetailIdInAndUserId(request.ids(), userFamily.userId(), competenceDate);

        if (entities.isEmpty() || entities.size() != request.ids().size()) {
            throw ApiException.notFound("Detalhes salariais não encontrados ");
        }

        salaryDetailRepository.deleteAll(entities);
        eventPublisher.publish(EventType.SALARY_DETAIL_DELETED, userFamily.userId(), request);

        return getSalaryDetail(jwt, competenceDate, pageable);
    }

    private SalaryDetailItemTypeEntity findItemTypeOrThrow(int idType) {
        return salaryDetailItemTypeRepository.findById(idType)
                .orElseThrow(() -> ApiException.badRequest(List.of(
                        new Violacao("idType", "Tipo de item não encontrado: " + idType)
                )));
    }

    private SalaryDetailItemEntity upsertItem(int code, String name) {
        return salaryDetailItemRepository.findByCodeAndName(code, name)
                .orElseGet(() -> salaryDetailItemRepository.save(
                        SalaryDetailItemEntity.builder().code(code).name(name).build()
                ));
    }

    private SalaryDetailResponse toResponse(SalaryDetailEntity entity) {
        return new SalaryDetailResponse(
                entity.getId(),
                entity.getItemType().getId(),
                entity.getItem().getCode(),
                entity.getItem().getName(),
                entity.getQuantity(),
                entity.getAmount()
        );
    }
}
