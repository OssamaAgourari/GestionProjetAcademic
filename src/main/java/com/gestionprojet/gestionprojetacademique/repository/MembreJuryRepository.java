package com.gestionprojet.gestionprojetacademique.repository;

import com.gestionprojet.gestionprojetacademique.model.MembreJury;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MembreJuryRepository extends JpaRepository<MembreJury, Long> {
    Optional<MembreJury> findByEmail(String email);
    boolean existsByEmail(String email);
}
