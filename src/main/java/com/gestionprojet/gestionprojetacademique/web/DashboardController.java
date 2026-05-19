package com.gestionprojet.gestionprojetacademique.web;

import com.gestionprojet.gestionprojetacademique.model.enums.StatutProjet;
import com.gestionprojet.gestionprojetacademique.repository.*;

import java.time.LocalDate;
import com.gestionprojet.gestionprojetacademique.service.ProjetService;
import com.gestionprojet.gestionprojetacademique.service.SoutenanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final ProjetRepository projetRepository;
    private final EtudiantRepository etudiantRepository;
    private final EncadrantAcademiqueRepository encadrantAcadRepo;
    private final EncadrantProfessionnelRepository encadrantProRepo;
    private final MembreJuryRepository membreJuryRepo;
    private final RapportVersionRepository rapportRepository;
    private final SoutenanceRepository soutenanceRepository;
    private final ProjetService projetService;
    private final SoutenanceService soutenanceService;

    @GetMapping("/")
    public String root() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        if (auth == null) return "redirect:/login";

        boolean isAdmin = auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMINISTRATEUR"));
        boolean isEtudiant = auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ETUDIANT"));
        boolean isEncadrant = auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ENCADRANT_ACADEMIQUE"));
        boolean isEncadrantPro = auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ENCADRANT_PROFESSIONNEL"));
        boolean isJury = auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MEMBRE_JURY"));

        if (isAdmin) {
            long total = projetRepository.count();
            long propose = projetRepository.findByStatut(StatutProjet.PROPOSE).size();
            long enCours = projetRepository.findByStatut(StatutProjet.EN_COURS).size();
            long soutenu = projetRepository.findByStatut(StatutProjet.SOUTENU).size();
            long archive = projetRepository.findByStatut(StatutProjet.ARCHIVE).size();
            long enRetard = projetRepository.findEnRetard().size();

            model.addAttribute("totalProjets", total);
            model.addAttribute("projetsPropose", propose);
            model.addAttribute("projetsEnCours", enCours);
            model.addAttribute("projetsSoutenus", soutenu);
            model.addAttribute("projetsArchive", archive);
            model.addAttribute("projetsEnRetard", enRetard);

            model.addAttribute("totalEtudiants", etudiantRepository.count());
            model.addAttribute("totalEncadrants", encadrantAcadRepo.count());
            model.addAttribute("totalEncadrantsPro", encadrantProRepo.count());
            model.addAttribute("totalJury", membreJuryRepo.count());
            model.addAttribute("totalRapports", rapportRepository.count());
            model.addAttribute("totalSoutenances", soutenanceRepository.count());

            model.addAttribute("pctEnCours",   total > 0 ? (enCours  * 100 / total) : 0);
            model.addAttribute("pctPropose",   total > 0 ? (propose  * 100 / total) : 0);
            model.addAttribute("pctSoutenu",   total > 0 ? (soutenu  * 100 / total) : 0);
            model.addAttribute("pctArchive",   total > 0 ? (archive  * 100 / total) : 0);

            model.addAttribute("projetsRecents", projetService.findAll(
                    PageRequest.of(0, 8, Sort.by(Sort.Direction.DESC, "dateCreation"))).getContent());
            model.addAttribute("projetsRetard", projetService.findAll(
                    PageRequest.of(0, 50)).getContent().stream()
                    .filter(p -> p.statut() == StatutProjet.EN_COURS
                            && p.dateFinPrevue() != null
                            && p.dateFinPrevue().isBefore(LocalDate.now()))
                    .toList());

            return "admin/dashboard";
        }

        if (isEtudiant) {
            etudiantRepository.findByEmail(auth.getName()).ifPresent(e -> {
                var mes = projetService.findByEtudiant(e.getId());
                model.addAttribute("mesProjets", mes);
                model.addAttribute("totalMesProjets", mes.size());
                model.addAttribute("enCours", mes.stream().filter(p -> p.statut() == StatutProjet.EN_COURS).count());
                model.addAttribute("proposes", mes.stream().filter(p -> p.statut() == StatutProjet.PROPOSE).count());
                model.addAttribute("soutenus", mes.stream().filter(p -> p.statut() == StatutProjet.SOUTENU).count());
            });
            return "dashboard";
        }

        if (isEncadrant) {
            encadrantAcadRepo.findByEmail(auth.getName()).ifPresent(e -> {
                var mes = projetService.findByEncadrant(e.getId());
                model.addAttribute("mesProjets", mes);
                model.addAttribute("totalMesProjets", mes.size());
            });
            return "dashboard";
        }

        if (isEncadrantPro) {
            encadrantProRepo.findByEmail(auth.getName()).ifPresent(e -> {
                var mes = projetService.findByEncadrantProfessionnel(e.getId());
                model.addAttribute("mesProjets", mes);
                model.addAttribute("totalMesProjets", mes.size());
            });
            return "dashboard";
        }

        if (isJury) {
            membreJuryRepo.findByEmail(auth.getName()).ifPresent(jury -> {
                var mes = soutenanceService.findByMembreJury(jury.getId());
                model.addAttribute("mesSoutenances", mes);
                model.addAttribute("totalMesSoutenances", mes.size());
            });
            return "dashboard";
        }

        return "dashboard";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }
}
