# Claude Code Prompt — ISMAGI Academic Projects Management System

## 0. Mission

You are tasked with building a complete, production-quality **Spring Boot 3.x** web application that implements the functional specification described in the document `Cahier des charges fonctionnel – Gestion des projets académiques` from ISMAGI (course: *Technologies DotNet et JEE*, Pr. ETTAZARINI Younes).

This is an **academic project for a team of two students**. The application must demonstrate mastery of every Spring Framework topic studied in class. The code must be clean, idiomatic, layered, and runnable end-to-end.

A reference project (`formations-1775818685`) is provided as a structural example. Follow the same conventions: package layout, profile-based configuration, JPA mappings with Lombok, MySQL connector, `application.properties` with separate `dev` and `prod` profiles.

> **Important — read this first:** Do not skip phases. Work strictly **phase by phase**. After each phase, build the project (`./mvnw clean compile`) to verify nothing is broken, then move to the next phase. Never start phase N+1 if phase N does not compile.

---

## 1. Functional Specification Summary (from the cahier des charges)

### 1.1 Context
A system to manage the entire lifecycle of **PFE** (Projet de Fin d'Études) and **PFA** (Projet de Fin d'Année) at ISMAGI: tracking students and their projects, assigning academic and professional supervisors, planning and managing supervision sessions, tracking report versions, organizing the defense (`soutenance`) with the jury, and ensuring complete archiving.

### 1.2 Actors (roles)
- **Étudiant** — submits reports, consults project progress, reads feedback.
- **Encadrant Académique** — plans and runs supervision sessions, tracks students, provides comments and intermediate grades. **Mandatory** on every project.
- **Encadrant Professionnel** — same as academic supervisor but external; **optional**.
- **Membre du Jury** — evaluates the project at defense, records grades and comments.
- **Administrateur** — manages project creation, supervisor and jury assignment, defense organization, full system access.

### 1.3 Functionalities (must all be implemented)
1. Create and manage PFE/PFA projects with one student, one mandatory academic supervisor, one optional professional supervisor.
2. Each project has a progress tracking and a status that evolves: `PROPOSE` → `EN_COURS` → `SOUTENU`.
3. Register and consult internship locations (`lieux de stage`) or professional activity.
4. Students submit **multiple versions** of their report and view the **version history**.
5. Students access feedback provided by their supervisors.
6. Supervisors plan supervision sessions (`séances d'encadrement`), which can be **présentielle** or **distancielle**, and enter notes/comments per session.
7. Plan the **soutenance**: assign jury members, set date and location.
8. Each jury member's notes and comments are recorded. The system **computes the final grade**.
9. All information on sessions, report versions, and evaluations is conserved.
10. Role-based access control: students see only their projects/reports; supervisors see and evaluate the projects they're responsible for; jurors enter grades only; admins have full access.

### 1.4 Constraints
- Track multiple projects in parallel.
- Guarantee **at least one academic supervisor** per project.
- Archive all information.
- Reports must be downloadable or viewable online.
- Soutenance scheduling must respect deadlines.
- Full history of versions, sessions, and evaluations must be preserved.

### 1.5 Global flow
Project created → Admin assigns student + supervisors → Student submits reports → Supervisors comment and evaluate in sessions → Final report submitted → Soutenance planned → Jury evaluates → Final result recorded → Project archived.

---

## 2. Technology Stack & Conventions

Follow the reference project (`formations-1775818685`) exactly for stack and conventions:

- **Java 21**
- **Spring Boot 3.x** (latest 3.x stable; the reference uses 4.x but **use 3.3.x or 3.4.x** because Spring Boot 4 starter names like `spring-boot-starter-webmvc` are not standard — use the conventional names below)
- **Spring Boot starters**:
  - `spring-boot-starter-web` (REST API)
  - `spring-boot-starter-data-jpa` (persistence)
  - `spring-boot-starter-validation` (Bean Validation / Jakarta Validation)
  - `spring-boot-starter-security` (role-based access)
  - `spring-boot-starter-thymeleaf` (server-rendered views — required by the spec since the app must be a "web app we can run")
  - `spring-boot-devtools` (hot reload, dev only)
  - `spring-boot-starter-actuator` (health/metrics)
  - `spring-boot-starter-test` (JUnit 5 + Mockito + Spring Test)
