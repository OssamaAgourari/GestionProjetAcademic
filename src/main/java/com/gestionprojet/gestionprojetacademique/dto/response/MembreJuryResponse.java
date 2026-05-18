package com.gestionprojet.gestionprojetacademique.dto.response;

public record MembreJuryResponse(
        Long id,
        String nom,
        String prenom,
        String email,
        String specialite,
        String institution
) {}
