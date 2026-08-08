package br.com.finance.modules.expense.dto;

public enum ExpenseShared {

    INDIVIDUAL(false, "Individual"),
    FAMILIAR(true, "Familiar");

    private final boolean value;

    private final String description;

    ExpenseShared(boolean value, String description) {
        this.value = value;
        this.description = description;
    }

    public boolean getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public static ExpenseShared get(boolean value) {
        return value ? FAMILIAR : INDIVIDUAL;
    }
}
