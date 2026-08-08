package br.com.finance.modules.account.dto;

public enum AccountType {

    CORRENTE(1, "Corrente"),
    POUPANCA(2, "Poupança"),
    SALARIO(3, "Salário"),
    PAGAMENTO(4, "Pagamento");

    private final int id;

    private final String description;

    AccountType(int id, String description) {
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
        for (AccountType type : AccountType.values()) {
            if (type.getId() == id) {
                return true;
            }
        }
        return false;
    }

    public static AccountType get(int id) {
        for (AccountType type : AccountType.values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        return null;
    }
}
