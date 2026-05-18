package com.gestionprojet.gestionprojetacademique.repository;

import com.gestionprojet.gestionprojetacademique.model.Administrateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdministrateurRepository extends JpaRepository<Administrateur, Long> {
    Optional<Administrateur> findByEmail(String email);
    boolean existsByEmail(String email);
}
