package br.com.finance.modules.event.dto;

public record EventPayload<T>(
        EventType type,
        EventAction action,
        String userID,
        T object
) {
}
