/* =============================================================
   header.js — Componente Header reutilizable
   ============================================================= */

const Header = (() => {

    /**
     * @param {HTMLElement} container - Elemento donde se inserta el header
     * @param {Object} options - { title, subtitle }
     */
    function render(container, { title = 'HR-Core', subtitle = '' } = {}) {
        const header = document.createElement('header');
        header.className = 'header';
        header.id = 'app-header';

        header.innerHTML = `
            <div class="header__left">
                <h1 class="header__title">${title}</h1>
                ${subtitle ? `<p class="header__subtitle">${subtitle}</p>` : ''}
            </div>
            <div class="header__right">
                <button class="header__btn header__btn-notif" id="btn-notifications"
                        aria-label="Notificaciones" title="Notificaciones">
                    🔔
                </button>
                <button class="header__btn" id="btn-settings"
                        aria-label="Configuración" title="Configuración">
                    ⚙️
                </button>
            </div>
        `;

        container.appendChild(header);

        // Acciones de botones del header
        header.querySelector('#btn-notifications').addEventListener('click', () => {
            Toast.info('Notificaciones', 'No tienes notificaciones nuevas.');
        });

        header.querySelector('#btn-settings').addEventListener('click', () => {
            Toast.info('Configuración', 'Próximamente disponible.');
        });
    }

    return { render };

})();

window.Header = Header;
