/* =============================================================
   sidebar.js — Componente Sidebar de navegación
   ============================================================= */

const Sidebar = (() => {

    const NAV_ITEMS = [
        { group: 'Principal' },
        { path: '/dashboard',  label: 'Dashboard',    icon: 'home' },

        { group: 'Módulos HR' },
        { path: '/employees',  label: 'Empleados',    icon: 'users',    badge: null },
        { path: '/absences',   label: 'Vacaciones',   icon: 'calendar', badge: '3' },
        { path: '/attendance', label: 'Asistencia',   icon: 'clock' },
        { path: '/benefits',   label: 'Beneficios',   icon: 'gift' },

        { group: 'Administración' },
        { path: '/rbac',       label: 'Roles',        icon: 'shield', roles: ['ADMIN'] },
        { path: '/audit',      label: 'Auditoría',    icon: 'chart',  roles: ['ADMIN', 'RRHH'] },
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
                        <div class="sidebar__user-role">${user?.rol || 'Sin rol'}</div>
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
    }

    function _buildNavItems(user) {
        return NAV_ITEMS.map(item => {
            // Separador de grupo
            if (item.group) {
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
