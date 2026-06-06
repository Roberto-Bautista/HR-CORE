/* =============================================================
   dashboard.js — Página de Dashboard (KPIs y resumen)
   Sprint 2: conectado a estadísticas reales del backend
   ============================================================= */

const DashboardPage = (() => {

    async function render(container) {
        container.innerHTML = '';

        const layout = document.createElement('div');
        layout.className = 'app-layout';

        Sidebar.render(layout);

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

            <!-- KPI Cards (se llenarán cuando la API responda) -->
            <div class="grid-stats animate-fade-in" id="stats-grid">
                <div class="stat-card"><div class="loading-overlay"><div class="spinner"></div></div></div>
                <div class="stat-card"><div class="loading-overlay"><div class="spinner"></div></div></div>
                <div class="stat-card"><div class="loading-overlay"><div class="spinner"></div></div></div>
                <div class="stat-card"><div class="loading-overlay"><div class="spinner"></div></div></div>
            </div>

            <!-- Segunda fila: empleados recientes -->
            <div class="card animate-slide-up">
                <div class="card__header">
                    <div>
                        <div class="card__title">Empleados Registrados</div>
                        <div class="card__subtitle">Últimos empleados en el sistema</div>
                    </div>
                    <button class="btn btn--primary btn--sm" onclick="Router.navigate('/employees')">
                        Ver todos →
                    </button>
                </div>
                <div id="recent-employees">
                    <div class="loading-overlay"><div class="spinner"></div><span>Cargando...</span></div>
                </div>
            </div>
        `;

        main.appendChild(content);
        layout.appendChild(main);
        container.appendChild(layout);

        // Cargar datos reales del backend
        await _loadDashboardData();
    }

    async function _loadDashboardData() {
        try {
            // Cargar estadísticas y empleados en paralelo
            const [stats, employees] = await Promise.all([
                Api.get('/v1/employees/stats').catch(() => null),
                Api.get('/v1/employees').catch(() => []),
            ]);

            // Llenar las stat cards
            const statsData = stats || { totalEmpleados: 0, empleadosActivos: 0, empleadosInactivos: 0 };
            _renderStatCards(statsData, employees.length);

            // Llenar los últimos 5 empleados
            _renderRecentEmployees(employees.slice(0, 5));

        } catch (err) {
            Toast.error('Error de conexión', 'No se pudo conectar al servidor. ¿Está corriendo el backend?');
            _renderStatCards({ totalEmpleados: 0, empleadosActivos: 0, empleadosInactivos: 0 }, 0);
            _renderRecentEmployees([]);
        }
    }

    function _renderStatCards(stats, totalRegistered) {
        const grid = document.getElementById('stats-grid');
        if (!grid) return;

        const cards = [
            {
                value: stats.totalEmpleados,
                label: 'Total Empleados',
                icon: 'users',
                color: 'indigo',
            },
            {
                value: stats.empleadosActivos,
                label: 'Empleados Activos',
                icon: 'check',
                color: 'success',
            },
            {
                value: stats.empleadosInactivos,
                label: 'Inactivos / Cesados',
                icon: 'clock',
                color: 'warning',
            },
            {
                value: '—',
                label: 'Vacaciones Pendientes',
                icon: 'calendar',
                color: 'cyan',
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

    function _renderRecentEmployees(employees) {
        const container = document.getElementById('recent-employees');
        if (!container) return;

        if (employees.length === 0) {
            container.innerHTML = `
                <div class="empty-state" style="padding:var(--space-8);">
                    <div class="empty-state__icon">👥</div>
                    <p class="empty-state__title">Sin empleados aún</p>
                    <p class="empty-state__text">Crea tu primer empleado para ver datos aquí.</p>
                    <button class="btn btn--primary mt-4" onclick="Router.navigate('/employees')">
                        ➕ Crear Empleado
                    </button>
                </div>
            `;
            return;
        }

        container.innerHTML = employees.map(emp => `
            <div style="display:flex;align-items:center;gap:var(--space-3);
                        padding:var(--space-3) 0;border-bottom:1px solid var(--border-color);">
                <div class="avatar">${Utils.getInitials(emp.nombre, emp.apellido)}</div>
                <div style="flex:1;">
                    <p style="font-size:var(--font-size-sm);font-weight:500;">${emp.nombre} ${emp.apellido}</p>
                    <p style="font-size:var(--font-size-xs);color:var(--clr-text-500);">
                        ${emp.cargo} · ${emp.departamento}
                    </p>
                </div>
                <span class="badge ${Utils.statusToBadge(emp.status)}">
                    ${Utils.statusToLabel(emp.status)}
                </span>
            </div>
        `).join('');
    }

    return { render };

})();

window.DashboardPage = DashboardPage;
