/* =============================================================
   state.js — Manejo de estado global (patrón Store simple)
   
   Maneja el estado de la aplicación de forma reactiva sin
   ningún framework. Los componentes se suscriben a cambios.
   ============================================================= */

const State = (() => {

    // Estado inicial de la aplicación
    let _state = {
        user: null,          // { id, nombre, apellido, email, rol }
        token: null,         // JWT token
        currentRoute: null,  // Ruta activa actual
        isLoading: false,
    };

    // Suscriptores: { key: [callback, ...] }
    const _subscribers = {};

    /** Lee un valor del estado */
    function get(key) {
        return key ? _state[key] : { ..._state };
    }

    /** Actualiza el estado y notifica a los suscriptores */
    function set(key, value) {
        const oldValue = _state[key];
        _state[key] = value;

        // Notificar a los suscriptores de esta key
        if (_subscribers[key]) {
            _subscribers[key].forEach(cb => cb(value, oldValue));
        }
        // Notificar a los suscriptores globales (key='*')
        if (_subscribers['*']) {
            _subscribers['*'].forEach(cb => cb({ key, value, oldValue }));
        }
    }

    /** Suscribirse a cambios de una key */
    function subscribe(key, callback) {
        if (!_subscribers[key]) _subscribers[key] = [];
        _subscribers[key].push(callback);

        // Retorna función para desuscribirse
        return () => {
            _subscribers[key] = _subscribers[key].filter(cb => cb !== callback);
        };
    }

    // =========================================================
    // Persistencia en localStorage (token y usuario)
    // =========================================================

    function persist() {
        if (_state.token) {
            localStorage.setItem('hrcore_token', _state.token);
        } else {
            localStorage.removeItem('hrcore_token');
        }
        if (_state.user) {
            localStorage.setItem('hrcore_user', JSON.stringify(_state.user));
        } else {
            localStorage.removeItem('hrcore_user');
        }
    }

    function restore() {
        const token = localStorage.getItem('hrcore_token');
        const user  = localStorage.getItem('hrcore_user');
        if (token) _state.token = token;
        if (user)  _state.user  = JSON.parse(user);
    }

    function clear() {
        _state.token = null;
        _state.user  = null;
        localStorage.removeItem('hrcore_token');
        localStorage.removeItem('hrcore_user');
    }

    // Restaurar estado al cargar la app
    restore();

    return { get, set, subscribe, persist, restore, clear };

})();

window.State = State;
