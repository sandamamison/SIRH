# To-do SIRH – répartition pour 5 personnes

Répartition par bloc du cours *Conception logiciel – Gestion RH*, avec une phase commune au début et une à la fin. Pour chaque personne, les tâches vont de la conception jusqu'au MVP.

## Tous ensemble (début)

- [x] Valider le modèle de données commun (diagramme ER du fichier de conception)
- [x] Choisir la stack technique et créer le dépôt Git (branches, conventions de nommage)
- [x] Définir les statuts et le format de traçabilité des transitions (acteur, date, commentaire)

## Personne 1 : Bloc 1, Besoin en personnel

> Refait selon `normes.md` (base `sirh`, PK `id`, FK `(table)Id`) et en MVC orienté service, sous forme d'API. Vues non traitées pour l'instant.

- [x] Configuration : `application.properties` hors Git (+ `.example`), dépendance validation
- [x] SQL `bloc1.sql` (departement, poste, demandeRecrutement, validationDemande, historiqueStatutDemande)
- [x] Enums : `StatutDemande` (transitions), `NiveauValidation`, `MotifRecrutement`, `DecisionValidation`
- [x] Entités JPA renommées (`id`, `posteId`, `departementId`…)
- [x] Repositories
- [x] Services : Departement, Poste, DemandeRecrutement
- [x] Règles RG1-01 à RG1-07 codées dans les services + tests
- [x] Workflow Manager → RH → Direction (soumettre / valider / refuser) avec historique
- [x] Controllers REST + DTO + gestion d'erreurs (`/api/departements`, `/api/postes`, `/api/demandes`)
- [ ] Reporté : écrans (JSP simple) — formulaire de demande, liste à valider, suivi d'état

## Personne 2 : Bloc 2, Recrutement

- [ ] Entités `OffreEmploi`, `Candidature`, `Entretien`, `ÉvaluationCandidat`
- [ ] Création d'une offre à partir d'une demande validée (lien avec la personne 1)
- [ ] Règles RG2-01 à RG2-08 (une seule candidature par offre et par candidat, clôture de l'offre…)
- [ ] Écrans : publication d'offre, liste des candidatures avec filtres, planning des entretiens, fiche d'évaluation

## Personne 3 : Bloc 3, Sélection et embauche

- [ ] Lire le guide détaillé : [`bloc3_selection_embauche.md`](bloc3_selection_embauche.md)
- [ ] Entités `Personne`, `PropositionEmbauche`, `Employé` (matricule unique)
- [ ] Séparation `Personne` / `Candidature` / `Employé` (CANDIDAT ≠ EMPLOYÉ)
- [ ] Traitement de l'acceptation : création du dossier salarié, du matricule, du contrat et de l'affectation
- [ ] Écrans : envoi et suivi de la proposition, fiche salarié

## Personne 4 : Bloc 4, Contrats et affectations

- [ ] Entités `Contrat` et `Affectation` avec historique (pas de simple `type_contrat` sur l'employé)
- [ ] Règles RG4-01 à RG4-04 (un seul contrat actif, renouvellement qui crée un nouveau contrat lié au précédent)
- [ ] Gestion de la vacance d'un poste (`Poste` indépendant de l'employé)
- [ ] Écrans : historique des contrats, création et renouvellement, affectations

## Personne 5 : Onboarding, documents et transverse

- [ ] Entités `Onboarding`, `OnboardingTâche`, `Document`
- [ ] Checklist type (contrat signé, badge, compte, matériel, formation, paie) et création automatique à l'embauche
- [ ] Gestion des rôles et des droits d'accès (Manager, RH, Direction, Salarié)
- [ ] Notifications email (optionnel) et stockage des documents

## Tous ensemble (fin)

- [ ] Intégrer les blocs et tester le parcours complet : demande → offre → candidature → embauche → contrat → onboarding
- [ ] Revue croisée du code et des règles de gestion
- [ ] Rédiger le document d'architecture et préparer la démo du MVP

## Priorités MVP

D'abord les blocs 1 à 4 sans les entretiens ni les notifications. Les évaluations (personne 2) et la checklist d'onboarding (personne 5) viennent ensuite.
