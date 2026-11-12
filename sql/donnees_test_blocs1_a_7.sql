-- ============================================================================
-- SIRH - Jeu de données de test complet (Blocs 1 à 7)
-- Base    : sirh
-- Mot de passe de tous les utilisateurs : password
-- ============================================================================

BEGIN;

TRUNCATE TABLE
    absence,
    demande_conge,
    solde_conge,
    droit_conge,
    type_conge,
    anomalie_pointage,
    pointage,
    planning,
    horaire,
    regime_travail,
    journal_audit,
    document,
    onboarding_tache,
    onboarding,
    historique_propositions,
    propositions_embauche,
    evaluationcandidat,
    intervenantentretien,
    entretien,
    historiquestatutcandidature,
    candidature,
    offreEmploi,
    historiqueStatutDemande,
    validationDemande,
    demandeRecrutement,
    affectation,
    contrat,
    poste,
    departement,
    utilisateur_roles,
    utilisateurs,
    employes,
    personnes
RESTART IDENTITY CASCADE;

-- 1. Personnes (Identités)
INSERT INTO personnes (id, nom, prenom, email, telephone, adresse)
VALUES
    (1, 'Rakoto', 'Jean', 'jean.rakoto@sirh.local', '0340000001', 'Antananarivo'),
    (2, 'Rasoanaivo', 'Marie', 'marie.rasoanaivo@sirh.local', '0340000002', 'Antananarivo'),
    (3, 'Andriamihaja', 'Paul', 'paul.andriamihaja@sirh.local', '0340000003', 'Toamasina'),
    (4, 'Randria', 'Sophie', 'sophie.randria@sirh.local', '0340000004', 'Fianarantsoa'),
    (5, 'Razafindrakoto', 'Luc', 'luc.razafindrakoto@sirh.local', '0340000005', 'Antsirabe');

-- 2. Employés
INSERT INTO employes (id, matricule, personneid, dateembauche, statut)
VALUES
    (1, 'EMP001', 1, DATE '2024-01-15', 'ACTIF'),
    (2, 'EMP002', 2, DATE '2024-02-01', 'ACTIF'),
    (3, 'EMP003', 3, DATE '2024-03-01', 'ACTIF');

-- 3. Utilisateurs et Rôles (mot de passe : 'password' haché en BCrypt)
INSERT INTO utilisateurs (id, username, passwordhash, personneid, actif)
VALUES
    (1, 'admin', '$2b$10$fLTRuyR8XcyNUVnivMz4wusuWUI9IQdDBteQMu4lUeuVaYlux7Nym', 1, TRUE),
    (2, 'direction', '$2b$10$fLTRuyR8XcyNUVnivMz4wusuWUI9IQdDBteQMu4lUeuVaYlux7Nym', 2, TRUE),
    (3, 'rh', '$2b$10$fLTRuyR8XcyNUVnivMz4wusuWUI9IQdDBteQMu4lUeuVaYlux7Nym', 3, TRUE),
    (4, 'manager', '$2b$10$fLTRuyR8XcyNUVnivMz4wusuWUI9IQdDBteQMu4lUeuVaYlux7Nym', 4, TRUE),
    (5, 'candidat', '$2b$10$fLTRuyR8XcyNUVnivMz4wusuWUI9IQdDBteQMu4lUeuVaYlux7Nym', 5, TRUE);

INSERT INTO utilisateur_roles (utilisateurid, role)
VALUES
    (1, 'ADMIN'),
    (2, 'DIRECTION'),
    (3, 'RH'),
    (4, 'MANAGER'),
    (5, 'CANDIDAT');

-- 4. Départements et Postes (Organisation)
INSERT INTO departement (id, codeDepartement, nomDepartement, description, employeResponsableId)
VALUES
    (1, 'IT', 'Informatique', 'Développement logiciel et infrastructure', 1),
    (2, 'RH', 'Ressources Humaines', 'Recrutement et gestion administrative', 2),
    (3, 'FIN', 'Finance', 'Comptabilité et gestion de paie', 3);

UPDATE employes SET departementid = 1 WHERE id = 1;
UPDATE employes SET departementid = 2 WHERE id = 2;
UPDATE employes SET departementid = 3 WHERE id = 3;

INSERT INTO poste (id, codePoste, titre, descriptionMission, salaireMin, salaireMax, departementId)
VALUES
    (1, 'DEV-BACK', 'Développeur Backend', 'Conception d''API et services métiers', 1500000, 3000000, 1),
    (2, 'RH-GEST', 'Gestionnaire RH', 'Suivi du personnel et des contrats', 1200000, 2200000, 2),
    (3, 'COMPT', 'Comptable', 'Gestion des variables de paie et factures', 1300000, 2400000, 3);

