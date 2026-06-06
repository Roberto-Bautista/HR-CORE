/* =============================================================
   absences.js — Página de Vacaciones (placeholder Sprint 3)
   ============================================================= */

const AbsencesPage = (() => {

    function render(container) {
        container.innerHTML = '';
        const layout = document.createElement('div');
        layout.className = 'app-layout';
        Sidebar.render(layout);

        const main = document.createElement('main');
        main.className = 'main-content';
        Header.render(main, { title: 'Vacaciones', subtitle: 'Gestión de solicitudes de ausencia' });

        const content = document.createElement('div');
        content.className = 'page-content';
        content.innerHTML = `
            <div class="page-header animate-fade-in">
                <div class="page-header__left">
                    <h1>Gestión de Vacaciones</h1>
                    <p>Flujo de aprobación con Patrón State</p>
                </div>
                <button class="btn btn--primary" onclick="Toast.info('Sprint 3', 'Este módulo se implementa en el Sprint 3.')">
                    ➕ Solicitar Vacaciones
                </button>
            </div>
            <div class="card animate-slide-up">
                <div class="empty-state">
                    <div class="empty-state__icon">🏖️</div>
                    <p class="empty-state__title">Módulo en desarrollo</p>
                    <p class="empty-state__text">La gestión de vacaciones con el Patrón State se implementará en el Sprint 3.</p>
                    <button class="btn btn--primary mt-4" onclick="Router.navigate('/dashboard')">
                        Volver al Dashboard
                    </button>
                </div>
            </div>
        `;

        main.appendChild(content);
        layout.appendChild(main);
        container.appendChild(layout);
    }

    return { render };
})();

window.AbsencesPage = AbsencesPage;
