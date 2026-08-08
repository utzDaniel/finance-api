package br.com.finance.modules.payroll.dto;

public enum EntryType {

    DESCONTO(1, "Desconto"),
    PROVENTO(2, "Provento"),
    BENEFICIO(3, "Benificio");

    private final int id;
    private final String description;

    EntryType(int id, String description) {
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
        for (EntryType type : EntryType.values()) {
            if (type.getId() == id) {
                return true;
            }
        }
        return false;
    }

    public static EntryType get(int id) {
        for (EntryType type : EntryType.values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        return null;
    }

}
