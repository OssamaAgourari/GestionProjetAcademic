package com.gestionprojet.gestionprojetacademique.repository;

import com.gestionprojet.gestionprojetacademique.model.Projet;
import com.gestionprojet.gestionprojetacademique.model.enums.StatutProjet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProjetRepository extends JpaRepository<Projet, Long> {
    List<Projet> findByEtudiantId(Long etudiantId);
    List<Projet> findByEncadrantAcademiqueId(Long encadrantId);
    List<Projet> findByStatut(StatutProjet statut);
    Page<Projet> findByStatut(StatutProjet statut, Pageable pageable);
    Page<Projet> findAll(Pageable pageable);

    @Query("SELECT p FROM Projet p WHERE p.statut = 'EN_COURS' AND p.dateFinPrevue < CURRENT_DATE")
    List<Projet> findEnRetard();
}
