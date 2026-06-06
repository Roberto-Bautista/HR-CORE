/* =============================================================
   benefits.js — Página de Beneficios (placeholder Sprint 5)
   ============================================================= */

const BenefitsPage = (() => {
    function render(container) {
        container.innerHTML = '';
        const layout = document.createElement('div');
        layout.className = 'app-layout';
        Sidebar.render(layout);
        const main = document.createElement('main');
        main.className = 'main-content';
        Header.render(main, { title: 'Beneficios', subtitle: 'Portal de beneficios corporativos' });
        const content = document.createElement('div');
        content.className = 'page-content';
        content.innerHTML = `
            <div class="page-header animate-fade-in">
                <div class="page-header__left">
                    <h1>Gestión de Beneficios</h1>
                    <p>Elegibilidad con Patrón Strategy (salario, antigüedad, cargo)</p>
                </div>
            </div>
            <div class="card animate-slide-up">
                <div class="empty-state">
                    <div class="empty-state__icon">🎁</div>
                    <p class="empty-state__title">Módulo en desarrollo</p>
                    <p class="empty-state__text">El portal de beneficios con Strategy de elegibilidad se implementa en el Sprint 5.</p>
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

window.BenefitsPage = BenefitsPage;
