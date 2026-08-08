package br.com.finance.modules.event;

import br.com.finance.modules.event.dto.EventAction;
import br.com.finance.modules.event.dto.EventType;

public interface EventStrategy<T> {

    void process(T payload, EventAction action);

    EventType getTypeHandler();

}
