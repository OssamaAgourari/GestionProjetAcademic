package com.gestionprojet.gestionprojetacademique.service.impl;

import com.gestionprojet.gestionprojetacademique.dto.request.SubmitRapportRequest;
import com.gestionprojet.gestionprojetacademique.dto.response.RapportVersionResponse;
import com.gestionprojet.gestionprojetacademique.exception.BusinessRuleViolationException;
import com.gestionprojet.gestionprojetacademique.exception.ResourceNotFoundException;
import com.gestionprojet.gestionprojetacademique.mapper.RapportMapper;
import com.gestionprojet.gestionprojetacademique.model.Projet;
import com.gestionprojet.gestionprojetacademique.model.RapportVersion;
import com.gestionprojet.gestionprojetacademique.model.enums.StatutProjet;
import com.gestionprojet.gestionprojetacademique.repository.ProjetRepository;
import com.gestionprojet.gestionprojetacademique.repository.RapportVersionRepository;
import com.gestionprojet.gestionprojetacademique.service.RapportService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RapportServiceImpl implements RapportService {

    private final RapportVersionRepository rapportVersionRepository;
    private final ProjetRepository projetRepository;
    private final RapportMapper rapportMapper;

    @Value("${app.upload.dir:./uploads/rapports}")
    private String uploadDir;

    @PostConstruct
    public void initUploadDir() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
            log.info("Répertoire d'upload initialisé: {}", uploadDir);
        } catch (IOException e) {
            log.error("Impossible de créer le répertoire d'upload", e);
        }
    }

    @Override
    public RapportVersionResponse soumettreVersion(Long projetId, SubmitRapportRequest request, MultipartFile file) {
        Projet projet = projetRepository.findById(projetId)
                .orElseThrow(() -> new ResourceNotFoundException("Projet", projetId));

        if (projet.getStatut() == StatutProjet.SOUTENU || projet.getStatut() == StatutProjet.ARCHIVE) {
            throw new BusinessRuleViolationException("Impossible de soumettre un rapport pour un projet " + projet.getStatut());
        }

        int nextVersion = rapportVersionRepository.findMaxNumeroVersionByProjetId(projetId) + 1;
        String cheminFichier = null;

        if (file != null && !file.isEmpty()) {
            cheminFichier = saveFile(projetId, nextVersion, file);
        }

        RapportVersion version = RapportVersion.builder()
                .projet(projet)
                .numeroVersion(nextVersion)
                .cheminFichier(cheminFichier)
                .commentaireEtudiant(request.commentaireEtudiant())
                .build();

        RapportVersion saved = rapportVersionRepository.save(version);
        log.info("Rapport v{} soumis pour le projet {}", nextVersion, projetId);
        return rapportMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RapportVersionResponse> listerVersions(Long projetId) {
        return rapportVersionRepository.findByProjetIdOrderByNumeroVersionDesc(projetId).stream()
                .map(rapportMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] telecharger(Long versionId) {
        RapportVersion version = rapportVersionRepository.findById(versionId)
                .orElseThrow(() -> new ResourceNotFoundException("RapportVersion", versionId));
        if (version.getCheminFichier() == null) {
            throw new ResourceNotFoundException("Aucun fichier associé à cette version");
        }
        try {
            return Files.readAllBytes(Path.of(version.getCheminFichier()));
        } catch (IOException e) {
            log.error("Erreur lors du téléchargement du rapport {}", versionId, e);
            throw new ResourceNotFoundException("Fichier introuvable sur le serveur");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public RapportVersionResponse findById(Long id) {
        return rapportMapper.toResponse(rapportVersionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RapportVersion", id)));
    }

    private String saveFile(Long projetId, int version, MultipartFile file) {
        try {
            Path dir = Paths.get(uploadDir, String.valueOf(projetId));
            Files.createDirectories(dir);
            String filename = "v" + version + "_" + file.getOriginalFilename();
            Path target = dir.resolve(filename);
            Files.copy(file.getInputStream(), target);
            return target.toString();
        } catch (IOException e) {
            log.error("Erreur lors de la sauvegarde du fichier", e);
            throw new BusinessRuleViolationException("Impossible de sauvegarder le fichier");
        }
    }
}
