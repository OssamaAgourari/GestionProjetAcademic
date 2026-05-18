package com.gestionprojet.gestionprojetacademique.repository;

import com.gestionprojet.gestionprojetacademique.model.SeanceEncadrement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeanceEncadrementRepository extends JpaRepository<SeanceEncadrement, Long> {
    List<SeanceEncadrement> findByProjetIdOrderByDateSeanceDesc(Long projetId);
    List<SeanceEncadrement> findByEncadrantId(Long encadrantId);
}
