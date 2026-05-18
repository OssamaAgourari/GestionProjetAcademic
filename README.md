# Gestion des Projets Académiques — ISMAGI

Système de gestion du cycle de vie complet des **PFE** et **PFA** à l'ISMAGI.  
Cours : *Technologies DotNet et JEE* — Pr. ETTAZARINI Younes

## Équipe
- Étudiant 1 : _______________
- Étudiant 2 : _______________

---

## Stack technique

| Composant | Technologie |
|---|---|
| Backend | Spring Boot 3.4.5 + Java 17 |
| Persistance | Spring Data JPA + Hibernate + PostgreSQL |
| Sécurité | Spring Security 6 + JWT (jjwt 0.12) |
| Vues | Thymeleaf 3 + Bootstrap 5 |
| Mapping | MapStruct 1.5.5 |
| Documentation API | springdoc-openapi 2.5 (Swagger UI) |
| Tests | JUnit 5 + Mockito + Spring Test |
| Métriques | Spring Actuator |

---

## Lancer l'application (dev)

**Prérequis** : PostgreSQL local avec une base `db_projets_dev`.

```bash
# Créer la base
psql -U postgres -c "CREATE DATABASE db_projets_dev;"

# Lancer en profil dev
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

L'application démarre sur : **http://localhost:8081/api**

---

## Accès par défaut (profil dev)

Le seeder (`DevDataSeeder`) crée automatiquement ces comptes au premier démarrage :

| Rôle | Email | Mot de passe |
|---|---|---|
| Administrateur | admin@ismagi.ma | Pass@123 |
| Étudiant 1 | etudiant1@ismagi.ma | Pass@123 |
| Étudiant 2 | etudiant2@ismagi.ma | Pass@123 |
| Encadrant académique 1 | enc.acad1@ismagi.ma | Pass@123 |
| Encadrant académique 2 | enc.acad2@ismagi.ma | Pass@123 |
| Encadrant professionnel | enc.pro1@ismagi.ma | Pass@123 |
| Membre jury 1 | jury1@ismagi.ma | Pass@123 |
| Membre jury 2 | jury2@ismagi.ma | Pass@123 |
| Membre jury 3 | jury3@ismagi.ma | Pass@123 |

---

## URLs importantes

| URL | Description |
|---|---|
| http://localhost:8081/api/login | Interface web de connexion |
| http://localhost:8081/api/dashboard | Tableau de bord |
| http://localhost:8081/api/projets | Liste des projets |
| http://localhost:8081/api/swagger-ui.html | Swagger UI (API REST) |
| http://localhost:8081/api/actuator/health | Santé de l'application |
| http://localhost:8081/api/v3/api-docs | OpenAPI JSON |

---

## API REST — Endpoints principaux

### Authentification
- `POST /api/v1/auth/login` — Connexion → JWT
- `POST /api/v1/auth/register/etudiant` — Inscription étudiant

### Projets
- `GET /api/v1/projets` — Liste paginée
- `GET /api/v1/projets/{id}` — Détail complet
- `POST /api/v1/projets` — Créer (ADMIN)
- `PUT /api/v1/projets/{id}` — Modifier (ADMIN)
- `PATCH /api/v1/projets/{id}/encadrants` — Affecter encadrants (ADMIN)
- `PATCH /api/v1/projets/{id}/statut` — Changer statut (ADMIN)
- `GET /api/v1/projets/mine` — Mes projets (ETUDIANT)
- `GET /api/v1/projets/encadrement` — Projets encadrés (ENCADRANT)

### Rapports
- `GET /api/v1/projets/{projetId}/rapports` — Versions
- `POST /api/v1/projets/{projetId}/rapports` — Soumettre (ETUDIANT, multipart)
- `GET /api/v1/rapports/{id}/download` — Télécharger

### Feedbacks
- `POST /api/v1/rapports/{versionId}/feedbacks` — Ajouter (ENCADRANT)
- `GET /api/v1/rapports/{versionId}/feedbacks` — Lister

### Séances
- `POST /api/v1/projets/{projetId}/seances` — Planifier (ENCADRANT)
- `GET /api/v1/projets/{projetId}/seances` — Lister
- `PUT /api/v1/seances/{id}` — Mettre à jour

### Soutenances
- `POST /api/v1/projets/{projetId}/soutenance` — Planifier (ADMIN)
- `GET /api/v1/soutenances/{id}` — Détail
- `POST /api/v1/soutenances/{id}/cloturer` — Clôturer + calculer note (ADMIN)

### Évaluations jury
- `POST /api/v1/soutenances/{soutenanceId}/evaluations` — Évaluer (JURY)
- `GET /api/v1/soutenances/{soutenanceId}/evaluations` — Lister

### Utilisateurs
- `GET /api/v1/utilisateurs/me` — Mon profil
- `GET /api/v1/utilisateurs?role=ETUDIANT` — Par rôle (ADMIN)

---

## Fonctionnalités couvertes (cahier des charges)

- [x] 1. Création et gestion des projets PFE/PFA avec étudiant + encadrants
- [x] 2. Suivi du statut : `PROPOSE` → `EN_COURS` → `SOUTENU` → `ARCHIVE`
- [x] 3. Enregistrement des lieux de stage / activité professionnelle
- [x] 4. Soumission de multiple versions de rapport + historique
- [x] 5. Consultation des feedbacks des encadrants
- [x] 6. Planification des séances (présentielle/distancielle) avec notes
- [x] 7. Planification de la soutenance avec affectation du jury
- [x] 8. Notes de chaque juré + calcul automatique de la note finale
- [x] 9. Conservation complète de l'historique
- [x] 10. Contrôle d'accès par rôle (RBAC)

## Concepts Spring démontrés

| Concept | Où |
|---|---|
| IoC / DI | `@RequiredArgsConstructor` dans tous les services et contrôleurs |
| Bean lifecycle | `@PostConstruct` dans `RapportServiceImpl` (init upload dir) + `DevDataSeeder` |
| Profiles | `application-dev/prod/test.properties` + `@Profile("dev")` sur `DevDataSeeder` |
| Spring Data JPA | Repositories avec derived queries + `@Query` JPQL + `Pageable` |
| Relations JPA | OneToOne, OneToMany, ManyToOne, composite key (`EvaluationJury`), `@MappedSuperclass` |
| Transactions | `@Transactional` sur services, `@Transactional(readOnly=true)` sur lectures |
| Validation | Jakarta Validation sur tous les DTOs + `@Valid` dans les contrôleurs |
| Exception handling | `@RestControllerAdvice` + exceptions métier personnalisées |
| Spring Security | JWT pour REST, form-login pour Thymeleaf, `@PreAuthorize`, BCrypt |
| Spring MVC | `@Controller` + vues Thymeleaf Bootstrap 5 |
| REST best practices | URLs versionnées `/api/v1`, status codes corrects, DTOs uniquement |
| Logging | `@Slf4j` + `log.info/warn/error` dans tous les services |
| OpenAPI | Swagger UI auto-généré avec schéma JWT |
| Actuator | `/actuator/health`, `/actuator/metrics` exposés |
