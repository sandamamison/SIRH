-- ============================================================================
-- SIRH - Donnees de test Bloc 1
-- Employes simules (sans FK avant le merge du Bloc 3) : 1=manager, 2=RH, 3=Direction
-- ============================================================================

INSERT INTO departement(codeDepartement, nomDepartement) VALUES
    ('IT',  'Informatique'),
    ('RH',  'Ressources humaines'),
    ('FIN', 'Finance');

INSERT INTO poste(codePoste, titre, salaireMin, salaireMax, departementId) VALUES
    ('DEV-SR-01', 'Developpeur senior',    2000000, 3500000, 1),
    ('RH-MGR-01', 'Responsable RH',        2500000, 4000000, 2),
    ('ANA-FIN-01','Analyste financier',     1800000, 2800000, 3);

-- Demande CREATION valide
INSERT INTO demandeRecrutement(posteId, motif, justification, budgetPrevu, dateSouhaitee, employeDemandeurId)
VALUES (1, 'CREATION', 'Renforcement equipe dev', 3500000, '2026-12-01', 1);

-- Demande en cours de validation (SOUMISE -> VALIDEE_RH)
INSERT INTO demandeRecrutement(posteId, motif, justification, budgetPrevu, dateSouhaitee, statut, employeDemandeurId)
VALUES (2, 'CREATION', 'Remplacement suite a depart', 4000000, '2026-11-15', 'SOUMISE', 1);

INSERT INTO historiqueStatutDemande(demandeRecrutementId, ancienStatut, nouveauStatut, employeActeurId, commentaire)
VALUES (2, 'BROUILLON', 'SOUMISE', 1, 'Soumission de la demande');

INSERT INTO validationDemande(demandeRecrutementId, employeValideurId, niveau, decision, commentaire)
VALUES (2, 2, 'RH', 'APPROUVE', 'Besoin confirme');

INSERT INTO historiqueStatutDemande(demandeRecrutementId, ancienStatut, nouveauStatut, employeActeurId, commentaire)
VALUES (2, 'SOUMISE', 'VALIDEE_RH', 2, 'Validation RH');
