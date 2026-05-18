package com.gestionprojet.gestionprojetacademique.controller;

import com.gestionprojet.gestionprojetacademique.dto.request.CreateSeanceRequest;
import com.gestionprojet.gestionprojetacademique.dto.response.SeanceResponse;
import com.gestionprojet.gestionprojetacademique.repository.EncadrantAcademiqueRepository;
import com.gestionprojet.gestionprojetacademique.service.SeanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Séances d'encadrement")
@SecurityRequirement(name = "bearerAuth")
public class SeanceController {

    private final SeanceService seanceService;
    private final EncadrantAcademiqueRepository encadrantRepository;

    @PostMapping("/projets/{projetId}/seances")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ENCADRANT_ACADEMIQUE', 'ADMINISTRATEUR')")
    @Operation(summary = "Planifier une séance (Encadrant)")
    public SeanceResponse planifier(@PathVariable Long projetId,
                                    @Valid @RequestBody CreateSeanceRequest request,
                                    Principal principal) {
        Long encadrantId = encadrantRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Encadrant introuvable")).getId();
        return seanceService.planifier(projetId, encadrantId, request);
    }

    @GetMapping("/projets/{projetId}/seances")
    @Operation(summary = "Lister les séances d'un projet")
    public List<SeanceResponse> listerSeances(@PathVariable Long projetId) {
        return seanceService.listerParProjet(projetId);
    }

    @PutMapping("/seances/{id}")
    @PreAuthorize("hasAnyRole('ENCADRANT_ACADEMIQUE', 'ADMINISTRATEUR')")
    @Operation(summary = "Mettre à jour les notes d'une séance")
    public SeanceResponse mettreAJour(@PathVariable Long id, @Valid @RequestBody CreateSeanceRequest request) {
        return seanceService.mettreAJourNotes(id, request);
    }
}
