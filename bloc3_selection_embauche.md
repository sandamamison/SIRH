# Bloc 3 — Sélection et embauche

Ce document détaille le travail à réaliser pour le bloc 3 du SIRH. Le bloc
commence lorsqu'une candidature est déclarée **retenue** et se termine lorsque
le candidat devient un salarié avec un dossier, un matricule et les éléments
nécessaires à la création de son contrat et de son affectation.

## 1. Objectif fonctionnel

Permettre à un utilisateur habilité, généralement un membre des RH, de :

1. sélectionner une candidature retenue ;
2. envoyer une proposition d'embauche au candidat ;
3. suivre la réponse du candidat ;
4. transformer l'acceptation en dossier salarié ;
5. attribuer un matricule unique ;
6. déclencher la création du contrat et de l'affectation ;
7. conserver l'historique de l'opération.

Le résultat attendu est un salarié correctement rattaché à la candidature
retenue, sans perdre les informations ni l'historique de son parcours de
recrutement.

## 2. Périmètre et dépendances

### Entrée du bloc 3

Le bloc 2 doit fournir :

- une `OffreEmploi` existante ;
- une `Candidature` liée à cette offre ;
- un candidat identifié par une `Personne` ;
- une décision finale indiquant que la candidature est retenue.

Une proposition ne doit pas être créée à partir d'une candidature refusée,
annulée ou encore en cours d'évaluation.

### Sortie du bloc 3

En cas d'acceptation, le système doit fournir :

- un `Employé` ;
- un matricule unique ;
- un dossier salarié initialisé à partir des données de la personne ;
- un contrat à créer ou à transmettre au bloc 4 ;
- une affectation à créer ou à transmettre au bloc 4 ;
- un historique des changements de statut.

Le bloc 4 reste responsable du détail du cycle de vie des contrats et des
affectations : types de contrat, renouvellements, périodes de validité et
contrôle des chevauchements. Le bloc 3 déclenche leur création au moment de
l'embauche et transmet les données nécessaires.

## 3. Règle de conception principale

> **CANDIDAT ≠ EMPLOYÉ**

Une personne peut déposer plusieurs candidatures avant d'être embauchée. Il ne
faut donc pas remplacer la candidature par un employé, ni supprimer la
candidature après l'embauche.

Le modèle recommandé est :

```text
Personne 1 ──── * Candidature
Personne 1 ──── 0..1 Employé
Candidature 1 ──── 0..1 PropositionEmbauche
Candidature retenue ────> Employé créé après acceptation
Employé 1 ──── * Contrat
Employé 1 ──── * Affectation
```

Une même `Personne` ne doit pas recevoir un second dossier salarié si elle est
déjà employée. Dans ce cas, l'application doit signaler la situation au lieu
de créer silencieusement un doublon.

## 4. Entités et données à prévoir

Les noms exacts peuvent être adaptés aux conventions du projet, mais les
responsabilités suivantes doivent être conservées.

### `Personne`

Cette entité représente l'identité d'une personne, indépendamment de sa
candidature ou de son statut de salarié.

Champs minimaux :

| Champ | Rôle |
|---|---|
| `id` | Identifiant technique |
| `nom` | Nom de famille |
| `prenom` | Prénom |
| `email` | Adresse de contact |
| `telephone` | Numéro de téléphone |
| `adresse` | Adresse, si nécessaire au dossier |
| `createdAt` / `updatedAt` | Traçabilité |

L'email ne doit pas être utilisé comme identifiant technique. Il peut être
contrôlé comme donnée de contact, mais une personne doit être identifiée par
son `id`.

### `PropositionEmbauche`

Cette entité matérialise la proposition envoyée au candidat.

Champs minimaux :

| Champ | Rôle |
|---|---|
| `id` | Identifiant de la proposition |
| `candidatureId` | Candidature retenue concernée |
| `statut` | `ENVOYEE`, `ACCEPTEE`, `REFUSEE` ou `EXPIREE` |
| `dateEnvoi` | Date et heure d'envoi |
| `dateReponse` | Date et heure de la réponse |
| `dateExpiration` | Date limite de réponse, si utilisée |
| `commentaire` | Remarque RH ou motif de refus |
| `createdBy` | Utilisateur ayant créé la proposition |
| `createdAt` / `updatedAt` | Traçabilité |

Une candidature retenue ne doit avoir qu'une proposition active. Une nouvelle
proposition doit être explicitement autorisée comme remplacement ou relance,
et ne doit pas écraser l'historique.

### `Employé`

