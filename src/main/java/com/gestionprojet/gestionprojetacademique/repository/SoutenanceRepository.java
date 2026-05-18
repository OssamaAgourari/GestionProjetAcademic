package com.gestionprojet.gestionprojetacademique.repository;

import com.gestionprojet.gestionprojetacademique.model.Soutenance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SoutenanceRepository extends JpaRepository<Soutenance, Long> {
    Optional<Soutenance> findByProjetId(Long projetId);
    boolean existsByProjetId(Long projetId);
}
