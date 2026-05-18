package com.gestionprojet.gestionprojetacademique.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gestionprojet.gestionprojetacademique.model.enums.StatutProjet;
import com.gestionprojet.gestionprojetacademique.model.enums.TypeProjet;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Projet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TypeProjet type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    @Builder.Default
    private StatutProjet statut = StatutProjet.PROPOSE;

    @Column(nullable = false)
    private LocalDate dateCreation;

    private LocalDate dateDebut;
    private LocalDate dateFinPrevue;

    @Column(length = 255)
    private String lieuStage;

    private Double resultatFinal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etudiant_id", nullable = false)
    @JsonIgnore
    private Etudiant etudiant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encadrant_academique_id", nullable = false)
    @JsonIgnore
    private EncadrantAcademique encadrantAcademique;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encadrant_professionnel_id")
    @JsonIgnore
    private EncadrantProfessionnel encadrantProfessionnel;

    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private List<RapportVersion> versionsRapport = new ArrayList<>();

    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private List<SeanceEncadrement> seances = new ArrayList<>();

    @OneToOne(mappedBy = "projet", cascade = CascadeType.ALL)
    @JsonIgnore
    private Soutenance soutenance;

    @PrePersist
    protected void onCreate() {
        if (dateCreation == null) dateCreation = LocalDate.now();
    }
}
