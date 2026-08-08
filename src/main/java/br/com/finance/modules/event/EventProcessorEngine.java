package br.com.finance.modules.event;

import br.com.finance.modules.event.dto.EventPayload;
import br.com.finance.modules.event.dto.EventType;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.Map;

@Service
@SuppressWarnings("rawtypes")
public class EventProcessorEngine {

    private final EventPublisher eventPublisher;

    private final Map<EventType, EventStrategy> strategies = new EnumMap<>(EventType.class);


    public EventProcessorEngine(ApplicationContext context, EventPublisher eventPublisher) {
        Map<String, EventStrategy> beans = context.getBeansOfType(EventStrategy.class);

        for (EventStrategy strategy : beans.values()) {
            this.strategies.put(strategy.getTypeHandler(), strategy);
        }
        this.eventPublisher = eventPublisher;
    }

    @SuppressWarnings("unchecked")
    public <T> void process(EventPayload<T> payload) {

        EventStrategy<T> strategy = strategies.get(payload.type());

        eventPublisher.publish(payload.type(), payload.userID(), payload);

        if (strategy != null) {
            strategy.process(payload.object(), payload.action());
        }
    }

}
