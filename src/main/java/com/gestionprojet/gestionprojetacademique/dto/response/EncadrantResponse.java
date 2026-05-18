package com.gestionprojet.gestionprojetacademique.dto.response;

public record EncadrantResponse(
        Long id,
        String nom,
        String prenom,
        String email,
        String telephone,
        String specialite,
        String grade,
        String role
) {}
