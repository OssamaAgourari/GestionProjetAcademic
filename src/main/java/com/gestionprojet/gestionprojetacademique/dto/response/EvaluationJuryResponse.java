package com.gestionprojet.gestionprojetacademique.dto.response;

import java.time.LocalDateTime;

public record EvaluationJuryResponse(
        Long soutenanceId,
        Long membreJuryId,
        String membreJuryNom,
        String membreJuryPrenom,
        Double note,
        String commentaire,
        LocalDateTime dateEvaluation
) {}
