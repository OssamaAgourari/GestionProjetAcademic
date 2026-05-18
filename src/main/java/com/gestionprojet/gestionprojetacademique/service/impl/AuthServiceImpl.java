package com.gestionprojet.gestionprojetacademique.service.impl;

import com.gestionprojet.gestionprojetacademique.dto.request.LoginRequest;
import com.gestionprojet.gestionprojetacademique.dto.request.RegisterEtudiantRequest;
import com.gestionprojet.gestionprojetacademique.dto.response.JwtResponse;
import com.gestionprojet.gestionprojetacademique.exception.DuplicateResourceException;
import com.gestionprojet.gestionprojetacademique.model.Etudiant;
import com.gestionprojet.gestionprojetacademique.model.enums.Role;
import com.gestionprojet.gestionprojetacademique.repository.EtudiantRepository;
import com.gestionprojet.gestionprojetacademique.security.JwtUtil;
import com.gestionprojet.gestionprojetacademique.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final EtudiantRepository etudiantRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public JwtResponse authenticate(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.motDePasse()));
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);
        String role = userDetails.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        log.info("Authentification réussie: {}", request.email());
        return new JwtResponse(token, null, null, null, request.email(), role);
    }

    @Override
    public JwtResponse registerEtudiant(RegisterEtudiantRequest request) {
        if (etudiantRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Un compte existe déjà avec cet email: " + request.email());
        }
        if (etudiantRepository.existsByMatricule(request.matricule())) {
            throw new DuplicateResourceException("Ce matricule est déjà utilisé: " + request.matricule());
        }

        Etudiant etudiant = Etudiant.builder()
                .nom(request.nom())
                .prenom(request.prenom())
                .email(request.email())
                .telephone(request.telephone())
                .motDePasse(passwordEncoder.encode(request.motDePasse()))
                .matricule(request.matricule())
                .filiere(request.filiere())
                .niveau(request.niveau())
                .role(Role.ETUDIANT)
                .actif(true)
                .build();

        Etudiant saved = etudiantRepository.save(etudiant);
        log.info("Étudiant enregistré: {}", saved.getEmail());

        LoginRequest loginRequest = new LoginRequest(request.email(), request.motDePasse());
        return authenticate(loginRequest);
    }
}
