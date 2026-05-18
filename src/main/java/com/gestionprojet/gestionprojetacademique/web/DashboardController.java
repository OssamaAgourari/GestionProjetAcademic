package com.gestionprojet.gestionprojetacademique.web;

import com.gestionprojet.gestionprojetacademique.repository.*;
import com.gestionprojet.gestionprojetacademique.model.enums.StatutProjet;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final ProjetRepository projetRepository;

    @GetMapping("/")
    public String root() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        if (auth == null) return "redirect:/login";

        boolean isAdmin = auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMINISTRATEUR"));
        if (isAdmin) {
            model.addAttribute("totalProjets", projetRepository.count());
            model.addAttribute("projetsEnCours", projetRepository.findByStatut(StatutProjet.EN_COURS).size());
            model.addAttribute("projetsSoutenus", projetRepository.findByStatut(StatutProjet.SOUTENU).size());
            model.addAttribute("projetsPropose", projetRepository.findByStatut(StatutProjet.PROPOSE).size());
            model.addAttribute("projets", projetRepository.findAll(Pageable.ofSize(10)).getContent());
            return "admin/dashboard";
        }
        return "dashboard";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }
}
