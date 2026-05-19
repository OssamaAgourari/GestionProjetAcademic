package com.gestionprojet.gestionprojetacademique.service;

import com.gestionprojet.gestionprojetacademique.dto.request.AffecterEncadrantsRequest;
import com.gestionprojet.gestionprojetacademique.dto.request.CreateProjetRequest;
import com.gestionprojet.gestionprojetacademique.dto.request.UpdateProjetRequest;
import com.gestionprojet.gestionprojetacademique.dto.response.ProjetDetailResponse;
import com.gestionprojet.gestionprojetacademique.dto.response.ProjetResponse;
import com.gestionprojet.gestionprojetacademique.model.enums.StatutProjet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProjetService {
    ProjetResponse create(CreateProjetRequest request);
    ProjetResponse update(Long id, UpdateProjetRequest request);
    ProjetDetailResponse findById(Long id);
    Page<ProjetResponse> findAll(Pageable pageable);
    Page<ProjetResponse> findByStatut(StatutProjet statut, Pageable pageable);
    List<ProjetResponse> findByEtudiant(Long etudiantId);
    List<ProjetResponse> findByEncadrant(Long encadrantId);
    List<ProjetResponse> findByEncadrantProfessionnel(Long encadrantId);
    ProjetResponse affecterEncadrants(Long id, AffecterEncadrantsRequest request);
    ProjetResponse changerStatut(Long id, StatutProjet statut);
    void archiver(Long id);
}
