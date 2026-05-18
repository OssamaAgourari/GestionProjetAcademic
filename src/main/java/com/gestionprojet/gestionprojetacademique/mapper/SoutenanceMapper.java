package com.gestionprojet.gestionprojetacademique.mapper;

import com.gestionprojet.gestionprojetacademique.dto.response.EvaluationJuryResponse;
import com.gestionprojet.gestionprojetacademique.dto.response.SoutenanceResponse;
import com.gestionprojet.gestionprojetacademique.model.EvaluationJury;
import com.gestionprojet.gestionprojetacademique.model.Soutenance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SoutenanceMapper {

    @Mapping(target = "projetId", source = "projet.id")
    SoutenanceResponse toResponse(Soutenance soutenance);

    @Mapping(target = "soutenanceId", source = "id.soutenanceId")
    @Mapping(target = "membreJuryId", source = "id.membreJuryId")
    @Mapping(target = "membreJuryNom", source = "membreJury.nom")
    @Mapping(target = "membreJuryPrenom", source = "membreJury.prenom")
    EvaluationJuryResponse toEvaluationResponse(EvaluationJury evaluation);
}
