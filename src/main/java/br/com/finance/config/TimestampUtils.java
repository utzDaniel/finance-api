package br.com.finance.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public final class TimestampUtils {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    public static final String DATA_REGEX = "^\\d{4}-(0[1-9]|1[0-2])-\\d{2}$";

    private TimestampUtils() {
        throw new UnsupportedOperationException("Classe utilitária não pode ser instanciada");
    }

    public static String now() {
        return LocalDateTime.now(ZoneOffset.UTC).format(ISO_FORMATTER);
    }

    public static LocalDate parseCompetence(String competence) {
        return LocalDate.of(Integer.parseInt(competence.substring(0, 4)),
                Integer.parseInt(competence.substring(5, 7)),
                1
        );
    }

    public static LocalDate parse(String data) {
        return LocalDate.of(Integer.parseInt(data.substring(0, 4)),
                Integer.parseInt(data.substring(5, 7)),
                Integer.parseInt(data.substring(8, 10))
        );
    }
}

