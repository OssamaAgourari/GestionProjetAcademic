package com.gestionprojet.gestionprojetacademique.service.impl;

import com.gestionprojet.gestionprojetacademique.dto.request.CreateSeanceRequest;
import com.gestionprojet.gestionprojetacademique.dto.response.SeanceResponse;
import com.gestionprojet.gestionprojetacademique.exception.ResourceNotFoundException;
import com.gestionprojet.gestionprojetacademique.mapper.SeanceMapper;
import com.gestionprojet.gestionprojetacademique.model.SeanceEncadrement;
import com.gestionprojet.gestionprojetacademique.repository.EncadrantAcademiqueRepository;
import com.gestionprojet.gestionprojetacademique.repository.ProjetRepository;
import com.gestionprojet.gestionprojetacademique.repository.SeanceEncadrementRepository;
import com.gestionprojet.gestionprojetacademique.service.SeanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class SeanceServiceImpl implements SeanceService {

    private final SeanceEncadrementRepository seanceRepository;
    private final ProjetRepository projetRepository;
    private final EncadrantAcademiqueRepository encadrantRepository;
    private final SeanceMapper seanceMapper;

    @Override
    public SeanceResponse planifier(Long projetId, Long encadrantId, CreateSeanceRequest request) {
        var projet = projetRepository.findById(projetId)
                .orElseThrow(() -> new ResourceNotFoundException("Projet", projetId));
        var encadrant = encadrantRepository.findById(encadrantId)
                .orElseThrow(() -> new ResourceNotFoundException("Encadrant", encadrantId));

        SeanceEncadrement seance = SeanceEncadrement.builder()
                .projet(projet)
                .encadrant(encadrant)
                .dateSeance(request.dateSeance())
                .dureeMinutes(request.dureeMinutes())
                .mode(request.mode())
                .lieuOuLien(request.lieuOuLien())
                .notes(request.notes())
                .commentaires(request.commentaires())
                .build();

        SeanceEncadrement saved = seanceRepository.save(seance);
        log.info("Séance planifiée pour le projet {}", projetId);
        return seanceMapper.toResponse(saved);
    }

    @Override
    public SeanceResponse mettreAJourNotes(Long seanceId, CreateSeanceRequest request) {
        SeanceEncadrement seance = seanceRepository.findById(seanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Séance", seanceId));
        seance.setNotes(request.notes());
        seance.setCommentaires(request.commentaires());
        seance.setDureeMinutes(request.dureeMinutes());
        return seanceMapper.toResponse(seanceRepository.save(seance));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeanceResponse> listerParProjet(Long projetId) {
        return seanceRepository.findByProjetIdOrderByDateSeanceDesc(projetId).stream()
                .map(seanceMapper::toResponse).toList();
    }
}
