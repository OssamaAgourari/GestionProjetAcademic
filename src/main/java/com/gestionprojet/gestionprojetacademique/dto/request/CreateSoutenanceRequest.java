package com.gestionprojet.gestionprojetacademique.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record CreateSoutenanceRequest(
        @NotNull @Future LocalDateTime dateSoutenance,
        @NotBlank String lieu,
        List<Long> membreJuryIds
) {}
