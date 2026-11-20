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


// ============================================================
// MODULE PAIE
// ============================================================

let currentPeriodeId = null;

function formatMoney(val) {
  if (val === null || val === undefined) return '—';
  return new Intl.NumberFormat('fr-FR', { minimumFractionDigits: 2, maximumFractionDigits: 2 }).format(val) + ' Ar';
}

// --- Créer / ouvrir une période ---
document.getElementById('form-periode-paie').addEventListener('submit', async (e) => {
  e.preventDefault();
  const mois = parseInt(document.getElementById('paie-mois').value);
  const annee = parseInt(document.getElementById('paie-annee').value);
  try {
    const periode = await request('/paie/periodes', {
      method: 'POST',
      body: JSON.stringify({ mois, annee })
    });
    currentPeriodeId = periode.id;
    document.getElementById('periode-info').textContent =
      `✅ Période ${periode.code} (ID: ${periode.id}) — Statut: ${periode.statut}`;
    document.getElementById('paie-actions').style.display = '';
    await chargerBulletins(currentPeriodeId);
  } catch (err) {
    notify(`Erreur période: ${err.message}`, 'error');
  }
});

// --- Générer les bulletins ---
document.getElementById('btn-generer-paie').addEventListener('click', async () => {
  if (!currentPeriodeId) return;
  const msg = document.getElementById('paie-action-msg');
  msg.textContent = '⏳ Génération en cours…';
  try {
    const bulletins = await request(`/paie/periodes/${currentPeriodeId}/generer`, { method: 'POST' });
    msg.textContent = `✅ ${bulletins.length} bulletin(s) générés.`;
    await chargerBulletins(currentPeriodeId);
  } catch (err) {
    msg.textContent = '';
    notify(`Erreur génération: ${err.message}`, 'error');
  }
});

// --- Export Excel ---
document.getElementById('btn-excel-etat').addEventListener('click', () => {
  if (!currentPeriodeId) { notify('Sélectionnez une période d\'abord.', 'error'); return; }
  window.location.href = `/api/paie/periodes/${currentPeriodeId}/excel`;
});

document.getElementById('form-variable-paie').addEventListener('submit', async (e) => {
  e.preventDefault();
  const msg = document.getElementById('variable-msg');
  if (!currentPeriodeId) { msg.textContent = 'Sélectionnez une période d’abord.'; return; }
  try {
    await request(`/paie/periodes/${currentPeriodeId}/variables`, {
      method: 'POST',
      body: JSON.stringify({
        employeId: Number(document.getElementById('variable-employe').value),
        type: document.getElementById('variable-type').value,
        libelle: document.getElementById('variable-libelle').value,
        montant: Number(document.getElementById('variable-montant').value)
      })
    });
    msg.textContent = '✅ Variable enregistrée. Régénérez les bulletins pour recalculer la période.';
    e.target.reset();
  } catch (err) {
    msg.textContent = `Erreur : ${err.message}`;
  }
});