- **MySQL** — `com.mysql:mysql-connector-j` (runtime)
- **H2** — `com.h2database:h2` (runtime, `test` scope) for tests
- **Lombok** — `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Slf4j`
- **MapStruct** — `org.mapstruct:mapstruct` 1.5.x for DTO ↔ Entity mapping (add annotation processor in `maven-compiler-plugin`)
- **springdoc-openapi-starter-webmvc-ui** — auto-generated Swagger UI at `/swagger-ui.html`
- **JWT** — `io.jsonwebtoken:jjwt-api`, `jjwt-impl`, `jjwt-jackson` (0.12.x) for stateless authentication

### 2.1 Project naming
- `groupId`: `ma.ismagi`
- `artifactId`: `gestion-projets-academiques`
- Base package: `ma.ismagi.projets`

### 2.2 Package layout (mandatory)
```
ma.ismagi.projets
├── GestionProjetsApplication.java
├── config              ← Spring config classes (Security, OpenAPI, CORS, Web)
├── security            ← JWT filter, UserDetailsService, password encoder
├── model               ← JPA entities + enums (no business logic)
│   └── enums
├── repository          ← Spring Data JPA repositories
├── dto                 ← Request/Response DTOs (records or @Data classes)
│   ├── request
│   └── response
├── mapper              ← MapStruct mappers (Entity ↔ DTO)
├── service             ← Interfaces (`XxxService`) + implementations (`XxxServiceImpl`)
├── controller          ← REST controllers (`/api/v1/...`)
├── web                 ← Thymeleaf MVC controllers (server-rendered views)
├── exception           ← Custom exceptions + `@ControllerAdvice` global handler
└── util                ← Helpers, constants
```
Resources:
```
src/main/resources
├── application.properties           ← common config + active profile
├── application-dev.properties       ← MySQL local, ddl-auto=create-drop, show-sql=true
├── application-prod.properties      ← env-var driven, ddl-auto=update
├── application-test.properties      ← H2, ddl-auto=create-drop
├── data.sql                         ← seed data (dev only)
├── static/                          ← CSS, JS, images for Thymeleaf views
└── templates/                       ← Thymeleaf templates (.html)
    ├── fragments/                   ← header, footer, nav (th:fragment)
    ├── auth/                        ← login.html, register.html
    ├── projet/                      ← list, detail, form
    ├── rapport/                     ← versions, submit
    ├── seance/                      ← planning, detail
    ├── soutenance/                  ← schedule, evaluate
    └── admin/                       ← admin dashboards
```

---

## 3. Domain Model (entities + relationships)

Implement **every** entity below. Use Lombok `@Data @NoArgsConstructor @AllArgsConstructor @Builder` on entities (except where `@EqualsAndHashCode(callSuper = true)` is needed for inheritance). Use `@Enumerated(EnumType.STRING)` for all enums.

### 3.1 Inheritance: `Utilisateur` (`@MappedSuperclass`)
Mirrors the `Personne` pattern from the reference. Fields:
- `Long id` (`@Id @GeneratedValue(IDENTITY)`)
- `String nom`, `String prenom`, `String email` (unique, length 150), `String telephone`, `String motDePasse` (BCrypt hash, length 255), `LocalDateTime dateCreation`, `boolean actif`
- An enum field `Role role` (`@Enumerated(STRING)`) with values: `ETUDIANT`, `ENCADRANT_ACADEMIQUE`, `ENCADRANT_PROFESSIONNEL`, `MEMBRE_JURY`, `ADMINISTRATEUR`

> Note: although the role is stored on `Utilisateur`, we still create concrete subclasses below to model the domain richly. Use `@MappedSuperclass` (not `@Inheritance`) so each subclass becomes its own table — same approach as the reference project's `Personne` → `Employe`/`Formateur`.

### 3.2 Concrete user entities (each `extends Utilisateur`)
- **`Etudiant`** (table `etudiants`): `String matricule` (unique), `String filiere`, `String niveau` (e.g., `"2A"`, `"3A"`).
- **`EncadrantAcademique`** (table `encadrants_academiques`): `String specialite`, `String grade` (e.g., `"PA"`, `"PH"`).
- **`EncadrantProfessionnel`** (table `encadrants_professionnels`): `String entreprise`, `String poste`.
- **`MembreJury`** (table `membres_jury`): `String specialite`, `String institution`.
- **`Administrateur`** (table `administrateurs`): no extra fields.

