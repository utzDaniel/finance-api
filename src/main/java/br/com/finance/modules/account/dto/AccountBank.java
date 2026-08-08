package br.com.finance.modules.account.dto;

public enum AccountBank {

    CAIXA_FEDERAL(1, "Caixa Federal"),
    ITAU(2, "Itau"),
    BRADESCO(3, "Bradesco"),
    SANTANDER(4, "Santander"),
    BB(5, "Banco do Brasil"),
    BTG(6, "BTG Pactual");

    private final int id;

    private final String description;

    AccountBank(int id, String description) {
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
        for (AccountBank bank : AccountBank.values()) {
            if (bank.getId() == id) {
                return true;
            }
        }
        return false;
    }

    public static AccountBank get(int id) {
        for (AccountBank bank : AccountBank.values()) {
            if (bank.getId() == id) {
                return bank;
            }
        }
        return null;
    }
}
