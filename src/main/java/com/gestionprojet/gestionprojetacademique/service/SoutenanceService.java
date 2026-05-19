package com.gestionprojet.gestionprojetacademique.service;

import com.gestionprojet.gestionprojetacademique.dto.request.CreateSoutenanceRequest;
import com.gestionprojet.gestionprojetacademique.dto.response.SoutenanceResponse;

public interface SoutenanceService {
    SoutenanceResponse planifier(Long projetId, CreateSoutenanceRequest request);
    SoutenanceResponse findById(Long id);
    SoutenanceResponse cloturer(Long soutenanceId);
    java.util.List<SoutenanceResponse> findAll();
    java.util.List<SoutenanceResponse> findByMembreJury(Long juryId);
}
