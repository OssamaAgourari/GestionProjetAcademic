package com.gestionprojet.gestionprojetacademique.security;

import com.gestionprojet.gestionprojetacademique.model.Utilisateur;
import com.gestionprojet.gestionprojetacademique.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final EtudiantRepository etudiantRepository;
    private final EncadrantAcademiqueRepository encadrantAcademiqueRepository;
    private final EncadrantProfessionnelRepository encadrantProfessionnelRepository;
    private final MembreJuryRepository membreJuryRepository;
    private final AdministrateurRepository administrateurRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Utilisateur utilisateur = findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable: " + email));

        log.debug("Utilisateur chargé: {} ({})", email, utilisateur.getRole());
        return User.builder()
                .username(utilisateur.getEmail())
                .password(utilisateur.getMotDePasse())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + utilisateur.getRole().name())))
                .accountExpired(false)
                .accountLocked(!utilisateur.isActif())
                .credentialsExpired(false)
                .disabled(!utilisateur.isActif())
                .build();
    }

    private Optional<? extends Utilisateur> findByEmail(String email) {
        return etudiantRepository.findByEmail(email)
                .<Optional<? extends Utilisateur>>map(Optional::of)
                .orElseGet(() -> encadrantAcademiqueRepository.findByEmail(email)
                        .<Optional<? extends Utilisateur>>map(Optional::of)
                        .orElseGet(() -> encadrantProfessionnelRepository.findByEmail(email)
                                .<Optional<? extends Utilisateur>>map(Optional::of)
                                .orElseGet(() -> membreJuryRepository.findByEmail(email)
                                        .<Optional<? extends Utilisateur>>map(Optional::of)
                                        .orElseGet(() -> administrateurRepository.findByEmail(email)
                                                .map(a -> a)))));
    }
}
