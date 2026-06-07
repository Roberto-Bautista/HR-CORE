/* =============================================================
   worker-dashboard.js — Dashboard personal del colaborador
   ============================================================= */

const WorkerDashboardPage = (() => {

    async function render(container) {
        container.innerHTML = '';

        const layout = document.createElement('div');
        layout.className = 'app-layout';

        Sidebar.render(layout);

        const main = document.createElement('main');
        main.className = 'main-content';

        const user = Auth.currentUser();

        Header.render(main, {
            title: 'Mi Espacio',
            subtitle: Utils.formatDate(new Date().toISOString()),
        });

        const content = document.createElement('div');
        content.className = 'page-content';

        content.innerHTML = `
            <div class="page-header animate-fade-in">
                <div class="page-header__left">
                    <h1>¡Hola, ${user?.nombre || 'Colaborador'}! 👋</h1>
                    <p>Bienvenido a tu panel personal de HR-Core.</p>
                </div>
            </div>

            <div class="grid-stats animate-fade-in" style="grid-template-columns: repeat(auto-fit, minmax(240px, 1fr)); margin-bottom: var(--space-6);" id="worker-balance-cards">
                <div class="stat-card"><div class="loading-overlay"><div class="spinner"></div></div></div>
                <div class="stat-card"><div class="loading-overlay"><div class="spinner"></div></div></div>
                <div class="stat-card"><div class="loading-overlay"><div class="spinner"></div></div></div>
            </div>

            <div style="display: grid; grid-template-columns: 1fr 320px; gap: var(--space-6); align-items: start;" class="animate-slide-up">
                <!-- Solicitudes Recientes -->
                <div class="card">
                    <div class="card__header">
                        <div>
                            <div class="card__title">Mis Solicitudes Recientes</div>
                            <div class="card__subtitle">Historial de vacaciones solicitadas</div>
                        </div>
                        <button class="btn btn--ghost btn--sm" onclick="Router.navigate('/my-absences')">
                            Ver todas
                        </button>
                    </div>
                    <div id="worker-recent-requests" style="padding: var(--space-4);">
                        <div class="loading-overlay"><div class="spinner"></div><span>Cargando solicitudes...</span></div>
                    </div>
                </div>

                <!-- Detalles de Colaborador -->
                <div class="card">
                    <div class="card__header">
                        <div>
                            <div class="card__title">Mi Perfil Profesional</div>
                            <div class="card__subtitle">Datos institucionales</div>
                        </div>
                    </div>
                    <div style="padding: var(--space-5); display: flex; flex-direction: column; gap: var(--space-4);">
                        <div style="display:flex; align-items:center; gap:var(--space-3); border-bottom: 1px solid var(--border-color); padding-bottom: var(--space-4);">
                            <div class="avatar avatar--lg" style="width:48px; height:48px; font-size:1.2rem;">
                                ${Utils.getInitials(user?.nombre || '', user?.apellido || '')}
                            </div>
                            <div>
                                <div style="font-weight: 600; font-size: 1.05rem;">${user?.nombre} ${user?.apellido}</div>
                                <div style="font-size: var(--font-size-xs); color: var(--clr-text-500);">${user?.codigo || ''}</div>
                            </div>
                        </div>
                        
                        <div>
                            <label style="font-size: var(--font-size-xs); color: var(--clr-text-500); font-weight:500;">CARGO</label>
                            <div style="font-size: var(--font-size-sm); font-weight: 500; margin-top: 2px;" id="worker-profile-cargo">Cargando...</div>
                        </div>

                        <div>
                            <label style="font-size: var(--font-size-xs); color: var(--clr-text-500); font-weight:500;">DEPARTAMENTO</label>
                            <div style="font-size: var(--font-size-sm); font-weight: 500; margin-top: 2px;" id="worker-profile-dept">Cargando...</div>
                        </div>

                        <div>
                            <label style="font-size: var(--font-size-xs); color: var(--clr-text-500); font-weight:500;">CORREO ELECTRÓNICO</label>
                            <div style="font-size: var(--font-size-sm); font-weight: 500; margin-top: 2px;">${user?.email}</div>
                        </div>

                        <div>
                            <label style="font-size: var(--font-size-xs); color: var(--clr-text-500); font-weight:500;">FECHA DE INGRESO</label>
                            <div style="font-size: var(--font-size-sm); font-weight: 500; margin-top: 2px;" id="worker-profile-ingreso">Cargando...</div>
                        </div>
                    </div>
                </div>
            </div>
        `;

        main.appendChild(content);
        layout.appendChild(main);
        container.appendChild(layout);

        await _loadWorkerData();
    }

    async function _loadWorkerData() {
        const user = Auth.currentUser();
        if (!user) return;

        try {
            // Cargar datos del colaborador, balance y solicitudes
            const [employee, balance, requests] = await Promise.all([
                Api.get(`/v1/employees/${user.id}`).catch(() => null),
                Api.get(`/v1/absences/balance/${user.id}`).catch(() => null),
                Api.get(`/v1/absences/employee/${user.id}`).catch(() => []),
            ]);

            // Llenar datos de perfil
            if (employee) {
                const cargoEl = document.getElementById('worker-profile-cargo');
                const deptEl = document.getElementById('worker-profile-dept');
                const ingresoEl = document.getElementById('worker-profile-ingreso');
                if (cargoEl) cargoEl.textContent = employee.cargo;
                if (deptEl) deptEl.textContent = employee.departamento;
                if (ingresoEl) ingresoEl.textContent = Utils.formatDate(employee.fechaAlta);
            }

            // Llenar balance cards
            _renderBalanceCards(balance || { diasTotales: 30, diasUsados: 0, diasDisponibles: 30 });

            // Llenar solicitudes recientes (últimas 3)
            _renderRecentRequests(requests.slice(0, 3));

        } catch (err) {
            console.error('Error loading worker dashboard data:', err);
            Toast.error('Error', 'No se pudieron cargar tus datos de panel de control.');
        }
    }

    function _renderBalanceCards(balance) {
        const container = document.getElementById('worker-balance-cards');
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

    function _renderRecentRequests(requests) {
        const container = document.getElementById('worker-recent-requests');
        if (!container) return;

        if (requests.length === 0) {
            container.innerHTML = `
                <div class="empty-state" style="padding: var(--space-6);">
                    <div class="empty-state__icon">✈️</div>
                    <p class="empty-state__title">Sin solicitudes</p>
                    <p class="empty-state__text">Aún no has creado ninguna solicitud de vacaciones.</p>
                </div>
            `;
            return;
        }

        container.innerHTML = `
            <div style="display:flex; flex-direction:column; gap:var(--space-3);">
                ${requests.map(req => {
                    const statusClass = Utils.statusToBadge(req.estado);
                    return `
                        <div style="display:flex; align-items:center; justify-content:space-between; 
                                    padding:var(--space-3); border:1px solid var(--border-color); 
                                    border-radius:var(--radius-md); background:var(--clr-bg-600);">
                            <div>
                                <div style="font-weight:600; font-size:var(--font-size-sm);">
                                    ${Utils.formatDate(req.fechaInicio)} al ${Utils.formatDate(req.fechaFin)}
                                </div>
                                <div style="font-size:var(--font-size-xs); color:var(--clr-text-500); margin-top:2px;">
                                    ${req.diasSolicitados} días solicitados · Creado el ${Utils.formatDate(req.createdAt)}
                                </div>
                            </div>
                            <div style="display:flex; flex-direction:column; align-items:flex-end; gap:var(--space-1);">
                                <span class="badge ${statusClass}">${Utils.statusToLabel(req.estado)}</span>
                                ${req.estado === 'RECHAZADA' && req.motivoRechazo ? `
                                    <div style="font-size:10px; color:var(--clr-danger-400); max-width:180px; text-align:right;" title="${req.motivoRechazo}">
                                        Motivo: ${req.motivoRechazo}
                                    </div>
                                ` : ''}
                            </div>
                        </div>
                    `;
                }).join('')}
            </div>
        `;
    }

    return { render };

})();

window.WorkerDashboardPage = WorkerDashboardPage;
