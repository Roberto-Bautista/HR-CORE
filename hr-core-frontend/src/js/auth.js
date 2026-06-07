/* =============================================================
   auth.js — Manejo de autenticación JWT
   ============================================================= */

const Auth = (() => {

    /** Inicia sesión: simula la llamada al backend para pruebas del UI */
    async function login(email, password) {
        
        // Simulación de delay de red (1 segundo) para que veas el spinner del botón
        return new Promise((resolve, reject) => {
            setTimeout(() => {
                if (email && password) {
                    const mockUser = {
                        id: 'u1',
                        nombre: 'Administrador',
                        apellido: 'HR',
                        email: email,
                        rol: 'ADMIN'
                    };
                    State.set('token', 'mock-jwt-token-123');
                    State.set('user', mockUser);
                    State.persist();
                    resolve({ token: 'mock-jwt-token-123', user: mockUser });
                } else {
                    reject(new Error("Credenciales inválidas"));
                }
            }, 1000);
        });

        /* 
        ========================================================
        CÓDIGO REAL (Se usará cuando el backend esté listo):
        ========================================================
        const data = await Api.post('/v1/auth/login', { email, password });
        State.set('token', data.token);
        State.set('user',  data.user);
        State.persist();
        return data;
        */
    }

    /** Inicia sesión como trabajador (temporal para Fase 3) */
    function loginAsWorker(employee) {
        const mockUser = {
            id: employee.id,
            nombre: employee.nombre,
            apellido: employee.apellido,
            email: `${employee.nombre.toLowerCase()}@empresa.com`,
            rol: 'WORKER',
            codigo: employee.codigo
        };
        State.set('token', 'mock-jwt-token-worker-' + employee.id);
        State.set('user', mockUser);
        State.persist();
        return mockUser;
    }

    /** Cierra sesión: limpia estado y redirige al login */
    function logout() {
        State.clear();
        Router.navigate('/login');
    }

    /** ¿Está el usuario autenticado? */
    function isAuthenticated() {
        return !!State.get('token');
    }

    /** Obtiene el usuario actual */
    function currentUser() {
        return State.get('user');
    }

    /** Verifica si el usuario tiene un rol específico */
    function hasRole(role) {
        const user = currentUser();
        return user?.rol === role;
    }

    return { login, loginAsWorker, logout, isAuthenticated, currentUser, hasRole };

})();

window.Auth = Auth;
