package com.gestionprojet.gestionprojetacademique.repository;

import com.gestionprojet.gestionprojetacademique.model.Soutenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SoutenanceRepository extends JpaRepository<Soutenance, Long> {
    Optional<Soutenance> findByProjetId(Long projetId);
    boolean existsByProjetId(Long projetId);

    @Query("SELECT DISTINCT e.soutenance FROM EvaluationJury e WHERE e.membreJury.id = :juryId")
    List<Soutenance> findByMembreJuryId(@Param("juryId") Long juryId);
}
