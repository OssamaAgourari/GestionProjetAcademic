package com.gestionprojet.gestionprojetacademique.controller;

import com.gestionprojet.gestionprojetacademique.dto.response.EtudiantResponse;
import com.gestionprojet.gestionprojetacademique.dto.response.EncadrantResponse;
import com.gestionprojet.gestionprojetacademique.dto.response.MembreJuryResponse;
import com.gestionprojet.gestionprojetacademique.mapper.UtilisateurMapper;
import com.gestionprojet.gestionprojetacademique.model.enums.Role;
import com.gestionprojet.gestionprojetacademique.repository.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

@RestController
@RequestMapping("/api/v1/utilisateurs")
@RequiredArgsConstructor
@Tag(name = "Utilisateurs")
@SecurityRequirement(name = "bearerAuth")
public class UtilisateurController {

    private final EtudiantRepository etudiantRepository;
    private final EncadrantAcademiqueRepository encadrantAcademiqueRepository;
    private final EncadrantProfessionnelRepository encadrantProfessionnelRepository;
    private final MembreJuryRepository membreJuryRepository;
    private final UtilisateurMapper utilisateurMapper;

    @GetMapping("/me")
    @Operation(summary = "Mon profil")
    public Map<String, Object> me(Principal principal) {
        return Map.of("email", principal.getName());
    }

    @GetMapping("/etudiants")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    @Operation(summary = "Lister les étudiants (Admin)")
    public List<EtudiantResponse> listEtudiants() {
        return etudiantRepository.findAll().stream()
                .map(utilisateurMapper::toEtudiantResponse).toList();
    }

    @GetMapping("/encadrants-academiques")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    @Operation(summary = "Lister les encadrants académiques (Admin)")
    public List<EncadrantResponse> listEncadrantsAcademiques() {
        return encadrantAcademiqueRepository.findAll().stream()
                .map(utilisateurMapper::toEncadrantAcademiqueResponse).toList();
    }

    @GetMapping("/encadrants-professionnels")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    @Operation(summary = "Lister les encadrants professionnels (Admin)")
    public List<EncadrantResponse> listEncadrantsProfessionnels() {
        return encadrantProfessionnelRepository.findAll().stream()
                .map(utilisateurMapper::toEncadrantProfessionnelResponse).toList();
    }

    @GetMapping("/membres-jury")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    @Operation(summary = "Lister les membres du jury (Admin)")
    public List<MembreJuryResponse> listMembresJury() {
        return membreJuryRepository.findAll().stream()
                .map(utilisateurMapper::toMembreJuryResponse).toList();
    }
}