Cette entité représente la personne après son embauche.

Champs minimaux :

| Champ | Rôle |
|---|---|
| `id` | Identifiant technique |
| `personneId` | Lien vers l'identité conservée |
| `matricule` | Identifiant métier unique |
| `dateEmbauche` | Date effective d'embauche |
| `statut` | Statut courant du salarié |
| `createdAt` / `updatedAt` | Traçabilité |

Le matricule doit être généré côté serveur et protégé par une contrainte
d'unicité en base de données. Il ne doit pas être calculé uniquement dans
l'interface, car deux embauches simultanées pourraient obtenir le même numéro.

### Liens avec les entités existantes

- `Candidature.personneId` doit pointer vers `Personne`.
- `PropositionEmbauche.candidatureId` doit pointer vers la candidature retenue.
- `Employé.personneId` doit pointer vers la même `Personne`.
- `Contrat.employeId` et `Affectation.employeId` doivent pointer vers
  `Employé`.

## 5. Workflow de la proposition

Le cycle nominal est :

```text
CANDIDATURE RETENUE
        │
        ▼
     ENVOYEE ───────► EXPIREE
        │
        ├────────────► REFUSEE
        │
        ▼
    ACCEPTEE
        │
        ▼
EMPLOYÉ + MATRICULE + CONTRAT + AFFECTATION
```

Transitions autorisées :

| Statut courant | Action | Nouveau statut |
|---|---|---|
| aucun | envoyer une proposition | `ENVOYEE` |
| `ENVOYEE` | candidat accepte | `ACCEPTEE` |
| `ENVOYEE` | candidat refuse | `REFUSEE` |
| `ENVOYEE` | date limite dépassée | `EXPIREE` |

Une proposition `ACCEPTEE`, `REFUSEE` ou `EXPIREE` est finale. Il ne faut pas
la repasser directement à `ENVOYEE`. Une nouvelle proposition, si elle est
autorisée par le métier, doit être un nouvel enregistrement.

Chaque transition doit conserver au minimum l'auteur, la date et un
commentaire facultatif. Une table ou un mécanisme d'historique peut être
partagé avec les autres blocs.

## 6. Traitement de l'acceptation

L'acceptation doit être exécutée comme une opération cohérente et répétable
sans créer de doublon :

1. vérifier que la proposition est bien `ENVOYEE` ;
2. vérifier que la candidature est `RETENUE` ;
3. vérifier que la personne n'est pas déjà liée à un employé ;
4. passer la proposition à `ACCEPTEE` ;
5. créer l'`Employé` avec un matricule unique ;
6. initialiser le dossier salarié depuis `Personne` ;
7. créer le contrat initial, ou publier un événement/demande pour le bloc 4 ;
8. créer l'affectation initiale, ou publier un événement/demande pour le bloc 4 ;
9. enregistrer l'historique et retourner le résultat à l'interface.

Les étapes 4 à 8 doivent être réalisées dans une transaction. Si une étape
échoue, aucune acceptation partielle ne doit rester en base. L'opération doit
également être idempotente : un double clic ou une nouvelle requête sur une
proposition déjà acceptée doit retourner l'employé existant ou une erreur
fonctionnelle explicite, sans créer un deuxième employé.

## 7. Contrôles métier

Les règles à implémenter sont les suivantes :

- **RG3-01** : une proposition est liée à une candidature retenue ;
- **RG3-02** : les statuts sont `ENVOYEE`, `ACCEPTEE`, `REFUSEE` et `EXPIREE` ;
- **RG3-03** : l'acceptation crée un `Employé` avec un matricule unique ;
- **RG3-04** : les données de `Personne` sont reprises sans supprimer le
  candidat ni sa candidature ;
- **RG3-05** : le contrat et l'affectation sont créés à la suite de
  l'embauche ;
- une proposition ne peut pas être envoyée sans candidature retenue ;
- les champs obligatoires de la personne et de la proposition doivent être
  validés avant l'envoi ;
- une proposition expirée ne peut pas être acceptée ;
- un employé déjà créé ne doit pas être recréé.

Les erreurs doivent être compréhensibles pour l'utilisateur, par exemple :

- « Cette candidature n'est pas retenue. »
- « Cette proposition a déjà été traitée. »
- « Cette personne possède déjà un dossier salarié. »
- « Impossible de créer le matricule : veuillez réessayer. »

## 8. Écrans à réaliser

### 8.1 Envoi d'une proposition

Afficher :

