# Mise à jour des entités Java — Bloc 3 (Sélection & Embauche)

> **Contexte :** Le schéma SQL a été revu et corrigé lors de deux audits successifs
> (voir [`sirh_conception/modele_sirh.sql`](../sirh_conception/modele_sirh.sql)).
> Les entités Java du Bloc 3 ont été créées **avant** ces audits.
> Elles doivent être mises à jour pour rester synchronisées avec la base.
>
> **Branche cible :** `3-Selection-et-embauche`  
> **Style de référence :** regarder les entités du Bloc 1 dans
> `src/main/java/SI/RH/entity/` (même branche après merge du main).

---

## Ce qu'il faut faire

### 1. Créer l'enum `StatutProposition`

Créer le fichier `src/main/java/SI/RH/entity/enums/StatutProposition.java` :

```java
package SI.RH.entity.enums;

/**
 * RG3-02 : Statuts possibles d'une proposition d'embauche.
 * ENVOYEE → ACCEPTEE ou REFUSEE ou EXPIREE
 */
public enum StatutProposition {
    ENVOYEE,
    ACCEPTEE,
    REFUSEE,
    EXPIREE
}
```

> **Pourquoi :** L'ancienne entité utilisait `statutId: int`, ce qui est un
> anti-pattern (entier sans signification lisible). Le SQL utilise maintenant
> un ENUM PostgreSQL nommé `statut_proposition`.

---

### 2. Réécrire `Personne.java`

**Champs à ajouter / corriger :**

| Champ actuel | Action | Champ corrigé |
|---|---|---|
| `created_at: Date` | Renommer | `dateCreation: Date` |
| `updated_at: Date` | **Supprimer** — absent du SQL | — |
| *(absent)* | Ajouter | `dateNaissance: Date` |

**Résultat attendu (champs) :**
```
id, nom, prenom, email, telephone, adresse, dateNaissance, dateCreation
```

---

### 3. Réécrire `Employe.java`

**Champs à ajouter / corriger / supprimer :**

| Champ actuel | Action | Champ corrigé | Raison |
|---|---|---|---|
| `statutId: int` | **Remplacer** | `actif: boolean` | Le SQL stocke un booléen, pas un ID de statut |
| `created_at: Date` | **Supprimer** | — | Absent du SQL |
| `updated_at: Date` | **Supprimer** | — | Absent du SQL |
| *(absent)* | Ajouter | `idProposition: Integer` | RG3-03/04 : tracabilité de l'embauche d'origine (nullable — employés hors recrutement) |
| *(absent)* | Ajouter | `idManager: Integer` | Hiérarchie : référence un autre Employe (auto-référence, nullable) |
| *(absent)* | Ajouter | `numeroSecuriteSociale: String` | Présent dans le SQL |
| *(absent)* | Ajouter | `compteBancaire: String` | RIB/IBAN, optionnel |

**Résultat attendu (champs) :**
```
id, matricule, idPersonne, idProposition, idManager,
dateEmbauche, compteBancaire, numeroSecuriteSociale, actif
```

---

### 4. Réécrire `PropositionEmbauche.java`

C'est la réécriture la plus importante. L'ancienne version n'avait ni le bon
statut ni les bons champs financiers/temporels.

**Champs à ajouter / corriger / supprimer :**

| Champ actuel | Action | Champ corrigé | Raison |
|---|---|---|---|
| `statutId: int` | **Remplacer** | `statut: StatutProposition` | Enum typé |
| `dateEnvoi: Date` | **Renommer** | `dateEmission: Date` | Alignement SQL |
| `dateExpiration: Date` | **Supprimer** | — | Absent du SQL (c'est un statut EXPIREE) |
| `created_at: Date` | **Supprimer** | — | Absent du SQL |
| `updated_at: Date` | **Supprimer** | — | Absent du SQL |
| *(absent)* | Ajouter | `salaireMensuel: int` | Salaire proposé au candidat — NOT NULL |
| *(absent)* | Ajouter | `dateDebutPrevue: Date` | Date de prise de poste prévue — NOT NULL |
| *(absent)* | Ajouter | `dateReponse: Date` | Date à laquelle le candidat répond (nullable) |

**Résultat attendu (champs) :**
```
id, idCandidature, salaireMensuel, dateDebutPrevue,
dateEmission, dateReponse, statut
```

**Contrainte métier à documenter dans le Javadoc :**
- `dateReponse` est obligatoire si `statut` est `ACCEPTEE` ou `REFUSEE`.
- `dateDebutPrevue >= dateEmission`.

---

## Récapitulatif des fichiers à toucher

```
src/main/java/SI/RH/entity/
├── enums/
│   └── StatutProposition.java    ← À CRÉER
├── Personne.java                 ← À METTRE À JOUR
├── Employe.java                  ← À METTRE À JOUR
└── PropositionEmbauche.java      ← À RÉÉCRIRE
```

## Références utiles

- Schéma SQL complet : [`sirh_conception/modele_sirh.sql`](../sirh_conception/modele_sirh.sql) — chercher les blocs `BLOC 0` et `BLOC 3`
- Règles de gestion : [`doc/conception_sirh.md`](conception_sirh.md) — section 6 (Bloc 3) et section 7 (Bloc 4)
- Style des entités Bloc 1 à imiter : `src/main/java/SI/RH/entity/Departement.java`
