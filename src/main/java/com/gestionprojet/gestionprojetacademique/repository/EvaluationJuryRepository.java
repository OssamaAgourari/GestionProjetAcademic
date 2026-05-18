package com.gestionprojet.gestionprojetacademique.repository;

import com.gestionprojet.gestionprojetacademique.model.EvaluationJury;
import com.gestionprojet.gestionprojetacademique.model.EvaluationJuryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EvaluationJuryRepository extends JpaRepository<EvaluationJury, EvaluationJuryId> {
    List<EvaluationJury> findBySoutenanceId(Long soutenanceId);

    @Query("SELECT AVG(e.note) FROM EvaluationJury e WHERE e.soutenance.id = :soutenanceId")
    Optional<Double> findAverageNoteBySoutenanceId(Long soutenanceId);
}
