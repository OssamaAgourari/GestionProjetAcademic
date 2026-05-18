package com.gestionprojet.gestionprojetacademique.controller;

import com.gestionprojet.gestionprojetacademique.dto.request.LoginRequest;
import com.gestionprojet.gestionprojetacademique.dto.request.RegisterEtudiantRequest;
import com.gestionprojet.gestionprojetacademique.dto.response.JwtResponse;
import com.gestionprojet.gestionprojetacademique.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Connexion — retourne un JWT")
    public JwtResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.authenticate(request);
    }

    @PostMapping("/register/etudiant")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Inscription étudiant")
    public JwtResponse registerEtudiant(@Valid @RequestBody RegisterEtudiantRequest request) {
        return authService.registerEtudiant(request);
    }
}
