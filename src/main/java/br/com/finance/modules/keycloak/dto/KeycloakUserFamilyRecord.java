package br.com.finance.modules.keycloak.dto;

public record KeycloakUserFamilyRecord(
        String userId,
        String nome,
        String sobrenome,
        Long familyId,
        String familyName
) {
}