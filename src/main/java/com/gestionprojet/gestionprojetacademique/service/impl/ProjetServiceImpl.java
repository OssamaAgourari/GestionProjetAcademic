package com.gestionprojet.gestionprojetacademique.service.impl;

import com.gestionprojet.gestionprojetacademique.dto.request.AffecterEncadrantsRequest;
import com.gestionprojet.gestionprojetacademique.dto.request.CreateProjetRequest;
import com.gestionprojet.gestionprojetacademique.dto.request.UpdateProjetRequest;
import com.gestionprojet.gestionprojetacademique.dto.response.*;
import com.gestionprojet.gestionprojetacademique.exception.BusinessRuleViolationException;
import com.gestionprojet.gestionprojetacademique.exception.ResourceNotFoundException;
import com.gestionprojet.gestionprojetacademique.mapper.ProjetMapper;
import com.gestionprojet.gestionprojetacademique.mapper.RapportMapper;
import com.gestionprojet.gestionprojetacademique.mapper.SeanceMapper;
import com.gestionprojet.gestionprojetacademique.mapper.SoutenanceMapper;
import com.gestionprojet.gestionprojetacademique.mapper.UtilisateurMapper;
import com.gestionprojet.gestionprojetacademique.model.*;
import com.gestionprojet.gestionprojetacademique.model.enums.StatutProjet;
import com.gestionprojet.gestionprojetacademique.repository.*;
import com.gestionprojet.gestionprojetacademique.service.ProjetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProjetServiceImpl implements ProjetService {

    private final ProjetRepository projetRepository;
    private final EtudiantRepository etudiantRepository;
    private final EncadrantAcademiqueRepository encadrantAcademiqueRepository;
    private final EncadrantProfessionnelRepository encadrantProfessionnelRepository;
    private final ProjetMapper projetMapper;
    private final UtilisateurMapper utilisateurMapper;
    private final RapportMapper rapportMapper;
    private final SeanceMapper seanceMapper;
    private final SoutenanceMapper soutenanceMapper;

    @Override
    public ProjetResponse create(CreateProjetRequest request) {
        Etudiant etudiant = etudiantRepository.findById(request.etudiantId())
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant", request.etudiantId()));
        EncadrantAcademique encadrant = encadrantAcademiqueRepository.findById(request.encadrantAcademiqueId())
                .orElseThrow(() -> new ResourceNotFoundException("Encadrant académique", request.encadrantAcademiqueId()));

        EncadrantProfessionnel encadrantPro = null;
        if (request.encadrantProfessionnelId() != null) {
            encadrantPro = encadrantProfessionnelRepository.findById(request.encadrantProfessionnelId())
                    .orElseThrow(() -> new ResourceNotFoundException("Encadrant professionnel", request.encadrantProfessionnelId()));
        }

        Projet projet = Projet.builder()
                .titre(request.titre())
                .description(request.description())
                .type(request.type())
                .etudiant(etudiant)
                .encadrantAcademique(encadrant)
                .encadrantProfessionnel(encadrantPro)
                .dateDebut(request.dateDebut())
                .dateFinPrevue(request.dateFinPrevue())
                .lieuStage(request.lieuStage())
                .build();

        Projet saved = projetRepository.save(projet);
        log.info("Projet créé: id={}, titre={}", saved.getId(), saved.getTitre());
        return projetMapper.toResponse(saved);
    }

    @Override
    public ProjetResponse update(Long id, UpdateProjetRequest request) {
        Projet projet = getProjetOrThrow(id);
        if (request.titre() != null) projet.setTitre(request.titre());
        if (request.description() != null) projet.setDescription(request.description());
        if (request.dateDebut() != null) projet.setDateDebut(request.dateDebut());
        if (request.dateFinPrevue() != null) projet.setDateFinPrevue(request.dateFinPrevue());
        if (request.lieuStage() != null) projet.setLieuStage(request.lieuStage());
        log.info("Projet mis à jour: id={}", id);
        return projetMapper.toResponse(projetRepository.save(projet));
    }

    @Override
    @Transactional(readOnly = true)
    public ProjetDetailResponse findById(Long id) {
        Projet p = getProjetOrThrow(id);
        return buildDetailResponse(p);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjetResponse> findAll(Pageable pageable) {
        return projetRepository.findAll(pageable).map(projetMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjetResponse> findByEtudiant(Long etudiantId) {
        return projetRepository.findByEtudiantId(etudiantId).stream()
                .map(projetMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjetResponse> findByEncadrant(Long encadrantId) {
        return projetRepository.findByEncadrantAcademiqueId(encadrantId).stream()
                .map(projetMapper::toResponse).toList();
    }

    @Override
    public ProjetResponse affecterEncadrants(Long id, AffecterEncadrantsRequest request) {
        Projet projet = getProjetOrThrow(id);
        EncadrantAcademique encadrant = encadrantAcademiqueRepository.findById(request.encadrantAcademiqueId())
                .orElseThrow(() -> new ResourceNotFoundException("Encadrant académique", request.encadrantAcademiqueId()));
        projet.setEncadrantAcademique(encadrant);

        if (request.encadrantProfessionnelId() != null) {
            EncadrantProfessionnel ep = encadrantProfessionnelRepository.findById(request.encadrantProfessionnelId())
                    .orElseThrow(() -> new ResourceNotFoundException("Encadrant professionnel", request.encadrantProfessionnelId()));
            projet.setEncadrantProfessionnel(ep);
        }
        return projetMapper.toResponse(projetRepository.save(projet));
    }

    @Override
    public ProjetResponse changerStatut(Long id, StatutProjet statut) {
        Projet projet = getProjetOrThrow(id);
        log.info("Changement statut projet {}: {} -> {}", id, projet.getStatut(), statut);
        projet.setStatut(statut);
        return projetMapper.toResponse(projetRepository.save(projet));
    }

    @Override
    public void archiver(Long id) {
        Projet projet = getProjetOrThrow(id);
        if (projet.getStatut() != StatutProjet.SOUTENU) {
            throw new BusinessRuleViolationException("Seuls les projets soutenus peuvent être archivés");
        }
        projet.setStatut(StatutProjet.ARCHIVE);
        projetRepository.save(projet);
        log.info("Projet archivé: id={}", id);
    }

    private Projet getProjetOrThrow(Long id) {
        return projetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Projet", id));
    }

    private ProjetDetailResponse buildDetailResponse(Projet p) {
        List<RapportVersionResponse> versions = p.getVersionsRapport().stream()
                .map(rapportMapper::toResponse).toList();
        List<SeanceResponse> seances = p.getSeances().stream()
                .map(seanceMapper::toResponse).toList();
        SoutenanceResponse soutenance = p.getSoutenance() != null
                ? soutenanceMapper.toResponse(p.getSoutenance()) : null;

        EncadrantResponse encPro = p.getEncadrantProfessionnel() != null
                ? utilisateurMapper.toEncadrantProfessionnelResponse(p.getEncadrantProfessionnel()) : null;

        return new ProjetDetailResponse(
                p.getId(), p.getTitre(), p.getDescription(), p.getType(), p.getStatut(),
                p.getDateCreation(), p.getDateDebut(), p.getDateFinPrevue(), p.getLieuStage(),
                p.getResultatFinal(),
                utilisateurMapper.toEtudiantResponse(p.getEtudiant()),
                utilisateurMapper.toEncadrantAcademiqueResponse(p.getEncadrantAcademique()),
                encPro, versions, seances, soutenance
        );
    }
}
