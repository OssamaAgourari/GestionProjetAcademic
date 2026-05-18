package com.gestionprojet.gestionprojetacademique.dto.response;

import com.gestionprojet.gestionprojetacademique.model.enums.ModeSeance;

import java.time.LocalDateTime;

public record SeanceResponse(
        Long id,
        LocalDateTime dateSeance,
        int dureeMinutes,
        ModeSeance mode,
        String lieuOuLien,
        String notes,
        String commentaires,
        Long projetId,
        String encadrantNom,
        String encadrantPrenom
) {}
