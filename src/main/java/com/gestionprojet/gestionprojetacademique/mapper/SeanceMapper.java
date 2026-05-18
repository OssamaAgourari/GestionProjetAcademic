package com.gestionprojet.gestionprojetacademique.mapper;

import com.gestionprojet.gestionprojetacademique.dto.response.SeanceResponse;
import com.gestionprojet.gestionprojetacademique.model.SeanceEncadrement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SeanceMapper {

    @Mapping(target = "projetId", source = "projet.id")
    @Mapping(target = "encadrantNom", source = "encadrant.nom")
    @Mapping(target = "encadrantPrenom", source = "encadrant.prenom")
    SeanceResponse toResponse(SeanceEncadrement seance);
}
