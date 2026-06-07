/* =============================================================
   sidebar.js — Componente Sidebar de navegación
   ============================================================= */

const Sidebar = (() => {

    const NAV_ITEMS = [
        { group: 'Principal' },
        { path: '/dashboard',  label: 'Dashboard',    icon: 'home' },

        { group: 'Mi Espacio', roles: ['WORKER'] },
        { path: '/my-absences', label: 'Mis Vacaciones', icon: 'calendar', roles: ['WORKER'] },

        { group: 'Módulos HR', roles: ['ADMIN'] },
        { path: '/employees',  label: 'Empleados',    icon: 'users',    badge: null, roles: ['ADMIN'] },
        { path: '/absences',   label: 'Vacaciones',   icon: 'calendar', badge: null, roles: ['ADMIN'] },
        { path: '/attendance', label: 'Asistencia',   icon: 'clock',    roles: ['ADMIN', 'WORKER'] },
        { path: '/benefits',   label: 'Beneficios',   icon: 'gift',     roles: ['ADMIN'] },

        { group: 'Administración', roles: ['ADMIN'] },
        { path: '/rbac',       label: 'Roles',        icon: 'shield',   roles: ['ADMIN'] },
        { path: '/audit',      label: 'Auditoría',    icon: 'chart',    roles: ['ADMIN', 'RRHH'] },
    ];

    function render(container) {
        const user = Auth.currentUser();

        const sidebar = document.createElement('aside');
        sidebar.className = 'sidebar';
        sidebar.id = 'sidebar';

        sidebar.innerHTML = `
            <div class="sidebar__logo">
                <div class="sidebar__logo-icon">🏢</div>
                <span class="sidebar__logo-text">HR<span>-Core</span></span>
            </div>

            <nav class="sidebar__nav" role="navigation" aria-label="Navegación principal">
                ${_buildNavItems(user)}
            </nav>

            <div class="sidebar__footer">
                <div class="sidebar__user" id="sidebar-user" role="button" tabindex="0"
                     aria-label="Menú de usuario" title="Cerrar sesión">
                    <div class="avatar">
                        ${user ? Utils.getInitials(user.nombre, user.apellido) : 'U'}
                    </div>
                    <div class="sidebar__user-info">
                        <div class="sidebar__user-name">
                            ${user ? `${user.nombre} ${user.apellido}` : 'Usuario'}
                        </div>
                        <div class="sidebar__user-role">
                            ${user?.rol === 'ADMIN' ? 'Administrador' : 'Colaborador'}
                        </div>
                    </div>
                    <span style="color:var(--clr-text-500); font-size:0.9rem">🚪</span>
                </div>
            </div>
        `;

        container.appendChild(sidebar);

        // Evento de logout
        sidebar.querySelector('#sidebar-user').addEventListener('click', () => {
            if (confirm('¿Seguro que quieres cerrar sesión?')) {
                Auth.logout();
            }
        });

        // Evento de navegación en items
        sidebar.querySelectorAll('.nav-item[data-route]').forEach(item => {
            item.addEventListener('click', () => {
                Router.navigate(item.dataset.route);
            });
        });

        // Marcar la ruta activa
        _updateActive(sidebar);

        // Cargar badge de vacaciones pendientes si es admin
        if (user && user.rol === 'ADMIN') {
            _loadPendingVacationBadge(sidebar);
        }
    }

    async function _loadPendingVacationBadge(sidebar) {
        try {
            const stats = await Api.get('/v1/absences/stats').catch(() => null);
            if (stats && stats.pendientes > 0) {
                const navItem = sidebar.querySelector('[data-route="/absences"]');
                if (navItem) {
                    let badge = navItem.querySelector('.nav-item__badge');
                    if (!badge) {
                        badge = document.createElement('span');
                        badge.className = 'nav-item__badge';
                        navItem.appendChild(badge);
                    }
                    badge.textContent = stats.pendientes;
                }
            }
        } catch (err) {
            console.error('Error loading pending vacation badge:', err);
        }
    }

    function _buildNavItems(user) {
        return NAV_ITEMS.map(item => {
            // Separador de grupo
            if (item.group) {
                if (item.roles && user && !item.roles.includes(user.rol)) {
                    return '';
                }
                return `<div class="sidebar__section-label">${item.group}</div>`;
            }

            // Filtrar por rol si es necesario
            if (item.roles && user && !item.roles.includes(user.rol)) {
                return '';
            }

            const badge = item.badge
                ? `<span class="nav-item__badge">${item.badge}</span>`
                : '';

            return `
                <div class="nav-item" data-route="${item.path}"
                     role="button" tabindex="0" aria-label="${item.label}">
                    <span class="nav-item__icon">${Utils.iconSVG(item.icon)}</span>
                    <span class="nav-item__text">${item.label}</span>
                    ${badge}
                </div>
            `;
        }).join('');
    }

    function _updateActive(sidebar) {
        const current = Router.getCurrentPath();
        sidebar.querySelectorAll('.nav-item[data-route]').forEach(item => {
            item.classList.toggle('nav-item--active', item.dataset.route === current);
        });
    }

    return { render };

})();

window.Sidebar = Sidebar;
