package com.gestionprojet.gestionprojetacademique.controller;

import com.gestionprojet.gestionprojetacademique.dto.request.EvaluationJuryRequest;
import com.gestionprojet.gestionprojetacademique.dto.response.EvaluationJuryResponse;
import com.gestionprojet.gestionprojetacademique.service.EvaluationJuryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/soutenances")
@RequiredArgsConstructor
@Tag(name = "Évaluations du jury")
@SecurityRequirement(name = "bearerAuth")
public class EvaluationJuryController {

    private final EvaluationJuryService evaluationJuryService;

    @PostMapping("/{soutenanceId}/evaluations")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('MEMBRE_JURY', 'ADMINISTRATEUR')")
    @Operation(summary = "Enregistrer une évaluation (Jury)")
    public EvaluationJuryResponse enregistrer(@PathVariable Long soutenanceId,
                                               @Valid @RequestBody EvaluationJuryRequest request) {
        return evaluationJuryService.enregistrerEvaluation(soutenanceId, request);
    }

    @GetMapping("/{soutenanceId}/evaluations")
    @Operation(summary = "Lister les évaluations d'une soutenance")
    public List<EvaluationJuryResponse> lister(@PathVariable Long soutenanceId) {
        return evaluationJuryService.listerParSoutenance(soutenanceId);
    }
}
