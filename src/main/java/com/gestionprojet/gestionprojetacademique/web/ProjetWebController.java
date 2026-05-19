package com.gestionprojet.gestionprojetacademique.web;

import com.gestionprojet.gestionprojetacademique.dto.request.AffecterEncadrantsRequest;
import com.gestionprojet.gestionprojetacademique.dto.request.CreateSeanceRequest;
import com.gestionprojet.gestionprojetacademique.dto.request.CreateSoutenanceRequest;
import com.gestionprojet.gestionprojetacademique.dto.request.FeedbackRequest;
import com.gestionprojet.gestionprojetacademique.dto.request.CreateProjetRequest;
import com.gestionprojet.gestionprojetacademique.dto.request.SubmitRapportRequest;
import com.gestionprojet.gestionprojetacademique.dto.request.UpdateProjetRequest;
import com.gestionprojet.gestionprojetacademique.dto.response.ProjetDetailResponse;
import com.gestionprojet.gestionprojetacademique.dto.response.ProjetResponse;
import com.gestionprojet.gestionprojetacademique.exception.ResourceNotFoundException;
import com.gestionprojet.gestionprojetacademique.mapper.UtilisateurMapper;
import com.gestionprojet.gestionprojetacademique.model.Etudiant;
import com.gestionprojet.gestionprojetacademique.model.enums.StatutProjet;
import com.gestionprojet.gestionprojetacademique.model.enums.TypeProjet;
import com.gestionprojet.gestionprojetacademique.repository.EncadrantAcademiqueRepository;
import com.gestionprojet.gestionprojetacademique.repository.EncadrantProfessionnelRepository;
import com.gestionprojet.gestionprojetacademique.repository.EtudiantRepository;
import com.gestionprojet.gestionprojetacademique.model.enums.ModeSeance;
import com.gestionprojet.gestionprojetacademique.repository.EncadrantAcademiqueRepository;
import com.gestionprojet.gestionprojetacademique.repository.MembreJuryRepository;
import com.gestionprojet.gestionprojetacademique.service.FeedbackService;
import com.gestionprojet.gestionprojetacademique.service.ProjetService;
import com.gestionprojet.gestionprojetacademique.service.RapportService;
import com.gestionprojet.gestionprojetacademique.service.SeanceService;
import com.gestionprojet.gestionprojetacademique.service.SoutenanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/projets")
@RequiredArgsConstructor
public class ProjetWebController {

    private final ProjetService projetService;
    private final RapportService rapportService;
    private final SeanceService seanceService;
    private final FeedbackService feedbackService;
    private final SoutenanceService soutenanceService;
    private final EtudiantRepository etudiantRepository;
    private final EncadrantAcademiqueRepository encadrantAcademiqueRepository;
    private final EncadrantProfessionnelRepository encadrantProfessionnelRepository;
    private final MembreJuryRepository membreJuryRepository;
    private final UtilisateurMapper utilisateurMapper;

    @GetMapping
    public String list(Model model, Pageable pageable,
                       @RequestParam(required = false) StatutProjet statut,
                       Authentication auth) {
        boolean isEtudiant = auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ETUDIANT"));
        boolean isEncadrantPro = auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ENCADRANT_PROFESSIONNEL"));

        if (isEtudiant) {
            Etudiant etudiant = etudiantRepository.findByEmail(auth.getName())
                    .orElseThrow(() -> new ResourceNotFoundException("Étudiant introuvable: " + auth.getName()));
            List<ProjetResponse> mes = projetService.findByEtudiant(etudiant.getId());
            List<ProjetResponse> filtered = (statut != null)
                    ? mes.stream().filter(p -> p.statut() == statut).toList()
                    : mes;
            model.addAttribute("projets", new PageImpl<>(filtered));
            model.addAttribute("myProjectOnly", true);
        } else if (isEncadrantPro) {
            encadrantProfessionnelRepository.findByEmail(auth.getName()).ifPresent(e -> {
                List<ProjetResponse> mes = projetService.findByEncadrantProfessionnel(e.getId());
                List<ProjetResponse> filtered = (statut != null)
                        ? mes.stream().filter(p -> p.statut() == statut).toList()
                        : mes;
                model.addAttribute("projets", new PageImpl<>(filtered));
                model.addAttribute("myProjectOnly", true);
            });
        } else if (statut != null) {
            model.addAttribute("projets", projetService.findByStatut(statut, pageable));
        } else {
            model.addAttribute("projets", projetService.findAll(pageable));
        }

        model.addAttribute("statuts", StatutProjet.values());
        model.addAttribute("statutFiltre", statut);
        return "projet/list";
    }

