package com.gestionprojet.gestionprojetacademique.dto.request;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateProjetRequest(
        @Size(max = 255) String titre,
        String description,
        LocalDate dateDebut,
        LocalDate dateFinPrevue,
        String lieuStage
) {}
