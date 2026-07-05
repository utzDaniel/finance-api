package br.com.finance.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public final class TimestampUtils {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    public static final String COMPETENCE_DATE_REGEX = "^\\d{4}-(0[1-9]|1[0-2])-\\d{2}$";

    private TimestampUtils() {
        throw new UnsupportedOperationException("Classe utilitária não pode ser instanciada");
    }

    public static String now() {
        return LocalDateTime.now(ZoneOffset.UTC).format(ISO_FORMATTER);
    }

    public static LocalDate parseCompetenceDate(String competenceDate) {
        return LocalDate.of(Integer.parseInt(competenceDate.substring(0, 4)),
                Integer.parseInt(competenceDate.substring(5, 7)),
                1
        );
    }
}

