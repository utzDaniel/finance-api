package br.com.finance.modules.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventPublisher - Testes")
class EventPublisherTest {

    @Mock
    private JmsTemplate jmsTemplate;

    @InjectMocks
    private EventPublisher eventPublisher;

    @Test
    @DisplayName("publish - deve enviar evento para fila events com payload padronizado")
    void publishDeveEnviarEventoParaFilaComPayloadPadronizado() {
        Map<String, Object> payload = Map.of("competenceDate", "2026-06-01");

        eventPublisher.publish(EventType.SALARY_SUMMARY_UPDATED, "user-123", payload);

        ArgumentCaptor<Map<String, Object>> eventCaptor = ArgumentCaptor.forClass(Map.class);
        verify(jmsTemplate).convertAndSend(eq("events"), eventCaptor.capture());

        Map<String, Object> event = eventCaptor.getValue();
        assertEquals("SALARY_SUMMARY_UPDATED", event.get("type"));
        assertEquals("user-123", event.get("userId"));
        assertEquals(payload, event.get("payload"));
        assertNotNull(event.get("timestamp"));
    }
}
