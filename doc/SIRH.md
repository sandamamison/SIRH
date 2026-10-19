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
Besoin → Recrutement → Sélection & Embauche → Contrat & Onboarding → Vie du salarié → Évolution → Rupture
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
| Type | Durée | Renouvellement |
|------|-------|---------------|
| **CE** (Contrat d'Essai) | 6 mois | 1 fois max — **obligatoire** à la création |
| **CDD** | 2 ans | 1 fois (facultatif) |
| **CDI** | Indéterminée | — |

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

### Objets métier
`Contrat` · `Affectation` · `Onboarding` · `OnboardingTache` · `Document`

### Données facultatives
- Compte bancaire du salarié

### Traçabilité
- Qui a créé / signé / renouvelé
- De quand jusqu'à quand

---

## Bloc 5 — Vie du salarié

*(à compléter)*

- Gestion du personnel selon la structure de l'entreprise :
  - Section
  - Service
  - Département
  - etc.

---

## Bloc 6 — Évolution

- Gestion de carrière (évolution du poste)

*(à compléter)*

---

## Bloc 7 — Rupture du contrat

*(à compléter)*

---

## Règles transversales

| Règle | Description |
|-------|-------------|
| **Traçabilité** | Toute action doit enregistrer : qui, quand, quoi, combien |
| **Séparation des entités** | `POSTE ≠ EMPLOYÉ`, `CANDIDAT ≠ EMPLOYÉ` |
| **Historique des contrats** | Jamais d'écrasement, toujours un nouvel enregistrement |
| **Workflow de validation** | Chaque bloc a son propre circuit d'approbation |
| **Génération de documents** | Contrat de travail généré en PDF à partir des données saisies |

---

## À définir (prochaines séances)

- [ ] Stack technique (backend, frontend, BDD)
- [ ] Blocs 5, 6, 7 (vie du salarié, évolution, rupture)
- [ ] Modèle de données complet (MCD/ERD)
- [ ] Définition du MVP
- [ ] Architecture applicative