### 3.3 Core entities

- **`Projet`** (table `projets`)
  - `Long id`, `String titre` (255, not null), `String description` (TEXT)
  - `TypeProjet type` enum: `PFE`, `PFA`
  - `StatutProjet statut` enum: `PROPOSE`, `EN_COURS`, `SOUTENU`, `ARCHIVE` (default `PROPOSE`)
  - `LocalDate dateCreation`, `LocalDate dateDebut`, `LocalDate dateFinPrevue`
  - `String lieuStage` (nullable — internship location or professional activity)
  - `Double resultatFinal` (computed final grade, nullable until soutenance)
  - **Relations**:
    - `@ManyToOne` to `Etudiant` (`@JoinColumn(name = "etudiant_id")`, **not null**)
    - `@ManyToOne` to `EncadrantAcademique` (`@JoinColumn(name = "encadrant_academique_id")`, **not null** — enforced in service + DB constraint)
    - `@ManyToOne` to `EncadrantProfessionnel` (nullable)
    - `@OneToMany(mappedBy = "projet")` `List<RapportVersion> versionsRapport`
    - `@OneToMany(mappedBy = "projet")` `List<SeanceEncadrement> seances`
    - `@OneToOne(mappedBy = "projet")` `Soutenance soutenance`

- **`RapportVersion`** (table `rapport_versions`)
  - `Long id`, `int numeroVersion`, `LocalDateTime dateSoumission`, `String cheminFichier` (or `byte[] contenu` as `@Lob` — pick `cheminFichier` for simplicity: store path on disk), `String commentaireEtudiant`
  - `@ManyToOne` to `Projet`
  - `@OneToMany(mappedBy = "version")` `List<Feedback> feedbacks`

- **`Feedback`** (table `feedbacks`)
  - `Long id`, `String contenu` (TEXT), `LocalDateTime dateFeedback`, `Double noteIntermediaire` (nullable)
  - `@ManyToOne` to `RapportVersion` (named `version`)
  - `@ManyToOne` to `EncadrantAcademique` (the author)

- **`SeanceEncadrement`** (table `seances_encadrement`)
  - `Long id`, `LocalDateTime dateSeance`, `int dureeMinutes`
  - `ModeSeance mode` enum: `PRESENTIELLE`, `DISTANCIELLE`
  - `String lieuOuLien` (room or video-call URL), `String notes` (TEXT), `String commentaires` (TEXT)
  - `@ManyToOne` to `Projet`
  - `@ManyToOne` to `EncadrantAcademique`

- **`Soutenance`** (table `soutenances`)
  - `Long id`, `LocalDateTime dateSoutenance`, `String lieu`
  - `StatutSoutenance statut` enum: `PLANIFIEE`, `EFFECTUEE`, `ANNULEE`
  - `@OneToOne` to `Projet` (`@JoinColumn(name = "projet_id", unique = true)`)
  - `@OneToMany(mappedBy = "soutenance")` `List<EvaluationJury> evaluations`

- **`EvaluationJury`** (table `evaluations_jury`) — **composite key** (mirror the `Inscription`/`InscriptionId` pattern from the reference)
  - `@EmbeddedId EvaluationJuryId id` containing `Long soutenanceId`, `Long membreJuryId`
  - `@ManyToOne @MapsId("soutenanceId")` `Soutenance soutenance`
  - `@ManyToOne @MapsId("membreJuryId")` `MembreJury membreJury`
  - `Double note` (0..20), `String commentaire` (TEXT), `LocalDateTime dateEvaluation`

### 3.4 Business rules to enforce in the service layer
- A `Projet` **must** have an `EncadrantAcademique`.
- `RapportVersion.numeroVersion` auto-increments per project (compute `max(numeroVersion) + 1` in `RapportService`).
- Final grade = average of all `EvaluationJury.note` for the soutenance, rounded to 2 decimals. Compute it when **all** jury members have submitted, then set `Projet.resultatFinal` and `Projet.statut = SOUTENU`.
- Soutenance cannot be scheduled if `Projet.statut != EN_COURS` or if no final version exists.
- Students can only submit a new version if previous status is not `SOUTENU` or `ARCHIVE`.

