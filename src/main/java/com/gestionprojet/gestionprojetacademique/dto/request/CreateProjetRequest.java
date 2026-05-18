package com.gestionprojet.gestionprojetacademique.dto.request;

import com.gestionprojet.gestionprojetacademique.model.enums.TypeProjet;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateProjetRequest(
        @NotBlank @Size(max = 255) String titre,
        String description,
        @NotNull TypeProjet type,
        @NotNull Long etudiantId,
        @NotNull Long encadrantAcademiqueId,
        Long encadrantProfessionnelId,
        LocalDate dateDebut,
        LocalDate dateFinPrevue,
        String lieuStage
) {}
