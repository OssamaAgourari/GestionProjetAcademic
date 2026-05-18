package com.gestionprojet.gestionprojetacademique.config;

import com.gestionprojet.gestionprojetacademique.model.*;
import com.gestionprojet.gestionprojetacademique.model.enums.*;
import com.gestionprojet.gestionprojetacademique.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DevDataSeeder {

    private final AdministrateurRepository adminRepo;
    private final EtudiantRepository etudiantRepo;
    private final EncadrantAcademiqueRepository encadrantAcadRepo;
    private final EncadrantProfessionnelRepository encadrantProRepo;
    private final MembreJuryRepository membreJuryRepo;
    private final ProjetRepository projetRepo;
    private final RapportVersionRepository rapportRepo;
    private final SeanceEncadrementRepository seanceRepo;
    private final SoutenanceRepository soutenanceRepo;
    private final PasswordEncoder passwordEncoder;

    private static final String DEFAULT_PASSWORD = "Pass@123";

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void seed() {
        if (adminRepo.count() > 0) {
            log.info("Données déjà présentes, seed ignoré.");
            return;
        }
        log.info("=== Initialisation des données de développement ===");
        String hash = passwordEncoder.encode(DEFAULT_PASSWORD);

        // ── Utilisateurs ──────────────────────────────────────────────────
        adminRepo.save(Administrateur.builder()
                .nom("Ettazarini").prenom("Younes").email("admin@ismagi.ma")
                .telephone("0600000001").motDePasse(hash).actif(true).role(Role.ADMINISTRATEUR).build());

        List<Etudiant> etudiants = etudiantRepo.saveAll(List.of(
                etu(hash, "Alaoui",    "Mohamed",  "etudiant1@ismagi.ma", "ETU001", "Génie Informatique",     "3A"),
                etu(hash, "Benali",    "Fatima",   "etudiant2@ismagi.ma", "ETU002", "Génie Informatique",     "2A"),
                etu(hash, "Cherkaoui","Youssef",  "etudiant3@ismagi.ma", "ETU003", "Réseaux & Systèmes",     "3A"),
                etu(hash, "Dahbi",    "Kenza",    "etudiant4@ismagi.ma", "ETU004", "Génie Informatique",     "3A"),
                etu(hash, "Ennaji",   "Anas",     "etudiant5@ismagi.ma", "ETU005", "Intelligence Artificielle","3A"),
                etu(hash, "Filali",   "Hajar",    "etudiant6@ismagi.ma", "ETU006", "Cybersécurité",          "2A"),
                etu(hash, "Guerrouj", "Hamid",    "etudiant7@ismagi.ma", "ETU007", "Génie Informatique",     "3A"),
                etu(hash, "Hassani",  "Loubna",   "etudiant8@ismagi.ma", "ETU008", "Réseaux & Systèmes",     "3A")
        ));

        List<EncadrantAcademique> encAcads = encadrantAcadRepo.saveAll(List.of(
                encAcad(hash, "Mansouri", "Rachid", "enc.acad1@ismagi.ma", "Génie logiciel",       "Professeur Habilité"),
                encAcad(hash, "Lahlou",   "Samira", "enc.acad2@ismagi.ma", "Bases de données",     "Professeur Agrégé"),
                encAcad(hash, "Raji",     "Tariq",  "enc.acad3@ismagi.ma", "Intelligence artificielle","Professeur")
        ));

        List<EncadrantProfessionnel> encPros = encadrantProRepo.saveAll(List.of(
                encPro(hash, "Tazi",    "Karim",  "enc.pro1@ismagi.ma", "TechMaroc SA",       "Directeur Technique"),
                encPro(hash, "Saidi",   "Leila",  "enc.pro2@ismagi.ma", "Maroc Telecom",      "Chef de projet SI"),
                encPro(hash, "Qadiri",  "Nabil",  "enc.pro3@ismagi.ma", "CIH Bank",           "Responsable Informatique")
        ));

        membreJuryRepo.saveAll(List.of(
                jury(hash, "Idrissi",    "Hassan", "jury1@ismagi.ma", "Intelligence artificielle", "ENSIAS"),
                jury(hash, "Bouhaddou",  "Nadia",  "jury2@ismagi.ma", "Réseaux",                   "ENSA"),
                jury(hash, "Chraibi",    "Omar",   "jury3@ismagi.ma", "Cybersécurité",              "EMI"),
                jury(hash, "Benbrahim",  "Amal",   "jury4@ismagi.ma", "Génie logiciel",             "FST")
        ));

        // ── Projets PROPOSE ────────────────────────────────────────────────
        projetRepo.save(projet(
                "Application mobile de covoiturage étudiant", TypeProjet.PFA,
                "Développement d'une application Android/iOS permettant aux étudiants de partager leurs trajets.",
                StatutProjet.PROPOSE,
                etudiants.get(5), encAcads.get(2), null,
                LocalDate.of(2026, 2, 1), LocalDate.of(2026, 6, 30), null));

        projetRepo.save(projet(
                "Système de détection d'intrusions réseau", TypeProjet.PFA,
                "Mise en place d'un IDS basé sur le machine learning pour détecter les attaques réseau.",
                StatutProjet.PROPOSE,
                etudiants.get(5), encAcads.get(1), null,
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 7, 31), null));

        projetRepo.save(projet(
                "Chatbot RH intelligent pour PME", TypeProjet.PFA,
                "Conception d'un assistant virtuel basé sur NLP pour automatiser les processus RH.",
                StatutProjet.PROPOSE,
                etudiants.get(6), encAcads.get(2), encPros.get(1),
                LocalDate.of(2026, 4, 1), LocalDate.of(2026, 8, 31), "Maroc Telecom — Rabat"));

        // ── Projets EN_COURS ───────────────────────────────────────────────
        Projet p4 = projetRepo.save(projet(
                "Plateforme e-learning adaptative", TypeProjet.PFE,
                "Développement d'une plateforme d'apprentissage en ligne avec parcours personnalisés selon le profil apprenant.",
                StatutProjet.EN_COURS,
                etudiants.get(0), encAcads.get(0), encPros.get(0),
                LocalDate.of(2025, 9, 1), LocalDate.of(2026, 6, 30), "TechMaroc SA — Casablanca"));

        Projet p5 = projetRepo.save(projet(
                "Système de gestion des stocks par RFID", TypeProjet.PFE,
                "Automatisation de la gestion des stocks d'un entrepôt grâce à la technologie RFID et IoT.",
                StatutProjet.EN_COURS,
                etudiants.get(1), encAcads.get(1), encPros.get(2),
                LocalDate.of(2025, 10, 1), LocalDate.of(2026, 6, 30), "CIH Bank — Casablanca"));

        Projet p6 = projetRepo.save(projet(
                "Analyse de sentiments sur les réseaux sociaux", TypeProjet.PFE,
                "Système d'analyse automatique des opinions exprimées sur Twitter/LinkedIn en arabe et français.",
                StatutProjet.EN_COURS,
                etudiants.get(2), encAcads.get(2), null,
                LocalDate.of(2025, 11, 1), LocalDate.of(2026, 7, 15), null));

        Projet p7 = projetRepo.save(projet(
                "Application de télémédecine pour zones rurales", TypeProjet.PFE,
                "Solution de consultation médicale à distance adaptée aux zones à faible connectivité.",
                StatutProjet.EN_COURS,
                etudiants.get(3), encAcads.get(0), encPros.get(1),
                LocalDate.of(2025, 9, 15), LocalDate.of(2026, 6, 15), "Maroc Telecom — Rabat"));

        // ── Projets SOUTENU ────────────────────────────────────────────────
        Projet p8 = projetRepo.save(projetSoutenu(
                "Blockchain pour la traçabilité alimentaire", TypeProjet.PFE,
                "Implémentation d'une chaîne de blocs pour garantir l'authenticité et la traçabilité des produits alimentaires.",
                etudiants.get(4), encAcads.get(0), encPros.get(0),
                LocalDate.of(2024, 9, 1), LocalDate.of(2025, 6, 30), "TechMaroc SA — Casablanca", 15.5));

        Projet p9 = projetRepo.save(projetSoutenu(
                "Optimisation des tournées de livraison par algorithmes génétiques", TypeProjet.PFA,
                "Application des métaheuristiques pour résoudre le problème du voyageur de commerce dans un contexte logistique.",
                etudiants.get(7), encAcads.get(1), null,
                LocalDate.of(2024, 10, 1), LocalDate.of(2025, 7, 31), null, 14.0));

        Projet p10 = projetRepo.save(projetSoutenu(
                "Système de reconnaissance faciale pour contrôle d'accès", TypeProjet.PFE,
                "Développement d'un système biométrique utilisant les réseaux de neurones convolutifs (CNN) pour le contrôle d'accès.",
                etudiants.get(6), encAcads.get(2), encPros.get(2),
                LocalDate.of(2024, 9, 1), LocalDate.of(2025, 6, 30), "CIH Bank — Casablanca", 17.0));

        // ── Projet ARCHIVE ─────────────────────────────────────────────────
        Projet p11 = projetRepo.save(projetArchive(
                "Migration vers le cloud hybride", TypeProjet.PFE,
                "Étude et mise en œuvre d'une stratégie de migration vers une infrastructure cloud hybride (AWS + On-premise).",
                etudiants.get(0), encAcads.get(0), encPros.get(1),
                LocalDate.of(2023, 9, 1), LocalDate.of(2024, 6, 30), "Maroc Telecom — Rabat", 16.0));

        // ── Séances d'encadrement ──────────────────────────────────────────
        saveSeances(p4, encAcads.get(0));
        saveSeances(p5, encAcads.get(1));
        saveSeances(p6, encAcads.get(2));
        saveSeances(p7, encAcads.get(0));
        saveSeancesCompletes(p8, encAcads.get(0));
        saveSeancesCompletes(p9, encAcads.get(1));
        saveSeancesCompletes(p10, encAcads.get(2));
        saveSeancesCompletes(p11, encAcads.get(0));

        // ── Versions de rapport ────────────────────────────────────────────
        saveRapport(p4, 1, "Première version — structure générale du projet", LocalDateTime.of(2025, 11, 10, 10, 0));
        saveRapport(p4, 2, "Deuxième version — chapitre conception ajouté", LocalDateTime.of(2026, 1, 15, 14, 30));
        saveRapport(p5, 1, "Version initiale du rapport de stage", LocalDateTime.of(2025, 12, 5, 9, 0));
        saveRapport(p6, 1, "Rapport préliminaire — état de l'art", LocalDateTime.of(2026, 1, 20, 11, 0));
        saveRapport(p6, 2, "Rapport V2 — implémentation du modèle", LocalDateTime.of(2026, 3, 8, 16, 0));
        saveRapport(p7, 1, "Rapport de stage — phase analyse", LocalDateTime.of(2025, 12, 1, 10, 0));
        saveRapport(p8, 1, "Rapport V1", LocalDateTime.of(2024, 11, 5, 10, 0));
        saveRapport(p8, 2, "Rapport V2 — implémentation complète", LocalDateTime.of(2025, 2, 20, 14, 0));
        saveRapport(p8, 3, "Rapport final corrigé", LocalDateTime.of(2025, 5, 15, 9, 0));
        saveRapport(p9, 1, "Rapport final", LocalDateTime.of(2025, 6, 1, 10, 0));
        saveRapport(p10, 1, "Rapport V1", LocalDateTime.of(2024, 12, 10, 10, 0));
        saveRapport(p10, 2, "Rapport final", LocalDateTime.of(2025, 5, 20, 10, 0));
        saveRapport(p11, 1, "Rapport final archivé", LocalDateTime.of(2024, 5, 10, 10, 0));

        // ── Soutenances ────────────────────────────────────────────────────
        soutenanceRepo.save(Soutenance.builder()
                .projet(p8).lieu("Salle des conférences A — ISMAGI")
                .dateSoutenance(LocalDateTime.of(2025, 6, 25, 9, 0))
                .statut(StatutSoutenance.EFFECTUEE).build());

        soutenanceRepo.save(Soutenance.builder()
                .projet(p9).lieu("Amphithéâtre B — ISMAGI")
                .dateSoutenance(LocalDateTime.of(2025, 7, 10, 14, 0))
                .statut(StatutSoutenance.EFFECTUEE).build());

        soutenanceRepo.save(Soutenance.builder()
                .projet(p10).lieu("Salle des conférences A — ISMAGI")
                .dateSoutenance(LocalDateTime.of(2025, 6, 28, 10, 0))
                .statut(StatutSoutenance.EFFECTUEE).build());

        soutenanceRepo.save(Soutenance.builder()
                .projet(p11).lieu("Salle informatique 3 — ISMAGI")
                .dateSoutenance(LocalDateTime.of(2024, 6, 20, 9, 30))
                .statut(StatutSoutenance.EFFECTUEE).build());

        log.info("=== Seed terminé : {} étudiants, {} projets ===",
                etudiantRepo.count(), projetRepo.count());
        log.info("Connexion : admin@ismagi.ma / {}", DEFAULT_PASSWORD);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private Etudiant etu(String hash, String nom, String prenom, String email,
                         String matricule, String filiere, String niveau) {
        return Etudiant.builder()
                .nom(nom).prenom(prenom).email(email).motDePasse(hash)
                .actif(true).role(Role.ETUDIANT)
                .matricule(matricule).filiere(filiere).niveau(niveau).build();
    }

    private EncadrantAcademique encAcad(String hash, String nom, String prenom,
                                        String email, String specialite, String grade) {
        return EncadrantAcademique.builder()
                .nom(nom).prenom(prenom).email(email).motDePasse(hash)
                .actif(true).role(Role.ENCADRANT_ACADEMIQUE)
                .specialite(specialite).grade(grade).build();
    }

    private EncadrantProfessionnel encPro(String hash, String nom, String prenom,
                                          String email, String entreprise, String poste) {
        return EncadrantProfessionnel.builder()
                .nom(nom).prenom(prenom).email(email).motDePasse(hash)
                .actif(true).role(Role.ENCADRANT_PROFESSIONNEL)
                .entreprise(entreprise).poste(poste).build();
    }

    private MembreJury jury(String hash, String nom, String prenom,
                            String email, String specialite, String institution) {
        return MembreJury.builder()
                .nom(nom).prenom(prenom).email(email).motDePasse(hash)
                .actif(true).role(Role.MEMBRE_JURY)
                .specialite(specialite).institution(institution).build();
    }

    private Projet projet(String titre, TypeProjet type, String description,
                          StatutProjet statut, Etudiant etudiant,
                          EncadrantAcademique encAcad, EncadrantProfessionnel encPro,
                          LocalDate dateDebut, LocalDate dateFinPrevue, String lieuStage) {
        return Projet.builder()
                .titre(titre).type(type).description(description).statut(statut)
                .etudiant(etudiant).encadrantAcademique(encAcad).encadrantProfessionnel(encPro)
                .dateDebut(dateDebut).dateFinPrevue(dateFinPrevue).lieuStage(lieuStage).build();
    }

    private Projet projetSoutenu(String titre, TypeProjet type, String description,
                                 Etudiant etudiant, EncadrantAcademique encAcad,
                                 EncadrantProfessionnel encPro,
                                 LocalDate dateDebut, LocalDate dateFinPrevue,
                                 String lieuStage, double resultat) {
        return Projet.builder()
                .titre(titre).type(type).description(description).statut(StatutProjet.SOUTENU)
                .etudiant(etudiant).encadrantAcademique(encAcad).encadrantProfessionnel(encPro)
                .dateDebut(dateDebut).dateFinPrevue(dateFinPrevue).lieuStage(lieuStage)
                .resultatFinal(resultat).build();
    }

    private Projet projetArchive(String titre, TypeProjet type, String description,
                                 Etudiant etudiant, EncadrantAcademique encAcad,
                                 EncadrantProfessionnel encPro,
                                 LocalDate dateDebut, LocalDate dateFinPrevue,
                                 String lieuStage, double resultat) {
        return Projet.builder()
                .titre(titre).type(type).description(description).statut(StatutProjet.ARCHIVE)
                .etudiant(etudiant).encadrantAcademique(encAcad).encadrantProfessionnel(encPro)
                .dateDebut(dateDebut).dateFinPrevue(dateFinPrevue).lieuStage(lieuStage)
                .resultatFinal(resultat).build();
    }

    private void saveRapport(Projet projet, int version, String commentaire, LocalDateTime date) {
        rapportRepo.save(RapportVersion.builder()
                .projet(projet).numeroVersion(version)
                .commentaireEtudiant(commentaire)
                .dateSoumission(date).build());
    }

    private void saveSeances(Projet projet, EncadrantAcademique encadrant) {
        seanceRepo.save(SeanceEncadrement.builder()
                .projet(projet).encadrant(encadrant)
                .dateSeance(LocalDateTime.now().minusWeeks(6))
                .dureeMinutes(60).mode(ModeSeance.PRESENTIELLE)
                .lieuOuLien("Bureau encadrant — ISMAGI")
                .notes("Présentation du cahier des charges. Validation du périmètre fonctionnel.").build());
        seanceRepo.save(SeanceEncadrement.builder()
                .projet(projet).encadrant(encadrant)
                .dateSeance(LocalDateTime.now().minusWeeks(3))
                .dureeMinutes(45).mode(ModeSeance.DISTANCIELLE)
                .lieuOuLien("https://meet.google.com/xxx-yyy-zzz")
                .notes("Revue de la conception. Corrections demandées sur le modèle de données.").build());
    }

    private void saveSeancesCompletes(Projet projet, EncadrantAcademique encadrant) {
        seanceRepo.save(SeanceEncadrement.builder()
                .projet(projet).encadrant(encadrant)
                .dateSeance(projet.getDateDebut().atTime(10, 0).plusWeeks(2))
                .dureeMinutes(90).mode(ModeSeance.PRESENTIELLE)
                .lieuOuLien("Bureau encadrant — ISMAGI")
                .notes("Kick-off du projet. Planification des jalons.").build());
        seanceRepo.save(SeanceEncadrement.builder()
                .projet(projet).encadrant(encadrant)
                .dateSeance(projet.getDateDebut().atTime(10, 0).plusWeeks(6))
                .dureeMinutes(60).mode(ModeSeance.DISTANCIELLE)
                .lieuOuLien("Microsoft Teams")
                .notes("Validation de l'architecture. Point sur l'avancement.").build());
        seanceRepo.save(SeanceEncadrement.builder()
                .projet(projet).encadrant(encadrant)
                .dateSeance(projet.getDateDebut().atTime(14, 0).plusWeeks(12))
                .dureeMinutes(120).mode(ModeSeance.PRESENTIELLE)
                .lieuOuLien("Salle de réunion 2 — ISMAGI")
                .notes("Revue complète du rapport avant soumission. Corrections orthographiques et de fond.").build());
        seanceRepo.save(SeanceEncadrement.builder()
                .projet(projet).encadrant(encadrant)
                .dateSeance(projet.getDateDebut().atTime(10, 0).plusWeeks(16))
                .dureeMinutes(60).mode(ModeSeance.PRESENTIELLE)
                .lieuOuLien("Bureau encadrant — ISMAGI")
                .notes("Préparation de la soutenance. Entraînement à la présentation orale.").build());
    }
}
