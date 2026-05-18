package com.gestionprojet.gestionprojetacademique.dto.request;

import com.gestionprojet.gestionprojetacademique.model.enums.ModeSeance;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateSeanceRequest(
        @NotNull @Future LocalDateTime dateSeance,
        @Min(15) int dureeMinutes,
        @NotNull ModeSeance mode,
        String lieuOuLien,
        String notes,
        String commentaires
) {}
