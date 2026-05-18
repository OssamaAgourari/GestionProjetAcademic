package com.gestionprojet.gestionprojetacademique.controller;

import com.gestionprojet.gestionprojetacademique.dto.request.CreateSoutenanceRequest;
import com.gestionprojet.gestionprojetacademique.dto.response.SoutenanceResponse;
import com.gestionprojet.gestionprojetacademique.service.SoutenanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Soutenances")
@SecurityRequirement(name = "bearerAuth")
public class SoutenanceController {

    private final SoutenanceService soutenanceService;

    @PostMapping("/projets/{projetId}/soutenance")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    @Operation(summary = "Planifier une soutenance (Admin)")
    public SoutenanceResponse planifier(@PathVariable Long projetId,
                                        @Valid @RequestBody CreateSoutenanceRequest request) {
        return soutenanceService.planifier(projetId, request);
    }

    @GetMapping("/soutenances/{id}")
    @Operation(summary = "Détail d'une soutenance")
    public SoutenanceResponse findById(@PathVariable Long id) {
        return soutenanceService.findById(id);
    }

    @PostMapping("/soutenances/{id}/cloturer")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    @Operation(summary = "Clôturer une soutenance et calculer la note finale (Admin)")
    public SoutenanceResponse cloturer(@PathVariable Long id) {
        return soutenanceService.cloturer(id);
    }
}
