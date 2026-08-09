package br.com.finance.modules.event;

import br.com.finance.modules.event.dto.EventType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("EventType - Testes do enum de tipos de eventos")
class EventTypeTest {

    @Test
    @DisplayName("Deve conter todos os tipos de eventos esperados")
    void deveConterTodosTiposDeEventos() {
        EventType[] tipos = EventType.values();

        assertEquals(5, tipos.length, "Deve haver exatamente 5 tipos de eventos");
        assertNotNull(EventType.valueOf("PAYROLL"));
        assertNotNull(EventType.valueOf("EXPENSE"));
        assertNotNull(EventType.valueOf("ACCOUNT"));
        assertNotNull(EventType.valueOf("TRANSACTION"));
        assertNotNull(EventType.valueOf("COMPETENCE"));
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar tipo inexistente")
    void deveLancarExcecaoAoBuscarTipoInexistente() {
        assertThrows(IllegalArgumentException.class,
                () -> EventType.valueOf("TIPO_INEXISTENTE"));
    }

}
