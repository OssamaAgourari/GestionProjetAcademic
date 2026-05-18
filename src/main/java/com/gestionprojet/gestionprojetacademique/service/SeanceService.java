package com.gestionprojet.gestionprojetacademique.service;

import com.gestionprojet.gestionprojetacademique.dto.request.CreateSeanceRequest;
import com.gestionprojet.gestionprojetacademique.dto.response.SeanceResponse;

import java.util.List;

public interface SeanceService {
    SeanceResponse planifier(Long projetId, Long encadrantId, CreateSeanceRequest request);
    SeanceResponse mettreAJourNotes(Long seanceId, CreateSeanceRequest request);
    List<SeanceResponse> listerParProjet(Long projetId);
}
