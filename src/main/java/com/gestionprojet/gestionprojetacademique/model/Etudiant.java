package com.gestionprojet.gestionprojetacademique.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "etudiants")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Etudiant extends Utilisateur {

    @Column(unique = true, nullable = false, length = 20)
    private String matricule;

    @Column(length = 100)
    private String filiere;

    @Column(length = 10)
    private String niveau;
}
