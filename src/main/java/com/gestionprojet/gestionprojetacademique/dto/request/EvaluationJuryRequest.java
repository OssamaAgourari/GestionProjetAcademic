package com.gestionprojet.gestionprojetacademique.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record EvaluationJuryRequest(
        @NotNull Long membreJuryId,
        @NotNull @Min(0) @Max(20) Double note,
        String commentaire
) {}
