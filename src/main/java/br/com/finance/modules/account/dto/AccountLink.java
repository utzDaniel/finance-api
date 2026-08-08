package br.com.finance.modules.account.dto;

public enum AccountLink {

    NENHUM(1, "Nenhum"),
    SALARIO(2, "Salario"),
    FLASH(3, "Flash"),
    ALELO(4, "Alelo");

    private final int id;

    private final String description;

    AccountLink(int id, String description) {
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
        for (AccountLink link : AccountLink.values()) {
            if (link.getId() == id) {
                return true;
            }
        }
        return false;
    }

    public static AccountLink get(int id) {
        for (AccountLink link : AccountLink.values()) {
            if (link.getId() == id) {
                return link;
            }
        }
        return null;
    }
}
