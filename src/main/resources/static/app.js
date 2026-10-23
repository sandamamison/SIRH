const API = '/api';
const message = document.querySelector('#message');
const labels = { departements: 'Départements', postes: 'Postes', demandes: 'Demandes', offres: 'Offres', candidatures: 'Candidatures', entretiens: 'Entretiens', evaluations: 'Évaluations', propositions: 'Propositions', contrats: 'Contrats', affectations: 'Affectations' };

async function request(path, options = {}) {
  const response = await fetch(`${API}${path}`, { headers: { 'Content-Type': 'application/json' }, ...options });
  const type = response.headers.get('content-type') || '';
  const body = type.includes('json') ? await response.json() : await response.text();
  if (!response.ok) throw new Error(`${response.status} — ${typeof body === 'string' ? body : body.message || JSON.stringify(body)}`);
  return body;
}

function notify(text, type = '') { message.textContent = text; message.className = `message ${type}`; }
function formatDate(value) {
  if (!value) return '—';
  return new Intl.DateTimeFormat('fr-FR', { dateStyle: 'medium' }).format(new Date(value));
}

function displayValue(value, key) {
  if (value === null || value === undefined || value === '') return '—';
  if (key.toLowerCase().includes('date') || key.toLowerCase().includes('heure') || key.toLowerCase().endsWith('at')) {
    return String(value).includes('T') ? new Intl.DateTimeFormat('fr-FR', { dateStyle: 'medium', timeStyle: 'short' }).format(new Date(value)) : formatDate(value);
  }
  return String(value);
}

function pretty(value) { return typeof value === 'string' ? value : JSON.stringify(value, null, 2); }

const visibleFields = {
  departements: ['id', 'codeDepartement', 'nomDepartement', 'description'],
  postes: ['id', 'codePoste', 'titre', 'salaireMin', 'salaireMax', 'departementId'],
  demandes: ['id', 'motif', 'statut', 'budgetPrevu', 'dateSouhaitee', 'posteId'],
  offres: ['id', 'titreOffre', 'statut', 'lieuTravail', 'salairePropose', 'datePublication'],
  candidatures: ['id', 'offreEmploiId', 'personneId', 'statut', 'dateCandidature'],
  entretiens: ['id', 'candidatureId', 'dateHeure', 'typeEntretien', 'statut'],
  evaluations: ['id', 'entretienId', 'employeEvaluateurId', 'scoreGlobal', 'avis'],
  propositions: ['id', 'candidatureId', 'statut', 'dateEnvoi', 'dateExpiration', 'createdBy'],
  contrats: ['id', 'numeroContrat', 'employeId', 'typeContrat', 'dateDebut', 'dateFin', 'salaireBrut', 'statut'],
  affectations: ['id', 'employeId', 'posteId', 'dateDebut', 'dateFin']
};

const fieldLabels = {
  id: 'Identifiant', codeDepartement: 'Code', nomDepartement: 'Nom', description: 'Description',
  codePoste: 'Code', titre: 'Titre', salaireMin: 'Salaire minimum', salaireMax: 'Salaire maximum',
  departementId: 'Département', motif: 'Motif', statut: 'Statut', budgetPrevu: 'Budget prévu',
  dateSouhaitee: 'Date souhaitée', posteId: 'Poste', titreOffre: 'Titre', lieuTravail: 'Lieu',
  salairePropose: 'Salaire proposé', datePublication: 'Publication', offreEmploiId: 'Offre',
  personneId: 'Candidat', dateCandidature: 'Candidature', candidatureId: 'Candidature',
  dateHeure: 'Date et heure', typeEntretien: 'Type', entretienId: 'Entretien',
  employeEvaluateurId: 'Évaluateur', scoreGlobal: 'Score', avis: 'Avis', dateEnvoi: 'Envoi',
  dateExpiration: 'Expiration', createdBy: 'Créateur', numeroContrat: 'N° contrat',
  employeId: 'Employé', typeContrat: 'Type', dateDebut: 'Début', dateFin: 'Fin',
  salaireBrut: 'Salaire brut'
};