// --- Charger et afficher les bulletins ---
async function chargerBulletins(periodeId) {
  const container = document.getElementById('bulletins-list');
  container.innerHTML = '<p class="muted">Chargement…</p>';
  try {
    const bulletins = await request(`/paie/periodes/${periodeId}/bulletins`);
    if (!bulletins.length) {
      container.innerHTML = '<p class="muted">Aucun bulletin. Cliquez sur "Générer les bulletins".</p>';
      return;
    }
    container.innerHTML = '';
    const table = document.createElement('table');
    table.style.cssText = 'width:100%;border-collapse:collapse;font-size:13px;';
    table.innerHTML = `<thead>
      <tr style="background:#2d7a2d;color:#fff;text-align:left;">
        <th style="padding:8px;">Matricule</th>
        <th style="padding:8px;">Nom & Prénom</th>
        <th style="padding:8px;">Fonction</th>
        <th style="padding:8px;">Salaire de base</th>
        <th style="padding:8px;">Brut</th>
        <th style="padding:8px;">IRSA</th>
        <th style="padding:8px;">Net à payer</th>
        <th style="padding:8px;">Statut</th>
        <th style="padding:8px;">Actions</th>
      </tr></thead><tbody id="bulletins-tbody"></tbody>`;
    container.appendChild(table);

    const tbody = document.getElementById('bulletins-tbody');
    bulletins.forEach((b, idx) => {
      const tr = document.createElement('tr');
      tr.style.background = idx % 2 === 0 ? '#f9f9f9' : '#fff';
      tr.innerHTML = `
        <td style="padding:7px;">${b.matricule || '—'}</td>
        <td style="padding:7px;">${(b.nom || '') + ' ' + (b.prenom || '')}</td>
        <td style="padding:7px;">${b.fonction || '—'}</td>
        <td style="padding:7px;text-align:right;">${formatMoney(b.salaireBase)}</td>
        <td style="padding:7px;text-align:right;">${formatMoney(b.salaireBrut)}</td>
        <td style="padding:7px;text-align:right;">${formatMoney(b.totalIrsa)}</td>
        <td style="padding:7px;text-align:right;font-weight:bold;color:#1a5f1a;">${formatMoney(b.netAPayer)}</td>
        <td style="padding:7px;">${b.statut || '—'}</td>
        <td style="padding:7px;white-space:nowrap;">
          <button onclick="voirBulletin(${b.id})" class="btn" style="padding:4px 8px;font-size:11px;">📋 Détail</button>
          <a href="/api/paie/bulletins/${b.id}/pdf" target="_blank" class="btn" style="padding:4px 8px;font-size:11px;background:#c0392b;color:#fff;text-decoration:none;">📄 PDF</a>
        </td>`;
      tbody.appendChild(tr);
    });
  } catch (err) {
    container.innerHTML = `<p style="color:red;">Erreur: ${err.message}</p>`;
  }
}