    @GetMapping("/nouveau")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public String nouveauForm(Model model) {
        populateFormModel(model);
        model.addAttribute("editMode", false);
        return "projet/form";
    }

    @PostMapping("/nouveau")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public String creer(
            @RequestParam String titre,
            @RequestParam(required = false) String description,
            @RequestParam TypeProjet type,
            @RequestParam Long etudiantId,
            @RequestParam Long encadrantAcademiqueId,
            @RequestParam(required = false) Long encadrantProfessionnelId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFinPrevue,
            @RequestParam(required = false) String lieuStage,
            RedirectAttributes ra) {
        try {
            ProjetResponse p = projetService.create(new CreateProjetRequest(
                    titre, description, type, etudiantId, encadrantAcademiqueId,
                    encadrantProfessionnelId, dateDebut, dateFinPrevue, lieuStage));
            ra.addFlashAttribute("successMessage", "Projet « " + p.titre() + " » créé avec succès.");
            return "redirect:/projets/" + p.id();
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/projets/nouveau";
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, Authentication auth) {
        ProjetDetailResponse projet = projetService.findById(id);

        boolean isEtudiant = auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ETUDIANT"));
        boolean isEncadrantProDetail = auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ENCADRANT_PROFESSIONNEL"));

        if (isEtudiant) {
            Etudiant etudiant = etudiantRepository.findByEmail(auth.getName()).orElse(null);
            if (etudiant == null || !etudiant.getId().equals(projet.etudiant().id())) {
                return "redirect:/projets?error=acces_refuse";
            }
        } else if (isEncadrantProDetail) {
            var encPro = encadrantProfessionnelRepository.findByEmail(auth.getName()).orElse(null);
            if (encPro == null || projet.encadrantProfessionnel() == null
                    || !encPro.getId().equals(projet.encadrantProfessionnel().id())) {
                return "redirect:/projets?error=acces_refuse";
            }
        }

        model.addAttribute("projet", projet);
        model.addAttribute("statuts", StatutProjet.values());
        model.addAttribute("modes", ModeSeance.values());
        model.addAttribute("encadrantsAcademiques", encadrantAcademiqueRepository.findAll().stream()
                .map(utilisateurMapper::toEncadrantAcademiqueResponse).toList());
        model.addAttribute("encadrantsProfessionnels", encadrantProfessionnelRepository.findAll().stream()
                .map(utilisateurMapper::toEncadrantProfessionnelResponse).toList());
        model.addAttribute("membresJury", membreJuryRepository.findAll().stream()
                .map(utilisateurMapper::toMembreJuryResponse).toList());
        return "projet/detail";
    }

    @GetMapping("/{id}/modifier")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'ENCADRANT_ACADEMIQUE')")
    public String modifierForm(@PathVariable Long id, Model model) {
        model.addAttribute("projet", projetService.findById(id));
        model.addAttribute("editMode", true);
        return "projet/form";
    }

