# Normes de Conception & Développement du Projet SIRH

Ce document résume les règles et conventions collectives arrêtées pour le projet.

---

## 1. Base de données PostgreSQL

* **Nom de la base** : `sirh` (`jdbc:postgresql://localhost:5432/sirh`).
* **Clé primaire (PK)** : Toujours nommée simplement **`id`** (`SERIAL PRIMARY KEY`).
* **Clé étrangère (FK)** : Toujours nommée **`(table)Id`** en camelCase (ex: `departementId`, `posteId`, `demandeRecrutementId`).
* **Rôles multiples vers la même table** : `(table)RôleId` (ex: `employeDemandeurId`, `employeRemplaceId`, `employeValideurId`, `departementParentId`).
* **Pas de guillemets** : Ne jamais mettre de guillemets autour des identifiants dans les scripts SQL pour préserver l'insensibilité à la casse standard de PostgreSQL (`departementid` en interne).
* **Modularité par bloc** : Chaque bloc possède son propre script SQL autonome (ex: `sirh_conception/bloc1.sql`). Les clés étrangères vers les tables des autres blocs sont ajoutées via des `ALTER TABLE` lors de la fusion du mercredi.
* **Types énumérés en base** : Stockés en `VARCHAR` avec contrainte `CHECK (...)`. L'énumération Java reste la source de vérité unique.

---

## 2. Développement Java & Spring Boot

* **Architecture** : MVC orienté service :
  ```
  controller (REST, mince) → service (métier, @Transactional) → repository (Spring Data) → entity (JPA)
  ```
* **Annotations Spring Boot & JPA** :
  * `@Entity` et `@Table(name = "...")` sur chaque entité.
  * `@Id` et `@GeneratedValue(strategy = GenerationType.IDENTITY)` sur les clés primaires.
  * `@Enumerated(EnumType.STRING)` sur les énumérations.
  * `@PrePersist` pour initialiser les horodatages automatiques.
* **Stratégie de nommage Hibernate** :
  * `PhysicalNamingStrategyStandardImpl` configurée dans `application.properties` pour respecter fidèlement les noms écrits (`departementId`, `demandeRecrutement`).
* **Types temporels** : Utilisation exclusive de l'API `java.time` (`LocalDate`, `LocalDateTime`). Jamais de `java.util.Date`.
* **Énumérations** : Les `enum` portent également la logique métier de machine à états et de transition (ex: `StatutDemande.peutPasserA(...)`, `NiveauValidation.pourStatut(...)`).
* **Contrôleurs REST** : Reçoivent et retournent des `record` DTO avec validation `@Valid`. Les entités internes ne sont jamais exposées directement par l'API.

---

## 3. Git et Configuration

* **Fichier `application.properties`** : Strictement ignoré par Git (`.gitignore`).
* **Template commité** : Seul `src/main/resources/application.properties.example` est versionné. Chaque développeur le copie localement et adapte ses identifiants.
* **Commits réguliers** : Commits fréquents par tâche et entraide active au sein de l'équipe.
