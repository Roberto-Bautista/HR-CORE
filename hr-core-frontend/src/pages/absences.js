/* =============================================================
   absences.js — Página de Gestión de Vacaciones (Admin)
   ============================================================= */

const AbsencesPage = (() => {

    let _requests = [];
    let _activeTab = 'pending'; // 'pending' | 'all'

    const COLUMNS = [
        {
            key: 'nombreEmpleado',
            label: 'Colaborador',
            render: (_, row) => `
                <div style="display:flex;align-items:center;gap:var(--space-3);">
                    <div class="avatar">${Utils.getInitials(row.nombreEmpleado || 'C', '')}</div>
                    <div>
                        <div style="font-weight:500;">${row.nombreEmpleado || 'Colaborador'}</div>
                        <div style="font-size:var(--font-size-xs);color:var(--clr-text-500);">${row.empleadoId ? row.empleadoId.substring(0, 8) : ''}</div>
                    </div>
                </div>`,
        },
        {
            key: 'periodo',
            label: 'Período',
            render: (_, row) => `
                <div style="font-size:var(--font-size-sm);">
                    📅 ${Utils.formatDate(row.fechaInicio)} al ${Utils.formatDate(row.fechaFin)}
                </div>`
        },
        {
            key: 'diasSolicitados',
            label: 'Días',
            render: val => `<strong>${val}</strong>`
        },
        {
            key: 'createdAt',
            label: 'Fecha Solicitud',
            render: val => Utils.formatDate(val)
        },
        {
            key: 'estado',
            label: 'Estado',
            render: (val, row) => `
                <div style="display:flex; flex-direction:column; gap:2px;">
                    <span class="badge ${Utils.statusToBadge(val)}">${Utils.statusToLabel(val)}</span>
                    ${val === 'RECHAZADA' && row.motivoRechazo ? `
                        <span style="font-size:11px; color:var(--clr-text-500); max-width:180px;" title="${row.motivoRechazo}">
                            Motivo: ${row.motivoRechazo}
                        </span>
                    ` : ''}
                </div>`
        },
        {
            key: '_actions',
            label: 'Acciones',
            render: (_, row) => {
                if (row.estado === 'PENDIENTE_JEFE') {
                    return `
                        <div style="display:flex; gap:var(--space-2);">
                            <button class="btn btn--success btn--sm" style="background-color: var(--clr-success-500); color: white;"
                                    onclick="AbsencesPage.approveRequest('${row.id}')">✓ Aprobar</button>
                            <button class="btn btn--danger btn--sm" style="background-color: var(--clr-danger-500); color: white;"
                                    onclick="AbsencesPage.rejectRequest('${row.id}')">✗ Rechazar</button>
                        </div>`;
                }
                return `<span style="font-size:var(--font-size-xs); color:var(--clr-text-500);">Sin acciones</span>`;
            }
        }
    ];

    async function render(container) {
        container.innerHTML = '';
        const layout = document.createElement('div');
        layout.className = 'app-layout';
        Sidebar.render(layout);

        const main = document.createElement('main');
        main.className = 'main-content';
        Header.render(main, { title: 'Gestión de Vacaciones', subtitle: 'Bandeja de revisión de solicitudes' });

        const content = document.createElement('div');
        content.className = 'page-content';
        content.innerHTML = `
            <div class="page-header animate-fade-in">
                <div class="page-header__left">
                    <h1>Aprobación de Vacaciones</h1>
                    <p>Revisa y gestiona las solicitudes de vacaciones de los colaboradores</p>
                </div>
                <div class="page-header__actions">
                    <button class="btn btn--secondary" id="btn-refresh-absences">
                        🔄 Actualizar
                    </button>
                </div>
            </div>

            <!-- Panel de Estadísticas Cortas -->
            <div class="grid-stats animate-fade-in" style="grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); margin-bottom: var(--space-6);" id="absences-stats-grid">
                <div class="stat-card"><div class="loading-overlay"><div class="spinner"></div></div></div>
                <div class="stat-card"><div class="loading-overlay"><div class="spinner"></div></div></div>
                <div class="stat-card"><div class="loading-overlay"><div class="spinner"></div></div></div>
            </div>

            <!-- Pestañas y Filtro -->
            <div class="card animate-slide-up">
                <div class="card__header" style="flex-wrap: wrap; gap: var(--space-3);">
                    <!-- Tabs -->
                    <div style="display:flex; background:var(--clr-bg-600); border-radius:var(--radius-md); padding:3px; border:1px solid var(--border-color);">
                        <button class="btn btn--sm" id="tab-pending" style="border-radius:var(--radius-sm); font-weight:600; cursor:pointer; border:none; transition:all var(--transition-fast);">
                            Pendientes
                        </button>
                        <button class="btn btn--sm" id="tab-all" style="border-radius:var(--radius-sm); font-weight:600; cursor:pointer; border:none; transition:all var(--transition-fast); background:transparent; color:var(--clr-text-500);">
                            Historial / Todas
                        </button>
                    </div>
                </div>
                <div id="absences-table-container">
                    <div style="padding:var(--space-8); text-align:center;">
                        <div class="spinner" style="margin:0 auto var(--space-4);"></div>
                        <span>Cargando bandeja de solicitudes...</span>
                    </div>
                </div>
            </div>
        `;

        main.appendChild(content);
        layout.appendChild(main);
        container.appendChild(layout);

        _attachEvents();
        await _loadData();
    }

    function _attachEvents() {
        const refreshBtn = document.getElementById('btn-refresh-absences');
        if (refreshBtn) {
            refreshBtn.addEventListener('click', _loadData);
        }

        const tabPending = document.getElementById('tab-pending');
        const tabAll = document.getElementById('tab-all');

        if (tabPending && tabAll) {
            tabPending.addEventListener('click', () => {
                _activeTab = 'pending';
                tabPending.style.background = 'var(--clr-bg-700)';
                tabPending.style.color = 'var(--clr-text-100)';
                tabAll.style.background = 'transparent';
                tabAll.style.color = 'var(--clr-text-500)';
                _renderTable();
            });

            tabAll.addEventListener('click', () => {
                _activeTab = 'all';
                tabAll.style.background = 'var(--clr-bg-700)';
                tabAll.style.color = 'var(--clr-text-100)';
                tabPending.style.background = 'transparent';
                tabPending.style.color = 'var(--clr-text-500)';
                _renderTable();
            });
        }
    }

    async function _loadData() {
        try {
            // Cargar estadísticas y solicitudes en paralelo
            const [stats, pendingList, allList] = await Promise.all([
                Api.get('/v1/absences/stats').catch(() => null),
                Api.get('/v1/absences/pending').catch(() => []),
                Api.get('/v1/absences').catch(() => []),
            ]);

            _renderStats(stats || { pendientes: 0, aprobadas: 0, rechazadas: 0 });
            _requests = { pending: pendingList, all: allList };

            _renderTable();

        } catch (err) {
            console.error('Error loading absences admin data:', err);
            Toast.error('Error', 'No se pudieron cargar las solicitudes de vacaciones.');
        }
    }

    function _renderStats(stats) {
        const grid = document.getElementById('absences-stats-grid');
        if (!grid) return;

        const cards = [
            {
                value: stats.pendientes,
                label: 'Pendientes de Aprobación',
                icon: 'clock',
                color: 'warning',
            },
            {
                value: stats.aprobadas,
                label: 'Aprobadas',
                icon: 'check',
                color: 'success',
            },
            {
                value: stats.rechazadas,
                label: 'Rechazadas',
                icon: 'chart',
                color: 'danger',
            },
        ];

        grid.innerHTML = cards.map(c => `
            <div class="stat-card stat-card--${c.color}">
                <div class="stat-card__icon stat-card__icon--${c.color}">
                    ${Utils.iconSVG(c.icon)}
                </div>
                <div class="stat-card__value">${c.value}</div>
                <div class="stat-card__label">${c.label}</div>
            </div>
        `).join('');
    }

    function _renderTable() {
        const container = document.getElementById('absences-table-container');
        if (!container) return;

        const data = _activeTab === 'pending' ? _requests.pending : _requests.all;
        const emptyMsg = _activeTab === 'pending' 
            ? 'No hay solicitudes pendientes de aprobación.' 
            : 'No hay registros de solicitudes.';

        DataTable.render(container, {
            columns: COLUMNS,
            data: data,
            emptyMessage: emptyMsg
        });
    }

    async function approveRequest(id) {
        if (!confirm('¿Estás seguro de que deseas APROBAR esta solicitud de vacaciones?')) {
            return;
        }

        try {
            await Api.patch(`/v1/absences/${id}/aprobar`);
            Toast.success('Aprobada', 'La solicitud de vacaciones ha sido aprobada.');
            await _loadData();
        } catch (err) {
            console.error('Error approving request:', err);
            Toast.error('Error', err.message || 'No se pudo aprobar la solicitud.');
        }
    }

    function rejectRequest(id) {
        Modal.open({
            title: 'Rechazar Solicitud de Vacaciones',
            size: 'md',
            body: `
                <form id="reject-vacation-form" style="display:flex; flex-direction:column; gap:var(--space-4);">
                    <p style="font-size:var(--font-size-sm); color:var(--clr-text-300);">
                        Ingresa el motivo del rechazo de la solicitud. Esto será visible para el colaborador.
                    </p>
                    <div class="form-group">
                        <label class="form-label form-label--required" for="reject-reason">Motivo de Rechazo</label>
                        <textarea class="form-input" id="reject-reason" required style="height:100px; padding:var(--space-3);" 
                                  placeholder="Ej: Falta de cobertura en el equipo debido a cierres de mes..."></textarea>
                    </div>
                    <div id="reject-error-msg" class="hidden" 
                         style="background:rgba(239,68,68,0.1); border:1px solid var(--clr-danger-500);
                                border-radius:var(--radius-md); padding:var(--space-3) var(--space-4);
                                font-size:var(--font-size-sm); color:var(--clr-danger-500); display:none;">
                    </div>
                </form>
            `,
            footer: `
                <button type="button" class="btn btn--secondary" onclick="Modal.close()">Cancelar</button>
                <button type="submit" class="btn btn--danger" style="background-color: var(--clr-danger-500); color: white;" id="btn-confirm-reject" form="reject-vacation-form">Rechazar Solicitud</button>
            `
        });

        const form = document.getElementById('reject-vacation-form');
        const reasonInput = document.getElementById('reject-reason');
        const errorEl = document.getElementById('reject-error-msg');
        const submitBtn = document.getElementById('btn-confirm-reject');

        form.addEventListener('submit', async (e) => {
            e.preventDefault();

            const reason = reasonInput.value.trim();
            if (!reason) {
                errorEl.textContent = 'Por favor ingresa un motivo para rechazar la solicitud.';
                errorEl.style.display = 'block';
                return;
            }

            submitBtn.classList.add('btn--loading');
            submitBtn.disabled = true;

            try {
                await Api.patch(`/v1/absences/${id}/rechazar`, { motivo: reason });
                Toast.success('Rechazada', 'La solicitud de vacaciones ha sido rechazada.');
                Modal.close();
                await _loadData();
            } catch (err) {
                console.error('Error rejecting request:', err);
                errorEl.textContent = err.message || 'Error al procesar el rechazo.';
                errorEl.style.display = 'block';
            } finally {
                submitBtn.classList.remove('btn--loading');
                submitBtn.disabled = false;
            }
        });
    }

    return { render, approveRequest, rejectRequest };

})();

window.AbsencesPage = AbsencesPage;