-- 5. Bloc 1 : Besoins en personnel et validations
INSERT INTO demandeRecrutement
    (id, posteId, motif, justification, effectifDemande, budgetPrevu, dateSouhaitee, statut, employeDemandeurId)
VALUES
    (1, 1, 'CREATION', 'Renfort pour le développement de la plateforme SIRH', 2, 2800000, DATE '2026-11-01', 'RECRUTEMENT_OUVERT', 1),
    (2, 2, 'CREATION', 'Besoin d''un gestionnaire RH dédié au pôle paie', 1, 2000000, DATE '2026-12-01', 'VALIDEE_RH', 2);

INSERT INTO validationDemande (id, demandeRecrutementId, employeValideurId, niveau, decision, commentaire)
VALUES
    (1, 1, 2, 'RH', 'APPROUVE', 'Budget validé en accord avec la grille RH'),
    (2, 1, 3, 'DIRECTION', 'APPROUVE', 'Validation finale de la direction');

INSERT INTO historiqueStatutDemande (id, demandeRecrutementId, ancienStatut, nouveauStatut, employeActeurId, commentaire)
VALUES
    (1, 1, NULL, 'BROUILLON', 1, 'Création de la demande'),
    (2, 1, 'BROUILLON', 'SOUMISE', 1, 'Demande transmise au RH'),
    (3, 1, 'SOUMISE', 'VALIDEE_RH', 2, 'Validation RH'),
    (4, 1, 'VALIDEE_RH', 'VALIDEE_DIRECTION', 3, 'Validation Direction'),
    (5, 1, 'VALIDEE_DIRECTION', 'RECRUTEMENT_OUVERT', 2, 'Ouverture du recrutement');

-- 6. Bloc 2 : Offres, candidatures, entretiens et évaluations
INSERT INTO offreEmploi
    (id, demandeRecrutementId, titreOffre, description, salairePropose, horairesTravail, lieuTravail, datePublication, statut)
VALUES
    (1, 1, 'Ingénieur Développeur Java / Spring Boot', 'Participez à la refonte du SIRH en microservices ou MVC.', 2500000, '08h30 - 17h30', 'Antananarivo Ankorondrano', DATE '2026-10-15', 'PUBLIEE');

INSERT INTO candidature
    (id, offreemploiid, personneid, cvurl, lettremotivation, pretentionsalariale, accordsalaire, statut)
VALUES
    (1, 1, 5, 'cv-luc.pdf', 'Passionné par l''architecture backend et les SIRH.', 2400000, TRUE, 'RETENUE'),
    (2, 1, 4, 'cv-sophie.pdf', 'Expérience solide en conception d''applications d''entreprise.', 2600000, TRUE, 'PRESELECTIONNEE');

INSERT INTO entretien (id, candidatureid, dateheure, lieuoulien, typeentretien, statut)
VALUES
    (1, 1, TIMESTAMP '2026-10-22 10:00:00', 'Salle Réunion 1', 'TECHNIQUE', 'EFFECTUE');

INSERT INTO intervenantentretien (id, entretienid, employeid, roleintervenant)
VALUES
    (1, 1, 1, 'EXAMINATEUR');

INSERT INTO evaluationcandidat (id, entretienid, employeevaluateurid, scoreglobal, pointsforts, pointsfaibles, avis)
VALUES
    (1, 1, 1, 18, 'Excellente maîtrise Java, tests unitaires et base SQL', 'Communication fluide', 'FAVORABLE');

-- 7. Bloc 3 : Proposition d'embauche
INSERT INTO propositions_embauche (id, candidature_id, statut, date_envoi, date_reponse, date_expiration, commentaire, created_by)
VALUES
    (1, 1, 'ACCEPTEE', TIMESTAMP '2026-10-25 09:00:00', TIMESTAMP '2026-10-26 14:00:00', DATE '2026-11-20', 'Proposition salariale retenue', 'rh');

-- 8. Bloc 4 : Contrats, affectations, onboarding et documents
INSERT INTO contrat
    (id, numeroContrat, employeId, typeContrat, dateDebut, dateFin, salaireBrut, statut, employeCreateurId, employeSignataireId, dateSignature)
VALUES
    (1, 'CTR-EMP001-2024', 1, 'CDI', DATE '2024-01-15', NULL, 2800000, 'SIGNE', 2, 2, DATE '2024-01-15'),
    (2, 'CTR-EMP002-2024', 2, 'CDI', DATE '2024-02-01', NULL, 2100000, 'SIGNE', 2, 2, DATE '2024-02-01'),
    (3, 'CTR-EMP003-2024', 3, 'CDI', DATE '2024-03-01', NULL, 2200000, 'SIGNE', 2, 2, DATE '2024-03-01');

