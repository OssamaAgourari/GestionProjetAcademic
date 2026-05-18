package com.gestionprojet.gestionprojetacademique.controller;

import com.gestionprojet.gestionprojetacademique.dto.request.FeedbackRequest;
import com.gestionprojet.gestionprojetacademique.dto.response.FeedbackResponse;
import com.gestionprojet.gestionprojetacademique.repository.EncadrantAcademiqueRepository;
import com.gestionprojet.gestionprojetacademique.service.FeedbackService;
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
@RequestMapping("/api/v1/rapports")
@RequiredArgsConstructor
@Tag(name = "Feedbacks")
@SecurityRequirement(name = "bearerAuth")
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final EncadrantAcademiqueRepository encadrantRepository;

    @PostMapping("/{versionId}/feedbacks")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ENCADRANT_ACADEMIQUE', 'ADMINISTRATEUR')")
    @Operation(summary = "Ajouter un feedback (Encadrant)")
    public FeedbackResponse ajouterFeedback(@PathVariable Long versionId,
                                            @Valid @RequestBody FeedbackRequest request,
                                            Principal principal) {
        Long encadrantId = encadrantRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Encadrant introuvable")).getId();
        return feedbackService.ajouterFeedback(versionId, encadrantId, request);
    }

    @GetMapping("/{versionId}/feedbacks")
    @Operation(summary = "Lister les feedbacks d'une version")
    public List<FeedbackResponse> listerFeedbacks(@PathVariable Long versionId) {
        return feedbackService.listerParVersion(versionId);
    }
}
