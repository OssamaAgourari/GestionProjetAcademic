package com.gestionprojet.gestionprojetacademique.dto.response;

public record EtudiantResponse(
        Long id,
        String nom,
        String prenom,
        String email,
        String telephone,
        String matricule,
        String filiere,
        String niveau,
        boolean actif
) {}