INSERT INTO affectation (id, employeId, posteId, dateDebut, dateFin)
VALUES
    (1, 1, 1, DATE '2024-01-15', NULL),
    (2, 2, 2, DATE '2024-02-01', NULL),
    (3, 3, 3, DATE '2024-03-01', NULL);

INSERT INTO onboarding (id, employe_id, date_debut, statut)
VALUES
    (1, 1, DATE '2024-01-15', 'TERMINE'),
    (2, 2, DATE '2024-02-01', 'TERMINE'),
    (3, 3, DATE '2024-03-01', 'EN_COURS');

INSERT INTO onboarding_tache (id, onboarding_id, libelle, responsable_id, echeance, statut, obligatoire)
VALUES
    (1, 3, 'Contrat signé', 2, DATE '2024-03-05', 'FAIT', TRUE),
    (2, 3, 'Badge d''accès', 2, DATE '2024-03-05', 'FAIT', TRUE),
    (3, 3, 'Compte informatique et messagerie', 1, DATE '2024-03-05', 'FAIT', TRUE),
    (4, 3, 'Matériel (poste, téléphone)', 1, DATE '2024-03-06', 'EN_COURS', TRUE),
    (5, 3, 'Formation d''intégration', 2, DATE '2024-03-10', 'A_FAIRE', TRUE),
    (6, 3, 'Inscription à la paie', 2, DATE '2024-03-15', 'A_FAIRE', TRUE);

INSERT INTO document (id, type_document, fichier_url, employe_id, contrat_id, date_depot, date_validite)
VALUES
    (1, 'CONTRAT_SIGNE', '/uploads/contrats/ctr-emp001.pdf', 1, 1, DATE '2024-01-15', NULL),
    (2, 'PIECE_IDENTITE', '/uploads/id/cin-rakoto.pdf', 1, NULL, DATE '2024-01-15', DATE '2029-01-15'),
    (3, 'DIPLOME', '/uploads/diplomes/master-rakoto.pdf', 1, NULL, DATE '2024-01-15', NULL);

-- 9. Bloc 5 : Journal d'audit
INSERT INTO journal_audit (id, acteur_id, type_action, table_cible, enregistrement_id, detail, date_action)
VALUES
    (1, 1, 'CREATION', 'demandeRecrutement', 1, 'Création demande de recrutement pour DEV-BACK', TIMESTAMP '2026-10-10 08:30:00'),
    (2, 2, 'MODIFICATION', 'demandeRecrutement', 1, 'Validation RH de la demande 1', TIMESTAMP '2026-10-11 09:15:00'),
    (3, 2, 'CONSULTATION', 'employes', 1, 'Consultation du dossier administratif de EMP001', TIMESTAMP '2026-11-12 14:00:00');

-- 10. Bloc 6 : Temps de travail et pointage
INSERT INTO regime_travail (id, libelle, nb_heures_semaine)
VALUES
    (1, 'Temps plein 40h', 40),
    (2, 'Temps plein 35h', 35);

INSERT INTO horaire (id, libelle, heure_debut, heure_fin, pause_minutes)
VALUES
    (1, 'Journée standard', TIME '08:00:00', TIME '17:00:00', 60),
    (2, 'Équipe du matin', TIME '06:00:00', TIME '14:00:00', 30);

INSERT INTO planning (id, employe_id, horaire_id, semaine_debut, regime_travail_id)
VALUES
    (1, 1, 1, DATE '2026-11-09', 1),
    (2, 2, 1, DATE '2026-11-09', 1),
    (3, 3, 1, DATE '2026-11-09', 1);

INSERT INTO pointage (id, employe_id, direction, horodatage, origine, planning_id)
VALUES
    (1, 1, 'ENTREE', TIMESTAMP '2026-11-12 07:55:00', 'BADGE', 1),
    (2, 1, 'SORTIE', TIMESTAMP '2026-11-12 17:05:00', 'BADGE', 1),
    (3, 2, 'ENTREE', TIMESTAMP '2026-11-12 08:10:00', 'APPLI', 2),
    (4, 2, 'SORTIE', TIMESTAMP '2026-11-12 17:00:00', 'APPLI', 2);

INSERT INTO anomalie_pointage (id, employe_id, pointage_id, type_anomalie, description, statut)
VALUES
    (1, 2, 3, 'RETARD', 'Pointage à 08h10 (horaire prévu 08h00)', 'DETECTEE');

