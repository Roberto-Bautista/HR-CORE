/* =============================================================
   toast.js — Componente de notificaciones Toast reutilizable
   
   Uso:
     Toast.success('Empleado creado correctamente')
     Toast.error('No se pudo guardar', 'Detalle del error')
     Toast.warning('Atención')
     Toast.info('Información')
   ============================================================= */

const Toast = (() => {

    const ICONS = {
        success: '✅',
        error:   '❌',
        warning: '⚠️',
        info:    'ℹ️',
    };

    function show(type, title, message = '', duration = 4000) {
        const container = document.getElementById('toast-container');
        if (!container) return;

        const id = Utils.uid();
        const toast = document.createElement('div');
        toast.className = `toast toast--${type}`;
        toast.id = id;
        toast.setAttribute('role', 'alert');

        toast.innerHTML = `
            <span class="toast__icon">${ICONS[type]}</span>
            <div class="toast__content">
                <div class="toast__title">${title}</div>
                ${message ? `<div class="toast__message">${message}</div>` : ''}
            </div>
            <span class="toast__close" onclick="Toast.dismiss('${id}')" aria-label="Cerrar">✕</span>
        `;

        container.appendChild(toast);

        // Auto-dismiss
        if (duration > 0) {
            setTimeout(() => dismiss(id), duration);
        }

        return id;
    }

    function dismiss(id) {
        const toast = document.getElementById(id);
        if (!toast) return;
        toast.classList.add('toast--leaving');
        toast.addEventListener('animationend', () => toast.remove(), { once: true });
    }

    const success = (title, msg, dur)  => show('success', title, msg, dur);
    const error   = (title, msg, dur)  => show('error',   title, msg, dur);
    const warning = (title, msg, dur)  => show('warning', title, msg, dur);
    const info    = (title, msg, dur)  => show('info',    title, msg, dur);

    return { show, dismiss, success, error, warning, info };

})();

window.Toast = Toast;
