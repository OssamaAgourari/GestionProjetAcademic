package com.gestionprojet.gestionprojetacademique.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gestionprojet.gestionprojetacademique.model.enums.StatutSoutenance;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "soutenances")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Soutenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dateSoutenance;

    @Column(nullable = false, length = 255)
    private String lieu;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    @Builder.Default
    private StatutSoutenance statut = StatutSoutenance.PLANIFIEE;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projet_id", unique = true, nullable = false)
    @JsonIgnore
    private Projet projet;

    @OneToMany(mappedBy = "soutenance", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private List<EvaluationJury> evaluations = new ArrayList<>();
}
