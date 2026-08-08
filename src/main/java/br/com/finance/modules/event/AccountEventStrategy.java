package br.com.finance.modules.event;

import br.com.finance.modules.event.dto.AccountPayload;
import br.com.finance.modules.event.dto.EventAction;
import br.com.finance.modules.event.dto.EventType;
import org.springframework.stereotype.Component;

@Component
public class AccountEventStrategy implements EventStrategy<AccountPayload> {

    @Override
    public void process(AccountPayload payload, EventAction action) {

    }

    @Override
    public EventType getTypeHandler() {
        return EventType.ACCOUNT;
    }
}
