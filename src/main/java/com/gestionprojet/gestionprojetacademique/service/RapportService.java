package com.gestionprojet.gestionprojetacademique.service;

import com.gestionprojet.gestionprojetacademique.dto.request.SubmitRapportRequest;
import com.gestionprojet.gestionprojetacademique.dto.response.RapportVersionResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RapportService {
    RapportVersionResponse soumettreVersion(Long projetId, SubmitRapportRequest request, MultipartFile file);
    List<RapportVersionResponse> listerVersions(Long projetId);
    byte[] telecharger(Long versionId);
    RapportVersionResponse findById(Long id);
}
