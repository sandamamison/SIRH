# SIRH

## Pages de test HTML/JS

Une interface de test du Bloc 3 est disponible dans
`src/main/resources/static/`. Elle permet de lister et créer des propositions
d'embauche, consulter leur historique, tester les actions d'acceptation,
refus ou expiration, et rechercher un employé.

Pour l'utiliser :

```bash
./mvnw spring-boot:run
```

Puis ouvrir <http://localhost:8080/> dans un navigateur. La page appelle les
endpoints `/api/propositions-embauche` et `/api/employes` sur la même origine.
La base PostgreSQL configurée dans `src/main/resources/application.properties`
doit être accessible.
# SIRH - Système d'Information des Ressources Humaines

Projet de gestion RH modulaire développé avec **Spring Boot 4**, **Java 21** et **PostgreSQL**.

---

## 1. Prérequis

* **Java 21+** (JDK 21)
* **Maven 3.9+** (ou `mvnw`)
* **PostgreSQL 16+**

---

## 2. Configuration & Démarrage

### A. Initialiser la base de données

Dans un terminal PostgreSQL (`psql`) :

```sql
CREATE DATABASE sirh;
```

Appliquer le schéma complet utilisé par l'application. **Cette commande supprime
et recrée les 17 tables du périmètre applicatif** :

```bash
psql -U postgres -d sirh -f sql/base_complete.sql
```

Le script ne modifie pas les éventuelles tables qui ne font pas partie de ce
périmètre. Les anciens scripts de données de test par bloc ne sont pas inclus
dans cette réinitialisation complète.

Charger ensuite les comptes de démonstration et leurs rôles :

```bash
psql -U postgres -d sirh -f sql/donnees_users.sql
```

Pour charger un scénario complet de démonstration avec départements, postes,
demandes, offres, candidatures, entretiens, propositions, contrats et
affectations :

```bash
psql -U postgres -d sirh -f sql/donnees_test_completes.sql
```

Ce dernier script est réservé aux tests et réinitialise les tables applicatives.

Les comptes utilisent temporairement le mot de passe `password` :
`admin`, `direction`, `rh`, `manager` et `candidat`. Ces mots de passe doivent
être remplacés avant toute utilisation réelle.

L'application utilise une page de connexion et une session sécurisée. Les rôles sont contrôlés par endpoint : la direction
et l'administrateur ont un accès global, le RH gère le recrutement et les
contrats, le manager crée et soumet des demandes, et le candidat répond aux
propositions d'embauche.

### B. Configurer l'application locale

Copier le modèle de configuration (non suivi par Git) :

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Modifier `src/main/resources/application.properties` avec vos identifiants PostgreSQL locaux (`spring.datasource.password`).

### C. Lancer les tests et l'application

Exécuter la suite de tests unitaires et d'intégration :

```bash
mvn test
```

Lancer l'application Spring Boot :

```bash
mvn spring-boot:run
```

L'API est accessible sur `http://localhost:8080/api/`.

---

## 3. Endpoints de l'API Bloc 1 (Besoin en personnel)

| Méthode | Route | Rôle |
|---|---|---|
| `GET` | `/api/departements` | Liste des départements |
| `POST` | `/api/departements` | Créer un département |
| `GET` | `/api/postes` | Liste des postes (filtrable par `?departementId=`) |
| `POST` | `/api/postes` | Créer un poste |
| `POST` | `/api/demandes` | Créer une demande de recrutement (`BROUILLON`) |
| `PUT` | `/api/demandes/{id}` | Modifier une demande (`BROUILLON` uniquement) |
| `GET` | `/api/demandes` | Liste des demandes (filtrable par `?statut=` et `?demandeurId=`) |
| `GET` | `/api/demandes/{id}` | Suivi d'état complet (détail + validations + historique) |
| `POST` | `/api/demandes/{id}/soumettre` | Le manager soumet la demande (`SOUMISE`) |
| `POST` | `/api/demandes/{id}/valider` | Validation RH (`VALIDEE_RH`) ou Direction (`VALIDEE_DIRECTION`) |
| `POST` | `/api/demandes/{id}/refuser` | Refus motivé avec commentaire obligatoire (`REJETEE`) |

---

## 4. Documentation & Normes

* Normes de l'équipe (PK, FK, conventions de nommage) : [`doc/normes.md`](doc/normes.md)
* Règles de conception BDD : [`doc/regles_conception_bdd.md`](doc/regles_conception_bdd.md)
* Suivi des tâches par bloc : [`doc/todo_sirh.md`](doc/todo_sirh.md)
