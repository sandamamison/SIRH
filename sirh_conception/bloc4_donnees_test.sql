-- ============================================================================
-- SIRH - Donnees de test Bloc 4 : Contrats et Affectations
-- Prerequis : bloc1.sql, bloc1_donnees_test.sql, bloc4.sql
-- Employes simules (sans FK avant le merge du Bloc 3) :
--   1 = Manager IT
--   2 = RH
--   3 = Direction
--   4 = Candidat embauche (1er contrat CE)
--   5 = Candidat embauche (2e contrat, renouvele)
-- ============================================================================

-- ============================================================================
-- AFFECTATIONS
-- ============================================================================
-- Historique d'affectation de l'employe 4 sur le poste 1 (Developpeur senior)
-- Periode 1 : 01/01/2025 -> 31/12/2025 (affectation initiale)
INSERT INTO affectation(employeId, posteId, dateDebut, dateFin) VALUES
    (4, 1, '2025-01-01', '2025-12-31');

-- Affectation actuelle (2026) de l'employe 4 sur le meme poste
-- (mutation interne ou prolongement - l'historique est conserve)
INSERT INTO affectation(employeId, posteId, dateDebut, dateFin) VALUES
    (4, 1, '2026-01-01', NULL);

-- Employe 5 : affecte au poste 2 (Responsable RH) depuis le 01/03/2025
INSERT INTO affectation(employeId, posteId, dateDebut, dateFin) VALUES
    (5, 2, '2025-03-01', NULL);

-- Employe 1 (Manager IT) : affecte au poste 3 (Analyste financier) - exemple
-- d'affectation historiquement terminee
INSERT INTO affectation(employeId, posteId, dateDebut, dateFin) VALUES
    (1, 3, '2024-01-01', '2024-12-31');

-- ============================================================================
-- CONTRATS
-- ============================================================================
-- Scenarios couverts :
--   1. CDD signe et TERMINE  (historique)
--   2. CDD signe et RENOUVELE (chaine de renouvellement)
--   3. CDI signe et actif (cas nominal)
--   4. CE signe et NON_RENOUVELE (fin de contrat d'essai)
--   5. CDD au statut CREE (en attente de signature)
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Contrat 1 : employe 4 - CDD signe sur 2024, TERMINE
-- ----------------------------------------------------------------------------
INSERT INTO contrat(
    numeroContrat, employeId, typeContrat, dateDebut, dateFin,
    salaireBrut, statut, employeCreateurId, employeSignataireId,
    dateSignature
) VALUES (
    'CTR-4-20240101', 4, 'CDD', '2024-01-01', '2024-12-31',
    2500000, 'TERMINE', 2, 3, '2023-12-15'
);

-- ----------------------------------------------------------------------------
-- Contrat 2 : employe 4 - CDD signe sur 2025 (renouvellement du contrat 1)
-- Le contrat 1 doit avoir le statut RENOUVELE (RG4-03)
-- ----------------------------------------------------------------------------
UPDATE contrat SET statut = 'RENOUVELE' WHERE numeroContrat = 'CTR-4-20240101';

INSERT INTO contrat(
    numeroContrat, employeId, typeContrat, dateDebut, dateFin,
    salaireBrut, statut, contratPrecedentId, employeCreateurId,
    employeSignataireId, dateSignature
) VALUES (
    'CTR-4-20250101', 4, 'CDD', '2025-01-01', '2025-12-31',
    2700000, 'SIGNE', 1, 2, 3, '2024-12-20'
);

-- ----------------------------------------------------------------------------
-- Contrat 3 : employe 4 - CDI signe depuis le 01/01/2026 (contrat actif)
-- Renouvellement/transformation du contrat 2
-- ----------------------------------------------------------------------------
UPDATE contrat SET statut = 'RENOUVELE' WHERE numeroContrat = 'CTR-4-20250101';

INSERT INTO contrat(
    numeroContrat, employeId, typeContrat, dateDebut, dateFin,
    salaireBrut, statut, contratPrecedentId, employeCreateurId,
    employeSignataireId, dateSignature
) VALUES (
    'CTR-4-20260101', 4, 'CDI', '2026-01-01', NULL,
    3200000, 'SIGNE', 2, 2, 3, '2025-12-18'
);

-- ----------------------------------------------------------------------------
-- Contrat 4 : employe 5 - CE signe, puis NON_RENOUVELE (echec periode d'essai)
-- ----------------------------------------------------------------------------
INSERT INTO contrat(
    numeroContrat, employeId, typeContrat, dateDebut, dateFin,
    salaireBrut, statut, employeCreateurId, employeSignataireId,
    dateSignature
) VALUES (
    'CTR-5-20250301', 5, 'CE', '2025-03-01', '2025-08-31',
    3000000, 'NON_RENOUVELE', 2, 3, '2025-02-25'
);

-- ----------------------------------------------------------------------------
-- Contrat 5 : employe 5 - CDI propose, en attente de signature (CREE)
-- Cree par RH (2) mais pas encore signe par Direction (3)
-- ----------------------------------------------------------------------------
INSERT INTO contrat(
    numeroContrat, employeId, typeContrat, dateDebut, dateFin,
    salaireBrut, statut, employeCreateurId
) VALUES (
    'CTR-5-20250901', 5, 'CDI', '2025-09-01', NULL,
    3500000, 'CREE', 2
);

-- ============================================================================
-- VERIFICATIONS POST-INSERTION (optionnel, a executer manuellement)
-- ============================================================================
-- 1) L'employe 4 doit avoir 3 contrats : 1 TERMINE, 1 RENOUVELE, 1 SIGNE (CDI actif)
--    SELECT numeroContrat, typeContrat, statut, dateDebut, dateFin
--    FROM contrat WHERE employeId = 4 ORDER BY dateDebut;
--
-- 2) L'employe 5 doit avoir 2 contrats : 1 NON_RENOUVELE, 1 CREE
--    SELECT numeroContrat, typeContrat, statut, dateDebut, dateFin
--    FROM contrat WHERE employeId = 5 ORDER BY dateDebut;
--
-- 3) Le contrat actif de l'employe 4 est bien le CDI (dateFin NULL)
--    SELECT * FROM contrat
--    WHERE employeId = 4 AND dateFin IS NULL;
--
-- 4) Le poste 1 (Developpeur senior) n'est PAS vacant (affectation active)
--    SELECT NOT EXISTS (
--        SELECT 1 FROM affectation WHERE posteId = 1 AND dateFin IS NULL
--    ) AS poste1_vacant; -- doit retourner FALSE
--
-- 5) Le poste 2 (Responsable RH) n'est PAS vacant (employe 5 actif)
--    SELECT NOT EXISTS (
--        SELECT 1 FROM affectation WHERE posteId = 2 AND dateFin IS NULL
--    ) AS poste2_vacant; -- doit retourner FALSE