-- 11. Bloc 7 : Types de congés, droits, soldes, demandes et absences
INSERT INTO type_conge (id, code, libelle, plafonne, duree_max_jours)
VALUES
    (1, 'CA', 'Congé Payé Annuel', TRUE, 30),
    (2, 'RTT', 'Récupération Temps de Travail', TRUE, 12),
    (3, 'MALADIE', 'Congé Maladie', FALSE, NULL);

INSERT INTO droit_conge (id, employe_id, type_conge_id, annee, jours_acquis)
VALUES
    (1, 1, 1, 2026, 25.0),
    (2, 1, 2, 2026, 10.0),
    (3, 2, 1, 2026, 25.0),
    (4, 3, 1, 2026, 20.0);

INSERT INTO solde_conge (id, employe_id, type_conge_id, solde_courant)
VALUES
    (1, 1, 1, 21.0),
    (2, 1, 2, 8.0),
    (3, 2, 1, 18.0),
    (4, 3, 1, 20.0);

INSERT INTO demande_conge
    (id, employe_id, type_conge_id, date_debut, date_fin, statut, commentaire, validateur_manager_id, validateur_rh_id)
VALUES
    (1, 1, 1, DATE '2026-12-24', DATE '2026-12-31', 'VALIDEE_RH', 'Congés de fin d''année en famille', 1, 2),
    (2, 2, 1, DATE '2026-11-20', DATE '2026-11-24', 'SOUMISE', 'Repos prolongé', NULL, NULL);

INSERT INTO absence (id, employe_id, type_absence, date_debut, date_fin, demande_conge_id, justifiee)
VALUES
    (1, 1, 'CONGE_ANNUEL', DATE '2026-12-24', DATE '2026-12-31', 1, TRUE);

-- Réajustement des séquences d'ID pour PostgreSQL
SELECT setval(pg_get_serial_sequence('personnes', 'id'), 5, TRUE);
SELECT setval(pg_get_serial_sequence('employes', 'id'), 3, TRUE);
SELECT setval(pg_get_serial_sequence('utilisateurs', 'id'), 5, TRUE);
SELECT setval(pg_get_serial_sequence('departement', 'id'), 3, TRUE);
SELECT setval(pg_get_serial_sequence('poste', 'id'), 3, TRUE);
SELECT setval(pg_get_serial_sequence('demanderecrutement', 'id'), 2, TRUE);
SELECT setval(pg_get_serial_sequence('validationDemande', 'id'), 2, TRUE);
SELECT setval(pg_get_serial_sequence('historiqueStatutDemande', 'id'), 5, TRUE);
SELECT setval(pg_get_serial_sequence('offreemploi', 'id'), 1, TRUE);
SELECT setval(pg_get_serial_sequence('candidature', 'id'), 2, TRUE);
SELECT setval(pg_get_serial_sequence('entretien', 'id'), 1, TRUE);
SELECT setval(pg_get_serial_sequence('intervenantentretien', 'id'), 1, TRUE);
SELECT setval(pg_get_serial_sequence('evaluationcandidat', 'id'), 1, TRUE);
SELECT setval(pg_get_serial_sequence('propositions_embauche', 'id'), 1, TRUE);
SELECT setval(pg_get_serial_sequence('contrat', 'id'), 3, TRUE);
SELECT setval(pg_get_serial_sequence('affectation', 'id'), 3, TRUE);
SELECT setval(pg_get_serial_sequence('onboarding', 'id'), 3, TRUE);
SELECT setval(pg_get_serial_sequence('onboarding_tache', 'id'), 6, TRUE);
SELECT setval(pg_get_serial_sequence('document', 'id'), 3, TRUE);
SELECT setval(pg_get_serial_sequence('journal_audit', 'id'), 3, TRUE);
SELECT setval(pg_get_serial_sequence('regime_travail', 'id'), 2, TRUE);
SELECT setval(pg_get_serial_sequence('horaire', 'id'), 2, TRUE);
SELECT setval(pg_get_serial_sequence('planning', 'id'), 3, TRUE);
SELECT setval(pg_get_serial_sequence('pointage', 'id'), 4, TRUE);
SELECT setval(pg_get_serial_sequence('anomalie_pointage', 'id'), 1, TRUE);
SELECT setval(pg_get_serial_sequence('type_conge', 'id'), 3, TRUE);
SELECT setval(pg_get_serial_sequence('droit_conge', 'id'), 4, TRUE);
SELECT setval(pg_get_serial_sequence('solde_conge', 'id'), 4, TRUE);
SELECT setval(pg_get_serial_sequence('demande_conge', 'id'), 2, TRUE);
SELECT setval(pg_get_serial_sequence('absence', 'id'), 1, TRUE);

COMMIT;
