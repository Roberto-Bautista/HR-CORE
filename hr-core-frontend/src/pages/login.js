/* =============================================================
   login.js — Página de inicio de sesión con Login Dual
   ============================================================= */

const LoginPage = (() => {

    let _employees = [];

    function render(container) {
        container.innerHTML = `
            <div class="login-page">
                <div class="login-card animate-slide-up">

                    <div class="login-card__logo">
                        <div class="login-card__logo-icon">🏢</div>
                        <div class="login-card__logo-name">HR<span>-Core</span></div>
                    </div>

                    <h2 class="login-card__title">Bienvenido de vuelta</h2>
                    <p class="login-card__subtitle">Elige tu modo de acceso para continuar</p>

                    <!-- PESTAÑAS DUAL LOGIN -->
                    <div class="login-tabs" style="display:flex;background:var(--clr-bg-800);border-radius:var(--radius-md);padding:3px;margin-bottom:var(--space-6);border:1px solid var(--border-color);">
                        <button type="button" class="login-tab login-tab--active" id="tab-admin" 
                                style="flex:1;padding:var(--space-2);border-radius:var(--radius-sm);font-size:var(--font-size-sm);font-weight:600;text-align:center;cursor:pointer;border:none;background:var(--clr-bg-600);color:var(--clr-text-100);transition:all var(--transition-fast);">
                            🔑 Administrador
                        </button>
                        <button type="button" class="login-tab" id="tab-worker" 
                                style="flex:1;padding:var(--space-2);border-radius:var(--radius-sm);font-size:var(--font-size-sm);font-weight:600;text-align:center;cursor:pointer;border:none;background:transparent;color:var(--clr-text-500);transition:all var(--transition-fast);">
                            👥 Colaborador
                        </button>
                    </div>

                    <!-- FORMULARIO ADMIN -->
                    <div id="admin-form-container">
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
                                                   color:var(--clr-text-500);font-size:1rem;background:none;border:none;cursor:pointer;"
                                            aria-label="Mostrar/ocultar contraseña">
                                        👁️
                                    </button>
                                </div>
                            </div>

                            <div id="login-error" class="hidden" role="alert"
                                 style="background:rgba(239,68,68,0.1);border:1px solid var(--clr-danger-500);
                                        border-radius:var(--radius-md);padding:var(--space-3) var(--space-4);
                                        font-size:var(--font-size-sm);color:var(--clr-danger-500);
                                        margin-bottom:var(--space-5);display:none;">
                            </div>

                            <button type="submit" class="btn btn--primary btn--full btn--lg" id="btn-login">
                                <span class="btn__text">Iniciar Sesión</span>
                            </button>
                        </form>
                    </div>

                    <!-- FORMULARIO TRABAJADOR -->
                    <div id="worker-form-container" style="display:none;">
                        <form id="worker-login-form" novalidate>
                            <div class="form-group">
                                <label class="form-label form-label--required" for="worker-select">
                                    Seleccionar Colaborador
                                </label>
                                <select class="form-select" id="worker-select" required style="width:100%;">
                                    <option value="">Cargando colaboradores...</option>
                                </select>
                            </div>

                            <div id="worker-login-error" class="hidden" role="alert"
                                 style="background:rgba(239,68,68,0.1);border:1px solid var(--clr-danger-500);
                                        border-radius:var(--radius-md);padding:var(--space-3) var(--space-4);
                                        font-size:var(--font-size-sm);color:var(--clr-danger-500);
                                        margin-bottom:var(--space-5);display:none;">
                            </div>

                            <button type="submit" class="btn btn--primary btn--full btn--lg" id="btn-login-worker">
                                <span class="btn__text">Ingresar como Colaborador</span>
                            </button>
                        </form>
                    </div>

                    <p class="login-card__footer">
                        ¿Problemas para ingresar? Contacta al <strong>Administrador del Sistema</strong>
                    </p>
                </div>
            </div>
        `;

        _attachEvents();
        _loadEmployees();
    }

    async function _loadEmployees() {
        const select = document.getElementById('worker-select');
        if (!select) return;

        try {
            _employees = await Api.get('/v1/employees');
            const activeEmployees = _employees.filter(e => e.status === 'ACTIVO');
            
            if (activeEmployees.length === 0) {
                select.innerHTML = '<option value="">No hay colaboradores activos registrados</option>';
                return;
            }

            select.innerHTML = '<option value="">Selecciona tu usuario...</option>' + 
                activeEmployees.map(e => `<option value="${e.id}">${e.nombre} ${e.apellido} (${e.codigo})</option>`).join('');
        } catch (err) {
            select.innerHTML = '<option value="">Error al cargar colaboradores</option>';
            console.error('Error fetching employees for login:', err);
        }
    }

    function _attachEvents() {
        const formAdmin    = document.getElementById('login-form');
        const formWorker   = document.getElementById('worker-login-form');
        const emailEl      = document.getElementById('login-email');
        const passEl       = document.getElementById('login-password');
        const errorEl      = document.getElementById('login-error');
        const workerErrorEl = document.getElementById('worker-login-error');
        const btnLogin     = document.getElementById('btn-login');
        const btnLoginWrk  = document.getElementById('btn-login-worker');
        const btnToggle    = document.getElementById('btn-toggle-pwd');

        const tabAdmin     = document.getElementById('tab-admin');
        const tabWorker    = document.getElementById('tab-worker');
        const adminContainer = document.getElementById('admin-form-container');
        const workerContainer = document.getElementById('worker-form-container');

        // Toggle Tabs
        tabAdmin.addEventListener('click', () => {
            tabAdmin.classList.add('login-tab--active');
            tabAdmin.style.background = 'var(--clr-bg-600)';
            tabAdmin.style.color = 'var(--clr-text-100)';
            
            tabWorker.classList.remove('login-tab--active');
            tabWorker.style.background = 'transparent';
            tabWorker.style.color = 'var(--clr-text-500)';

            adminContainer.style.display = 'block';
            workerContainer.style.display = 'none';
        });

        tabWorker.addEventListener('click', () => {
            tabWorker.classList.add('login-tab--active');
            tabWorker.style.background = 'var(--clr-bg-600)';
            tabWorker.style.color = 'var(--clr-text-100)';

            tabAdmin.classList.remove('login-tab--active');
            tabAdmin.style.background = 'transparent';
            tabAdmin.style.color = 'var(--clr-text-500)';

            adminContainer.style.display = 'none';
            workerContainer.style.display = 'block';
        });

        // Toggle visibilidad de contraseña
        btnToggle.addEventListener('click', () => {
            const isText = passEl.type === 'text';
            passEl.type = isText ? 'password' : 'text';
            btnToggle.textContent = isText ? '👁️' : '🙈';
        });

        // Submit Admin Form
        formAdmin.addEventListener('submit', async (e) => {
            e.preventDefault();

            const email    = emailEl.value.trim();
            const password = passEl.value;

            if (!email || !password) {
                _showError('Por favor ingresa tu email y contraseña.', errorEl);
                return;
            }

            btnLogin.classList.add('btn--loading');
            errorEl.style.display = 'none';

            try {
                await Auth.login(email, password);
                Toast.success('Sesión iniciada', `Bienvenido al sistema HR-Core`);
                Router.navigate('/dashboard');
            } catch (err) {
                _showError(err.message || 'Credenciales incorrectas. Inténtalo de nuevo.', errorEl);
            } finally {
                btnLogin.classList.remove('btn--loading');
            }
        });

        // Submit Worker Form
        formWorker.addEventListener('submit', async (e) => {
            e.preventDefault();

            const selectEl = document.getElementById('worker-select');
            const empId = selectEl.value;

            if (!empId) {
                _showError('Por favor selecciona un colaborador de la lista.', workerErrorEl);
                return;
            }

            btnLoginWrk.classList.add('btn--loading');
            workerErrorEl.style.display = 'none';

            try {
                const emp = _employees.find(e => e.id === empId);
                if (!emp) {
                    throw new Error('Colaborador no encontrado');
                }
                Auth.loginAsWorker(emp);
                Toast.success('Sesión iniciada', `Ingresaste como ${emp.nombre} ${emp.apellido}`);
                Router.navigate('/dashboard');
            } catch (err) {
                _showError(err.message || 'Error al iniciar sesión como colaborador.', workerErrorEl);
            } finally {
                btnLoginWrk.classList.remove('btn--loading');
            }
        });
    }

    function _showError(msg, element) {
        if (element) { 
            element.textContent = msg; 
            element.style.display = 'block'; 
        }
    }

    return { render };

})();

window.LoginPage = LoginPage;
