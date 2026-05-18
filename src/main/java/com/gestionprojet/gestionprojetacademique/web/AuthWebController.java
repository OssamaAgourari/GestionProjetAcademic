package com.gestionprojet.gestionprojetacademique.web;

import com.gestionprojet.gestionprojetacademique.dto.request.RegisterEtudiantRequest;
import com.gestionprojet.gestionprojetacademique.exception.DuplicateResourceException;
import com.gestionprojet.gestionprojetacademique.service.AuthService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthWebController {

    private final AuthService authService;

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String nom,
            @RequestParam String prenom,
            @RequestParam String email,
            @RequestParam(required = false) String telephone,
            @RequestParam String matricule,
            @RequestParam(required = false) String filiere,
            @RequestParam(required = false) String niveau,
            @RequestParam String motDePasse,
            Model model) {
        try {
            RegisterEtudiantRequest request = new RegisterEtudiantRequest(
                    nom, prenom, email, telephone, motDePasse, matricule, filiere, niveau);
            authService.registerEtudiant(request);
            return "redirect:/login?registered";
        } catch (DuplicateResourceException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "auth/register";
        } catch (Exception e) {
            log.error("Erreur lors de l'inscription", e);
            model.addAttribute("errorMessage", "Une erreur est survenue. Veuillez réessayer.");
            return "auth/register";
        }
    }
}
