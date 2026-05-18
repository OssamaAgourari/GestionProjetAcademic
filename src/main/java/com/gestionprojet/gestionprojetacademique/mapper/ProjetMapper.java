package com.gestionprojet.gestionprojetacademique.mapper;

import com.gestionprojet.gestionprojetacademique.dto.response.ProjetResponse;
import com.gestionprojet.gestionprojetacademique.model.Projet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UtilisateurMapper.class})
public interface ProjetMapper {

    @Mapping(target = "etudiant", source = "etudiant")
    @Mapping(target = "encadrantAcademique", source = "encadrantAcademique")
    @Mapping(target = "encadrantProfessionnel", source = "encadrantProfessionnel")
    ProjetResponse toResponse(Projet projet);
}
