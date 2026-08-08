package br.com.finance.modules.payroll.dto;

public enum PayrollEvent {

    LIQUIDO(1, "Líquido"),
    FLASH(8, "Flash"),
    ALELO(9, "Alelo"),
    INSS(500, "INSS"),
    IRRF(505, "IRRF"),
    VALE_ALIMENTACAO(686, "Vale Alimentação");

    private final int id;
    private final String description;

    PayrollEvent(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public static boolean isValid(int id) {
        for (PayrollEvent event : PayrollEvent.values()) {
            if (event.getId() == id) {
                return true;
            }
        }
        return false;
    }

    public static PayrollEvent get(int id) {
        for (PayrollEvent event : PayrollEvent.values()) {
            if (event.getId() == id) {
                return event;
            }
        }
        return null;
    }

}
