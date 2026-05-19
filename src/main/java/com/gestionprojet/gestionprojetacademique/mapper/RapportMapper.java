package com.gestionprojet.gestionprojetacademique.mapper;

import com.gestionprojet.gestionprojetacademique.dto.response.FeedbackResponse;
import com.gestionprojet.gestionprojetacademique.dto.response.RapportVersionResponse;
import com.gestionprojet.gestionprojetacademique.model.Feedback;
import com.gestionprojet.gestionprojetacademique.model.RapportVersion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RapportMapper {

    @Mapping(target = "projetId", source = "projet.id")
    @Mapping(target = "feedbacks", source = "feedbacks")
    RapportVersionResponse toResponse(RapportVersion version);

    @Mapping(target = "versionId", source = "version.id")
    @Mapping(target = "encadrantNom", source = "encadrant.nom")
    @Mapping(target = "encadrantPrenom", source = "encadrant.prenom")
    FeedbackResponse toFeedbackResponse(Feedback feedback);
}