---

## 4. Phased Build Plan

Execute strictly in order. Run `./mvnw clean compile` at the end of each phase. Do not move on if compilation fails.

---

### **Phase 0 — Bootstrap**
1. Generate the project skeleton with Spring Initializr equivalent — create `pom.xml` manually with all dependencies listed in §2.
2. Create `GestionProjetsApplication.java` (main class, `@SpringBootApplication`).
3. Create the empty package directories listed in §2.2.
4. Create `application.properties` with `spring.application.name=gestion-projets-academiques`, `server.servlet.context-path=/api`, `spring.profiles.active=dev`.
5. Create `application-dev.properties`, `application-prod.properties`, `application-test.properties` mirroring the reference project.
6. Create `.gitignore` (target/, .idea/, *.iml, .DS_Store, /uploads/).
7. Run `./mvnw clean compile`. **Stop and fix if it fails.**

---

### **Phase 1 — Domain Model (entities + enums)**
1. Create all enums in `model/enums/`: `Role`, `TypeProjet`, `StatutProjet`, `ModeSeance`, `StatutSoutenance`.
2. Create the `Utilisateur` mapped superclass (§3.1).
3. Create concrete user entities (`Etudiant`, `EncadrantAcademique`, `EncadrantProfessionnel`, `MembreJury`, `Administrateur`) extending `Utilisateur`. Use `@EqualsAndHashCode(callSuper = true)`.
4. Create `Projet`, `RapportVersion`, `Feedback`, `SeanceEncadrement`, `Soutenance`, `EvaluationJury`, `EvaluationJuryId`.
5. Annotate every relationship with cascade/fetch where appropriate (default `LAZY` for `@ManyToOne`/`@OneToMany`; `EAGER` only where strictly needed). Use `@JsonIgnore` or DTOs to avoid serialization loops.
6. Build. Verify Hibernate creates all tables on startup (`ddl-auto=create-drop` in dev).

---

### **Phase 2 — Repositories**
For each entity, create a `JpaRepository<Entity, IdType>` interface in `repository/`. Add at least one custom query method per repository using Spring Data naming conventions, plus one `@Query` JPQL example. Examples:
- `ProjetRepository`: `List<Projet> findByEtudiantId(Long id)`, `List<Projet> findByEncadrantAcademiqueId(Long id)`, `List<Projet> findByStatut(StatutProjet s)`, `@Query("SELECT p FROM Projet p WHERE p.statut = 'EN_COURS' AND p.dateFinPrevue < CURRENT_DATE") List<Projet> findEnRetard()`.
- `EtudiantRepository`: `Optional<Etudiant> findByEmail(String email)`, `Optional<Etudiant> findByMatricule(String m)`.
- `RapportVersionRepository`: `List<RapportVersion> findByProjetIdOrderByNumeroVersionDesc(Long id)`, `Optional<RapportVersion> findTopByProjetIdOrderByNumeroVersionDesc(Long id)`.
- `EvaluationJuryRepository`: `List<EvaluationJury> findBySoutenanceId(Long id)`.
- Same level of detail for every other repository.

Build.

---

### **Phase 3 — DTOs + MapStruct mappers**
1. In `dto/request/`, create: `LoginRequest`, `RegisterEtudiantRequest`, `CreateProjetRequest`, `UpdateProjetRequest`, `SubmitRapportRequest`, `FeedbackRequest`, `CreateSeanceRequest`, `CreateSoutenanceRequest`, `EvaluationJuryRequest`.
2. In `dto/response/`, create: `JwtResponse`, `UtilisateurResponse`, `EtudiantResponse`, `EncadrantResponse`, `ProjetResponse`, `ProjetDetailResponse` (with nested versions, séances, soutenance), `RapportVersionResponse`, `FeedbackResponse`, `SeanceResponse`, `SoutenanceResponse`, `EvaluationJuryResponse`.
3. Use Jakarta Validation annotations (`@NotBlank`, `@Email`, `@Size`, `@NotNull`, `@Min`, `@Max`, `@Future`) on request DTOs.
4. Create a MapStruct mapper per aggregate: `ProjetMapper`, `RapportMapper`, `SeanceMapper`, `SoutenanceMapper`, `UtilisateurMapper`. Use `@Mapper(componentModel = "spring")`.
5. Build.

