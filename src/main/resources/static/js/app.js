/**
 * app.js — Lógica del cliente web para Gestión de Héroes Mexicanos
 * Consume la API REST con fetch() · Diseño minimalista
 */

const API = '/api/v1/heroes';

// ─── Estado ────────────────────────────────────────────────
const state = {
    heroes:   [],
    filtered: [],
    epocas:   new Set(),
    movs:     new Set(),
    estados:  new Set(),
    view:     'table',          // 'table' | 'cards'
    deleteId: null
};

// ─── Referencias DOM ───────────────────────────────────────
const $  = id => document.getElementById(id);
const el = {
    // Topbar
    btnNavNew:      $('btn-nav-new'),
    btnNavRefresh:  $('btn-nav-refresh'),
    apiDot:         $('api-dot'),
    apiLabel:       $('api-label'),

    // Stats
    statTotal:      $('stat-total'),
    statEpocas:     $('stat-epocas'),
    statMovs:       $('stat-movimientos'),
    statEstados:    $('stat-estados'),

    // Toolbar
    search:         $('search-input'),
    filterEpoca:    $('filter-epoca'),
    filterMov:      $('filter-movimiento'),
    filterEstado:   $('filter-estado'),
    btnReset:       $('btn-reset'),

    // Header button
    btnHeaderNew:   $('btn-header-new'),

    // Data card
    loader:         $('loader'),
    viewTable:      $('view-table'),
    viewCards:      $('view-cards'),
    emptyState:     $('empty-state'),
    tableBody:      $('table-body'),
    countBadge:     $('count-badge'),
    btnViewTable:   $('btn-view-table'),
    btnViewCards:   $('btn-view-cards'),

    // Modal Form
    modalForm:      $('modal-form'),
    modalFormTitle: $('modal-form-title'),
    heroForm:       $('hero-form'),
    heroId:         $('hero-id'),
    nombre:         $('nombre'),
    apellido:       $('apellido'),
    fecha:          $('fechaNacimiento'),
    estadoNac:      $('estadoNacimiento'),
    epoca:          $('epoca'),
    movimiento:     $('movimiento'),
    descripcion:    $('descripcion'),
    formError:      $('form-error'),
    btnCloseForm:   $('btn-close-form'),
    btnCancelForm:  $('btn-cancel-form'),
    btnSubmitForm:  $('btn-submit-form'),

    // Modal Delete
    modalDelete:    $('modal-delete'),
    deleteName:     $('delete-name'),
    btnCloseDelete: $('btn-close-delete'),
    btnCancelDel:   $('btn-cancel-delete'),
    btnConfirmDel:  $('btn-confirm-delete'),

    // Modal Detail
    modalDetail:    $('modal-detail'),
    detailName:     $('detail-name'),
    detailTags:     $('detail-tags'),
    detailId:       $('detail-id'),
    detailFecha:    $('detail-fecha'),
    detailEstado:   $('detail-estado'),
    detailMov:      $('detail-movimiento'),
    detailDesc:     $('detail-desc'),
    btnCloseDetail:  $('btn-close-detail'),
    btnCloseDetail2: $('btn-close-detail-2'),

    // Toasts
    toasts:         $('toasts')
};

// ─── Init ──────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
    bindEvents();
    loadHeroes();
});

function bindEvents() {
    // Topbar
    el.btnNavNew.addEventListener('click',     () => openForm());
    el.btnHeaderNew.addEventListener('click',  () => openForm());
    el.btnNavRefresh.addEventListener('click', loadHeroes);

    // Toolbar
    el.search.addEventListener('input',          localFilter);
    el.filterEpoca.addEventListener('change',    handleEpoca);
    el.filterMov.addEventListener('change',      handleMov);
    el.filterEstado.addEventListener('change',   handleEstado);
    el.btnReset.addEventListener('click',        resetFilters);

    // View toggle
    el.btnViewTable.addEventListener('click',    () => switchView('table'));
    el.btnViewCards.addEventListener('click',    () => switchView('cards'));

    // Form modal
    el.btnCloseForm.addEventListener('click',   closeForm);
    el.btnCancelForm.addEventListener('click',  closeForm);
    el.btnSubmitForm.addEventListener('click',  submitForm);

    // Delete modal
    el.btnCloseDelete.addEventListener('click', closeDelete);
    el.btnCancelDel.addEventListener('click',   closeDelete);
    el.btnConfirmDel.addEventListener('click',  confirmDelete);

    // Detail modal
    el.btnCloseDetail.addEventListener('click',  closeDetail);
    el.btnCloseDetail2.addEventListener('click', closeDetail);

    // Close on overlay click
    [el.modalForm, el.modalDelete, el.modalDetail].forEach(m => {
        m.addEventListener('click', e => { if (e.target === m) m.classList.add('hidden'); });
    });
}

