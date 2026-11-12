# Guide des requêtes cURL — Test de tous les Blocs SIRH (1 à 7)

Ce guide fournit les commandes `curl` prêtes à l'emploi pour tester l'ensemble des modules du SIRH via HTTP Basic Authentication.

---

## 🔑 Comptes de test disponibles

| Utilisateur | Mot de passe | Rôle principal |
|---|---|---|
| `rh` | `password` | `RH` (gestion contrats, offres, onboarding, congés, pointage) |
| `manager` | `password` | `MANAGER` (soumission de demandes, validation congés) |
| `direction` | `password` | `DIRECTION` (validation recrutement, direction) |
| `candidat` | `password` | `CANDIDAT` (acceptation / refus proposition) |
| `admin` | `password` | `ADMIN` (tous les droits) |

---

## Exécuter le script complet

Un script automatique est fourni à la racine :
```bash
./test_api_curls.sh
```

---

## Commandes cURL par Bloc

### Bloc 1 — Expression du besoin en personnel

```bash
# 1. Lister les départements
curl -u rh:password http://localhost:8080/api/departements

# 2. Lister les postes
curl -u rh:password http://localhost:8080/api/postes

# 3. Lister les demandes de recrutement
curl -u rh:password http://localhost:8080/api/demandes

# 4. Soumettre une nouvelle demande (Manager)
curl -u manager:password -X POST http://localhost:8080/api/demandes \
     -H "Content-Type: application/json" \
     -d '{
       "posteId": 1,
       "motif": "CREATION",
       "justification": "Renfort équipe projet",
       "effectifDemande": 1,
       "budgetPrevu": 2500000,
       "dateSouhaitee": "2026-12-01",
       "employeDemandeurId": 1
     }'
```

---

### Bloc 2 — Recrutement (Offres & Candidatures)

```bash
# 1. Lister les offres publiées
curl -u rh:password http://localhost:8080/api/offres

# 2. Obtenir les candidatures d'une offre
curl -u rh:password http://localhost:8080/api/candidatures/offre/1

# 3. Obtenir les détails d'un entretien
curl -u rh:password http://localhost:8080/api/entretiens/1
```

---

### Bloc 3 — Proposition d'embauche & Salarié

```bash
# 1. Consulter une proposition d'embauche
curl -u rh:password http://localhost:8080/api/propositions-embauche/1

# 2. Accepter une proposition (Candidat)
curl -u candidat:password -X PUT http://localhost:8080/api/propositions-embauche/1/accepter \
     -H "Content-Type: application/json" \
     -d '{"dateReponse": "2026-11-12", "commentaire": "J accepte avec grand plaisir"}'

# 3. Lister les salariés
curl -u rh:password http://localhost:8080/api/employes
```

---

### Bloc 4 — Contrats & Onboarding

```bash
# 1. Historique des contrats d'un employé
curl -u rh:password "http://localhost:8080/api/contrats?employeId=1"

# 2. Consulter le parcours d'onboarding d'un employé
curl -u rh:password http://localhost:8080/api/onboardings/employe/3

# 3. Lister les tâches d'un parcours d'onboarding
curl -u rh:password http://localhost:8080/api/onboardings/3/taches

# 4. Valider une tâche d'onboarding (passe à FAIT)
curl -u rh:password -X PUT http://localhost:8080/api/onboardings/taches/4/valider

# 5. Consulter les documents administratifs d'un salarié
curl -u rh:password http://localhost:8080/api/documents/employe/1

# 6. Ajouter un document administratif
curl -u rh:password -X POST http://localhost:8080/api/documents \
     -H "Content-Type: application/json" \
     -d '{
       "employeId": 1,
       "typeDocument": "JUSTIFICATIF",
       "fichierUrl": "/uploads/justificatifs/rib-rakoto.pdf"
     }'
```

---

### Bloc 5 — Dossier administratif du salarié & Journal d'audit

```bash
# 1. Consulter le dossier complet d'un salarié (avec traçabilité d'audit)
curl -u rh:password "http://localhost:8080/api/dossier-salarie/1?acteurId=2"

# 2. Consulter l'historique d'audit d'un acteur
curl -u rh:password http://localhost:8080/api/audit/acteur/1

# 3. Consulter les logs d'audit sur une table / enregistrement précis
curl -u rh:password "http://localhost:8080/api/audit/enregistrement?tableCible=employes&enregistrementId=1"
```

---

### Bloc 6 — Temps de travail & Pointage

```bash
# 1. Enregistrer un pointage (ENTREE ou SORTIE)
curl -u rh:password -X POST http://localhost:8080/api/pointages \
     -H "Content-Type: application/json" \
     -d '{
       "employeId": 1,
       "direction": "ENTREE",
       "origine": "BADGE"
     }'

# 2. Consulter les pointages du jour d'un employé
curl -u rh:password http://localhost:8080/api/pointages/employe/1

# 3. Calculer le temps réellement travaillé sur une journée (en minutes)
curl -u rh:password "http://localhost:8080/api/pointages/employe/1/temps-travaille?date=2026-11-12"

# 4. Consulter les anomalies détectées (retards, pointages orphelins...)
curl -u rh:password http://localhost:8080/api/pointages/employe/2/anomalies

# 5. Assigner un horaire hebdomadaire à un employé
curl -u rh:password -X POST http://localhost:8080/api/plannings \
     -H "Content-Type: application/json" \
     -d '{
       "employeId": 1,
       "horaireId": 1,
       "dateSemaine": "2026-11-16",
       "regimeId": 1
     }'
```

---

### Bloc 7 — Congés & Absences

```bash
# 1. Consulter les soldes de congés d'un employé
curl -u rh:password http://localhost:8080/api/conges/soldes/employe/1

# 2. Soumettre une demande de congé (contrôle automatique solde et non-chevauchement)
curl -u rh:password -X POST http://localhost:8080/api/conges \
     -H "Content-Type: application/json" \
     -d '{
       "employeId": 1,
       "typeCongeId": 2,
       "dateDebut": "2026-11-18",
       "dateFin": "2026-11-19",
       "commentaire": "Récupération RTT"
     }'

# 3. Valider par le Manager
curl -u manager:password -X PUT http://localhost:8080/api/conges/2/valider-manager \
     -H "Content-Type: application/json" \
     -d '{"acteurId": 1}'

# 4. Valider par les RH (débite le solde et crée automatiquement l'absence correspondante)
curl -u rh:password -X PUT http://localhost:8080/api/conges/2/valider-rh \
     -H "Content-Type: application/json" \
     -d '{"acteurId": 2}'

# 5. Consulter les absences enregistrées pour un salarié
curl -u rh:password http://localhost:8080/api/absences/employe/1
```
