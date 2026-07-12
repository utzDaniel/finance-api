package br.com.finance.modules.salary;

import br.com.finance.config.ApiException;
import br.com.finance.modules.event.EventPublisher;
import br.com.finance.modules.event.EventType;
import br.com.finance.modules.keycloak.KeycloakService;
import br.com.finance.modules.keycloak.dto.KeycloakUserFamilyRecord;
import br.com.finance.modules.salary.dto.AddSalaryDetailRequest;
import br.com.finance.modules.salary.dto.DeleteSalaryDetailRequest;
import br.com.finance.modules.salary.dto.SalaryDetailEntity;
import br.com.finance.modules.salary.dto.SalaryDetailItemEntity;
import br.com.finance.modules.salary.dto.SalaryDetailItemTypeEntity;
import br.com.finance.modules.salary.dto.SalaryDetailResponse;
import br.com.finance.modules.salary.dto.UpdateSalaryDetailRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SalaryService - Testes")
class SalaryServiceTest {

    @Mock
    private SalaryDetailRepository salaryDetailRepository;

    @Mock
    private SalaryDetailItemRepository salaryDetailItemRepository;

    @Mock
    private SalaryDetailItemTypeRepository salaryDetailItemTypeRepository;

    @Mock
    private KeycloakService keycloakService;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private SalaryService salaryService;

    private static final String USER_ID = "u-1";
    private static final LocalDate COMPETENCE = LocalDate.of(2026, 7, 1);
    private static final Pageable PAGEABLE = PageRequest.of(0, 10);

    // -------------------------------------------------------------------------
    // getSalaryDetail
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getSalaryDetail - deve retornar página vazia quando não há registros")
    void getSalaryDetailDeveRetornarPaginaVazia() {
        Jwt jwt = buildJwt("john.doe");
        when(keycloakService.getUserFamily(jwt)).thenReturn(userFamily());
        when(salaryDetailRepository.findAllByUserIdAndCompetenceDate(USER_ID, COMPETENCE, PAGEABLE))
                .thenReturn(Page.empty());

        Page<SalaryDetailResponse> result = salaryService.getSalaryDetail(jwt, COMPETENCE, PAGEABLE);

        assertEquals(0, result.getTotalElements());
    }

    @Test
    @DisplayName("getSalaryDetail - deve mapear campos corretamente")
    void getSalaryDetailDeveMapearCamposCorretamente() {
        Jwt jwt = buildJwt("john.doe");
        SalaryDetailEntity entity = buildEntity(10L, USER_ID, 1, 1001, "INSS", 1, new BigDecimal("150.00"));

        when(keycloakService.getUserFamily(jwt)).thenReturn(userFamily());
        when(salaryDetailRepository.findAllByUserIdAndCompetenceDate(USER_ID, COMPETENCE, PAGEABLE))
                .thenReturn(new PageImpl<>(List.of(entity)));

        Page<SalaryDetailResponse> result = salaryService.getSalaryDetail(jwt, COMPETENCE, PAGEABLE);

        assertEquals(1, result.getTotalElements());
        SalaryDetailResponse response = result.getContent().get(0);
        assertEquals(10L, response.id());
        assertEquals(1, response.idType());
        assertEquals(1001, response.code());
        assertEquals("INSS", response.name());
        assertEquals(1, response.quantity());
        assertEquals(new BigDecimal("150.00"), response.amount());
    }