// ─── API Calls ─────────────────────────────────────────────

async function loadHeroes() {
    showLoader(true);
    try {
        const res  = await fetch(API);
        if (!res.ok) throw new Error(res.status);
        state.heroes   = await res.json();
        state.filtered = [...state.heroes];
        setApiStatus(true);
        populateFilters();
        updateStats();
        render();
    } catch {
        setApiStatus(false);
        toast('No se pudo conectar con la API REST', 'error');
        render();
    } finally {
        showLoader(false);
    }
}

async function handleEpoca() {
    const v = el.filterEpoca.value;
    if (!v) { localFilter(); return; }
    showLoader(true);
    try {
        const res = await fetch(`${API}/epoca/${encodeURIComponent(v)}`);
        if (!res.ok) throw new Error();
        state.filtered = await res.json();
        render();
    } catch {
        toast('Error al filtrar por época', 'error');
    } finally { showLoader(false); }
}

async function handleMov() {
    const v = el.filterMov.value;
    if (!v) { localFilter(); return; }
    showLoader(true);
    try {
        const res = await fetch(`${API}/movimiento/${encodeURIComponent(v)}`);
        if (!res.ok) throw new Error();
        state.filtered = await res.json();
        render();
    } catch {
        toast('Error al filtrar por movimiento', 'error');
    } finally { showLoader(false); }
}

async function handleEstado() {
    const v = el.filterEstado.value;
    if (!v) { localFilter(); return; }
    showLoader(true);
    try {
        const res = await fetch(`${API}/estado/${encodeURIComponent(v)}`);
        if (!res.ok) throw new Error();
        state.filtered = await res.json();
        render();
    } catch {
        toast('Error al filtrar por estado', 'error');
    } finally { showLoader(false); }
}

async function submitForm(e) {
    e.preventDefault();
    hideFormError();

    const id   = el.heroId.value;
    const data = {
        nombre:           el.nombre.value.trim(),
        apellido:         el.apellido.value.trim(),
        fechaNacimiento:  el.fecha.value,
        estadoNacimiento: el.estadoNac.value.trim(),
        epoca:            el.epoca.value.trim(),
        movimiento:       el.movimiento.value.trim(),
        descripcion:      el.descripcion.value.trim()
    };

    // Validación básica front
    for (const [k, v] of Object.entries(data)) {
        if (k !== 'descripcion' && !v) {
            showFormError('Todos los campos marcados con * son obligatorios.'); return;
        }
    }
    if (data.fechaNacimiento > new Date().toISOString().split('T')[0]) {
        showFormError('La fecha de nacimiento no puede ser posterior a hoy.'); return;
    }

    const url    = id ? `${API}/${id}` : API;
    const method = id ? 'PUT' : 'POST';

    try {
        const res  = await fetch(url, {
            method,
            headers: { 'Content-Type': 'application/json' },
            body:    JSON.stringify(data)
        });
        const body = await res.json();
        if (!res.ok) {
            showFormError(body.message || body.error || 'Error al procesar la solicitud.');
            return;
        }
        toast(`Héroe ${id ? 'actualizado' : 'registrado'} correctamente`, 'success');
        closeForm();
        loadHeroes();
    } catch {
        showFormError('No se pudo comunicar con la API REST.');
    }
}

async function confirmDelete() {
    if (!state.deleteId) return;
    try {
        const res = await fetch(`${API}/${state.deleteId}`, { method: 'DELETE' });
        if (!res.ok && res.status !== 204) {
            const b = await res.json();
            throw new Error(b.message);
        }
        toast('Héroe eliminado', 'success');
        closeDelete();
        loadHeroes();
    } catch (err) {
        toast(err.message || 'Error al eliminar', 'error');
        closeDelete();
    }
}

