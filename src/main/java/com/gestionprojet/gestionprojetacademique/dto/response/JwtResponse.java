package com.gestionprojet.gestionprojetacademique.dto.response;

public record JwtResponse(
        String token,
        String type,
        Long id,
        String nom,
        String prenom,
        String email,
        String role
) {
    public JwtResponse(String token, Long id, String nom, String prenom, String email, String role) {
        this(token, "Bearer", id, nom, prenom, email, role);
    }
}
