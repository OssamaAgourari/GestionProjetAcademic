package com.gestionprojet.gestionprojetacademique.repository;

import com.gestionprojet.gestionprojetacademique.model.EncadrantAcademique;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EncadrantAcademiqueRepository extends JpaRepository<EncadrantAcademique, Long> {
    Optional<EncadrantAcademique> findByEmail(String email);
    boolean existsByEmail(String email);
}
