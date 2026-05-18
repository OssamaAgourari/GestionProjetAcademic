package com.gestionprojet.gestionprojetacademique.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class EvaluationJuryId implements Serializable {
    private Long soutenanceId;
    private Long membreJuryId;
}