---

### **Phase 4 — Custom exceptions + global handler**
1. In `exception/`, create: `ResourceNotFoundException`, `BusinessRuleViolationException`, `UnauthorizedActionException`, `DuplicateResourceException`.
2. Create `GlobalExceptionHandler` annotated with `@RestControllerAdvice`. Handle:
   - `ResourceNotFoundException` → 404
   - `BusinessRuleViolationException` → 422
   - `UnauthorizedActionException` → 403
   - `DuplicateResourceException` → 409
   - `MethodArgumentNotValidException` → 400 with field errors map
   - `Exception` (catch-all) → 500
   Each returns a uniform `ErrorResponse` record `(LocalDateTime timestamp, int status, String error, String message, String path, Map<String,String> fieldErrors)`.
3. Build.

---

### **Phase 5 — Service layer**
For **every** aggregate, create `XxxService` (interface) + `XxxServiceImpl` (`@Service @Transactional @RequiredArgsConstructor @Slf4j`). Enforce all business rules from §3.4. Inject only repositories and mappers — never controllers.

Mandatory services and methods:
- `UtilisateurService`: `register`, `findByEmail`, `findById`, `listByRole`.
- `ProjetService`: `create`, `update`, `findById`, `findAll`, `findByEtudiant`, `findByEncadrant`, `affecterEncadrants`, `changerStatut`, `archiver`.
- `RapportService`: `soumettreVersion(Long projetId, SubmitRapportRequest, MultipartFile)`, `listerVersions`, `telecharger(Long versionId)`.
- `FeedbackService`: `ajouterFeedback`, `listerParVersion`.
- `SeanceService`: `planifier`, `mettreAJourNotes`, `listerParProjet`.
- `SoutenanceService`: `planifier`, `assignerJury`, `verifierEligibilite`, `cloturer` (computes final grade).
- `EvaluationJuryService`: `enregistrerEvaluation`, `listerParSoutenance`.
- `AuthService`: `authenticate(LoginRequest)`, returns `JwtResponse`.

For file upload (RapportService.soumettreVersion), save files under `./uploads/rapports/{projetId}/v{numero}_{originalFilename}` and store the path.

Write unit tests for `ProjetServiceImpl` and `SoutenanceServiceImpl` with Mockito. Build + run tests.

---

### **Phase 6 — Security (Spring Security + JWT)**
1. In `security/`, create:
   - `JwtUtil` — generate/validate tokens with `io.jsonwebtoken`. Methods: `generateToken(UserDetails)`, `extractUsername`, `isValid`. Secret in `application-*.properties` as `app.jwt.secret` and `app.jwt.expiration-ms`.
   - `JwtAuthenticationFilter extends OncePerRequestFilter`.
   - `CustomUserDetailsService implements UserDetailsService` — looks up the user across all user repositories by email and maps `Role` to `SimpleGrantedAuthority("ROLE_" + role.name())`.
2. In `config/`, create `SecurityConfig`:
   - `@Configuration @EnableWebSecurity @EnableMethodSecurity`
   - `SecurityFilterChain`: stateless session, disable CSRF for `/api/**`, allow `/api/v1/auth/**`, `/swagger-ui/**`, `/v3/api-docs/**`, `/h2-console/**`; everything else requires authentication. Add `JwtAuthenticationFilter` before `UsernamePasswordAuthenticationFilter`.
   - For Thymeleaf views (paths without `/api`), enable form login with a custom login page `/login` and CSRF enabled.
   - `BCryptPasswordEncoder` bean.
   - `AuthenticationManager` bean.
3. Use `@PreAuthorize("hasRole('ADMINISTRATEUR')")`, `@PreAuthorize("hasAnyRole('ENCADRANT_ACADEMIQUE','ADMINISTRATEUR')")`, etc., on controller methods.
4. Build.

---

### **Phase 7 — REST API controllers**
All under `/api/v1/`. Use `@RestController @RequestMapping @RequiredArgsConstructor @Validated`. Use proper HTTP verbs and status codes (`@ResponseStatus(HttpStatus.CREATED)` for creates, `204 No Content` for deletes). Document each endpoint with `@Operation` and `@ApiResponse` (springdoc-openapi).

Mandatory controllers:

