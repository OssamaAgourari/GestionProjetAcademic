package com.gestionprojet.gestionprojetacademique.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "membres_jury")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class MembreJury extends Utilisateur {

    @Column(length = 100)
    private String specialite;

    @Column(length = 150)
    private String institution;
}
