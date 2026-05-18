package com.gestionprojet.gestionprojetacademique.controller;

import com.gestionprojet.gestionprojetacademique.dto.request.SubmitRapportRequest;
import com.gestionprojet.gestionprojetacademique.dto.response.RapportVersionResponse;
import com.gestionprojet.gestionprojetacademique.service.RapportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Rapports")
@SecurityRequirement(name = "bearerAuth")
public class RapportController {

    private final RapportService rapportService;

    @GetMapping("/projets/{projetId}/rapports")
    @Operation(summary = "Lister les versions de rapport")
    public List<RapportVersionResponse> listerVersions(@PathVariable Long projetId) {
        return rapportService.listerVersions(projetId);
    }

    @PostMapping(value = "/projets/{projetId}/rapports", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ETUDIANT')")
    @Operation(summary = "Soumettre une version de rapport (Étudiant)")
    public RapportVersionResponse soumettre(
            @PathVariable Long projetId,
            @RequestParam(required = false) String commentaireEtudiant,
            @RequestPart(name = "file", required = false) MultipartFile file) {
        return rapportService.soumettreVersion(projetId, new SubmitRapportRequest(commentaireEtudiant), file);
    }

    @GetMapping("/rapports/{id}/download")
    @Operation(summary = "Télécharger un rapport")
    public ResponseEntity<byte[]> download(@PathVariable Long id) {
        byte[] content = rapportService.telecharger(id);
        RapportVersionResponse version = rapportService.findById(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"rapport_v" + version.numeroVersion() + ".pdf\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(content);
    }
}
