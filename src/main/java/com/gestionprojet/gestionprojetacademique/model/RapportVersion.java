package com.gestionprojet.gestionprojetacademique.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rapport_versions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RapportVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int numeroVersion;

    @Column(nullable = false)
    private LocalDateTime dateSoumission;

    @Column(length = 500)
    private String cheminFichier;

    @Column(columnDefinition = "TEXT")
    private String commentaireEtudiant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projet_id", nullable = false)
    @JsonIgnore
    private Projet projet;

    @OneToMany(mappedBy = "version", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private List<Feedback> feedbacks = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (dateSoumission == null) dateSoumission = LocalDateTime.now();
    }
}
