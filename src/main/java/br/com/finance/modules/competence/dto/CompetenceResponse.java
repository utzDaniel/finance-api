package br.com.finance.modules.competence.dto;

import java.time.LocalDate;

public record CompetenceResponse(LocalDate competence,
                                 CompetenceStatusResponse status) {
}