function renderList(id, values) {
  const target = document.querySelector(`#list-${id}`);
  if (!target) return;
  target.replaceChildren();
  if (!Array.isArray(values) || !values.length) { target.innerHTML = '<span class="muted">Aucune donnée.</span>'; return; }
  values.forEach(value => {
    const item = document.createElement('article');
    item.className = 'data-item';
    const fields = visibleFields[id] || Object.keys(value).slice(0, 6);
    fields.filter(key => Object.prototype.hasOwnProperty.call(value, key)).forEach(key => {
      const row = document.createElement('span');
      const label = document.createElement('b');
      const content = document.createElement('em');
      label.textContent = fieldLabels[key] || key;
      content.textContent = displayValue(value[key], key);
      row.append(label, content);
      item.append(row);
    });
    target.append(item);
  });
}

const resourcePaths = {
  departements: '/departements',
  postes: '/postes',
  demandes: '/demandes',
  offres: '/offres',
  candidatures: '/candidatures',
  entretiens: '/entretiens',
  evaluations: '/evaluations',
  propositions: '/propositions-embauche',
  contrats: '/contrats?employeId=1',
  affectations: '/affectations?employeId=1'
};

async function loadResource(resource) {
  try {
    const value = await request(resourcePaths[resource]);
    renderList(resource, value);
    const stat = document.querySelector(`#stat-${resource}`);
    if (stat) stat.textContent = Array.isArray(value) ? value.length : '1';
  } catch (error) {
    renderList(resource, []);
    const stat = document.querySelector(`#stat-${resource}`);
    if (stat) stat.textContent = '—';
  }
}

async function refresh() {
  await Promise.all(Object.keys(labels).map(loadResource));
  notify('Données actualisées.', 'success');
}

function actionPath(action) {
  return {
    'create-departement': ['/departements', 'POST'],
    'create-poste': ['/postes', 'POST'],
    'create-demande': ['/demandes', 'POST'],
    'create-offre': ['/offres', 'POST'],
    'create-candidature': ['/candidatures', 'POST'],
    'create-entretien': ['/entretiens', 'POST'],
    'create-evaluation': ['/evaluations', 'POST'],
    'create-proposition': ['/propositions-embauche', 'POST'],
    'create-contrat': ['/contrats', 'POST'],
    'create-affectation': ['/affectations', 'POST']
  }[action];
}

async function submitForm(form) {
  const action = form.dataset.action;
  const [path, method] = actionPath(action);
  const body = {};
  new FormData(form).forEach((value, key) => {
    if (value === '') return;
    body[key] = key === 'accordSalaire' ? value === 'true' : ['posteId', 'employeDemandeurId', 'employeRemplaceId', 'effectifDemande', 'budgetPrevu',
      'employeResponsableId', 'departementId', 'salaireMin', 'salaireMax', 'offreEmploiId', 'personneId',
      'pretentionSalariale', 'candidatureId', 'entretienId', 'employeEvaluateurId', 'scoreGlobal',
      'employeId', 'employeCreateurId', 'salaireBrut'].includes(key) ? Number(value) : value;
  });
  if (action === 'create-offre') {
    const actionValue = body.action;
    delete body.action;
    const acteur = body.employeActeurId;
    delete body.employeActeurId;
    const offre = { ...body };
    body.offre = offre;
    body.offre.demandeRecrutementId = Number(body.offre.demandeRecrutementId);
    body.action = { employeActeurId: Number(acteur), commentaire: null };
    if (actionValue === 'PUBLIER') body.action.commentaire = 'Publication depuis la console';
  }
  await request(path, { method, body: JSON.stringify(body) });
  notify('Opération effectuée avec succès.', 'success');
  await refresh();
}

document.querySelector('#navigation').addEventListener('click', event => {
  const button = event.target.closest('[data-page]');
  if (!button) return;
  document.querySelectorAll('.nav-item').forEach(item => item.classList.toggle('active', item === button));
  document.querySelectorAll('.page').forEach(page => page.classList.toggle('active', page.dataset.pageContent === button.dataset.page));
});
document.querySelector('#refresh-button').addEventListener('click', refresh);
document.querySelector('#logout-button').addEventListener('click', async () => {
  try {
    await fetch('/logout', { method: 'POST' });
    window.location.href = '/login.html?logout=true';
  } catch (error) {
    notify(`Déconnexion impossible : ${error.message}`, 'error');
  }
});
document.querySelectorAll('form[data-action]').forEach(form => form.addEventListener('submit', async event => {
  event.preventDefault();
  try { await submitForm(form); } catch (error) { notify(`Erreur : ${error.message}`, 'error'); }
}));
refresh();
