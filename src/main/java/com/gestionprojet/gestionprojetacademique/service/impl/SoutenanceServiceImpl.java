package com.gestionprojet.gestionprojetacademique.service.impl;

import com.gestionprojet.gestionprojetacademique.dto.request.CreateSoutenanceRequest;
import com.gestionprojet.gestionprojetacademique.dto.response.SoutenanceResponse;
import com.gestionprojet.gestionprojetacademique.exception.BusinessRuleViolationException;
import com.gestionprojet.gestionprojetacademique.exception.DuplicateResourceException;
import com.gestionprojet.gestionprojetacademique.exception.ResourceNotFoundException;
import com.gestionprojet.gestionprojetacademique.mapper.SoutenanceMapper;
import com.gestionprojet.gestionprojetacademique.model.*;
import com.gestionprojet.gestionprojetacademique.model.enums.StatutProjet;
import com.gestionprojet.gestionprojetacademique.model.enums.StatutSoutenance;
import com.gestionprojet.gestionprojetacademique.repository.*;
import com.gestionprojet.gestionprojetacademique.service.SoutenanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class SoutenanceServiceImpl implements SoutenanceService {

    private final SoutenanceRepository soutenanceRepository;
    private final ProjetRepository projetRepository;
    private final MembreJuryRepository membreJuryRepository;
    private final EvaluationJuryRepository evaluationJuryRepository;
    private final RapportVersionRepository rapportVersionRepository;
    private final SoutenanceMapper soutenanceMapper;

    @Override
    public SoutenanceResponse planifier(Long projetId, CreateSoutenanceRequest request) {
        Projet projet = projetRepository.findById(projetId)
                .orElseThrow(() -> new ResourceNotFoundException("Projet", projetId));

        if (projet.getStatut() != StatutProjet.EN_COURS) {
            throw new BusinessRuleViolationException("La soutenance ne peut être planifiée que pour un projet EN_COURS");
        }
        if (soutenanceRepository.existsByProjetId(projetId)) {
            throw new DuplicateResourceException("Une soutenance existe déjà pour ce projet");
        }
        boolean hasVersions = rapportVersionRepository.findTopByProjetIdOrderByNumeroVersionDesc(projetId).isPresent();
        if (!hasVersions) {
            throw new BusinessRuleViolationException("Au moins une version de rapport est requise avant de planifier la soutenance");
        }

        Soutenance soutenance = Soutenance.builder()
                .projet(projet)
                .dateSoutenance(request.dateSoutenance())
                .lieu(request.lieu())
                .build();

        Soutenance saved = soutenanceRepository.save(soutenance);

        if (request.membreJuryIds() != null && !request.membreJuryIds().isEmpty()) {
            assignerJury(saved, request.membreJuryIds());
        }

        log.info("Soutenance planifiée pour le projet {}", projetId);
        return soutenanceMapper.toResponse(soutenanceRepository.findById(saved.getId()).orElseThrow());
    }

    @Override
    @Transactional(readOnly = true)
    public SoutenanceResponse findById(Long id) {
        return soutenanceMapper.toResponse(soutenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Soutenance", id)));
    }

    @Override
    public SoutenanceResponse cloturer(Long soutenanceId) {
        Soutenance soutenance = soutenanceRepository.findById(soutenanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Soutenance", soutenanceId));

        List<EvaluationJury> evaluations = evaluationJuryRepository.findBySoutenanceId(soutenanceId);
        if (evaluations.isEmpty()) {
            throw new BusinessRuleViolationException("Aucune évaluation soumise pour cette soutenance");
        }

        double moyenne = evaluations.stream().mapToDouble(EvaluationJury::getNote).average().orElse(0.0);
        double resultat = BigDecimal.valueOf(moyenne).setScale(2, RoundingMode.HALF_UP).doubleValue();

        Projet projet = soutenance.getProjet();
        projet.setResultatFinal(resultat);
        projet.setStatut(StatutProjet.SOUTENU);
        projetRepository.save(projet);

        soutenance.setStatut(StatutSoutenance.EFFECTUEE);
        log.info("Soutenance {} clôturée, résultat final: {}", soutenanceId, resultat);
        return soutenanceMapper.toResponse(soutenanceRepository.save(soutenance));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SoutenanceResponse> findAll() {
        return soutenanceRepository.findAll().stream()
                .map(soutenanceMapper::toResponse).toList();
    }

    private void assignerJury(Soutenance soutenance, List<Long> membreJuryIds) {
        for (Long juryId : membreJuryIds) {
            MembreJury membre = membreJuryRepository.findById(juryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Membre du jury", juryId));
            EvaluationJuryId evalId = new EvaluationJuryId(soutenance.getId(), juryId);
            if (!evaluationJuryRepository.existsById(evalId)) {
                EvaluationJury eval = EvaluationJury.builder()
                        .id(evalId)
                        .soutenance(soutenance)
                        .membreJury(membre)
                        .note(0.0)
                        .build();
                evaluationJuryRepository.save(eval);
            }
        }
    }
}
