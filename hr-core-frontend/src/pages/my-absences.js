/* =============================================================
   my-absences.js — Vista de vacaciones del colaborador
   ============================================================= */

const MyAbsencesPage = (() => {

    let _requests = [];
    let _balance = null;

    const COLUMNS = [
        {
            key: 'periodo',
            label: 'Período',
            render: (_, row) => `
                <div style="font-weight:500;">
                    📅 ${Utils.formatDate(row.fechaInicio)} al ${Utils.formatDate(row.fechaFin)}
                </div>`
        },
        {
            key: 'diasSolicitados',
            label: 'Días',
            render: val => `<strong style="font-size:1.05rem;">${val}</strong>`
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
                        <span style="font-size:11px; color:var(--clr-danger-400); max-width:180px;" title="${row.motivoRechazo}">
                            Motivo: ${row.motivoRechazo}
                        </span>
                    ` : ''}
                </div>`
        },
        {
            key: '_actions',
            label: 'Acciones',
            render: (_, row) => {
                if (row.estado === 'BORRADOR') {
                    return `
                        <div style="display:flex; flex-direction:column; gap:var(--space-2); min-width:120px;">
                            <button class="btn btn--secondary btn--sm" title="Enviar Solicitud"
                                    onclick="MyAbsencesPage.sendRequest('${row.id}')">🚀 Enviar</button>
                            <button class="btn btn--ghost btn--sm" title="Eliminar Borrador"
                                    onclick="MyAbsencesPage.deleteDraft('${row.id}')" 
                                    style="color: var(--clr-danger-500);">🗑️ Eliminar</button>
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

        Header.render(main, {
            title: 'Mis Vacaciones',
            subtitle: 'Solicita y gestiona tu descanso anual',
        });

        const content = document.createElement('div');
        content.className = 'page-content';

        content.innerHTML = `
            <div class="page-header animate-fade-in">
                <div class="page-header__left">
                    <h1>Mis Vacaciones</h1>
                    <p id="absences-subtitle">Cargando información de vacaciones...</p>
                </div>
                <div class="page-header__actions">
                    <button class="btn btn--primary" id="btn-new-request">
                        ➕ Nueva Solicitud
                    </button>
                </div>
            </div>

            <!-- Tabla de Solicitudes -->
            <div class="card animate-slide-up">
                <div class="card__header">
                    <div>
                        <div class="card__title">Historial de Solicitudes</div>
                        <div class="card__subtitle">Todas tus solicitudes y sus estados de aprobación</div>
                    </div>
                </div>
                <div id="my-requests-table-container">
                    <div style="padding:var(--space-8); text-align:center;">
                        <div class="spinner" style="margin:0 auto var(--space-4);"></div>
                        <span>Cargando historial de solicitudes...</span>
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
        const btnNew = document.getElementById('btn-new-request');
        if (btnNew) {
            btnNew.addEventListener('click', _openRequestModal);
        }
    }

    async function _loadData() {
        const user = Auth.currentUser();
        if (!user) return;

        const tableContainer = document.getElementById('my-requests-table-container');

        try {
            const [balance, requests] = await Promise.all([
                Api.get(`/v1/absences/balance/${user.id}`).catch(() => null),
                Api.get(`/v1/absences/employee/${user.id}`).catch(() => []),
            ]);

            _balance = balance || { diasTotales: 30, diasUsados: 0, diasDisponibles: 30 };
            _requests = requests;

            // Render table
            DataTable.render(tableContainer, {
                columns: COLUMNS,
                data: _requests,
                emptyMessage: 'No tienes solicitudes de vacaciones registradas.'
            });

            const subtitle = document.getElementById('absences-subtitle');
            if (subtitle) {
                subtitle.textContent = `Tienes ${_balance.diasDisponibles} días disponibles · ${_balance.diasUsados} usados de ${_balance.diasTotales} totales`;
            }

        } catch (err) {
            console.error('Error loading absences page data:', err);
            Toast.error('Error', 'No se pudieron cargar los datos de vacaciones.');
        }
    }

    function _renderBalanceCards(balance) {
        const container = document.getElementById('my-balance-cards');
        if (!container) return;

        const cards = [
            {
                value: balance.diasTotales,
                label: 'Días Totales asignados',
                icon: 'gift',
                color: 'indigo',
            },
            {
                value: balance.diasUsados,
                label: 'Días Usados / Gozados',
                icon: 'check',
                color: 'success',
            },
            {
                value: balance.diasDisponibles,
                label: 'Días Disponibles',
                icon: 'calendar',
                color: 'cyan',
            },
        ];

        container.innerHTML = cards.map(c => `
            <div class="stat-card stat-card--${c.color}">
                <div class="stat-card__icon stat-card__icon--${c.color}">
                    ${Utils.iconSVG(c.icon)}
                </div>
                <div class="stat-card__value">${c.value}</div>
                <div class="stat-card__label">${c.label}</div>
            </div>
        `).join('');
    }

    function _openRequestModal() {
        Modal.open({
            title: 'Nueva Solicitud de Vacaciones',
            size: 'md',
            body: `
                <form id="new-vacation-form" style="display:flex; flex-direction:column; gap:var(--space-4);">
                    <div style="display:grid; grid-template-columns:1fr 1fr; gap:var(--space-4);">
                        <div class="form-group">
                            <label class="form-label form-label--required" for="req-fecha-inicio">Fecha de Inicio</label>
                            <input type="date" class="form-input" id="req-fecha-inicio" required />
                        </div>
                        <div class="form-group">
                            <label class="form-label form-label--required" for="req-fecha-fin">Fecha de Fin</label>
                            <input type="date" class="form-input" id="req-fecha-fin" required />
                        </div>
                    </div>

                    <div class="card" style="background:var(--clr-bg-600); border:1px solid var(--border-color); padding:var(--space-4); display:flex; flex-direction:column; gap:var(--space-2);">
                        <div style="display:flex; justify-content:space-between; font-size:var(--font-size-sm);">
                            <span style="color:var(--clr-text-500);">Días disponibles:</span>
                            <strong>${_balance?.diasDisponibles || 0} días</strong>
                        </div>
                        <div style="display:flex; justify-content:space-between; font-size:var(--font-size-sm); border-top:1px solid var(--border-color); padding-top:var(--space-2);">
                            <span style="color:var(--clr-text-500);">Días a solicitar:</span>
                            <strong id="req-dias-calculados" style="color:var(--clr-accent-400);">0 días</strong>
                        </div>
                    </div>

                    <div id="req-error-msg" class="hidden" 
                         style="background:rgba(239,68,68,0.1); border:1px solid var(--clr-danger-500);
                                border-radius:var(--radius-md); padding:var(--space-3) var(--space-4);
                                font-size:var(--font-size-sm); color:var(--clr-danger-500); display:none;">
                    </div>
                </form>
            `,
            footer: `
                <button type="button" class="btn btn--secondary" onclick="Modal.close()">Cancelar</button>
                <button type="submit" class="btn btn--primary" id="btn-save-request" form="new-vacation-form">Guardar Borrador</button>
            `
        });

        const form = document.getElementById('new-vacation-form');
        const startInput = document.getElementById('req-fecha-inicio');
        const endInput = document.getElementById('req-fecha-fin');
        const daysEl = document.getElementById('req-dias-calculados');
        const errorEl = document.getElementById('req-error-msg');
        const saveBtn = document.getElementById('btn-save-request');

        // Set min date of today
        const todayStr = new Date().toISOString().split('T')[0];
        startInput.min = todayStr;
        endInput.min = todayStr;

        function updateCalculatedDays() {
            const startVal = startInput.value;
            const endVal = endInput.value;
            errorEl.style.display = 'none';

            if (startVal && endVal) {
                const start = new Date(startVal + 'T00:00:00');
                const end = new Date(endVal + 'T00:00:00');

                if (end < start) {
                    daysEl.textContent = '0 días';
                    return;
                }

                // Diff in days + 1 (same as domain logic)
                const diffTime = Math.abs(end - start);
                const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1;
                daysEl.textContent = `${diffDays} días`;

                if (diffDays > _balance.diasDisponibles) {
                    errorEl.textContent = `Advertencia: Solicitas ${diffDays} días pero solo tienes ${_balance.diasDisponibles} disponibles.`;
                    errorEl.style.display = 'block';
                }
            } else {
                daysEl.textContent = '0 días';
            }
        }

        startInput.addEventListener('change', () => {
            endInput.min = startInput.value;
            updateCalculatedDays();
        });
        endInput.addEventListener('change', updateCalculatedDays);

        form.addEventListener('submit', async (e) => {
            e.preventDefault();

            const startVal = startInput.value;
            const endVal = endInput.value;

            if (!startVal || !endVal) {
                errorEl.textContent = 'Por favor selecciona ambas fechas.';
                errorEl.style.display = 'block';
                return;
            }

            const start = new Date(startVal + 'T00:00:00');
            const end = new Date(endVal + 'T00:00:00');

            if (end < start) {
                errorEl.textContent = 'La fecha de fin no puede ser anterior a la de inicio.';
                errorEl.style.display = 'block';
                return;
            }

            const diffTime = Math.abs(end - start);
            const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1;

            if (diffDays > _balance.diasDisponibles) {
                errorEl.textContent = `No puedes solicitar ${diffDays} días de vacaciones. Tu saldo disponible es de ${_balance.diasDisponibles} días.`;
                errorEl.style.display = 'block';
                return;
            }

            saveBtn.classList.add('btn--loading');
            saveBtn.disabled = true;

            const user = Auth.currentUser();

            try {
                await Api.post('/v1/absences', {
                    empleadoId: user.id,
                    fechaInicio: startVal,
                    fechaFin: endVal
                });

                Toast.success('Éxito', 'Borrador de solicitud creado correctamente.');
                Modal.close();
                await _loadData();
            } catch (err) {
                console.error('Error saving vacation request:', err);
                errorEl.textContent = err.message || 'Error al guardar la solicitud.';
                errorEl.style.display = 'block';
            } finally {
                saveBtn.classList.remove('btn--loading');
                saveBtn.disabled = false;
            }
        });
    }

    async function sendRequest(id) {
        if (!confirm('¿Estás seguro de enviar esta solicitud para la aprobación de tu jefe? Una vez enviada no podrás modificarla ni eliminarla.')) {
            return;
        }

        try {
            await Api.patch(`/v1/absences/${id}/enviar`);
            Toast.success('Solicitud enviada', 'La solicitud ha sido enviada al jefe para su aprobación.');
            await _loadData();
        } catch (err) {
            console.error('Error sending vacation request:', err);
            Toast.error('Error', err.message || 'No se pudo enviar la solicitud.');
        }
    }

    async function deleteDraft(id) {
        if (!confirm('¿Estás seguro de que deseas eliminar este borrador de solicitud?')) {
            return;
        }

        try {
            await Api.del(`/v1/absences/${id}`);
            Toast.success('Borrador eliminado', 'El borrador de solicitud ha sido eliminado.');
            await _loadData();
        } catch (err) {
            console.error('Error deleting draft:', err);
            Toast.error('Error', err.message || 'No se pudo eliminar el borrador.');
        }
    }

    return { render, sendRequest, deleteDraft };

})();

window.MyAbsencesPage = MyAbsencesPage;