- les informations de la personne ;
- le poste et l'offre concernés ;
- la candidature et sa décision ;
- la date d'expiration éventuelle ;
- le commentaire ou les conditions de la proposition ;
- un bouton **Envoyer la proposition**.

Avant confirmation, afficher un récapitulatif. Après l'envoi, afficher le
statut `ENVOYEE`, la date d'envoi et l'utilisateur à l'origine de l'action.

### 8.2 Suivi des propositions

Prévoir une liste filtrable par :

- statut ;
- offre ou poste ;
- période d'envoi ;
- nom, prénom ou email du candidat.

Chaque ligne doit permettre d'ouvrir le détail, de consulter l'historique et,
selon le statut, d'enregistrer une réponse ou de constater l'expiration.

### 8.3 Fiche salarié

Après acceptation, afficher :

- le matricule ;
- l'identité reprise depuis `Personne` ;
- la date d'embauche ;
- la candidature et l'offre d'origine ;
- le statut de création du contrat ;
- le statut de création de l'affectation ;
- l'historique de l'embauche.

Les données héritées de la candidature ne doivent pas être modifiées
silencieusement depuis cette fiche. Si une correction est nécessaire, elle
doit passer par la fiche `Personne` et laisser une trace.

## 9. Services et API attendus

Les noms ci-dessous sont indicatifs :

```text
POST   /api/propositions-embauche
GET    /api/propositions-embauche
GET    /api/propositions-embauche/{id}
POST   /api/propositions-embauche/{id}/accepter
POST   /api/propositions-embauche/{id}/refuser
POST   /api/propositions-embauche/{id}/expirer
GET    /api/employes/{id}
```

Le service métier doit vérifier les règles avant d'appeler les repositories.
Le contrôleur ne doit pas contenir la logique de génération du matricule ni
les règles de transition.

Les réponses API doivent distinguer :

- une requête invalide (`400`) ;
- une ressource absente (`404`) ;
- une transition interdite (`409`) ;
- un utilisateur non autorisé (`403`) ;
- une erreur interne (`500`), journalisée côté serveur.

## 10. Sécurité et droits

Au minimum :

- RH : créer, envoyer et traiter une proposition ;
- Direction : consulter et éventuellement valider selon le workflow retenu ;
- Manager : consulter les informations nécessaires, sans modifier le dossier
  salarié si ce n'est pas prévu ;
- Salarié/candidat : accès limité à ses propres informations si un portail est
  implémenté.

Les contrôles d'autorisation doivent être appliqués côté serveur, pas
uniquement en masquant des boutons dans l'interface.

## 11. Tests d'acceptation

Le bloc est considéré comme terminé lorsque les scénarios suivants passent :

1. une candidature retenue permet de créer et d'envoyer une proposition ;
2. une candidature non retenue est refusée par le service ;
3. une proposition `ENVOYEE` peut être acceptée ;
4. l'acceptation crée un seul employé avec un matricule unique ;
5. les données de la personne et la candidature restent consultables ;
6. le contrat et l'affectation initiale sont déclenchés ;
7. une proposition refusée ou expirée ne peut pas être acceptée ;
8. deux appels simultanés d'acceptation ne créent pas deux employés ;
9. une personne déjà employée provoque une erreur explicite ;
10. chaque changement de statut est historisé ;
11. un utilisateur sans droit ne peut pas envoyer ni accepter une proposition.

Les tests doivent couvrir le service métier, les contraintes de persistance et
les principaux endpoints. Le scénario nominal complet doit aussi être vérifié
avec les blocs 2 et 4.

## 12. Ordre conseillé de réalisation

1. Valider les liens entre `Personne`, `Candidature`, `PropositionEmbauche` et
   `Employé` avec l'équipe.
2. Créer les entités, les relations et les contraintes d'unicité.
3. Implémenter les statuts et l'historique des transitions.
4. Implémenter le service d'envoi de proposition.
5. Implémenter le service transactionnel d'acceptation et la génération du
   matricule.
6. Brancher la création ou le déclenchement du contrat et de l'affectation
   avec le bloc 4.
7. Ajouter les écrans de suivi et la fiche salarié.
8. Ajouter les contrôles d'accès et les messages d'erreur.
9. Écrire les tests d'acceptation et exécuter le parcours complet.

## Définition de terminé

Le bloc 3 est terminé lorsque le parcours suivant est fonctionnel et traçable :

```text
Candidature retenue
→ Proposition envoyée
→ Proposition acceptée
→ Employé créé avec matricule unique
→ Contrat et affectation déclenchés
→ Historique consultable
```
