/* =============================================================
   modal.js — Componente Modal reutilizable
   
   Uso:
     Modal.open({
       title: 'Crear Empleado',
       size: 'lg',           // 'sm' | 'md' | 'lg'
       body: '<p>Contenido</p>',
       footer: `<button class="btn btn--primary" id="btn-confirm">Guardar</button>`,
       onClose: () => {}
     })
     Modal.close()
   ============================================================= */

const Modal = (() => {

    let _onClose = null;

    function open({ title, body, footer, size = 'md', onClose } = {}) {
        _onClose = onClose;

        // Eliminar modal previo si existe
        close();

        const overlay = document.createElement('div');
        overlay.className = 'modal-overlay';
        overlay.id = 'modal-overlay';

        overlay.innerHTML = `
            <div class="modal ${size === 'lg' ? 'modal--lg' : ''}" role="dialog" aria-modal="true" aria-labelledby="modal-title">
                <div class="modal__header">
                    <h2 class="modal__title" id="modal-title">${title}</h2>
                    <button class="modal__close" id="modal-close-btn" aria-label="Cerrar modal">✕</button>
                </div>
                <div class="modal__body" id="modal-body">
                    ${body || ''}
                </div>
                ${footer ? `<div class="modal__footer">${footer}</div>` : ''}
            </div>
        `;

        document.body.appendChild(overlay);

        // Cerrar con el botón X
        document.getElementById('modal-close-btn').addEventListener('click', close);

        // Cerrar haciendo clic fuera del modal
        overlay.addEventListener('click', (e) => {
            if (e.target === overlay) close();
        });

        // Cerrar con Escape
        document.addEventListener('keydown', _handleEscape);

        // Bloquear scroll del body
        document.body.style.overflow = 'hidden';
    }

    function close() {
        const overlay = document.getElementById('modal-overlay');
        if (overlay) overlay.remove();
        document.body.style.overflow = '';
        document.removeEventListener('keydown', _handleEscape);
        if (_onClose) { _onClose(); _onClose = null; }
    }

    function _handleEscape(e) {
        if (e.key === 'Escape') close();
    }

    /** Actualiza solo el contenido del body del modal abierto */
    function setBody(html) {
        const body = document.getElementById('modal-body');
        if (body) body.innerHTML = html;
    }

    return { open, close, setBody };

})();

window.Modal = Modal;
