package br.com.finance.modules.event;

import br.com.finance.modules.event.dto.EventType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("EventType - Testes do enum de tipos de eventos")
class EventTypeTest {

    @Test
    @DisplayName("Deve conter todos os tipos de eventos esperados")
    void deveConterTodosTiposDeEventos() {
        EventType[] tipos = EventType.values();

        assertEquals(10, tipos.length, "Deve haver exatamente 10 tipos de eventos");
        assertNotNull(EventType.valueOf("SALARY_SUMMARY_UPDATED"));
        assertNotNull(EventType.valueOf("SALARY_DETAIL_ADDED"));
        assertNotNull(EventType.valueOf("SALARY_DETAIL_UPDATED"));
        assertNotNull(EventType.valueOf("SALARY_DETAIL_DELETED"));
        assertNotNull(EventType.valueOf("EXPENSE_ADDED"));
        assertNotNull(EventType.valueOf("EXPENSE_UPDATED"));
        assertNotNull(EventType.valueOf("EXPENSE_DELETED"));
        assertNotNull(EventType.valueOf("PAYMENT_ADDED"));
        assertNotNull(EventType.valueOf("PAYMENT_UPDATED"));
        assertNotNull(EventType.valueOf("PAYMENT_DELETED"));

    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar tipo inexistente")
    void deveLancarExcecaoAoBuscarTipoInexistente() {
        assertThrows(IllegalArgumentException.class,
                () -> EventType.valueOf("TIPO_INEXISTENTE"));
    }

}