    // -------------------------------------------------------------------------
    // addSalaryDetail
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("addSalaryDetail - deve criar item novo quando combinação code+name não existe")
    void addSalaryDetailDeveCriarItemNovoQuandoCodeNaoExiste() {
        Jwt jwt = buildJwt("john.doe");
        AddSalaryDetailRequest request = new AddSalaryDetailRequest(1, 1001, "INSS", 1, new BigDecimal("150.00"));
        SalaryDetailItemTypeEntity itemType = buildItemType(1, "Desconto");
        SalaryDetailItemEntity newItem = buildItem(1, 1001, "INSS");

        when(keycloakService.getUserFamily(jwt)).thenReturn(userFamily());
        when(salaryDetailItemTypeRepository.findById(1)).thenReturn(Optional.of(itemType));
        when(salaryDetailItemRepository.findByCodeAndName(1001, "INSS")).thenReturn(Optional.empty());
        when(salaryDetailItemRepository.save(any())).thenReturn(newItem);
        when(salaryDetailRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(salaryDetailRepository.findAllByUserIdAndCompetenceDate(USER_ID, COMPETENCE, PAGEABLE))
                .thenReturn(Page.empty());

        salaryService.addSalaryDetail(jwt, COMPETENCE, request, PAGEABLE);

        verify(salaryDetailItemRepository).save(any(SalaryDetailItemEntity.class));
        verify(salaryDetailRepository).save(any(SalaryDetailEntity.class));
        verify(eventPublisher).publish(eq(EventType.SALARY_DETAIL_ADDED), eq(USER_ID), eq(request));
    }

    @Test
    @DisplayName("addSalaryDetail - deve reutilizar item existente quando code e name já existem juntos")
    void addSalaryDetailDeveReutilizarItemExistenteQuandoCodeNameCoincide() {
        Jwt jwt = buildJwt("john.doe");
        AddSalaryDetailRequest request = new AddSalaryDetailRequest(1, 1001, "INSS", 1, new BigDecimal("150.00"));
        SalaryDetailItemTypeEntity itemType = buildItemType(1, "Desconto");
        SalaryDetailItemEntity existingItem = buildItem(1, 1001, "INSS");

        when(keycloakService.getUserFamily(jwt)).thenReturn(userFamily());
        when(salaryDetailItemTypeRepository.findById(1)).thenReturn(Optional.of(itemType));
        when(salaryDetailItemRepository.findByCodeAndName(1001, "INSS")).thenReturn(Optional.of(existingItem));
        when(salaryDetailRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(salaryDetailRepository.findAllByUserIdAndCompetenceDate(USER_ID, COMPETENCE, PAGEABLE))
                .thenReturn(Page.empty());

        salaryService.addSalaryDetail(jwt, COMPETENCE, request, PAGEABLE);

        verify(salaryDetailItemRepository, never()).save(any());
        verify(salaryDetailRepository).save(any(SalaryDetailEntity.class));
    }

    @Test
    @DisplayName("addSalaryDetail - deve criar item novo quando mesmo code com nome diferente")
    void addSalaryDetailDeveCriarItemNovoQuandoMesmoCodeNomeDiferente() {
        Jwt jwt = buildJwt("john.doe");
        AddSalaryDetailRequest request = new AddSalaryDetailRequest(1, 1001, "INSS Diferente", 1, new BigDecimal("150.00"));
        SalaryDetailItemTypeEntity itemType = buildItemType(1, "Desconto");
        SalaryDetailItemEntity newItem = buildItem(2, 1001, "INSS Diferente");

        when(keycloakService.getUserFamily(jwt)).thenReturn(userFamily());
        when(salaryDetailItemTypeRepository.findById(1)).thenReturn(Optional.of(itemType));
        when(salaryDetailItemRepository.findByCodeAndName(1001, "INSS Diferente")).thenReturn(Optional.empty());
        when(salaryDetailItemRepository.save(any())).thenReturn(newItem);
        when(salaryDetailRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(salaryDetailRepository.findAllByUserIdAndCompetenceDate(USER_ID, COMPETENCE, PAGEABLE))
                .thenReturn(Page.empty());

        salaryService.addSalaryDetail(jwt, COMPETENCE, request, PAGEABLE);

        verify(salaryDetailItemRepository).save(any(SalaryDetailItemEntity.class));
        verify(salaryDetailRepository).save(any(SalaryDetailEntity.class));
    }

    @Test
    @DisplayName("addSalaryDetail - deve lançar 400 quando idType não encontrado")
    void addSalaryDetailDeveLancar400QuandoIdTypeNaoEncontrado() {
        Jwt jwt = buildJwt("john.doe");
        AddSalaryDetailRequest request = new AddSalaryDetailRequest(99, 1001, "INSS", 1, new BigDecimal("150.00"));

        when(keycloakService.getUserFamily(jwt)).thenReturn(userFamily());
        when(salaryDetailItemTypeRepository.findById(99)).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class,
                () -> salaryService.addSalaryDetail(jwt, COMPETENCE, request, PAGEABLE));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    // -------------------------------------------------------------------------
    // updateSalaryDetail
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("updateSalaryDetail - deve lançar 404 quando ids não encontrado")
    void updateSalaryDetailDeveLancar404QuandoIdNaoEncontrado() {
        Jwt jwt = buildJwt("john.doe");
        UpdateSalaryDetailRequest request = new UpdateSalaryDetailRequest(99L, 1, 1001, "INSS", 1, new BigDecimal("150.00"));

        when(keycloakService.getUserFamily(jwt)).thenReturn(userFamily());
        when(salaryDetailRepository.findByIdUserIdAndCompetenceDate(99L, USER_ID, COMPETENCE)).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class,
                () -> salaryService.updateSalaryDetail(jwt, COMPETENCE, request, PAGEABLE));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    @DisplayName("updateSalaryDetail - deve atualizar e publicar evento quando bem-sucedido")
    void updateSalaryDetailDeveAtualizarEPublicarEvento() {
        Jwt jwt = buildJwt("john.doe");
        UpdateSalaryDetailRequest request = new UpdateSalaryDetailRequest(10L, 2, 2001, "Bônus", 1, new BigDecimal("500.00"));
        SalaryDetailEntity entity = buildEntity(10L, USER_ID, 1, 1001, "INSS", 1, new BigDecimal("150.00"));
        SalaryDetailItemTypeEntity itemType = buildItemType(2, "Provento");
        SalaryDetailItemEntity item = buildItem(5, 2001, "Bônus");

        when(keycloakService.getUserFamily(jwt)).thenReturn(userFamily());
        when(salaryDetailRepository.findByIdUserIdAndCompetenceDate(10L, USER_ID, COMPETENCE)).thenReturn(Optional.of(entity));
        when(salaryDetailItemTypeRepository.findById(2)).thenReturn(Optional.of(itemType));
        when(salaryDetailItemRepository.findByCodeAndName(2001, "Bônus")).thenReturn(Optional.empty());
        when(salaryDetailItemRepository.save(any())).thenReturn(item);
        when(salaryDetailRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(salaryDetailRepository.findAllByUserIdAndCompetenceDate(USER_ID, COMPETENCE, PAGEABLE))
                .thenReturn(Page.empty());

        salaryService.updateSalaryDetail(jwt, COMPETENCE, request, PAGEABLE);

        ArgumentCaptor<SalaryDetailEntity> captor = ArgumentCaptor.forClass(SalaryDetailEntity.class);
        verify(salaryDetailRepository).save(captor.capture());
        assertEquals(2001, captor.getValue().getItem().getCode());
        assertEquals(new BigDecimal("500.00"), captor.getValue().getAmount());
        verify(eventPublisher).publish(eq(EventType.SALARY_DETAIL_UPDATED), eq(USER_ID), eq(request));
    }

    // -------------------------------------------------------------------------
    // deleteSalaryDetail
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("deleteSalaryDetail - deve lançar 404 quando algum id não encontrado")
    void deleteSalaryDetailDeveLancar404QuandoIdNaoEncontrado() {
        Jwt jwt = buildJwt("john.doe");
        DeleteSalaryDetailRequest request = new DeleteSalaryDetailRequest(List.of(10L, 99L));

        when(keycloakService.getUserFamily(jwt)).thenReturn(userFamily());
        when(salaryDetailRepository.findAllByDetailIdInAndUserId(List.of(10L, 99L), USER_ID, COMPETENCE))
                .thenReturn(List.of(buildEntity(10L, USER_ID, 1, 1001, "INSS", 1, new BigDecimal("150.00"))));

        ApiException ex = assertThrows(ApiException.class,
                () -> salaryService.deleteSalaryDetail(jwt, COMPETENCE, request, PAGEABLE));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    @DisplayName("deleteSalaryDetail - deve lançar 404 quando id pertence a outro usuário (não encontrado pelo filtro de userId)")
    void deleteSalaryDetailDeveLancar404QuandoIdDeOutroUsuario() {
        Jwt jwt = buildJwt("john.doe");
        DeleteSalaryDetailRequest request = new DeleteSalaryDetailRequest(List.of(10L, 20L));

        when(keycloakService.getUserFamily(jwt)).thenReturn(userFamily());
        // findAllByDetailIdInAndUserId filtra por userId, então o registro de outro usuário não retorna
        when(salaryDetailRepository.findAllByDetailIdInAndUserId(List.of(10L, 20L), USER_ID, COMPETENCE))
                .thenReturn(List.of(buildEntity(10L, USER_ID, 1, 1001, "INSS", 1, new BigDecimal("150.00"))));

        ApiException ex = assertThrows(ApiException.class,
                () -> salaryService.deleteSalaryDetail(jwt, COMPETENCE, request, PAGEABLE));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    @DisplayName("deleteSalaryDetail - deve deletar e publicar evento quando bem-sucedido")
    void deleteSalaryDetailDeveDeletarEPublicarEvento() {
        Jwt jwt = buildJwt("john.doe");
        DeleteSalaryDetailRequest request = new DeleteSalaryDetailRequest(List.of(10L, 11L));
        List<SalaryDetailEntity> entities = List.of(
                buildEntity(10L, USER_ID, 1, 1001, "INSS", 1, new BigDecimal("150.00")),
                buildEntity(11L, USER_ID, 2, 2001, "Bônus", 1, new BigDecimal("500.00"))
        );

        when(keycloakService.getUserFamily(jwt)).thenReturn(userFamily());
        when(salaryDetailRepository.findAllByDetailIdInAndUserId(List.of(10L, 11L), USER_ID, COMPETENCE)).thenReturn(entities);
        when(salaryDetailRepository.findAllByUserIdAndCompetenceDate(USER_ID, COMPETENCE, PAGEABLE))
                .thenReturn(Page.empty());

        salaryService.deleteSalaryDetail(jwt, COMPETENCE, request, PAGEABLE);

        verify(salaryDetailRepository).deleteAll(entities);
        verify(eventPublisher).publish(eq(EventType.SALARY_DETAIL_DELETED), eq(USER_ID), eq(request));
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private Jwt buildJwt(String username) {
        return Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("preferred_username", username)
                .build();
    }

    private KeycloakUserFamilyRecord userFamily() {
        return new KeycloakUserFamilyRecord(USER_ID, "John", "Doe", null, null);
    }

    private SalaryDetailItemTypeEntity buildItemType(int id, String name) {
        return SalaryDetailItemTypeEntity.builder().id(id).name(name).build();
    }

    private SalaryDetailItemEntity buildItem(int id, int code, String name) {
        return SalaryDetailItemEntity.builder().id(id).code(code).name(name).build();
    }

    private SalaryDetailEntity buildEntity(Long id, String userId, int typeId, int code, String itemName, int quantity, BigDecimal amount) {
        SalaryDetailItemTypeEntity itemType = buildItemType(typeId, "Tipo");
        SalaryDetailItemEntity item = buildItem(typeId * 10, code, itemName);
        return SalaryDetailEntity.builder()
                .id(id)
                .userId(userId)
                .competenceDate(COMPETENCE)
                .itemType(itemType)
                .item(item)
                .quantity(quantity)
                .amount(amount)
                .build();
    }
}
