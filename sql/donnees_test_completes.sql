-- ============================================================================
-- Jeu de données complet pour tester l'interface SIRH.
-- SCRIPT DESTRUCTIF : réinitialise les tables applicatives et recrée un scénario.
-- Mot de passe de tous les comptes : password
-- ============================================================================

BEGIN;

TRUNCATE TABLE
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

INSERT INTO personnes (id, nom, prenom, email, telephone, adresse)
VALUES
    (1, 'Rakoto', 'Jean', 'jean.rakoto@sirh.local', '0340000001', 'Antananarivo'),
    (2, 'Rasoanaivo', 'Marie', 'marie.rasoanaivo@sirh.local', '0340000002', 'Antananarivo'),
    (3, 'Andriamihaja', 'Paul', 'paul.andriamihaja@sirh.local', '0340000003', 'Toamasina'),
    (4, 'Randria', 'Sophie', 'sophie.randria@sirh.local', '0340000004', 'Fianarantsoa'),
    (5, 'Razafindrakoto', 'Luc', 'luc.razafindrakoto@sirh.local', '0340000005', 'Antsirabe');

INSERT INTO employes (id, matricule, personneid, dateembauche, statut)
VALUES
    (1, 'EMP-0001', 1, DATE '2024-01-15', 'ACTIF'),
    (2, 'EMP-0002', 2, DATE '2023-06-01', 'ACTIF'),
    (3, 'EMP-0003', 3, DATE '2022-09-12', 'ACTIF');

INSERT INTO utilisateurs (id, username, passwordhash, personneid, actif)
VALUES
    (1, 'admin', '$2b$10$fLTRuyR8XcyNUVnivMz4wusuWUI9IQdDBteQMu4lUeuVaYlux7Nym', 1, TRUE),
    (2, 'direction', '$2b$10$fLTRuyR8XcyNUVnivMz4wusuWUI9IQdDBteQMu4lUeuVaYlux7Nym', 2, TRUE),
    (3, 'rh', '$2b$10$fLTRuyR8XcyNUVnivMz4wusuWUI9IQdDBteQMu4lUeuVaYlux7Nym', 3, TRUE),
    (4, 'manager', '$2b$10$fLTRuyR8XcyNUVnivMz4wusuWUI9IQdDBteQMu4lUeuVaYlux7Nym', 4, TRUE),
    (5, 'candidat', '$2b$10$fLTRuyR8XcyNUVnivMz4wusuWUI9IQdDBteQMu4lUeuVaYlux7Nym', 5, TRUE);

INSERT INTO utilisateur_roles (utilisateurid, role)
VALUES
    (1, 'ADMIN'), (2, 'DIRECTION'), (3, 'RH'), (4, 'MANAGER'), (5, 'CANDIDAT');

INSERT INTO departement (id, codeDepartement, nomDepartement, description, employeResponsableId)
VALUES
    (1, 'IT', 'Informatique', 'Développement et infrastructure', 1),
    (2, 'RH', 'Ressources humaines', 'Recrutement et accompagnement', 2),
    (3, 'FIN', 'Finance', 'Comptabilité et contrôle', 3);

INSERT INTO poste (id, codePoste, titre, descriptionMission, salaireMin, salaireMax, departementId)
VALUES
    (1, 'DEV-BACK', 'Développeur backend', 'Développer les services backend', 1200000, 2500000, 1),
    (2, 'RH-REC', 'Chargé de recrutement', 'Gérer les recrutements', 1000000, 2200000, 2),
    (3, 'FIN-ANA', 'Analyste financier', 'Analyser les résultats financiers', 1300000, 2600000, 3);

INSERT INTO demandeRecrutement
    (id, posteId, motif, justification, employeRemplaceId, effectifDemande,
     budgetPrevu, dateSouhaitee, statut, employeDemandeurId)
VALUES
    (1, 1, 'CREATION', 'Renforcement de l''équipe backend', NULL, 1,
     2400000, DATE '2026-11-01', 'RECRUTEMENT_OUVERT', 1),
    (2, 2, 'REMPLACEMENT', 'Remplacement d''un chargé de recrutement', 2, 1,
     2100000, DATE '2026-12-01', 'VALIDEE_DIRECTION', 2);

INSERT INTO validationDemande
    (id, demandeRecrutementId, employeValideurId, niveau, decision, commentaire)
VALUES
    (1, 1, 2, 'RH', 'APPROUVE', 'Budget validé par les RH'),
    (2, 1, 2, 'DIRECTION', 'APPROUVE', 'Demande approuvée');

INSERT INTO historiqueStatutDemande
    (id, demandeRecrutementId, ancienStatut, nouveauStatut, employeActeurId, commentaire)
VALUES
    (1, 1, NULL, 'BROUILLON', 1, 'Création de la demande'),
    (2, 1, 'BROUILLON', 'SOUMISE', 1, 'Demande soumise'),
    (3, 1, 'SOUMISE', 'VALIDEE_RH', 2, 'Validation RH'),
    (4, 1, 'VALIDEE_RH', 'VALIDEE_DIRECTION', 2, 'Validation direction'),
    (5, 1, 'VALIDEE_DIRECTION', 'RECRUTEMENT_OUVERT', 2, 'Ouverture du recrutement');

