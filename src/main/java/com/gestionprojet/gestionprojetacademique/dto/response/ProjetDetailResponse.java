package com.gestionprojet.gestionprojetacademique.dto.response;

import com.gestionprojet.gestionprojetacademique.model.enums.StatutProjet;
import com.gestionprojet.gestionprojetacademique.model.enums.TypeProjet;

import java.time.LocalDate;
import java.util.List;

public record ProjetDetailResponse(
        Long id,
        String titre,
        String description,
        TypeProjet type,
        StatutProjet statut,
        LocalDate dateCreation,
        LocalDate dateDebut,
        LocalDate dateFinPrevue,
        String lieuStage,
        Double resultatFinal,
        EtudiantResponse etudiant,
        EncadrantResponse encadrantAcademique,
        EncadrantResponse encadrantProfessionnel,
        List<RapportVersionResponse> versionsRapport,
        List<SeanceResponse> seances,
        SoutenanceResponse soutenance
) {}
