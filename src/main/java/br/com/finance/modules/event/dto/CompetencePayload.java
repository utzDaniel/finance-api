package br.com.finance.modules.event.dto;

import java.time.LocalDate;

public record CompetencePayload(LocalDate competence, String userID) {
}
