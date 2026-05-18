package com.gestionprojet.gestionprojetacademique.service;

import com.gestionprojet.gestionprojetacademique.dto.request.FeedbackRequest;
import com.gestionprojet.gestionprojetacademique.dto.response.FeedbackResponse;

import java.util.List;

public interface FeedbackService {
    FeedbackResponse ajouterFeedback(Long versionId, Long encadrantId, FeedbackRequest request);
    List<FeedbackResponse> listerParVersion(Long versionId);
}