async function openDetail(id) {
    try {
        const res  = await fetch(`${API}/${id}`);
        if (!res.ok) throw new Error();
        const h = await res.json();
        el.detailName.textContent = `${h.nombre} ${h.apellido}`;
        el.detailTags.innerHTML   = `
            <span class="tag tag-accent">${esc(h.epoca)}</span>
            <span class="tag">${esc(h.movimiento)}</span>
            <span class="tag">${esc(h.estadoNacimiento)}</span>`;
        el.detailId.textContent      = h.id;
        el.detailFecha.textContent   = fmtDate(h.fechaNacimiento);
        el.detailEstado.textContent  = h.estadoNacimiento;
        el.detailMov.textContent     = h.movimiento;
        el.detailDesc.textContent    = h.descripcion || 'Sin reseña registrada.';
        el.modalDetail.classList.remove('hidden');
    } catch {
        toast('No se pudo cargar el detalle', 'error');
    }
}

// ─── Render ────────────────────────────────────────────────

function render() {
    const list = state.filtered;
    el.countBadge.textContent = `${list.length} registro${list.length !== 1 ? 's' : ''}`;

    const isEmpty = list.length === 0;
    el.emptyState.classList.toggle('hidden', !isEmpty);

    if (isEmpty) {
        el.viewTable.classList.add('hidden');
        el.viewCards.classList.add('hidden');
        return;
    }

    if (state.view === 'table') {
        el.viewCards.classList.add('hidden');
        el.viewTable.classList.remove('hidden');
        renderTable(list);
    } else {
        el.viewTable.classList.add('hidden');
        el.viewCards.classList.remove('hidden');
        renderCards(list);
    }
}

function renderTable(list) {
    el.tableBody.innerHTML = list.map(h => `
        <tr>
            <td class="td-id">${h.id}</td>
            <td>
                <div class="hero-name">${esc(h.nombre)} ${esc(h.apellido)}</div>
                <div class="hero-desc">${esc(h.descripcion || '')}</div>
            </td>
            <td style="white-space:nowrap;color:var(--text-2);font-size:12px;">${fmtDate(h.fechaNacimiento)}</td>
            <td><span class="tag">${esc(h.estadoNacimiento)}</span></td>
            <td><span class="tag tag-accent">${esc(h.epoca)}</span></td>
            <td><span class="tag">${esc(h.movimiento)}</span></td>
            <td>
                <div class="actions-cell">
                    <button class="btn-icon view"   title="Ver detalle"  onclick="openDetail(${h.id})"><i class="fa-solid fa-eye"></i></button>
                    <button class="btn-icon edit"   title="Editar"       onclick="openEdit(${h.id})"><i class="fa-solid fa-pen"></i></button>
                    <button class="btn-icon delete" title="Eliminar"     onclick="openDeleteModal(${h.id},'${esc(h.nombre)} ${esc(h.apellido)}')"><i class="fa-solid fa-trash"></i></button>
                </div>
            </td>
        </tr>`).join('');
}

function renderCards(list) {
    el.viewCards.innerHTML = list.map(h => `
        <div class="hero-card">
            <div class="hero-card-name">${esc(h.nombre)} ${esc(h.apellido)}</div>
            <div class="hero-card-tags">
                <span class="tag tag-accent">${esc(h.epoca)}</span>
                <span class="tag">${esc(h.movimiento)}</span>
            </div>
            <div class="hero-card-desc">${esc(h.descripcion || 'Sin reseña.')}</div>
            <div class="hero-card-footer">
                <span class="hero-card-date">${fmtDate(h.fechaNacimiento)} · ${esc(h.estadoNacimiento)}</span>
                <div class="actions-cell">
                    <button class="btn-icon view"   onclick="openDetail(${h.id})"><i class="fa-solid fa-eye"></i></button>
                    <button class="btn-icon edit"   onclick="openEdit(${h.id})"><i class="fa-solid fa-pen"></i></button>
                    <button class="btn-icon delete" onclick="openDeleteModal(${h.id},'${esc(h.nombre)} ${esc(h.apellido)}')"><i class="fa-solid fa-trash"></i></button>
                </div>
            </div>
        </div>`).join('');
}

// ─── Filtros locales ───────────────────────────────────────

function localFilter() {
    const q = el.search.value.toLowerCase();
    state.filtered = state.heroes.filter(h =>
        !q ||
        h.nombre.toLowerCase().includes(q) ||
        h.apellido.toLowerCase().includes(q) ||
        (h.descripcion && h.descripcion.toLowerCase().includes(q)) ||
        h.epoca.toLowerCase().includes(q) ||
        h.movimiento.toLowerCase().includes(q) ||
        h.estadoNacimiento.toLowerCase().includes(q)
    );
    render();
}

