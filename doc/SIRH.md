# Conception d'un logiciel de gestion RH (SIRH)

> Du recrutement à la rupture du contrat : concevoir un SIRH autour du cycle de vie du salarié.

---

## Objectifs pédagogiques

- Comprendre les processus RH **avant** de concevoir les fonctionnalités du logiciel.
- Transformer un processus métier en règles de gestion, en données, en workflows et en interfaces.
- Concevoir un SIRH couvrant **tout le cycle de vie du salarié**.
- Éviter une conception limitée à de simples écrans CRUD.
- Faire évoluer un même cas pratique jusqu'à la définition de l'architecture et du MVP.

---

## Plan général

```
Besoin → Recrutement → Sélection & Embauche → Contrat & Onboarding → Dossier salarié → Temps & pointage → Congés & absences → Évolution → Rupture
```

---

## Bloc 1 — Expression du besoin en personnel

Un manager exprime un besoin : **création ou remplacement d'un poste**.

### Informations à saisir

- Poste, département, effectif
- Motif, budget, date souhaitée

### Workflow

```
Manager → RH → Direction → Ouverture du recrutement
```

### Objets métier

`Département` · `Poste` · `DemandeRecrutement` · `Validation`

### Point de conception clé

> Un **POSTE** existe indépendamment de l'**EMPLOYÉ**.

### Règle de gestion

- Tout doit être **traçable** : qui a fait quoi, quand, combien.

---

## Bloc 2 — Recrutement

Transformer une demande validée en offre d'emploi.

### Processus

1. Publier l'offre d'emploi
2. Recevoir les candidatures
3. Présélectionner les candidats
4. Organiser les entretiens et les tests
5. Conserver les évaluations et la décision finale

### Formulaire de candidature (en ligne)

- Salaire proposé (ok / non — avec remarque)
- Date et heure de travail
- Lieu de travail
- Poste visé
- *(autres champs à définir)*

### Objets métier

`OffreEmploi` · `Candidat` · `Candidature` · `Entretien` · `ÉvaluationCandidat`

---

## Bloc 3 — Sélection et embauche

Le candidat retenu reçoit une **proposition d'embauche**.

### Processus

1. Envoi de la proposition au candidat
2. Acceptation → création du **dossier salarié** et du **matricule**
3. Création du contrat, du poste et de l'affectation

### Points de conception clés

> `CANDIDAT ≠ EMPLOYÉ` — Une personne peut avoir plusieurs candidatures avant de devenir salariée.

### Objets métier

*(repris dans Bloc 4)*

---

## Bloc 4 — Contrat et onboarding

### Types de contrats