| Controller | Endpoints (minimum) |
|---|---|
| `AuthController` | `POST /auth/login`, `POST /auth/register/etudiant` |
| `ProjetController` | `GET /projets`, `GET /projets/{id}`, `POST /projets` (ADMIN), `PUT /projets/{id}` (ADMIN), `PATCH /projets/{id}/encadrants` (ADMIN), `PATCH /projets/{id}/statut` (ADMIN), `GET /projets/mine` (ETUDIANT — uses Principal), `GET /projets/encadrement` (ENCADRANT) |
| `RapportController` | `GET /projets/{projetId}/rapports`, `POST /projets/{projetId}/rapports` (ETUDIANT, multipart/form-data), `GET /rapports/{id}/download` |
| `FeedbackController` | `POST /rapports/{versionId}/feedbacks` (ENCADRANT), `GET /rapports/{versionId}/feedbacks` |
| `SeanceController` | `POST /projets/{projetId}/seances` (ENCADRANT), `GET /projets/{projetId}/seances`, `PUT /seances/{id}` |
| `SoutenanceController` | `POST /projets/{projetId}/soutenance` (ADMIN), `GET /soutenances/{id}`, `POST /soutenances/{id}/cloturer` (ADMIN) |
| `EvaluationJuryController` | `POST /soutenances/{soutenanceId}/evaluations` (JURY), `GET /soutenances/{soutenanceId}/evaluations` |
| `UtilisateurController` | `GET /utilisateurs/me`, `GET /utilisateurs?role=...` (ADMIN) |

Test every endpoint with `MockMvc` integration tests for at least `ProjetController` and `RapportController`. Build + test.

---

### **Phase 8 — Thymeleaf views (web app the user can actually run)**
Mirror REST controllers with MVC controllers in `web/` returning view names. Use Bootstrap 5 (CDN) for styling.

1. **Layout fragments** (`templates/fragments/layout.html`): `head`, `navbar` (shows links based on `sec:authorize` from `thymeleaf-extras-springsecurity6`), `footer`.
2. **Auth**: `templates/auth/login.html` (Spring Security form login).
3. **Dashboard** (`/dashboard`): redirects per role (etudiant → his projects, encadrant → projects he supervises, admin → admin panel).
4. **Projets**: list (`projets/list.html`), detail (`projets/detail.html` showing versions, séances, soutenance), form (`projets/form.html` for admin).
5. **Rapports**: submit form with file upload (`rapports/submit.html`), versions list per project.
6. **Séances**: planning view per project, form for encadrant.
7. **Soutenance**: schedule form (admin), jury evaluation form (`soutenance/evaluate.html`).
8. **Admin**: dashboard with stats (`SELECT COUNT(*) FROM projets GROUP BY statut`), user management.

Add `thymeleaf-extras-springsecurity6` dependency to use `sec:authorize="hasRole('ADMINISTRATEUR')"` in templates.

Build. Launch the app: `./mvnw spring-boot:run`. Verify you can log in and navigate.

---

### **Phase 9 — Seed data + dev profile**
1. Fill `data.sql` with at least: 1 admin, 2 students, 2 academic supervisors, 1 professional supervisor, 3 jury members, 2 projets (one `PROPOSE`, one `EN_COURS` with 2 report versions, 1 séance, 1 feedback).
2. Passwords pre-hashed with BCrypt (use a one-off main method or compute online): default password `Pass@123` for all seeds. Document this in the README.
3. In `application-dev.properties`, set `spring.jpa.defer-datasource-initialization=true` and `spring.sql.init.mode=always` so `data.sql` runs after Hibernate creates the schema.
4. Build + run.

---

### **Phase 10 — OpenAPI/Swagger + Actuator + final polish**
1. Add `springdoc-openapi-starter-webmvc-ui` (2.5.x). Configure title, version, security scheme (Bearer JWT) in `OpenApiConfig`.
2. Verify Swagger UI at `http://localhost:8081/api/swagger-ui.html`.
3. Verify `/api/actuator/health` returns UP.
4. Add a `README.md` at the project root with: project description, team members (placeholders for the user to fill), tech stack, how to run (dev: `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev`), default credentials, Swagger URL, screenshots section, list of fulfilled functional requirements with checkboxes.
5. Run `./mvnw clean verify`. All tests must pass.

---

