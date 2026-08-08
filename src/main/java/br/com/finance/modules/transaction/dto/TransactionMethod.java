package br.com.finance.modules.transaction.dto;

public enum TransactionMethod {

    CARTAO(1, "Cartão"),
    PIX(2, "Pix"),
    BOLETO(3, "Boleto");

    private final int id;

    private final String description;

    TransactionMethod(int id, String description) {
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
        for (TransactionMethod method : TransactionMethod.values()) {
            if (method.getId() == id) {
                return true;
            }
        }
        return false;
    }

    public static TransactionMethod get(int id) {
        for (TransactionMethod method : TransactionMethod.values()) {
            if (method.getId() == id) {
                return method;
            }
        }
        return null;
    }
}
