const API = {
    offres: '/api/offres',
    candidatures: '/api/candidatures',
    entretiens: '/api/entretiens',
    evaluations: '/api/evaluations'
};

async function charger(url, tbodyId, messageId, type) {
    const tbody = document.getElementById(tbodyId);
    const message = document.getElementById(messageId);

    tbody.replaceChildren();
    message.textContent = 'Chargement...';

    try {
        const response = await fetch(url);

        if (!response.ok) {
            throw new Error(`Erreur HTTP ${response.status}`);
        }

        const donnees = await response.json();
        const liste = Array.isArray(donnees) ? donnees : donnees.content;

        if (!Array.isArray(liste)) {
            throw new Error('Format de réponse inattendu');
        }

        liste.forEach(element => {
            const tr = document.createElement('tr');

            let valeurs;

            if (type === 'offres') {
                valeurs = [
                    element.id,
                    element.intitulePoste ?? element.titre ?? element.poste?.intitule,
                    element.statut,
                    element.datePublication
                ];
            } else if (type === 'candidatures') {
                valeurs = [
                    element.id,
                    element.offreEmploi?.id ?? element.offreEmploiId ?? '—',
                    element.personne?.nom ?? element.personne?.id ?? '—',
                    element.statut
                ];
            } else if (type === 'entretiens') {
                valeurs = [
                    element.id,
                    element.candidature?.id ?? '—',
                    element.dateHeure,
                    element.statut
                ];
            } else {
                valeurs = [
                    element.id,
                    element.entretien?.id ?? '—',
                    element.scoreGlobal ?? '—',
                    element.avis
                ];
            }

            valeurs.forEach(valeur => {
                const td = document.createElement('td');
                td.textContent = valeur ?? '—';
                tr.appendChild(td);
            });

            tbody.appendChild(tr);
        });

        message.textContent = `${liste.length} élément(s) chargé(s).`;

        const compteurs = {
            offres: 'nombreOffres',
            candidatures: 'nombreCandidatures',
            entretiens: 'nombreEntretiens'
        };

        if (compteurs[type]) {
            document.getElementById(compteurs[type]).textContent = liste.length;
        }
    } catch (erreur) {
        message.textContent = `Impossible de charger les données : ${erreur.message}`;
    }
}

function chargerOffres() {
    return charger(API.offres, 'listeOffres', 'messageOffres', 'offres');
}

function chargerCandidatures() {
    return charger(API.candidatures, 'listeCandidatures', 'messageCandidatures', 'candidatures');
}

function chargerEntretiens() {
    return charger(API.entretiens, 'listeEntretiens', 'messageEntretiens', 'entretiens');
}

function chargerEvaluations() {
    return charger(API.evaluations, 'listeEvaluations', 'messageEvaluations', 'evaluations');
}

document.addEventListener('DOMContentLoaded', () => {
    chargerOffres();
    chargerCandidatures();
    chargerEntretiens();
    chargerEvaluations();
});