## 5. Quality & Pedagogical Requirements (MUST be visible in the code)

The grader will look for evidence of each Spring topic. Make sure each of these is **explicitly present** somewhere in the code, and mention it in the README:

| Topic | Where to demonstrate it |
|---|---|
| **IoC / DI** | Constructor injection via `@RequiredArgsConstructor` in every service and controller |
| **Bean lifecycle** | At least one `@PostConstruct` (e.g., create upload directory) and one `@Configuration` with `@Bean` methods |
| **Profiles** | `dev` / `prod` / `test` properties files, `@Profile("dev")` on a seed runner bean |
| **Spring Data JPA** | Repositories with derived queries + JPQL `@Query` + pagination (`Pageable`) in `ProjetController.findAll` |
| **Relationships** | One-to-One (`Projet`↔`Soutenance`), One-to-Many (`Projet`→`RapportVersion`), Many-to-One (`SeanceEncadrement`→`Projet`), Composite key (`EvaluationJury`), Mapped superclass (`Utilisateur`) |
| **Transactions** | `@Transactional` on service implementations, `@Transactional(readOnly = true)` on read methods |
| **Validation** | Jakarta Validation on every request DTO + `@Valid` on controller parameters |
| **Exception handling** | `@RestControllerAdvice` + custom exceptions (Phase 4) |
| **Spring Security** | JWT for REST, form login for Thymeleaf, `@PreAuthorize`, `BCryptPasswordEncoder`, role hierarchy if useful |
| **Spring MVC** | `@Controller` + Thymeleaf views (Phase 8) |
| **REST best practices** | Versioned URLs (`/api/v1`), proper status codes, DTOs (never expose entities), pagination |
| **Logging** | `@Slf4j` and meaningful `log.info` / `log.warn` / `log.error` calls |
| **Testing** | Unit tests (Mockito), integration tests (`@SpringBootTest` + `MockMvc`), `@DataJpaTest` for repositories |
| **Documentation** | OpenAPI/Swagger UI auto-generated |
| **Actuator** | Health, info, metrics endpoints exposed |

---

## 6. Acceptance checklist (definition of done)

The app is finished when all of the following are true. Tick each box before declaring done.

- [ ] `./mvnw clean verify` succeeds; all tests green.
- [ ] `./mvnw spring-boot:run` starts on port 8081 (dev) without errors.
- [ ] MySQL schema `db_projets_dev` is auto-created with all tables on first run.
- [ ] Swagger UI lists every endpoint, grouped by tag, with JWT auth scheme.
- [ ] An admin can log in via `/login`, create a project, assign student + encadrant, see it in the list.
- [ ] A student can log in, submit a `.pdf` report version, and see version history.
- [ ] An encadrant can log in, plan a séance, add a feedback on a report version.
- [ ] An admin can schedule a soutenance and assign 3 jury members.
- [ ] Each jury member can log in and submit a grade; once all are in, the final grade is computed automatically and the project status becomes `SOUTENU`.
- [ ] All endpoints enforce role-based access correctly (try with the wrong role → 403).
- [ ] Every functional requirement from §1.3 is covered (verified by ticking the checklist in `README.md`).

---

## 7. Working rules for you (Claude Code)

1. **Read the reference project first** (`formations-1775818685`) and replicate its conventions for `pom.xml`, profile setup, JPA mappings, Lombok usage, and `data.sql`. Do **not** copy bugs (e.g., the reference's `ClientController` is empty and `ClientRepo` is in the wrong package — fix these patterns, don't reproduce them).
2. After each phase, run the build. Print the file tree (`tree -L 4 src`) and a one-line summary of what was done before starting the next phase.
3. Never invent dependencies that aren't on Maven Central. Use the exact starter names from §2 (Spring Boot 3.x conventional names — `spring-boot-starter-web`, not `webmvc`).
4. Never expose JPA entities directly in controller responses — always go through DTOs and mappers.
5. Keep methods short (< 30 lines). Extract helpers liberally.
6. Use English for code identifiers and French (matching the spec) for user-facing strings, enum values, and table names.
7. When in doubt about a business rule, re-read §1 and §3.4 — the spec is the source of truth.
8. At the end, produce a final report listing every file created, every endpoint, and the result of the acceptance checklist (§6).

Begin with **Phase 0** now.