| Type                           | Durée         | Renouvellement                                      |
| ------------------------------ | -------------- | --------------------------------------------------- |
| **CE** (Contrat d'Essai) | 6 mois         | 1 fois max — **obligatoire** à la création |
| **CDD**                  | 2 ans          | 1 fois (facultatif)                                 |
| **CDI**                  | Indéterminée | —                                                  |

> Par défaut : **CE** à la création d'un contrat.

### États d'un contrat

```
CRÉÉ → SIGNÉ → RENOUVELÉ
              → NON RENOUVELÉ
```

### Règle de conception

> Ne **jamais écraser** l'historique en stockant seulement `employe.type_contrat`.
> Un salarié peut avoir **plusieurs contrats successifs** : conserver l'historique complet.

### Onboarding — checklist d'intégration

- [ ] Contrat signé
- [ ] Badge créé
- [ ] Compte informatique créé
- [ ] Matériel attribué
- [ ] Formation effectuée
- [ ] Inscription à la paie

### Onboarding — workflow d'intégration

> Utiliser une **checklist** *et* un **workflow** d'intégration (pas une simple liste de cases à cocher).

```
Proposition acceptée → Onboarding créé → Tâches (OnboardingTache) assignées et suivies → Onboarding terminé
```

- Un `Onboarding` regroupe plusieurs `OnboardingTache` (une par ligne de la checklist).
- L'onboarding est terminé quand toutes les tâches obligatoires sont faites.

### Objets métier

`Contrat` · `Affectation` · `Onboarding` · `OnboardingTache` · `Document`

### Données facultatives

- Compte bancaire du salarié

### Traçabilité

- Qui a créé / signé / renouvelé
- De quand jusqu'à quand

---

## Bloc 5 — Dossier administratif du salarié

Le dossier salarié devient le **référentiel central du SIRH**.

### Objectifs

- Centraliser **identité, coordonnées, documents et situation professionnelle**.
- Conserver **contrats, affectations, salaire et historique de carrière**.
- Gérer les **documents administratifs et leur validité**.
- Prévoir **confidentialité, habilitations et journal d'audit**.

### Contenu du dossier

| Rubrique                    | Contenu                                                  |
| --------------------------- | -------------------------------------------------------- |
| **Identité & coordonnées**  | État civil, adresse, téléphone, e-mail, matricule        |
| **Situation professionnelle** | Poste, rattachement, statut, manager                   |
| **Contrats & affectations** | Historique complet (repris du Bloc 4)                    |
| **Salaire**                 | Salaire actuel et historique des évolutions              |
| **Historique de carrière**  | Changements de poste, d'affectation, de salaire          |
| **Documents**               | Pièces administratives, avec date de validité            |

### Gestion des documents

- Chaque document a un **type**, une **date de dépôt** et une **validité** (ex. date d'expiration).
- Suivre les documents **valides / expirés** (piste : alerte avant expiration).

### Confidentialité et audit

- **Habilitations** : définir qui peut consulter ou modifier quelle rubrique (salarié, manager, RH…).
- **Journal d'audit** : toute consultation ou modification sensible enregistre qui, quand, quoi.

### Structure de l'entreprise

Gestion du personnel selon la structure de l'entreprise :

- Section
- Service
- Département
- etc.

### Point de conception clé

> Le dossier salarié est le **référentiel central** : les blocs Temps de travail, Congés et Paie s'y rattachent, ils ne dupliquent pas ses données.

### Objets métier

`Employé` · `Document` · `Contrat` · `Affectation` · `JournalAudit`

*(la diapo ne liste pas les objets du Bloc 5 : liste proposée, à confirmer)*

---

## Bloc 6 — Temps de travail et pointage

### Objectifs

- Définir **horaires, planning et régime de travail**.
- Enregistrer **entrées, sorties, retards et heures supplémentaires**.
- Calculer le **temps réellement travaillé**.
- Détecter les **anomalies de pointage**.

### Flux

```
Horaires / planning → Pointage (entrées, sorties) → Calcul du temps réel → Détection d'anomalies
                                                                         → Absences + Variables de paie
```

### Point de conception clé

> Les données de temps **alimentent les absences et les variables de paie**.

### Exemples d'anomalies

- Entrée sans sortie (ou l'inverse)
- Pointage en dehors du planning
- Retard ou heures supplémentaires non justifiés

### Objets métier

`Horaire` · `Planning` · `RegimeTravail` · `Pointage` · `AnomaliePointage`

*(la diapo ne liste pas les objets du Bloc 6 : liste proposée, à confirmer)*

---

## Bloc 7 — Congés et absences

### Workflow

```
Demande de congé → Contrôle du solde → Validation manager / RH
```

### Règles de gestion

- Gérer **droits, soldes, types de congés, absences et justificatifs**.
- Contrôler les **chevauchements** et la **disponibilité du solde**.
- Après validation : mettre à jour le **planning** et les **variables de paie**.

### Objets métier

`TypeConge` · `DroitConge` · `SoldeConge` · `DemandeConge` · `Absence`

---

## Bloc 8 — Évolution

- Gestion de carrière (évolution du poste)

*(à compléter)*

---

## Bloc 9 — Rupture du contrat

*(à compléter)*

---

## Règles transversales

| Règle                              | Description                                                        |
| ----------------------------------- | ------------------------------------------------------------------ |
| **Traçabilité**             | Toute action doit enregistrer : qui, quand, quoi, combien          |
| **Séparation des entités**  | `POSTE ≠ EMPLOYÉ`, `CANDIDAT ≠ EMPLOYÉ`                    |
| **Historique des contrats**   | Jamais d'écrasement, toujours un nouvel enregistrement            |
| **Workflow de validation**    | Chaque bloc a son propre circuit d'approbation                     |
| **Génération de documents** | Contrat de travail généré en PDF à partir des données saisies |
| **Confidentialité**         | Accès au dossier salarié selon des habilitations, avec journal d'audit |
| **Alimentation de la paie** | Temps de travail, absences et congés produisent les variables de paie |

---

## À définir (prochaines séances)

- [ ] Stack technique (backend, frontend, BDD)
- [ ] Blocs 8 et 9 (évolution, rupture) — numérotation à confirmer avec le cours
- [ ] Objets métier des Blocs 5 et 6 (non listés sur les diapos)
- [ ] Modèle de données complet (MCD/ERD)
- [ ] Définition du MVP
- [ ] Architecture applicative
