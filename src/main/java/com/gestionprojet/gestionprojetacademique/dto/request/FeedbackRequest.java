package com.gestionprojet.gestionprojetacademique.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record FeedbackRequest(
        @NotBlank String contenu,
        @Min(0) @Max(20) Double noteIntermediaire
) {}
