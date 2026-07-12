package br.com.finance.modules.event;

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

        assertEquals(4, tipos.length, "Deve haver exatamente 4 tipos de eventos");
        assertNotNull(EventType.valueOf("SALARY_SUMMARY_UPDATED"));
        assertNotNull(EventType.valueOf("SALARY_DETAIL_ADDED"));
        assertNotNull(EventType.valueOf("SALARY_DETAIL_UPDATED"));
        assertNotNull(EventType.valueOf("SALARY_DETAIL_DELETED"));
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar tipo inexistente")
    void deveLancarExcecaoAoBuscarTipoInexistente() {
        assertThrows(IllegalArgumentException.class,
                () -> EventType.valueOf("TIPO_INEXISTENTE"));
    }

}
