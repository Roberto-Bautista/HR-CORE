/* =============================================================
   login.js — Página de inicio de sesión
   ============================================================= */

const LoginPage = (() => {

    function render(container) {
        container.innerHTML = `
            <div class="login-page">
                <div class="login-card animate-slide-up">

                    <div class="login-card__logo">
                        <div class="login-card__logo-icon">🏢</div>
                        <div class="login-card__logo-name">HR<span>-Core</span></div>
                    </div>

                    <h2 class="login-card__title">Bienvenido de vuelta</h2>
                    <p class="login-card__subtitle">Ingresa tus credenciales para continuar</p>

                    <form id="login-form" novalidate>

                        <div class="form-group">
                            <label class="form-label form-label--required" for="login-email">
                                Correo electrónico
                            </label>
                            <input
                                class="form-input"
                                id="login-email"
                                type="email"
                                placeholder="tu@empresa.com"
                                autocomplete="email"
                                required
                            />
                        </div>

                        <div class="form-group">
                            <label class="form-label form-label--required" for="login-password">
                                Contraseña
                            </label>
                            <div style="position:relative;">
                                <input
                                    class="form-input"
                                    id="login-password"
                                    type="password"
                                    placeholder="••••••••"
                                    autocomplete="current-password"
                                    required
                                    style="padding-right:3rem;"
                                />
                                <button type="button" id="btn-toggle-pwd"
                                        style="position:absolute;right:0.75rem;top:50%;transform:translateY(-50%);
                                               color:var(--clr-text-500);font-size:1rem;"
                                        aria-label="Mostrar/ocultar contraseña">
                                    👁️
                                </button>
                            </div>
                        </div>

                        <div id="login-error" class="hidden" role="alert"
                             style="background:rgba(239,68,68,0.1);border:1px solid var(--clr-danger-500);
                                    border-radius:var(--radius-md);padding:var(--space-3) var(--space-4);
                                    font-size:var(--font-size-sm);color:var(--clr-danger-500);
                                    margin-bottom:var(--space-5);">
                        </div>

                        <button type="submit" class="btn btn--primary btn--full btn--lg" id="btn-login">
                            <span class="btn__text">Iniciar Sesión</span>
                        </button>

                    </form>

                    <p class="login-card__footer">
                        ¿Problemas para ingresar? Contacta al <strong>Administrador del Sistema</strong>
                    </p>
                </div>
            </div>
        `;

        _attachEvents();
    }

    function _attachEvents() {
        const form      = document.getElementById('login-form');
        const emailEl   = document.getElementById('login-email');
        const passEl    = document.getElementById('login-password');
        const errorEl   = document.getElementById('login-error');
        const btnLogin  = document.getElementById('btn-login');
        const btnToggle = document.getElementById('btn-toggle-pwd');

        // Toggle visibilidad de contraseña
        btnToggle.addEventListener('click', () => {
            const isText = passEl.type === 'text';
            passEl.type = isText ? 'password' : 'text';
            btnToggle.textContent = isText ? '👁️' : '🙈';
        });

        // Submit del formulario
        form.addEventListener('submit', async (e) => {
            e.preventDefault();

            const email    = emailEl.value.trim();
            const password = passEl.value;

            // Validación básica
            if (!email || !password) {
                _showError('Por favor ingresa tu email y contraseña.');
                return;
            }

            // Estado de carga
            btnLogin.classList.add('btn--loading');
            errorEl.classList.add('hidden');

            try {
                await Auth.login(email, password);
                Toast.success('Sesión iniciada', `Bienvenido al sistema HR-Core`);
                Router.navigate('/dashboard');
            } catch (err) {
                _showError(err.message || 'Credenciales incorrectas. Inténtalo de nuevo.');
            } finally {
                btnLogin.classList.remove('btn--loading');
            }
        });
    }

    function _showError(msg) {
        const el = document.getElementById('login-error');
        if (el) { el.textContent = msg; el.classList.remove('hidden'); }
    }

    return { render };

})();

window.LoginPage = LoginPage;
