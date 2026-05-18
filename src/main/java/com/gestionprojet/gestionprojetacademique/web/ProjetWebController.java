package com.gestionprojet.gestionprojetacademique.web;

import com.gestionprojet.gestionprojetacademique.dto.request.AffecterEncadrantsRequest;
import com.gestionprojet.gestionprojetacademique.dto.request.CreateProjetRequest;
import com.gestionprojet.gestionprojetacademique.dto.request.SubmitRapportRequest;
import com.gestionprojet.gestionprojetacademique.dto.request.UpdateProjetRequest;
import com.gestionprojet.gestionprojetacademique.dto.response.ProjetResponse;
import com.gestionprojet.gestionprojetacademique.mapper.UtilisateurMapper;
import com.gestionprojet.gestionprojetacademique.model.enums.StatutProjet;
import com.gestionprojet.gestionprojetacademique.model.enums.TypeProjet;
import com.gestionprojet.gestionprojetacademique.repository.EncadrantAcademiqueRepository;
import com.gestionprojet.gestionprojetacademique.repository.EncadrantProfessionnelRepository;
import com.gestionprojet.gestionprojetacademique.repository.EtudiantRepository;
import com.gestionprojet.gestionprojetacademique.service.ProjetService;
import com.gestionprojet.gestionprojetacademique.service.RapportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/projets")
@RequiredArgsConstructor
public class ProjetWebController {

    private final ProjetService projetService;
    private final RapportService rapportService;
    private final EtudiantRepository etudiantRepository;
    private final EncadrantAcademiqueRepository encadrantAcademiqueRepository;
    private final EncadrantProfessionnelRepository encadrantProfessionnelRepository;
    private final UtilisateurMapper utilisateurMapper;

    @GetMapping
    public String list(Model model, Pageable pageable,
                       @RequestParam(required = false) StatutProjet statut) {
        if (statut != null) {
            model.addAttribute("projets", projetService.findAll(pageable)
                    .filter(p -> p.statut() == statut));
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
            ra.addFlashAttribute("successMessage", "Projet créé avec succès.");
            return "redirect:/projets/" + p.id();
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/projets/nouveau";
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("projet", projetService.findById(id));
        model.addAttribute("statuts", StatutProjet.values());
        model.addAttribute("encadrantsAcademiques", encadrantAcademiqueRepository.findAll().stream()
                .map(utilisateurMapper::toEncadrantAcademiqueResponse).toList());
        model.addAttribute("encadrantsProfessionnels", encadrantProfessionnelRepository.findAll().stream()
                .map(utilisateurMapper::toEncadrantProfessionnelResponse).toList());
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
            ra.addFlashAttribute("successMessage", "Statut mis à jour.");
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
    @PreAuthorize("hasRole('ETUDIANT')")
    public String soumettreRapport(
            @PathVariable Long id,
            @RequestParam(required = false) String commentaireEtudiant,
            @RequestParam(value = "file", required = false) MultipartFile file,
            RedirectAttributes ra) {
        try {
            rapportService.soumettreVersion(id, new SubmitRapportRequest(commentaireEtudiant), file);
            ra.addFlashAttribute("successMessage", "Rapport soumis avec succès.");
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
