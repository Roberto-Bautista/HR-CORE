/* =============================================================
   api.js — Cliente HTTP para comunicarse con el backend
   
   Wrapper sobre fetch() que maneja: JWT, errores, JSON parsing.
   Uso: Api.get('/v1/employees')
        Api.post('/v1/absences', { data })
   ============================================================= */

const Api = (() => {

    const BASE_URL = 'http://localhost:8080'; // Cambiar en producción con variable de entorno

    /**
     * Función principal de petición HTTP.
     * Adjunta automáticamente el JWT si existe en el State.
     */
    async function request(method, endpoint, body = null) {
        const token = State.get('token');

        const headers = { 'Content-Type': 'application/json' };
        if (token) headers['Authorization'] = `Bearer ${token}`;

        const config = { method, headers };
        if (body) config.body = JSON.stringify(body);

        try {
            const response = await fetch(`${BASE_URL}${endpoint}`, config);

            // Si la respuesta es 401, el token expiró: cerrar sesión
            if (response.status === 401) {
                Auth.logout();
                return;
            }

            // Intentar parsear JSON
            const data = await response.json().catch(() => null);

            if (!response.ok) {
                // El backend devuelve { errorCode, message } en errores
                const errorMsg = data?.message || `Error ${response.status}`;
                throw new ApiError(response.status, data?.errorCode || 'API_ERROR', errorMsg);
            }

            return data;

        } catch (err) {
            if (err instanceof ApiError) throw err;

            // Error de red (sin conexión al backend)
            throw new ApiError(0, 'NETWORK_ERROR', 'No se pudo conectar al servidor.');
        }
    }

    // Métodos HTTP convenientes
    const get    = (endpoint)        => request('GET',    endpoint);
    const post   = (endpoint, body)  => request('POST',   endpoint, body);
    const put    = (endpoint, body)  => request('PUT',    endpoint, body);
    const patch  = (endpoint, body)  => request('PATCH',  endpoint, body);
    const del    = (endpoint)        => request('DELETE', endpoint);

    return { get, post, put, patch, del };

})();

/** Clase de error personalizada para respuestas de la API */
class ApiError extends Error {
    constructor(status, errorCode, message) {
        super(message);
        this.status    = status;
        this.errorCode = errorCode;
        this.name      = 'ApiError';
    }
}

window.Api = Api;
window.ApiError = ApiError;
