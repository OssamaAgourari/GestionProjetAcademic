package com.gestionprojet.gestionprojetacademique.repository;

import com.gestionprojet.gestionprojetacademique.model.EncadrantProfessionnel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EncadrantProfessionnelRepository extends JpaRepository<EncadrantProfessionnel, Long> {
    Optional<EncadrantProfessionnel> findByEmail(String email);
    boolean existsByEmail(String email);
}
