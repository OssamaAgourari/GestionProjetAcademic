package com.gestionprojet.gestionprojetacademique.dto.response;

import com.gestionprojet.gestionprojetacademique.model.enums.StatutSoutenance;

import java.time.LocalDateTime;
import java.util.List;

public record SoutenanceResponse(
        Long id,
        LocalDateTime dateSoutenance,
        String lieu,
        StatutSoutenance statut,
        Long projetId,
        List<EvaluationJuryResponse> evaluations
) {}
