package com.gestionprojet.gestionprojetacademique.web;

import com.gestionprojet.gestionprojetacademique.dto.request.EvaluationJuryRequest;
import com.gestionprojet.gestionprojetacademique.dto.response.ProjetResponse;
import com.gestionprojet.gestionprojetacademique.dto.response.SoutenanceResponse;
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

    @GetMapping
    public String list(Model model) {
        List<SoutenanceResponse> soutenances = soutenanceService.findAll();
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
            juryIdConnecte = membreJuryRepository.findByEmail(auth.getName())
                    .map(m -> m.getId()).orElse(null);
        }

        model.addAttribute("soutenance", soutenance);
        model.addAttribute("projet", projet);
        model.addAttribute("evaluations", evaluationJuryService.listerParSoutenance(id));
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
