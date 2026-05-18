package com.gestionprojet.gestionprojetacademique.service.impl;

import com.gestionprojet.gestionprojetacademique.dto.request.EvaluationJuryRequest;
import com.gestionprojet.gestionprojetacademique.dto.response.EvaluationJuryResponse;
import com.gestionprojet.gestionprojetacademique.exception.ResourceNotFoundException;
import com.gestionprojet.gestionprojetacademique.mapper.SoutenanceMapper;
import com.gestionprojet.gestionprojetacademique.model.*;
import com.gestionprojet.gestionprojetacademique.repository.*;
import com.gestionprojet.gestionprojetacademique.service.EvaluationJuryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class EvaluationJuryServiceImpl implements EvaluationJuryService {

    private final EvaluationJuryRepository evaluationJuryRepository;
    private final SoutenanceRepository soutenanceRepository;
    private final MembreJuryRepository membreJuryRepository;
    private final SoutenanceMapper soutenanceMapper;

    @Override
    public EvaluationJuryResponse enregistrerEvaluation(Long soutenanceId, EvaluationJuryRequest request) {
        Soutenance soutenance = soutenanceRepository.findById(soutenanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Soutenance", soutenanceId));
        MembreJury membre = membreJuryRepository.findById(request.membreJuryId())
                .orElseThrow(() -> new ResourceNotFoundException("Membre du jury", request.membreJuryId()));

        EvaluationJuryId id = new EvaluationJuryId(soutenanceId, request.membreJuryId());
        EvaluationJury evaluation = evaluationJuryRepository.findById(id)
                .orElse(EvaluationJury.builder().id(id).soutenance(soutenance).membreJury(membre).build());

        evaluation.setNote(request.note());
        evaluation.setCommentaire(request.commentaire());

        EvaluationJury saved = evaluationJuryRepository.save(evaluation);
        log.info("Évaluation enregistrée: soutenance={}, jury={}, note={}", soutenanceId, request.membreJuryId(), request.note());
        return soutenanceMapper.toEvaluationResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvaluationJuryResponse> listerParSoutenance(Long soutenanceId) {
        return evaluationJuryRepository.findBySoutenanceId(soutenanceId).stream()
                .map(soutenanceMapper::toEvaluationResponse).toList();
    }
}
