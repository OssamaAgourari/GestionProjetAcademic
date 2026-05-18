package com.gestionprojet.gestionprojetacademique.controller;

import com.gestionprojet.gestionprojetacademique.dto.request.AffecterEncadrantsRequest;
import com.gestionprojet.gestionprojetacademique.dto.request.CreateProjetRequest;
import com.gestionprojet.gestionprojetacademique.dto.request.UpdateProjetRequest;
import com.gestionprojet.gestionprojetacademique.dto.response.ProjetDetailResponse;
import com.gestionprojet.gestionprojetacademique.dto.response.ProjetResponse;
import com.gestionprojet.gestionprojetacademique.model.enums.StatutProjet;
import com.gestionprojet.gestionprojetacademique.repository.EtudiantRepository;
import com.gestionprojet.gestionprojetacademique.repository.EncadrantAcademiqueRepository;
import com.gestionprojet.gestionprojetacademique.service.ProjetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/projets")
@RequiredArgsConstructor
@Tag(name = "Projets")
@SecurityRequirement(name = "bearerAuth")
public class ProjetController {

    private final ProjetService projetService;
    private final EtudiantRepository etudiantRepository;
    private final EncadrantAcademiqueRepository encadrantRepository;

    @GetMapping
    @Operation(summary = "Lister tous les projets (paginé)")
    public Page<ProjetResponse> findAll(Pageable pageable) {
        return projetService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détail d'un projet")
    public ProjetDetailResponse findById(@PathVariable Long id) {
        return projetService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    @Operation(summary = "Créer un projet (Admin)")
    public ProjetResponse create(@Valid @RequestBody CreateProjetRequest request) {
        return projetService.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    @Operation(summary = "Modifier un projet (Admin)")
    public ProjetResponse update(@PathVariable Long id, @Valid @RequestBody UpdateProjetRequest request) {
        return projetService.update(id, request);
    }

    @PatchMapping("/{id}/encadrants")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    @Operation(summary = "Affecter les encadrants (Admin)")
    public ProjetResponse affecterEncadrants(@PathVariable Long id, @Valid @RequestBody AffecterEncadrantsRequest request) {
        return projetService.affecterEncadrants(id, request);
    }

    @PatchMapping("/{id}/statut")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    @Operation(summary = "Changer le statut d'un projet (Admin)")
    public ProjetResponse changerStatut(@PathVariable Long id, @RequestParam StatutProjet statut) {
        return projetService.changerStatut(id, statut);
    }

    @GetMapping("/mine")
    @PreAuthorize("hasRole('ETUDIANT')")
    @Operation(summary = "Mes projets (Étudiant)")
    public List<ProjetResponse> myProjects(Principal principal) {
        return etudiantRepository.findByEmail(principal.getName())
                .map(e -> projetService.findByEtudiant(e.getId()))
                .orElse(List.of());
    }

    @GetMapping("/encadrement")
    @PreAuthorize("hasAnyRole('ENCADRANT_ACADEMIQUE', 'ADMINISTRATEUR')")
    @Operation(summary = "Projets que j'encadre")
    public List<ProjetResponse> myEncadrements(Principal principal) {
        return encadrantRepository.findByEmail(principal.getName())
                .map(e -> projetService.findByEncadrant(e.getId()))
                .orElse(List.of());
    }
}
