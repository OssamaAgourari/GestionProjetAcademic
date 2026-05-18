package com.gestionprojet.gestionprojetacademique.mapper;

import com.gestionprojet.gestionprojetacademique.dto.response.EncadrantResponse;
import com.gestionprojet.gestionprojetacademique.dto.response.EtudiantResponse;
import com.gestionprojet.gestionprojetacademique.dto.response.MembreJuryResponse;
import com.gestionprojet.gestionprojetacademique.model.EncadrantAcademique;
import com.gestionprojet.gestionprojetacademique.model.EncadrantProfessionnel;
import com.gestionprojet.gestionprojetacademique.model.Etudiant;
import com.gestionprojet.gestionprojetacademique.model.MembreJury;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UtilisateurMapper {

    EtudiantResponse toEtudiantResponse(Etudiant etudiant);

    @Mapping(target = "grade", source = "grade")
    @Mapping(target = "specialite", source = "specialite")
    @Mapping(target = "role", expression = "java(encadrant.getRole().name())")
    EncadrantResponse toEncadrantAcademiqueResponse(EncadrantAcademique encadrant);

    @Mapping(target = "grade", source = "poste")
    @Mapping(target = "specialite", source = "entreprise")
    @Mapping(target = "role", expression = "java(encadrant.getRole().name())")
    EncadrantResponse toEncadrantProfessionnelResponse(EncadrantProfessionnel encadrant);

    MembreJuryResponse toMembreJuryResponse(MembreJury membreJury);
}
