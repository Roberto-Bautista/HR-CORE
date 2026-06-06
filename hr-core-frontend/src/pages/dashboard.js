/* =============================================================
   dashboard.js — Página de Dashboard (KPIs y resumen)
   ============================================================= */

const DashboardPage = (() => {

    /** Datos de ejemplo para cuando el backend no esté listo */
    const MOCK_STATS = {
        totalEmpleados:     148,
        empleadosActivos:   142,
        vacacionesPendientes: 7,
        marcacionesHoy:     136,
        beneficiosActivos:  5,
    };

    function render(container) {
        container.innerHTML = '';

        // Layout principal
        const layout = document.createElement('div');
        layout.className = 'app-layout';

        // Sidebar
        Sidebar.render(layout);

        // Contenido principal
        const main = document.createElement('main');
        main.className = 'main-content';

        Header.render(main, {
            title: 'Dashboard',
            subtitle: Utils.formatDate(new Date().toISOString()),
        });

        const content = document.createElement('div');
        content.className = 'page-content';

        const user = Auth.currentUser();

        content.innerHTML = `
            <div class="page-header animate-fade-in">
                <div class="page-header__left">
                    <h1>Buenos días, ${user?.nombre || 'Usuario'} 👋</h1>
                    <p>Aquí está el resumen de hoy en HR-Core</p>
                </div>
            </div>

            <!-- KPI Cards -->
            <div class="grid-stats animate-fade-in" id="stats-grid">
                ${_buildStatCards(MOCK_STATS)}
            </div>

            <!-- Segunda fila -->
            <div class="grid-2" style="margin-top:var(--space-4);">

                <!-- Actividad reciente -->
                <div class="card animate-slide-up">
                    <div class="card__header">
                        <div>
                            <div class="card__title">Actividad Reciente</div>
                            <div class="card__subtitle">Últimas acciones en el sistema</div>
                        </div>
                    </div>
                    <div id="recent-activity">
                        ${_buildActivityList()}
                    </div>
                </div>

                <!-- Solicitudes pendientes -->
                <div class="card animate-slide-up">
                    <div class="card__header">
                        <div>
                            <div class="card__title">Solicitudes Pendientes</div>
                            <div class="card__subtitle">Requieren tu atención</div>
                        </div>
                        <button class="btn btn--ghost btn--sm" onclick="Router.navigate('/absences')">
                            Ver todas
                        </button>
                    </div>
                    <div id="pending-requests">
                        ${_buildPendingRequests()}
                    </div>
                </div>

            </div>
        `;

        main.appendChild(content);
        layout.appendChild(main);
        container.appendChild(layout);
    }

    function _buildStatCards(stats) {
        const cards = [
            {
                value: stats.totalEmpleados,
                label: 'Total Empleados',
                icon: 'users',
                color: 'indigo',
                trend: '+3',
                trendDir: 'up',
            },
            {
                value: stats.vacacionesPendientes,
                label: 'Vacaciones Pendientes',
                icon: 'calendar',
                color: 'warning',
                trend: '+2',
                trendDir: 'up',
            },
            {
                value: stats.marcacionesHoy,
                label: 'Marcaciones Hoy',
                icon: 'clock',
                color: 'cyan',
                trend: '91%',
                trendDir: 'up',
            },
            {
                value: stats.beneficiosActivos,
                label: 'Beneficios Activos',
                icon: 'gift',
                color: 'success',
                trend: 'Estable',
                trendDir: 'up',
            },
        ];

        return cards.map(c => `
            <div class="stat-card stat-card--${c.color}">
                <div class="stat-card__icon stat-card__icon--${c.color}">
                    ${Utils.iconSVG(c.icon)}
                </div>
                <div class="stat-card__value">${c.value}</div>
                <div class="stat-card__label">${c.label}</div>
                <span class="stat-card__trend stat-card__trend--${c.trendDir}">
                    ${c.trendDir === 'up' ? '↑' : '↓'} ${c.trend}
                </span>
            </div>
        `).join('');
    }

    function _buildActivityList() {
        const items = [
            { icon: '👤', text: 'María López fue dada de alta',         time: 'Hace 15 min' },
            { icon: '✅', text: 'Vacación de Carlos Ruiz aprobada',      time: 'Hace 1 hora' },
            { icon: '🕐', text: '142 marcaciones registradas esta mañana', time: 'Hace 2 horas' },
            { icon: '🎁', text: 'Pedro García se enroló en EPS Salud',   time: 'Ayer' },
        ];

        return items.map(item => `
            <div style="display:flex;align-items:center;gap:var(--space-3);
                        padding:var(--space-3) 0;border-bottom:1px solid var(--border-color);">
                <span style="font-size:1.25rem;">${item.icon}</span>
                <div style="flex:1;">
                    <p style="font-size:var(--font-size-sm);">${item.text}</p>
                    <p style="font-size:var(--font-size-xs);color:var(--clr-text-500);">${item.time}</p>
                </div>
            </div>
        `).join('');
    }

    function _buildPendingRequests() {
        const requests = [
            { name: 'Ana Torres',     type: 'Vacaciones', days: 5,  status: 'PENDIENTE_JEFE' },
            { name: 'Juan Pérez',     type: 'Vacaciones', days: 3,  status: 'PENDIENTE_RRHH' },
            { name: 'Sofia Mendez',   type: 'Vacaciones', days: 10, status: 'PENDIENTE_JEFE' },
        ];

        return requests.map(r => `
            <div style="display:flex;align-items:center;gap:var(--space-3);
                        padding:var(--space-3) 0;border-bottom:1px solid var(--border-color);">
                <div class="avatar">${Utils.getInitials(r.name.split(' ')[0], r.name.split(' ')[1])}</div>
                <div style="flex:1;">
                    <p style="font-size:var(--font-size-sm);font-weight:500;">${r.name}</p>
                    <p style="font-size:var(--font-size-xs);color:var(--clr-text-500);">
                        ${r.type} · ${r.days} días
                    </p>
                </div>
                <span class="badge ${Utils.statusToBadge(r.status)}">
                    ${Utils.statusToLabel(r.status)}
                </span>
            </div>
        `).join('');
    }

    return { render };

})();

window.DashboardPage = DashboardPage;