    @PostMapping("/{id}/modifier")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'ENCADRANT_ACADEMIQUE')")
    public String modifier(
            @PathVariable Long id,
            @RequestParam String titre,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFinPrevue,
            @RequestParam(required = false) String lieuStage,
            RedirectAttributes ra) {
        try {
            projetService.update(id, new UpdateProjetRequest(titre, description, dateDebut, dateFinPrevue, lieuStage));
            ra.addFlashAttribute("successMessage", "Projet mis à jour avec succès.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/projets/" + id;
    }

    @PostMapping("/{id}/statut")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public String changerStatut(
            @PathVariable Long id,
            @RequestParam StatutProjet statut,
            RedirectAttributes ra) {
        try {
            projetService.changerStatut(id, statut);
            ra.addFlashAttribute("successMessage", "Statut mis à jour vers " + statut + ".");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/projets/" + id;
    }

    @PostMapping("/{id}/encadrants")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public String affecterEncadrants(
            @PathVariable Long id,
            @RequestParam Long encadrantAcademiqueId,
            @RequestParam(required = false) Long encadrantProfessionnelId,
            RedirectAttributes ra) {
        try {
            projetService.affecterEncadrants(id, new AffecterEncadrantsRequest(encadrantAcademiqueId, encadrantProfessionnelId));
            ra.addFlashAttribute("successMessage", "Encadrants affectés avec succès.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/projets/" + id;
    }

    @PostMapping("/{id}/rapports")
    public String soumettreRapport(
            @PathVariable Long id,
            @RequestParam(required = false) String commentaireEtudiant,
            @RequestParam(value = "file", required = false) MultipartFile file,
            Authentication auth,
            RedirectAttributes ra) {

        boolean isEtudiant = auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ETUDIANT"));
        if (!isEtudiant) {
            ra.addFlashAttribute("errorMessage", "Seuls les étudiants peuvent soumettre un rapport.");
            return "redirect:/projets/" + id;
        }

        if (file == null || file.isEmpty()) {
            ra.addFlashAttribute("errorMessage", "Veuillez sélectionner un fichier PDF.");
            return "redirect:/projets/" + id;
        }

        try {
            rapportService.soumettreVersion(id, new SubmitRapportRequest(commentaireEtudiant), file);
            ra.addFlashAttribute("successMessage", "Rapport soumis avec succès.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/projets/" + id;
    }

    @PostMapping("/{id}/seances")
    public String ajouterSeance(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateSeance,
            @RequestParam int dureeMinutes,
            @RequestParam ModeSeance mode,
            @RequestParam(required = false) String lieuOuLien,
            @RequestParam(required = false) String notes,
            Authentication auth,
            RedirectAttributes ra) {
        try {
            boolean isAdmin = auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMINISTRATEUR"));
            Long encadrantId;
            if (isAdmin) {
                encadrantId = projetService.findById(id).encadrantAcademique().id();
            } else {
                encadrantId = encadrantAcademiqueRepository.findByEmail(auth.getName())
                        .orElseThrow(() -> new ResourceNotFoundException("Encadrant introuvable: " + auth.getName())).getId();
            }
            seanceService.planifier(id, encadrantId,
                    new CreateSeanceRequest(dateSeance, dureeMinutes, mode, lieuOuLien, notes, null));
            ra.addFlashAttribute("successMessage", "Séance enregistrée avec succès.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/projets/" + id;
    }

    @PostMapping("/rapports/{versionId}/feedbacks")
    public String ajouterFeedback(
            @PathVariable Long versionId,
            @RequestParam Long projetId,
            @RequestParam String contenu,
            @RequestParam(required = false) Double noteIntermediaire,
            Authentication auth,
            RedirectAttributes ra) {
        try {
            Long encadrantId = encadrantAcademiqueRepository.findByEmail(auth.getName())
                    .orElseThrow(() -> new ResourceNotFoundException("Encadrant introuvable: " + auth.getName())).getId();
            feedbackService.ajouterFeedback(versionId, encadrantId, new FeedbackRequest(contenu, noteIntermediaire));
            ra.addFlashAttribute("successMessage", "Feedback enregistré.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/projets/" + projetId;
    }

    @PostMapping("/{id}/planifier-soutenance")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public String planifierSoutenance(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateSoutenance,
            @RequestParam String lieu,
            @RequestParam(required = false) List<Long> membreJuryIds,
            RedirectAttributes ra) {
        try {
            soutenanceService.planifier(id, new CreateSoutenanceRequest(dateSoutenance, lieu, membreJuryIds));
            ra.addFlashAttribute("successMessage", "Soutenance planifiée avec succès.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/projets/" + id;
    }

    private void populateFormModel(Model model) {
        model.addAttribute("etudiants", etudiantRepository.findAll().stream()
                .map(utilisateurMapper::toEtudiantResponse).toList());
        model.addAttribute("encadrantsAcademiques", encadrantAcademiqueRepository.findAll().stream()
                .map(utilisateurMapper::toEncadrantAcademiqueResponse).toList());
        model.addAttribute("encadrantsProfessionnels", encadrantProfessionnelRepository.findAll().stream()
                .map(utilisateurMapper::toEncadrantProfessionnelResponse).toList());
        model.addAttribute("types", TypeProjet.values());
    }
}
