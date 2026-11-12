#!/usr/bin/env bash
# ==============================================================================
# SIRH - Suite de tests cURL pour l'ensemble des blocs (1 à 7)
# Prérequis : l'application Spring Boot doit tourner sur http://localhost:8080
# Utilisateurs de test :
#   rh:password        (Rôle RH)
#   manager:password   (Rôle MANAGER)
#   direction:password (Rôle DIRECTION)
#   admin:password     (Rôle ADMIN)
#   candidat:password  (Rôle CANDIDAT)
# ==============================================================================

BASE_URL="http://localhost:8081"

echo "=========================================="
echo " 1. TEST BLOC 1 : Besoin en personnel"
echo "=========================================="

echo "-> Liste des départements :"
curl -s -u rh:password "$BASE_URL/api/departements" | jq . || curl -s -u rh:password "$BASE_URL/api/departements"
echo -e "\n"

echo "-> Liste des postes :"
curl -s -u rh:password "$BASE_URL/api/postes" | jq . || curl -s -u rh:password "$BASE_URL/api/postes"
echo -e "\n"

echo "-> Liste des demandes de recrutement :"
curl -s -u rh:password "$BASE_URL/api/demandes" | jq . || curl -s -u rh:password "$BASE_URL/api/demandes"
echo -e "\n"

echo "=========================================="
echo " 2. TEST BLOC 2 : Recrutement (Offres & Candidatures)"
echo "=========================================="

echo "-> Liste des offres d'emploi :"
curl -s -u rh:password "$BASE_URL/api/offres" | jq . || curl -s -u rh:password "$BASE_URL/api/offres"
echo -e "\n"

echo "-> Candidatures pour l'offre 1 :"
echo "-> Liste de toutes les candidatures :"
curl -s -u rh:password "$BASE_URL/api/candidatures" | jq . || curl -s -u rh:password "$BASE_URL/api/candidatures"
echo -e "\n"

echo "=========================================="
echo " 3. TEST BLOC 3 : Proposition d'embauche & Salarié"
echo "=========================================="

echo "-> Proposition d'embauche pour la candidature 1 :"
curl -s -u rh:password "$BASE_URL/api/propositions-embauche/1" | jq . || curl -s -u rh:password "$BASE_URL/api/propositions-embauche/1"
echo -e "\n"

echo "-> Salarié (ID 1) :"
curl -s -u rh:password "$BASE_URL/api/employes/1" | jq . || curl -s -u rh:password "$BASE_URL/api/employes/1"
echo -e "\n"

echo "=========================================="
echo " 4. TEST BLOC 4 : Contrats & Onboarding"
echo "=========================================="

echo "-> Historique des contrats de l'employé 1 :"
curl -s -u rh:password "$BASE_URL/api/contrats?employeId=1" | jq . || curl -s -u rh:password "$BASE_URL/api/contrats?employeId=1"
echo -e "\n"

echo "-> Parcours d'onboarding de l'employé 3 :"
curl -s -u rh:password "$BASE_URL/api/onboardings/employe/3" | jq . || curl -s -u rh:password "$BASE_URL/api/onboardings/employe/3"
echo -e "\n"

echo "-> Tâches d'onboarding du parcours 3 :"
curl -s -u rh:password "$BASE_URL/api/onboardings/3/taches" | jq . || curl -s -u rh:password "$BASE_URL/api/onboardings/3/taches"
echo -e "\n"

echo "-> Documents administratifs de l'employé 1 :"
curl -s -u rh:password "$BASE_URL/api/documents/employe/1" | jq . || curl -s -u rh:password "$BASE_URL/api/documents/employe/1"
echo -e "\n"

echo "=========================================="
echo " 5. TEST BLOC 5 : Dossier administratif central & Audit"
echo "=========================================="

echo "-> Dossier complet du salarié 1 (avec acteurId=2 pour audit de consultation) :"
curl -s -u rh:password "$BASE_URL/api/dossier-salarie/1?acteurId=2" | jq . || curl -s -u rh:password "$BASE_URL/api/dossier-salarie/1?acteurId=2"
echo -e "\n"

echo "-> Journal d'audit pour la table 'employes' et enregistrement 1 :"
curl -s -u rh:password "$BASE_URL/api/audit/enregistrement?tableCible=employes&enregistrementId=1" | jq . || curl -s -u rh:password "$BASE_URL/api/audit/enregistrement?tableCible=employes&enregistrementId=1"
echo -e "\n"

echo "=========================================="
echo " 6. TEST BLOC 6 : Temps de travail & Pointage"
echo "=========================================="

echo "-> Pointages de l'employé 1 aujourd'hui :"
curl -s -u rh:password "$BASE_URL/api/pointages/employe/1" | jq . || curl -s -u rh:password "$BASE_URL/api/pointages/employe/1"
echo -e "\n"

echo "-> Temps travaillé en minutes pour l'employé 1 (2026-11-12) :"
curl -s -u rh:password "$BASE_URL/api/pointages/employe/1/temps-travaille?date=2026-11-12"
echo -e "\n"

echo "-> Anomalies de pointage pour l'employé 2 :"
curl -s -u rh:password "$BASE_URL/api/pointages/employe/2/anomalies" | jq . || curl -s -u rh:password "$BASE_URL/api/pointages/employe/2/anomalies"
echo -e "\n"

echo "-> Nouveau pointage d'ENTREE pour l'employé 1 :"
curl -s -u rh:password -X POST "$BASE_URL/api/pointages" \
     -H "Content-Type: application/json" \
     -d '{"employeId": 1, "direction": "ENTREE", "origine": "BADGE"}' | jq . || true
echo -e "\n"

echo "=========================================="
echo " 7. TEST BLOC 7 : Congés & Absences"
echo "=========================================="

echo "-> Soldes de congés de l'employé 1 :"
curl -s -u rh:password "$BASE_URL/api/conges/soldes/employe/1" | jq . || curl -s -u rh:password "$BASE_URL/api/conges/soldes/employe/1"
echo -e "\n"

echo "-> Demandes de congés de l'employé 1 :"
curl -s -u rh:password "$BASE_URL/api/conges/employe/1" | jq . || curl -s -u rh:password "$BASE_URL/api/conges/employe/1"
echo -e "\n"

echo "-> Absences enregistrées pour l'employé 1 :"
curl -s -u rh:password "$BASE_URL/api/absences/employe/1" | jq . || curl -s -u rh:password "$BASE_URL/api/absences/employe/1"
echo -e "\n"

echo "-> Soumission d'une nouvelle demande de congé (employe 1, type 2=RTT, 2 jours) :"
curl -s -u rh:password -X POST "$BASE_URL/api/conges" \
     -H "Content-Type: application/json" \
     -d '{"employeId": 1, "typeCongeId": 2, "dateDebut": "2026-11-18", "dateFin": "2026-11-19", "commentaire": "RTT test curl"}' | jq . || true
echo -e "\n"

echo "=========================================="
echo " Tests cURL terminés avec succès !"
echo "=========================================="
