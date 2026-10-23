# Entités Java à créer — Bloc 2 (Recrutement)

> **Branche :** `B2_recrutement`  
> **SQL de référence :** [`sirh_conception/modele_sirh.sql`](../sirh_conception/modele_sirh.sql) — chercher `BLOC 2`  
> **Style à imiter :** entités Bloc 1 dans `src/main/java/SI/RH/entity/`

---

## 1. Enums à créer

`src/main/java/SI/RH/entity/enums/`

| Fichier | Valeurs |
|---|---|
| `StatutOffre.java` | `BROUILLON, PUBLIEE, CLOTUREE, ANNULEE` |
| `StatutCandidature.java` | `RECUE, PRESELECTIONNEE, EN_ENTRETIEN, RETENUE, REJETEE` |
| `TypeEntretien.java` | `RH, TECHNIQUE, DIRECTION, TEST` |
| `StatutEntretien.java` | `PLANIFIE, EFFECTUE, ANNULE, REPORTE` |
| `AvisEvaluation.java` | `FAVORABLE, RESERVE, DEFAVORABLE` |

---

## 2. Entités à créer

### `OffreEmploi.java`

```
id, idDemande, titreOffre, description, salairePropose,
horairesTravail, lieuTravail, datePublication, dateCloture, statut
```

> `idDemande` est UNIQUE (1 demande → 1 offre max).  
> `statut` : type `StatutOffre`.

---

### `Candidature.java`

```
id, idOffre, idPersonne, dateCandidature, cvUrl,
lettreMotivation, pretentionSalariale, accordSalaire,
remarqueSalaire, statut
```

> `statut` : type `StatutCandidature`.  
> Contrainte métier : une seule candidature par `(idOffre, idPersonne)`.

---

### `Entretien.java`

```
id, idCandidature, dateHeure, lieuOuLien, typeEntretien, statut
```

> `typeEntretien` : type `TypeEntretien`.  
> `statut` : type `StatutEntretien`.

---

### `IntervenantEntretien.java`

Clé composée `(idEntretien, idEmploye)`.

```
idEntretien, idEmploye, roleIntervenant
```

> Représente les membres du jury convoqués pour un entretien.

---

### `EvaluationCandidat.java`

```
id, idEntretien, idEvaluateur, scoreGlobal,
pointsForts, pointsFaibles, avis, dateEvaluation
```

> `avis` : type `AvisEvaluation`.  
> `idEvaluateur` : référence un `Employe`.  
> `scoreGlobal` : entier entre 0 et 20.  
> Contrainte métier : une seule évaluation par `(idEntretien, idEvaluateur)`.

---

### `HistoriqueStatutCandidature.java`

```
id, idCandidature, ancienStatut, nouveauStatut, idActeur, commentaire, dateTransition
```

> `ancienStatut` / `nouveauStatut` : type `StatutCandidature`.  
> `idActeur` : référence un `Employe` (le RH qui a effectué la transition).

---

## Récapitulatif des fichiers

```
entity/
├── enums/
│   ├── StatutOffre.java
│   ├── StatutCandidature.java
│   ├── TypeEntretien.java
│   ├── StatutEntretien.java
│   └── AvisEvaluation.java
├── OffreEmploi.java
├── Candidature.java
├── Entretien.java
├── IntervenantEntretien.java
├── EvaluationCandidat.java
└── HistoriqueStatutCandidature.java
```
