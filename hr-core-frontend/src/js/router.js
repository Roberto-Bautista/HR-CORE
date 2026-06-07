/* =============================================================
   router.js — SPA Router vanilla (Hash-based)
   
   Maneja la navegación sin recargar la página usando el hash de la URL.
   Ejemplo: /#/employees, /#/absences
   
   Uso:
     Router.navigate('/employees')
     Router.register('/employees', EmployeesPage.render)
   ============================================================= */

const Router = (() => {

    // Mapa de rutas: { path: renderFn }
    const _routes = {};

    // Ruta actual
    let _currentPath = null;

    /**
     * Registra una ruta con su función de renderizado.
     * @param {string} path - Ej: '/employees'
     * @param {Function} renderFn - Función que recibe el elemento contenedor
     */
    function register(path, renderFn) {
        _routes[path] = renderFn;
    }

    /**
     * Navega a una ruta programáticamente.
     */
    function navigate(path) {
        window.location.hash = path;
    }

    /**
     * Obtiene el path actual del hash.
     */
    function getCurrentPath() {
        const hash = window.location.hash.slice(1) || '/dashboard';
        return hash;
    }

    /**
     * Renderiza la vista correspondiente a la ruta actual.
     */
    function _render() {
        const path = getCurrentPath();
        _currentPath = path;

        const root = document.getElementById('view-root');
        if (!root) return;

        // Rutas protegidas: si no está autenticado, ir al login
        if (path !== '/login' && !Auth.isAuthenticated()) {
            navigate('/login');
            return;
        }

        // Si está autenticado y va al login, redirigir al dashboard
        if (path === '/login' && Auth.isAuthenticated()) {
            navigate('/dashboard');
            return;
        }

        // Control de acceso por rol (Trabajador vs Admin)
        if (Auth.isAuthenticated()) {
            const isWorker = Auth.hasRole('WORKER');
            if (isWorker) {
                // El trabajador solo puede ver Dashboard y Mis Vacaciones
                if (path !== '/dashboard' && path !== '/my-absences') {
                    navigate('/dashboard');
                    return;
                }
            } else {
                // El admin no debe ver la vista personal de Mis Vacaciones
                if (path === '/my-absences') {
                    navigate('/dashboard');
                    return;
                }
            }
        }

        const renderFn = _routes[path];

        if (renderFn) {
            root.innerHTML = '';
            renderFn(root);
        } else {
            // 404
            root.innerHTML = `
                <div class="flex-center" style="min-height:100vh; flex-direction:column; gap:1rem;">
                    <div style="font-size:4rem">🔍</div>
                    <h2 style="font-size:1.5rem; color:var(--clr-text-300)">Página no encontrada</h2>
                    <button class="btn btn--primary" onclick="Router.navigate('/dashboard')">
                        Volver al inicio
                    </button>
                </div>`;
        }

        // Actualizar ítem activo del sidebar
        document.querySelectorAll('.nav-item').forEach(item => {
            const itemPath = item.dataset.route;
            item.classList.toggle('nav-item--active', itemPath === path);
        });

        State.set('currentRoute', path);
    }

    /**
     * Inicializa el router: registra rutas y escucha cambios de hash.
     */
    function init() {
        // Registrar todas las rutas de la app
        register('/login',      LoginPage.render);
        register('/dashboard',  DashboardPage.render);
        register('/employees',  EmployeesPage.render);
        register('/absences',   AbsencesPage.render);
        register('/attendance', AttendancePage.render);
        register('/benefits',   BenefitsPage.render);
        register('/my-absences', MyAbsencesPage.render);

        // Escuchar cambios de hash
        window.addEventListener('hashchange', _render);

        // Renderizar al cargar la página
        _render();
    }

    // Arrancar el router cuando el DOM esté listo
    document.addEventListener('DOMContentLoaded', init);

    return { register, navigate, getCurrentPath };

})();

window.Router = Router;
