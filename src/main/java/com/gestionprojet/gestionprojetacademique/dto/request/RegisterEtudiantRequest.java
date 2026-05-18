package com.gestionprojet.gestionprojetacademique.dto.request;

import jakarta.validation.constraints.*;

public record RegisterEtudiantRequest(
        @NotBlank @Size(max = 100) String nom,
        @NotBlank @Size(max = 100) String prenom,
        @NotBlank @Email @Size(max = 150) String email,
        @Size(max = 20) String telephone,
        @NotBlank @Size(min = 8) String motDePasse,
        @NotBlank @Size(max = 20) String matricule,
        @Size(max = 100) String filiere,
        @Size(max = 10) String niveau
) {}
