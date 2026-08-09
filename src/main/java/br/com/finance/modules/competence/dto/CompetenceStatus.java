package br.com.finance.modules.competence.dto;

public enum CompetenceStatus {

    ABERTA(1, "Aberto"),
    FECHADO(2, "Fechado"),
    NAO_INICIALIZADO(3, "Não Inicializado");

    private final int id;

    private final String description;

    CompetenceStatus(int id, String description) {
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
        for (CompetenceStatus status : CompetenceStatus.values()) {
            if (status.getId() == id) {
                return true;
            }
        }
        return false;
    }

    public static CompetenceStatus get(int id) {
        for (CompetenceStatus status : CompetenceStatus.values()) {
            if (status.getId() == id) {
                return status;
            }
        }
        return null;
    }

}
