/* =============================================================
   attendance.js — Página de Asistencia (placeholder Sprint 4)
   ============================================================= */

const AttendancePage = (() => {
    function render(container) {
        container.innerHTML = '';
        const layout = document.createElement('div');
        layout.className = 'app-layout';
        Sidebar.render(layout);
        const main = document.createElement('main');
        main.className = 'main-content';
        Header.render(main, { title: 'Asistencia', subtitle: 'Control de tiempo y marcaciones' });
        const content = document.createElement('div');
        content.className = 'page-content';
        content.innerHTML = `
            <div class="page-header animate-fade-in">
                <div class="page-header__left">
                    <h1>Control de Asistencia</h1>
                    <p>Registro de marcaciones con Patrón Strategy de cálculo de horas</p>
                </div>
            </div>
            <div class="card animate-slide-up">
                <div class="empty-state">
                    <div class="empty-state__icon">⏱️</div>
                    <p class="empty-state__title">Módulo en desarrollo</p>
                    <p class="empty-state__text">El control de asistencia con Strategy Pattern y Redis Cache se implementa en el Sprint 4.</p>
                    <button class="btn btn--primary mt-4" onclick="Router.navigate('/dashboard')">
                        Volver al Dashboard
                    </button>
                </div>
            </div>`;
        main.appendChild(content);
        layout.appendChild(main);
        container.appendChild(layout);
    }
    return { render };
})();

window.AttendancePage = AttendancePage;
