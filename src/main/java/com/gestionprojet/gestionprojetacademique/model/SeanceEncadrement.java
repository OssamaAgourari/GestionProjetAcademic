package com.gestionprojet.gestionprojetacademique.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gestionprojet.gestionprojetacademique.model.enums.ModeSeance;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "seances_encadrement")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeanceEncadrement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dateSeance;

    @Column(nullable = false)
    private int dureeMinutes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private ModeSeance mode;

    @Column(length = 255)
    private String lieuOuLien;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(columnDefinition = "TEXT")
    private String commentaires;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projet_id", nullable = false)
    @JsonIgnore
    private Projet projet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encadrant_id", nullable = false)
    @JsonIgnore
    private EncadrantAcademique encadrant;
}
