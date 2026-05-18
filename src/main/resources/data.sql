-- Seed data for dev profile
-- Default password for all users: Pass@123
-- BCrypt hash of 'Pass@123':
-- $2a$12$rGtVQLnnFGX1F9VkGe/5b.pvjsaXFzpJ2R5f3V4Z1TQ8kMDXI3bMK

-- Admin
INSERT INTO administrateurs (nom, prenom, email, telephone, mot_de_passe, date_creation, actif, role)
VALUES ('Ettazarini', 'Younes', 'admin@ismagi.ma', '0600000001',
        '$2a$12$rGtVQLnnFGX1F9VkGe/5b.pvjsaXFzpJ2R5f3V4Z1TQ8kMDXI3bMK',
        NOW(), true, 'ADMINISTRATEUR')
ON CONFLICT (email) DO NOTHING;

-- Étudiants
INSERT INTO etudiants (nom, prenom, email, telephone, mot_de_passe, date_creation, actif, role, matricule, filiere, niveau)
VALUES ('Alaoui', 'Mohamed', 'etudiant1@ismagi.ma', '0600000002',
        '$2a$12$rGtVQLnnFGX1F9VkGe/5b.pvjsaXFzpJ2R5f3V4Z1TQ8kMDXI3bMK',
        NOW(), true, 'ETUDIANT', 'ETU001', 'Informatique', '3A'),
       ('Benali', 'Fatima', 'etudiant2@ismagi.ma', '0600000003',
        '$2a$12$rGtVQLnnFGX1F9VkGe/5b.pvjsaXFzpJ2R5f3V4Z1TQ8kMDXI3bMK',
        NOW(), true, 'ETUDIANT', 'ETU002', 'Informatique', '2A')
ON CONFLICT (email) DO NOTHING;

-- Encadrants académiques
INSERT INTO encadrants_academiques (nom, prenom, email, telephone, mot_de_passe, date_creation, actif, role, specialite, grade)
VALUES ('Mansouri', 'Rachid', 'enc.acad1@ismagi.ma', '0600000004',
        '$2a$12$rGtVQLnnFGX1F9VkGe/5b.pvjsaXFzpJ2R5f3V4Z1TQ8kMDXI3bMK',
        NOW(), true, 'ENCADRANT_ACADEMIQUE', 'Génie logiciel', 'PH'),
       ('Lahlou', 'Samira', 'enc.acad2@ismagi.ma', '0600000005',
        '$2a$12$rGtVQLnnFGX1F9VkGe/5b.pvjsaXFzpJ2R5f3V4Z1TQ8kMDXI3bMK',
        NOW(), true, 'ENCADRANT_ACADEMIQUE', 'Bases de données', 'PA')
ON CONFLICT (email) DO NOTHING;

-- Encadrant professionnel
INSERT INTO encadrants_professionnels (nom, prenom, email, telephone, mot_de_passe, date_creation, actif, role, entreprise, poste)
VALUES ('Tazi', 'Karim', 'enc.pro1@ismagi.ma', '0600000006',
        '$2a$12$rGtVQLnnFGX1F9VkGe/5b.pvjsaXFzpJ2R5f3V4Z1TQ8kMDXI3bMK',
        NOW(), true, 'ENCADRANT_PROFESSIONNEL', 'TechMaroc SA', 'Directeur Technique')
ON CONFLICT (email) DO NOTHING;

-- Membres du jury
INSERT INTO membres_jury (nom, prenom, email, telephone, mot_de_passe, date_creation, actif, role, specialite, institution)
VALUES ('Idrissi', 'Hassan', 'jury1@ismagi.ma', '0600000007',
        '$2a$12$rGtVQLnnFGX1F9VkGe/5b.pvjsaXFzpJ2R5f3V4Z1TQ8kMDXI3bMK',
        NOW(), true, 'MEMBRE_JURY', 'Intelligence artificielle', 'ENSIAS'),
       ('Bouhaddou', 'Nadia', 'jury2@ismagi.ma', '0600000008',
        '$2a$12$rGtVQLnnFGX1F9VkGe/5b.pvjsaXFzpJ2R5f3V4Z1TQ8kMDXI3bMK',
        NOW(), true, 'MEMBRE_JURY', 'Réseaux', 'ENSA'),
       ('Chraibi', 'Omar', 'jury3@ismagi.ma', '0600000009',
        '$2a$12$rGtVQLnnFGX1F9VkGe/5b.pvjsaXFzpJ2R5f3V4Z1TQ8kMDXI3bMK',
        NOW(), true, 'MEMBRE_JURY', 'Cybersécurité', 'EMI')
ON CONFLICT (email) DO NOTHING;
