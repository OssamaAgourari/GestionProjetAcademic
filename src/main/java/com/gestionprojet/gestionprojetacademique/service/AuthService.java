package com.gestionprojet.gestionprojetacademique.service;

import com.gestionprojet.gestionprojetacademique.dto.request.LoginRequest;
import com.gestionprojet.gestionprojetacademique.dto.request.RegisterEtudiantRequest;
import com.gestionprojet.gestionprojetacademique.dto.response.JwtResponse;

public interface AuthService {
    JwtResponse authenticate(LoginRequest request);
    JwtResponse registerEtudiant(RegisterEtudiantRequest request);
}
