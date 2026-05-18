package com.gestionprojet.gestionprojetacademique.repository;

import com.gestionprojet.gestionprojetacademique.model.RapportVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RapportVersionRepository extends JpaRepository<RapportVersion, Long> {
    List<RapportVersion> findByProjetIdOrderByNumeroVersionDesc(Long projetId);
    Optional<RapportVersion> findTopByProjetIdOrderByNumeroVersionDesc(Long projetId);

    @Query("SELECT COALESCE(MAX(r.numeroVersion), 0) FROM RapportVersion r WHERE r.projet.id = :projetId")
    int findMaxNumeroVersionByProjetId(Long projetId);
}
