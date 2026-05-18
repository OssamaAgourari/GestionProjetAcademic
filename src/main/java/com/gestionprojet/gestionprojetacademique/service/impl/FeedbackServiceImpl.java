package com.gestionprojet.gestionprojetacademique.service.impl;

import com.gestionprojet.gestionprojetacademique.dto.request.FeedbackRequest;
import com.gestionprojet.gestionprojetacademique.dto.response.FeedbackResponse;
import com.gestionprojet.gestionprojetacademique.exception.ResourceNotFoundException;
import com.gestionprojet.gestionprojetacademique.mapper.RapportMapper;
import com.gestionprojet.gestionprojetacademique.model.Feedback;
import com.gestionprojet.gestionprojetacademique.model.RapportVersion;
import com.gestionprojet.gestionprojetacademique.repository.EncadrantAcademiqueRepository;
import com.gestionprojet.gestionprojetacademique.repository.FeedbackRepository;
import com.gestionprojet.gestionprojetacademique.repository.RapportVersionRepository;
import com.gestionprojet.gestionprojetacademique.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final RapportVersionRepository rapportVersionRepository;
    private final EncadrantAcademiqueRepository encadrantAcademiqueRepository;
    private final RapportMapper rapportMapper;

    @Override
    public FeedbackResponse ajouterFeedback(Long versionId, Long encadrantId, FeedbackRequest request) {
        RapportVersion version = rapportVersionRepository.findById(versionId)
                .orElseThrow(() -> new ResourceNotFoundException("RapportVersion", versionId));
        var encadrant = encadrantAcademiqueRepository.findById(encadrantId)
                .orElseThrow(() -> new ResourceNotFoundException("Encadrant", encadrantId));

        Feedback feedback = Feedback.builder()
                .contenu(request.contenu())
                .noteIntermediaire(request.noteIntermediaire())
                .version(version)
                .encadrant(encadrant)
                .build();

        Feedback saved = feedbackRepository.save(feedback);
        log.info("Feedback ajouté sur la version {} par l'encadrant {}", versionId, encadrantId);
        return rapportMapper.toFeedbackResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedbackResponse> listerParVersion(Long versionId) {
        return feedbackRepository.findByVersionId(versionId).stream()
                .map(rapportMapper::toFeedbackResponse).toList();
    }
}
