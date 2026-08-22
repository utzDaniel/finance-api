package br.com.finance.modules.expense.dto;

public enum ExpenseCategory {

    CUSTO_FIXO(1, "Custo Fixo"),
    CONFORTO(2, "Conforto"),
    METAS(3, "Metas"),
    PRAZERES(4, "Prazeres"),
    INVESTIMENTO(5, "Investimento"),
    CONHECIMENTO(6, "Conhecimento"),
    EMERGENCIA(7, "Emergencia");

    private final int id;

    private final String description;

    ExpenseCategory(int id, String description) {
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
        for (ExpenseCategory category : ExpenseCategory.values()) {
            if (category.getId() == id) {
                return true;
            }
        }
        return false;
    }

    public static ExpenseCategory get(int id) {
        for (ExpenseCategory category : ExpenseCategory.values()) {
            if (category.getId() == id) {
                return category;
            }
        }
        return null;
    }
}
