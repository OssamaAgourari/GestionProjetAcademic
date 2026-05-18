package com.gestionprojet.gestionprojetacademique.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "encadrants_academiques")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class EncadrantAcademique extends Utilisateur {

    @Column(length = 100)
    private String specialite;

    @Column(length = 20)
    private String grade;
}
