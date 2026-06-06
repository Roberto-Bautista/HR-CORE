/* =============================================================
   utils.js — Funciones de utilidad reutilizables
   Disponibles globalmente como window.Utils.*
   ============================================================= */

const Utils = (() => {

    /** Formatea una fecha ISO a "DD/MM/YYYY" */
    function formatDate(isoString) {
        if (!isoString) return '—';
        const d = new Date(isoString);
        return d.toLocaleDateString('es-PE', { day: '2-digit', month: '2-digit', year: 'numeric' });
    }

    /** Formatea moneda en soles */
    function formatCurrency(amount) {
        if (amount == null) return '—';
        return new Intl.NumberFormat('es-PE', { style: 'currency', currency: 'PEN' }).format(amount);
    }

    /** Obtiene las iniciales de un nombre completo */
    function getInitials(nombre = '', apellido = '') {
        return `${nombre.charAt(0)}${apellido.charAt(0)}`.toUpperCase();
    }

    /** Trunca un texto largo */
    function truncate(text, maxLength = 40) {
        if (!text) return '—';
        return text.length > maxLength ? text.slice(0, maxLength) + '…' : text;
    }

    /** Debounce: retrasa la ejecución de una función */
    function debounce(fn, delay = 300) {
        let timer;
        return function (...args) {
            clearTimeout(timer);
            timer = setTimeout(() => fn.apply(this, args), delay);
        };
    }

    /** Genera un ID único simple */
    function uid() {
        return `hrcore-${Date.now()}-${Math.random().toString(36).slice(2, 7)}`;
    }

    /** Retorna el HTML del ícono SVG de persona */
    function iconSVG(name) {
        const icons = {
            user:       '👤',
            users:      '👥',
            calendar:   '📅',
            clock:      '🕐',
            gift:       '🎁',
            shield:     '🛡️',
            chart:      '📊',
            settings:   '⚙️',
            logout:     '🚪',
            search:     '🔍',
            plus:       '➕',
            edit:       '✏️',
            trash:      '🗑️',
            check:      '✅',
            x:          '✕',
            bell:       '🔔',
            home:       '🏠',
            briefcase:  '💼',
            mail:       '📧',
            lock:       '🔒',
            eye:        '👁️',
            refresh:    '🔄',
            download:   '⬇️',
        };
        return icons[name] || '•';
    }

    /** Convierte estado de empleado a clase de badge */
    function statusToBadge(status) {
        const map = {
            'ACTIVO':   'badge--success',
            'INACTIVO': 'badge--warning',
            'CESADO':   'badge--danger',
            'APROBADA':        'badge--success',
            'RECHAZADA':       'badge--danger',
            'PENDIENTE_JEFE':  'badge--warning',
            'PENDIENTE_RRHH':  'badge--info',
            'BORRADOR':        'badge--neutral',
        };
        return map[status] || 'badge--neutral';
    }

    /** Convierte estado a texto legible */
    function statusToLabel(status) {
        const map = {
            'ACTIVO':          'Activo',
            'INACTIVO':        'Inactivo',
            'CESADO':          'Cesado',
            'APROBADA':        'Aprobada',
            'RECHAZADA':       'Rechazada',
            'PENDIENTE_JEFE':  'Pend. Jefe',
            'PENDIENTE_RRHH':  'Pend. RRHH',
            'BORRADOR':        'Borrador',
        };
        return map[status] || status;
    }

    /** Muestra / oculta un elemento */
    function toggleVisible(el, show) {
        if (!el) return;
        el.classList.toggle('hidden', !show);
    }

    /** Crea un elemento HTML con atributos y contenido */
    function createElement(tag, { className, id, html, text, attrs } = {}) {
        const el = document.createElement(tag);
        if (className) el.className = className;
        if (id)        el.id = id;
        if (html)      el.innerHTML = html;
        if (text)      el.textContent = text;
        if (attrs)     Object.entries(attrs).forEach(([k, v]) => el.setAttribute(k, v));
        return el;
    }

    return {
        formatDate, formatCurrency, getInitials,
        truncate, debounce, uid, iconSVG,
        statusToBadge, statusToLabel,
        toggleVisible, createElement,
    };

})();

window.Utils = Utils;