// --- Voir le détail d'un bulletin ---
async function voirBulletin(id) {
  try {
    const b = await request(`/paie/bulletins/${id}`);
    document.getElementById('bulletin-detail-title').textContent =
      `Fiche de paie — ${b.matricule} ${b.nom || ''} ${b.prenom || ''} — ${b.periodeCode}`;
    document.getElementById('hs-bulletin-id').value = id;

    const body = document.getElementById('bulletin-detail-body');
    body.innerHTML = '';

    // En-tête info salarié
    const infoGrid = document.createElement('div');
    infoGrid.style.cssText = 'display:grid;grid-template-columns:repeat(4,1fr);gap:8px;margin-bottom:16px;background:#f5f9f5;padding:12px;border-radius:6px;';
    const infos = [
      ['Matricule', b.matricule], ['Classification', b.classification], ['N° CNaPS', b.numeroCnaps], ['Indice', b.indice ? formatMoney(b.indice) : '—'],
      ['Nom & Prénom', (b.nom || '') + ' ' + (b.prenom || '')], ['Salaire de base', formatMoney(b.salaireBase)], ['Taux journalier', formatMoney(b.tauxJournalier)], ['Taux horaire', formatMoney(b.tauxHoraire)],
      ['Fonction', b.fonction], ["Date d'embauche", b.dateEmbauche ? new Intl.DateTimeFormat('fr-FR').format(new Date(b.dateEmbauche)) : '—'], ['Ancienneté', b.ancienneteTexte || '—'], ['Statut', b.statut]
    ];
    infos.forEach(([label, val]) => {
      const div = document.createElement('div');
      div.innerHTML = `<small style="color:#666;">${label}</small><br><strong>${val || '—'}</strong>`;
      infoGrid.appendChild(div);
    });
    body.appendChild(infoGrid);

    // Tableau des rubriques
    const tbl = document.createElement('table');
    tbl.style.cssText = 'width:100%;border-collapse:collapse;font-size:13px;margin-bottom:16px;';
    tbl.innerHTML = `<thead><tr style="background:#2d7a2d;color:#fff;">
      <th style="padding:7px;text-align:left;">Désignation</th>
      <th style="padding:7px;text-align:center;">Nombre</th>
      <th style="padding:7px;text-align:right;">Taux</th>
      <th style="padding:7px;text-align:right;">Montant</th>
    </tr></thead><tbody id="lignes-tbody"></tbody>`;
    body.appendChild(tbl);

    const ltbody = tbl.querySelector('tbody');
    if (b.lignes) {
      b.lignes.forEach((l, i) => {
        const isTotal = l.type === 'TOTAL' || l.type === 'RETENUE_TOTAL';
        const isRetenue = l.type === 'RETENUE' || l.type === 'COTISATION';
        const tr = document.createElement('tr');
        tr.style.background = isTotal ? '#e8f5e9' : i % 2 === 0 ? '#fafafa' : '#fff';
        if (isTotal) tr.style.fontWeight = 'bold';
        const montantColor = isRetenue ? '#c0392b' : '#1a5f1a';
        tr.innerHTML = `
          <td style="padding:5px 7px;">${l.designation || ''}</td>
          <td style="padding:5px 7px;text-align:center;">${l.nombre && l.nombre != 0 ? (l.code === 'SALAIRE_BASE' ? '1 mois' : l.nombre) : ''}</td>
          <td style="padding:5px 7px;text-align:right;">${l.taux && l.taux != 0 ? (l.code && l.code.startsWith('IRSA') ? l.taux + '%' : formatMoney(l.taux)) : ''}</td>
          <td style="padding:5px 7px;text-align:right;color:${montantColor};">${l.montant && l.montant != 0 ? formatMoney(Math.abs(l.montant)) : ''}</td>`;
        ltbody.appendChild(tr);
      });
    }

    // Totaux récapitulatifs
    const recap = document.createElement('div');
    recap.style.cssText = 'display:grid;grid-template-columns:repeat(3,1fr);gap:10px;margin-top:12px;';
    const tots = [
      ['Total Brut', b.salaireBrut, '#1a5f1a'],
      ['Retenue CNaPS (1%)', b.retenueCnaps, '#c0392b'],
      ['Retenue Sanitaire (1%)', b.retenueSanitaire, '#c0392b'],
      ['Total IRSA', b.totalIrsa, '#c0392b'],
      ['Total Retenues', b.totalRetenues, '#c0392b'],
      ['🟢 NET À PAYER', b.netAPayer, '#fff', '#1a5f1a']
    ];
    tots.forEach(([label, val, color, bg]) => {
      const div = document.createElement('div');
      div.style.cssText = `background:${bg || '#f0f0f0'};padding:10px;border-radius:6px;text-align:center;`;
      div.innerHTML = `<small>${label}</small><br><strong style="color:${color};font-size:15px;">${formatMoney(val)}</strong>`;
      recap.appendChild(div);
    });
    body.appendChild(recap);

    // HS semaines
    if (b.calculHsSemaines && b.calculHsSemaines.length > 0) {
      const hTitle = document.createElement('h4');
      hTitle.textContent = 'Heures supplémentaires';
      hTitle.style.marginTop = '16px';
      body.appendChild(hTitle);
      const hsTbl = document.createElement('table');
      hsTbl.style.cssText = 'width:100%;border-collapse:collapse;font-size:12px;';
      hsTbl.innerHTML = `<thead><tr style="background:#555;color:#fff;">
        <th style="padding:5px;">Semaine</th>
        <th style="padding:5px;">H+30%</th><th style="padding:5px;">Mt 30%</th>
        <th style="padding:5px;">H+50%</th><th style="padding:5px;">Mt 50%</th>
        <th style="padding:5px;">H WE</th><th style="padding:5px;">Mt WE</th>
        <th style="padding:5px;">H Fér.</th><th style="padding:5px;">Mt Fér.</th>
        <th style="padding:5px;">Total H</th><th style="padding:5px;">Total Mt</th>
      </tr></thead><tbody></tbody>`;
      const hsBody = hsTbl.querySelector('tbody');
      b.calculHsSemaines.forEach(s => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
          <td style="padding:4px;text-align:center;">${s.numeroSemaine}</td>
          <td style="padding:4px;text-align:center;">${s.heures30 || 0}</td><td style="padding:4px;text-align:right;">${formatMoney(s.montant30)}</td>
          <td style="padding:4px;text-align:center;">${s.heures50 || 0}</td><td style="padding:4px;text-align:right;">${formatMoney(s.montant50)}</td>
          <td style="padding:4px;text-align:center;">${s.heuresWeekend || 0}</td><td style="padding:4px;text-align:right;">${formatMoney(s.montantWeekend)}</td>
          <td style="padding:4px;text-align:center;">${s.heuresFerie || 0}</td><td style="padding:4px;text-align:right;">${formatMoney(s.montantFerie)}</td>
          <td style="padding:4px;text-align:center;font-weight:bold;">${s.totalHeures || 0}</td>
          <td style="padding:4px;text-align:right;font-weight:bold;color:#1a5f1a;">${formatMoney(s.montantSemaine)}</td>`;
        hsBody.appendChild(tr);
      });
      body.appendChild(hsTbl);
    }

    document.getElementById('bulletin-detail').style.display = '';
    document.getElementById('bulletin-detail').scrollIntoView({ behavior: 'smooth' });
  } catch (err) {
    notify(`Erreur détail bulletin: ${err.message}`, 'error');
  }
}

// --- Fermer le détail ---
document.getElementById('btn-close-detail').addEventListener('click', () => {
  document.getElementById('bulletin-detail').style.display = 'none';
});

// --- Formulaire HS ---
document.getElementById('form-hs').addEventListener('submit', async (e) => {
  e.preventDefault();
  const id = document.getElementById('hs-bulletin-id').value;
  const msg = document.getElementById('hs-msg');
  if (!id) { msg.textContent = 'Ouvrez d\'abord un bulletin.'; return; }
  const payload = {
    semaines: [{
      numeroSemaine: parseInt(document.getElementById('hs-semaine').value),
      heures30: parseFloat(document.getElementById('hs-h30').value) || 0,
      heures50: parseFloat(document.getElementById('hs-h50').value) || 0,
      heuresWeekend: parseFloat(document.getElementById('hs-wk').value) || 0,
      heuresFerie: parseFloat(document.getElementById('hs-fer').value) || 0
    }]
  };
  try {
    msg.textContent = '⏳ Mise à jour…';
    const updated = await request(`/paie/bulletins/${id}/hs`, { method: 'PUT', body: JSON.stringify(payload) });
    msg.textContent = `✅ HS mis à jour. Net à payer: ${formatMoney(updated.netAPayer)}`;
    await chargerBulletins(currentPeriodeId);
    // Rafraîchir le détail
    voirBulletin(parseInt(id));
  } catch (err) {
    msg.textContent = `Erreur: ${err.message}`;
  }
});

const blockResources = [
  'competences', 'employe-competences', 'objectifs-evaluation', 'evaluations-rh', 'formations',
  'carrieres', 'promotions', 'mutations', 'historiques-salaires',
  'incidents-disciplinaires', 'procedures-disciplinaires', 'sanctions-disciplinaires',
  'ruptures', 'soldes-tout-compte'
];

function renderBlockList(resource, values) {
  const target = document.querySelector(`#list-${resource}`);
  if (!target) return;
  target.replaceChildren();
  if (!values.length) {
    target.innerHTML = '<span class="muted">Aucune donnée.</span>';
    return;
  }
  values.forEach(value => {
    const item = document.createElement('article');
    item.className = 'data-item';
    Object.entries(value).filter(([key]) => key !== 'id' && value[key] !== null && value[key] !== '')
      .slice(0, 7).forEach(([key, val]) => {
        const row = document.createElement('span');
        const label = document.createElement('b');
        const content = document.createElement('em');
        label.textContent = key;
        content.textContent = String(val);
        row.append(label, content);
        item.append(row);
      });
    target.append(item);
  });
}

async function loadBlockResource(resource) {
  try {
    const values = await request(`/${resource}`);
    renderBlockList(resource, Array.isArray(values) ? values : []);
  } catch (error) {
    renderBlockList(resource, []);
  }
}

async function refreshBlocks() {
  await Promise.all(blockResources.map(loadBlockResource));
}

function toBlockValue(key, value) {
  if (value === '') return undefined;
  if (key.endsWith('Id') || key === 'dureeHeures' || key === 'joursPreavis') return Number(value);
  if (['noteGlobale', 'augmentationSalaire', 'joursCongesRestants', 'cout'].includes(key)) return Number(value);
  return value;
}

document.querySelectorAll('form[data-block-action]').forEach(form => form.addEventListener('submit', async event => {
  event.preventDefault();
  const body = {};
  new FormData(form).forEach((value, key) => {
    const parsed = toBlockValue(key, value);
    if (parsed !== undefined) body[key] = parsed;
  });
  try {
    await request(form.dataset.blockAction, { method: 'POST', body: JSON.stringify(body) });
    form.reset();
    notify('Opération du bloc effectuée.', 'success');
    await refreshBlocks();
  } catch (error) {
    notify(`Erreur : ${error.message}`, 'error');
  }
}));

refreshBlocks();
