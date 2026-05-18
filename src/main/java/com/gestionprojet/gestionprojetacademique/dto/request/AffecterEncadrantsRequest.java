package com.gestionprojet.gestionprojetacademique.dto.request;

import jakarta.validation.constraints.NotNull;

public record AffecterEncadrantsRequest(
        @NotNull Long encadrantAcademiqueId,
        Long encadrantProfessionnelId
) {}
