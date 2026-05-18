package com.gestionprojet.gestionprojetacademique.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "evaluations_jury")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationJury {

    @EmbeddedId
    private EvaluationJuryId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("soutenanceId")
    @JoinColumn(name = "soutenance_id")
    @JsonIgnore
    private Soutenance soutenance;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("membreJuryId")
    @JoinColumn(name = "membre_jury_id")
    @JsonIgnore
    private MembreJury membreJury;

    @Column(nullable = false)
    private Double note;

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    @Column(nullable = false)
    private LocalDateTime dateEvaluation;

    @PrePersist
    protected void onCreate() {
        if (dateEvaluation == null) dateEvaluation = LocalDateTime.now();
    }
}
