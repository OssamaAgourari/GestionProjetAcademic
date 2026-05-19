package com.gestionprojet.gestionprojetacademique.web;

import com.gestionprojet.gestionprojetacademique.dto.request.EvaluationJuryRequest;
import com.gestionprojet.gestionprojetacademique.dto.response.EvaluationJuryResponse;
import com.gestionprojet.gestionprojetacademique.dto.response.ProjetResponse;
import com.gestionprojet.gestionprojetacademique.dto.response.SoutenanceResponse;
import com.gestionprojet.gestionprojetacademique.exception.UnauthorizedActionException;
import com.gestionprojet.gestionprojetacademique.model.MembreJury;
import com.gestionprojet.gestionprojetacademique.repository.EvaluationJuryRepository;
import com.gestionprojet.gestionprojetacademique.repository.MembreJuryRepository;
import com.gestionprojet.gestionprojetacademique.service.EvaluationJuryService;
import com.gestionprojet.gestionprojetacademique.service.ProjetService;
import com.gestionprojet.gestionprojetacademique.service.SoutenanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/soutenances")
@RequiredArgsConstructor
public class SoutenanceWebController {

    private final SoutenanceService soutenanceService;
    private final EvaluationJuryService evaluationJuryService;
    private final ProjetService projetService;
    private final MembreJuryRepository membreJuryRepository;
    private final EvaluationJuryRepository evaluationJuryRepository;

    @GetMapping
    public String list(Model model, Authentication auth) {
        boolean isJury = auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MEMBRE_JURY"));

        List<SoutenanceResponse> soutenances;
        if (isJury) {
            soutenances = membreJuryRepository.findByEmail(auth.getName())
                    .map(j -> soutenanceService.findByMembreJury(j.getId()))
                    .orElse(List.of());
        } else {
            soutenances = soutenanceService.findAll();
        }

        Map<Long, ProjetResponse> projetsMap = projetService.findAll(Pageable.unpaged()).getContent()
                .stream().collect(Collectors.toMap(ProjetResponse::id, p -> p));
        model.addAttribute("soutenances", soutenances);
        model.addAttribute("projetsMap", projetsMap);
        return "soutenance/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, Authentication auth) {
        SoutenanceResponse soutenance = soutenanceService.findById(id);
        ProjetResponse projet = projetService.findAll(Pageable.unpaged()).getContent().stream()
                .filter(p -> p.id().equals(soutenance.projetId())).findFirst().orElse(null);

        boolean isJury = auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MEMBRE_JURY"));
        Long juryIdConnecte = null;
        if (isJury) {
            MembreJury jury = membreJuryRepository.findByEmail(auth.getName()).orElse(null);
            if (jury != null) {
                juryIdConnecte = jury.getId();
                // only show the eval form if this jury member was assigned to this soutenance
                isJury = evaluationJuryRepository.existsBySoutenanceIdAndMembreJuryId(id, jury.getId());
            }
        }

        List<EvaluationJuryResponse> evaluations = evaluationJuryService.listerParSoutenance(id);
        double moyenneJury = evaluations.stream()
                .mapToDouble(EvaluationJuryResponse::note)
                .average()
                .orElse(0.0);

        model.addAttribute("soutenance", soutenance);
        model.addAttribute("projet", projet);
        model.addAttribute("evaluations", evaluations);
        model.addAttribute("moyenneJury", moyenneJury);
        model.addAttribute("juryIdConnecte", juryIdConnecte);
        model.addAttribute("isJury", isJury);
        return "soutenance/detail";
    }

    @PostMapping("/{id}/evaluer")
    public String evaluer(
            @PathVariable Long id,
            @RequestParam Double note,
            @RequestParam(required = false) String commentaire,
            Authentication auth,
            RedirectAttributes ra) {
        try {
            Long juryId = membreJuryRepository.findByEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Jury introuvable")).getId();
            if (!evaluationJuryRepository.existsBySoutenanceIdAndMembreJuryId(id, juryId)) {
                ra.addFlashAttribute("errorMessage", "Vous n'êtes pas assigné à cette soutenance.");
                return "redirect:/soutenances/" + id;
            }
            evaluationJuryService.enregistrerEvaluation(id, new EvaluationJuryRequest(juryId, note, commentaire));
            ra.addFlashAttribute("successMessage", "Évaluation enregistrée avec succès.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/soutenances/" + id;
    }

    @PostMapping("/{id}/cloturer")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public String cloturer(@PathVariable Long id, RedirectAttributes ra) {
        try {
            SoutenanceResponse s = soutenanceService.cloturer(id);
            ra.addFlashAttribute("successMessage",
                    "Soutenance clôturée. Note finale : " + s.projetId());
            return "redirect:/projets/" + s.projetId();
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/soutenances/" + id;
        }
    }
}
