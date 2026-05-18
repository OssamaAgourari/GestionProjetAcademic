package com.gestionprojet.gestionprojetacademique.service;

import com.gestionprojet.gestionprojetacademique.dto.request.EvaluationJuryRequest;
import com.gestionprojet.gestionprojetacademique.dto.response.EvaluationJuryResponse;

import java.util.List;

public interface EvaluationJuryService {
    EvaluationJuryResponse enregistrerEvaluation(Long soutenanceId, EvaluationJuryRequest request);
    List<EvaluationJuryResponse> listerParSoutenance(Long soutenanceId);
}
