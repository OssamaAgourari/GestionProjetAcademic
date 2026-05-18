package com.gestionprojet.gestionprojetacademique.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "encadrants_professionnels")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class EncadrantProfessionnel extends Utilisateur {

    @Column(length = 150)
    private String entreprise;

    @Column(length = 100)
    private String poste;
}
