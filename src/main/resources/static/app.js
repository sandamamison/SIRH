const API_BASE = '/api';
const elements = {
  list: document.querySelector('#propositions'),
  count: document.querySelector('#proposition-count'),
  message: document.querySelector('#message'),
  createForm: document.querySelector('#create-form'),
  employeeForm: document.querySelector('#employee-form'),
  employeeResult: document.querySelector('#employee-result'),
  refresh: document.querySelector('#refresh-button'),
  dialog: document.querySelector('#detail-dialog'),
  detail: document.querySelector('#proposition-detail'),
  history: document.querySelector('#proposition-history'),
  closeDialog: document.querySelector('#close-dialog'),
  template: document.querySelector('#proposition-template')
};

const state = { propositions: [] };

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: { 'Content-Type': 'application/json', ...(options.headers || {}) },
    ...options
  });
  const contentType = response.headers.get('content-type') || '';
  const body = contentType.includes('application/json')
    ? await response.json()
    : await response.text();
  if (!response.ok) {
    const detail = typeof body === 'string' ? body : body.message || JSON.stringify(body);
    throw new Error(`${response.status} — ${detail || 'La requête a échoué.'}`);
  }
  return body;
}

function formatDate(value) {
  if (!value) return '—';
  return new Intl.DateTimeFormat('fr-FR', { dateStyle: 'medium' }).format(new Date(value));
}

function formatDateTime(value) {
  if (!value) return '—';
  return new Intl.DateTimeFormat('fr-FR', { dateStyle: 'medium', timeStyle: 'short' }).format(new Date(value));
}

function showMessage(text, type = '') {
  elements.message.textContent = text;
  elements.message.className = `message ${type}`;
}

function actionButton(label, action, id) {
  const button = document.createElement('button');
  button.className = 'button button-small';
  button.type = 'button';
  button.textContent = label;
  button.dataset.action = action;
  button.dataset.id = id;
  return button;
}

function renderPropositions() {
  elements.count.textContent = state.propositions.length;
  elements.list.replaceChildren();
  if (!state.propositions.length) {
    elements.list.innerHTML = '<p class="muted">Aucune proposition trouvée.</p>';
    return;
  }
  const fragment = document.createDocumentFragment();
  state.propositions.forEach((proposition) => {
    const node = elements.template.content.cloneNode(true);
    const article = node.querySelector('.proposition');
    const status = node.querySelector('.status');
    status.textContent = proposition.statut || 'INCONNU';
    status.classList.add((proposition.statut || '').toLowerCase());
    node.querySelector('.proposition-title').textContent = `Proposition #${proposition.id}`;
    node.querySelector('.proposition-meta').textContent =
      `Candidature #${proposition.candidatureId} · envoyée le ${formatDateTime(proposition.dateEnvoi)}`;
    node.querySelector('.commentaire').textContent = proposition.commentaire || 'Aucun commentaire.';
    node.querySelector('.detail-button').dataset.id = proposition.id;
    const actions = node.querySelector('.actions');
    if (proposition.statut === 'ENVOYEE') {
      actions.append(actionButton('Accepter', 'accepter', proposition.id));
      actions.append(actionButton('Refuser', 'refuser', proposition.id));
      actions.append(actionButton('Expirer', 'expirer', proposition.id));
    }
    article.dataset.id = proposition.id;
    fragment.append(node);
  });
  elements.list.append(fragment);
}

async function loadPropositions() {
  elements.list.innerHTML = '<p class="muted">Chargement des propositions…</p>';
  try {
    state.propositions = await request('/propositions-embauche');
    renderPropositions();
    showMessage('');
  } catch (error) {
    state.propositions = [];
    renderPropositions();
    showMessage(`Impossible de charger les propositions : ${error.message}`, 'error');
  }
}

async function submitCreation(event) {
  event.preventDefault();
  const data = new FormData(elements.createForm);
  const payload = {
    candidatureId: Number(data.get('candidatureId')),
    dateExpiration: data.get('dateExpiration') || null,
    commentaire: data.get('commentaire') || null,
    createdBy: data.get('createdBy') || null
  };
  try {
    await request('/propositions-embauche', {
      method: 'POST',
      body: JSON.stringify(payload)
    });
    elements.createForm.reset();
    showMessage('Proposition créée avec succès.', 'success');
    await loadPropositions();
  } catch (error) {
    showMessage(`Création impossible : ${error.message}`, 'error');
  }
}

async function updateProposition(id, action) {
  const author = window.prompt("Auteur de l'action (facultatif) :", '') || '';
  try {
    await request(`/propositions-embauche/${id}/${action}?auteur=${encodeURIComponent(author)}`, { method: 'POST' });
    showMessage(`Action « ${action} » effectuée sur la proposition #${id}.`, 'success');
    await loadPropositions();
  } catch (error) {
    showMessage(`Action impossible : ${error.message}`, 'error');
  }
}

async function showDetails(id) {
  try {
    const [proposition, history] = await Promise.all([
      request(`/propositions-embauche/${id}`),
      request(`/propositions-embauche/${id}/historique`)
    ]);
    elements.detail.innerHTML = `
      <dl class="details">
        <dt>Statut</dt><dd><span class="status ${(proposition.statut || '').toLowerCase()}">${proposition.statut || '—'}</span></dd>
        <dt>Candidature</dt><dd>#${proposition.candidatureId ?? '—'}</dd>
        <dt>Date d'envoi</dt><dd>${formatDateTime(proposition.dateEnvoi)}</dd>
        <dt>Date d'expiration</dt><dd>${formatDate(proposition.dateExpiration)}</dd>
        <dt>Date de réponse</dt><dd>${formatDateTime(proposition.dateReponse)}</dd>
        <dt>Créée par</dt><dd>${proposition.createdBy || '—'}</dd>
        <dt>Commentaire</dt><dd>${proposition.commentaire || '—'}</dd>
      </dl>`;
    elements.history.replaceChildren();
    if (!history.length) {
      elements.history.innerHTML = '<p class="muted">Aucun événement enregistré.</p>';
    } else {
      history.forEach((event) => {
        const item = document.createElement('p');
        item.textContent = JSON.stringify(event);
        elements.history.append(item);
      });
    }
    elements.dialog.showModal();
  } catch (error) {
    showMessage(`Détail indisponible : ${error.message}`, 'error');
  }
}

async function searchEmployee(event) {
  event.preventDefault();
  const id = document.querySelector('#employee-id').value;
  elements.employeeResult.className = 'result';
  elements.employeeResult.textContent = 'Recherche en cours…';
  try {
    const employee = await request(`/employes/${id}`);
    elements.employeeResult.innerHTML = `
      <strong>Employé #${employee.id}</strong>
      <span>Matricule : ${employee.matricule || '—'}</span>
      <span>Personne : #${employee.personneId ?? '—'}</span>
      <span>Embauche : ${formatDate(employee.dateEmbauche)}</span>
      <span>Statut : ${employee.statut || '—'}</span>`;
  } catch (error) {
    elements.employeeResult.className = 'result error';
    elements.employeeResult.textContent = `Employé introuvable : ${error.message}`;
  }
}

elements.list.addEventListener('click', (event) => {
  const target = event.target.closest('[data-action], .detail-button');
  if (!target) return;
  if (target.dataset.action) updateProposition(target.dataset.id, target.dataset.action);
  else showDetails(target.dataset.id);
});
elements.createForm.addEventListener('submit', submitCreation);
elements.employeeForm.addEventListener('submit', searchEmployee);
elements.refresh.addEventListener('click', loadPropositions);
elements.closeDialog.addEventListener('click', () => elements.dialog.close());
loadPropositions();
