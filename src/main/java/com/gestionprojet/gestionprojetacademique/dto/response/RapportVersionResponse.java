package com.gestionprojet.gestionprojetacademique.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record RapportVersionResponse(
        Long id,
        int numeroVersion,
        LocalDateTime dateSoumission,
        String cheminFichier,
        String commentaireEtudiant,
        Long projetId,
        List<FeedbackResponse> feedbacks
) {}