function resetFilters() {
    el.search.value      = '';
    el.filterEpoca.value = '';
    el.filterMov.value   = '';
    el.filterEstado.value = '';
    state.filtered = [...state.heroes];
    render();
}

function populateFilters() {
    state.epocas.clear(); state.movs.clear(); state.estados.clear();
    state.heroes.forEach(h => {
        if (h.epoca)            state.epocas.add(h.epoca);
        if (h.movimiento)       state.movs.add(h.movimiento);
        if (h.estadoNacimiento) state.estados.add(h.estadoNacimiento);
    });
    fillSelect(el.filterEpoca,   state.epocas,  'Todas las épocas');
    fillSelect(el.filterMov,     state.movs,    'Todos los movimientos');
    fillSelect(el.filterEstado,  state.estados, 'Todos los estados');
}

function fillSelect(sel, set, def) {
    const cur = sel.value;
    sel.innerHTML = `<option value="">${def}</option>` +
        [...set].sort().map(v => `<option value="${esc(v)}">${esc(v)}</option>`).join('');
    sel.value = cur;
}

function updateStats() {
    el.statTotal.textContent   = state.heroes.length;
    el.statEpocas.textContent  = state.epocas.size;
    el.statMovs.textContent    = state.movs.size;
    el.statEstados.textContent = state.estados.size;
}

// ─── Modales ───────────────────────────────────────────────

function openForm(hero = null) {
    el.heroForm.reset();
    hideFormError();
    if (hero) {
        el.modalFormTitle.textContent = `Editar héroe #${hero.id}`;
        el.heroId.value        = hero.id;
        el.nombre.value        = hero.nombre;
        el.apellido.value      = hero.apellido;
        el.fecha.value         = hero.fechaNacimiento;
        el.estadoNac.value     = hero.estadoNacimiento;
        el.epoca.value         = hero.epoca;
        el.movimiento.value    = hero.movimiento;
        el.descripcion.value   = hero.descripcion || '';
    } else {
        el.modalFormTitle.textContent = 'Nuevo héroe';
        el.heroId.value = '';
    }
    el.modalForm.classList.remove('hidden');
    el.nombre.focus();
}

function openEdit(id) {
    const h = state.heroes.find(x => x.id === id);
    if (h) openForm(h);
}

function closeForm() { el.modalForm.classList.add('hidden'); }

function openDeleteModal(id, name) {
    state.deleteId        = id;
    el.deleteName.textContent = name;
    el.modalDelete.classList.remove('hidden');
}

function closeDelete() {
    state.deleteId = null;
    el.modalDelete.classList.add('hidden');
}

function closeDetail() { el.modalDetail.classList.add('hidden'); }

function showFormError(msg) {
    el.formError.textContent = msg;
    el.formError.classList.remove('hidden');
}

function hideFormError() { el.formError.classList.add('hidden'); }

// ─── UI helpers ────────────────────────────────────────────

function switchView(v) {
    state.view = v;
    el.btnViewTable.classList.toggle('active', v === 'table');
    el.btnViewCards.classList.toggle('active', v === 'cards');
    render();
}

function showLoader(on) {
    el.loader.classList.toggle('hidden', !on);
    if (on) {
        el.viewTable.classList.add('hidden');
        el.viewCards.classList.add('hidden');
        el.emptyState.classList.add('hidden');
    }
}

function setApiStatus(online) {
    el.apiDot.className   = `dot${online ? '' : ' offline'}`;
    el.apiLabel.textContent = online ? 'Conectado' : 'Desconectado';
}

function toast(msg, type = 'success') {
    const t = document.createElement('div');
    t.className = `toast ${type}`;
    t.innerHTML = `<i class="fa-solid ${type === 'success' ? 'fa-circle-check' : 'fa-circle-xmark'}"></i>${esc(msg)}`;
    el.toasts.appendChild(t);
    setTimeout(() => { t.style.opacity = '0'; setTimeout(() => t.remove(), 300); }, 3500);
}

// ─── Utilities ─────────────────────────────────────────────

function fmtDate(s) {
    if (!s) return '—';
    const [y, m, d] = s.split('-');
    return `${d}/${m}/${y}`;
}

function esc(s) {
    if (s == null) return '';
    return String(s)
        .replace(/&/g, '&amp;').replace(/</g, '&lt;')
        .replace(/>/g, '&gt;').replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}
