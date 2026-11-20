# Guide de démonstration et flux de test — Blocs 8 à 12

Ce document présente un scénario court pour expliquer le fonctionnement du SIRH au professeur et vérifier les fonctionnalités principales.

## 1. Préparation

1. Démarrer PostgreSQL avec la base `sirh`.
2. Vérifier la configuration dans `src/main/resources/application.properties`.
3. Démarrer l'application avec Java 21 :

```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 \
PATH="$JAVA_HOME/bin:$PATH" \
mvn spring-boot:run
```

4. Ouvrir `http://localhost:8080`.
5. Se connecter avec un compte de test, par exemple `rh / password`.

Les scripts SQL utiles sont :

- `sql/base_complete.sql` : structure de la base ;
- `sql/donnees_test_blocs8_a_12.sql` : salariés, postes et contrats de démonstration.

## 2. Scénario global à présenter

Le parcours recommandé est :

```text
Salarié et contrat
        ↓
Période de paie
        ↓
Bulletin de paie + heures supplémentaires
        ↓
État de paie Excel
        ↓
Évaluation et formation
        ↓
Carrière et mobilité
        ↓
Incident → procédure → sanction
        ↓
Rupture → solde de tout compte → sortie
```

L'application est organisée autour d'un dossier salarié. Les blocs utilisent les mêmes identifiants de salariés, contrats, postes et départements.

## 3. Bloc 8 — Paie

### Démonstration dans l'interface

1. Ouvrir le menu **Paie**.
2. Créer ou ouvrir une période, par exemple **novembre 2026**.
3. Cliquer sur **Générer les bulletins**.
4. Ouvrir le détail d'un salarié.
5. Vérifier :
   - salaire de base ;
   - salaire brut ;
   - primes et absences ;
   - heures supplémentaires ;
   - CNaPS et retenue sanitaire ;
   - tranches IRSA ;
   - total des retenues ;
   - net à payer.
6. Modifier les heures supplémentaires d'une semaine et vérifier le recalcul.
7. Cliquer sur **PDF** pour télécharger le bulletin individuel.
8. Cliquer sur **Exporter État de Paie (Excel)**.

### Explication courte

Le moteur part du contrat actif et du salaire brut. Il ajoute les primes, rappels, indemnités et heures supplémentaires, puis déduit les absences et les retenues. Les lignes détaillées sont conservées dans le bulletin afin de garder une trace du calcul.

### Endpoints principaux

```text
POST /api/paie/periodes
POST /api/paie/periodes/{id}/generer
GET  /api/paie/periodes/{id}/bulletins
GET  /api/paie/bulletins/{id}
PUT  /api/paie/bulletins/{id}/hs
GET  /api/paie/bulletins/{id}/pdf
GET  /api/paie/periodes/{id}/excel
```

## 4. Bloc 9 — Évaluation, compétences et formation

1. Ouvrir **Évaluation & formation**.
2. Créer une compétence.
3. Créer un objectif pour un salarié.
4. Enregistrer une évaluation avec une note et des commentaires.
5. Planifier une formation.
6. Vérifier que les enregistrements apparaissent dans les listes.

### Explication courte

Ce bloc permet de comparer les compétences attendues et acquises, de suivre les objectifs et d'identifier les besoins de formation. L'évaluation peut donc déclencher une action de formation.

### Endpoints principaux

```text
GET/POST /api/competences
GET/POST /api/employe-competences
GET/POST /api/objectifs-evaluation
GET/POST /api/evaluations-rh
GET/POST /api/formations
```

## 5. Bloc 10 — Carrière et mobilité

1. Ouvrir **Carrière & mobilité**.
2. Ajouter une ligne d'historique d'affectation.
3. Enregistrer une promotion avec ancien et nouveau poste.
4. Enregistrer une mutation entre deux départements.
5. Vérifier l'historique des salaires.

### Explication courte

Les changements ne remplacent pas l'historique. Chaque promotion, mutation ou évolution salariale est enregistrée avec une date et un motif afin de reconstituer la carrière du salarié.

### Endpoints principaux

```text
GET/POST /api/carrieres
GET/POST /api/promotions
GET/POST /api/mutations
GET/POST /api/historiques-salaires
```

## 6. Bloc 11 — Discipline

1. Ouvrir **Discipline**.
2. Déclarer un incident pour un salarié.
3. Créer une procédure avec une demande d'explication.
4. Enregistrer une sanction éventuelle.
5. Vérifier les listes d'incidents, procédures et sanctions.

### Explication courte

Le workflow disciplinaire suit l'ordre :

```text
Incident → demande d'explication → réponse du salarié
         → décision → sanction éventuelle
```

Cette séparation permet de conserver les justificatifs et l'historique de la décision.

### Endpoints principaux

```text
GET/POST /api/incidents-disciplinaires
GET/POST /api/procedures-disciplinaires
GET/POST /api/sanctions-disciplinaires
```

## 7. Bloc 12 — Rupture et offboarding

1. Ouvrir **Rupture & sortie**.
2. Créer une rupture en indiquant le salarié, le contrat, le motif et la date de sortie prévue.
3. Indiquer les jours de congés restants et le préavis.
4. Appeler ou tester `GET /api/ruptures/{id}/solde`.
5. Vérifier le solde de tout compte :
   - dernier salaire ;
   - indemnité de congés ;
   - indemnité de préavis ;
   - indemnité de licenciement si le motif est un licenciement ;
   - total brut ;
   - retenues ;
   - net du solde.

### Explication courte

Le bloc accompagne la sortie du salarié. Il conserve le motif et les dates de rupture, puis calcule les éléments financiers de fin de contrat avant l'archivage du dossier.

### Endpoints principaux

```text
GET/POST /api/ruptures
GET      /api/ruptures/{id}/solde
GET      /api/soldes-tout-compte
```

## 8. Vérifications rapides pour la présentation

| Vérification | Résultat attendu |
|---|---|
| Création d'une période de paie | La période apparaît avec son code et son statut |
| Génération de paie | Un bulletin est créé pour chaque salarié actif ayant un contrat |
| Modification des heures supplémentaires | Le brut, les retenues et le net sont recalculés |
| Export PDF | Un bulletin individuel lisible est téléchargé |
| Export Excel | Un état de paie et une feuille de calcul HS sont présents |
| Évaluation | L'objectif, la note et le besoin de formation sont enregistrés |
| Carrière | L'historique est conservé sans écraser les anciennes données |
| Discipline | Les trois étapes incident/procédure/sanction sont consultables |
| Rupture | Le solde de tout compte est calculé et consultable |

## 9. Phrase de conclusion à dire

> Notre SIRH centralise le dossier du salarié et couvre son cycle de vie : calcul de la paie, suivi des compétences, évolution de carrière, gestion disciplinaire et sortie. Chaque bloc conserve ses données et les relie au salarié, au contrat et à l'organisation.

