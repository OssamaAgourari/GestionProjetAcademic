package com.gestionprojet.gestionprojetacademique.dto.response;

import java.time.LocalDateTime;

public record FeedbackResponse(
        Long id,
        String contenu,
        LocalDateTime dateFeedback,
        Double noteIntermediaire,
        Long versionId,
        String encadrantNom,
        String encadrantPrenom
) {}