INSERT INTO offreEmploi
    (id, demandeRecrutementId, titreOffre, description, salairePropose,
     horairesTravail, lieuTravail, datePublication, dateCloture, statut)
VALUES
    (1, 1, 'Développeur backend Java', 'Rejoignez notre équipe technique.',
     2200000, '08:00 - 17:00', 'Antananarivo', DATE '2026-10-01', NULL, 'PUBLIEE'),
    (2, 2, 'Chargé de recrutement RH', 'Accompagnez nos futurs collaborateurs.',
     1900000, '08:00 - 17:00', 'Antananarivo', DATE '2026-10-05', NULL, 'PUBLIEE');

INSERT INTO candidature
    (id, offreemploiid, personneid, cvurl, lettremotivation, pretentionsalariale,
     accordsalaire, statut)
VALUES
    (1, 1, 5, 'cv-luc.pdf', 'Je souhaite rejoindre votre équipe technique.', 2100000, TRUE, 'RETENUE'),
    (2, 2, 4, 'cv-sophie.pdf', 'Mon expérience RH correspond à votre besoin.', 1800000, TRUE, 'PRESELECTIONNEE');

INSERT INTO historiquestatutcandidature
    (id, candidatureid, ancienstatut, nouveauStatut, employeacteurid, commentaire)
VALUES
    (1, 1, NULL, 'RECUE', 3, 'Candidature reçue'),
    (2, 1, 'RECUE', 'PRESELECTIONNEE', 3, 'Profil présélectionné'),
    (3, 1, 'PRESELECTIONNEE', 'RETENUE', 3, 'Profil retenu');

INSERT INTO entretien
    (id, candidatureid, dateheure, lieuoulien, typeentretien, statut)
VALUES
    (1, 1, TIMESTAMP '2026-10-20 09:00:00', 'Salle RH', 'TECHNIQUE', 'EFFECTUE'),
    (2, 2, TIMESTAMP '2026-10-28 14:00:00', 'Visioconférence', 'RH', 'PLANIFIE');

INSERT INTO intervenantentretien (id, entretienid, employeid, roleintervenant)
VALUES (1, 1, 1, 'TECHNIQUE'), (2, 1, 2, 'RH');

INSERT INTO evaluationcandidat
    (id, entretienid, employeevaluateurid, scoreglobal, pointsforts, pointsfaibles, avis)
VALUES
    (1, 1, 1, 17, 'Bonne maîtrise de Java et PostgreSQL', 'Communication à approfondir', 'FAVORABLE'),
    (2, 1, 2, 16, 'Profil sérieux et autonome', 'Peu d''expérience managériale', 'FAVORABLE');

INSERT INTO propositions_embauche
    (id, candidature_id, statut, date_envoi, date_expiration, commentaire, created_by)
VALUES
    (1, 1, 'ENVOYEE', TIMESTAMP '2026-10-21 10:00:00', DATE '2026-11-15',
     'Félicitations, nous vous proposons de rejoindre SIRH.', 'rh');

INSERT INTO historique_propositions
    (id, proposition_id, ancien_statut, nouveau_statut, auteur, commentaire)
VALUES
    (1, 1, NULL, 'ENVOYEE', 'rh', 'Proposition envoyée au candidat');

INSERT INTO contrat
    (id, numeroContrat, employeId, typeContrat, dateDebut, dateFin,
     salaireBrut, statut, employeCreateurId, employeSignataireId, dateSignature)
VALUES
    (1, 'CTR-EMP-0001', 1, 'CDI', DATE '2024-01-15', NULL, 2400000, 'SIGNE', 2, 2, DATE '2024-01-15');

INSERT INTO affectation (id, employeId, posteId, dateDebut, dateFin)
VALUES (1, 1, 1, DATE '2024-01-15', NULL);

SELECT setval(pg_get_serial_sequence('personnes', 'id'), 5, TRUE);
SELECT setval(pg_get_serial_sequence('employes', 'id'), 3, TRUE);
SELECT setval(pg_get_serial_sequence('utilisateurs', 'id'), 5, TRUE);
SELECT setval(pg_get_serial_sequence('departement', 'id'), 3, TRUE);
SELECT setval(pg_get_serial_sequence('poste', 'id'), 3, TRUE);
SELECT setval(pg_get_serial_sequence('demanderecrutement', 'id'), 2, TRUE);
SELECT setval(pg_get_serial_sequence('offreemploi', 'id'), 2, TRUE);
SELECT setval(pg_get_serial_sequence('candidature', 'id'), 2, TRUE);
SELECT setval(pg_get_serial_sequence('entretien', 'id'), 2, TRUE);
SELECT setval(pg_get_serial_sequence('propositions_embauche', 'id'), 1, TRUE);
SELECT setval(pg_get_serial_sequence('contrat', 'id'), 1, TRUE);
SELECT setval(pg_get_serial_sequence('affectation', 'id'), 1, TRUE);

COMMIT;
