/* =============================================================
   benefits.js — Portal de Beneficios Corporativos (Sprint 5)
   ============================================================= */

const BenefitsPage = (() => {

    let _catalog = [];
    let _myEnrollments = [];
    let _employee = null;
    let _activeTab = 'catalog'; // 'catalog' o 'admin-enrollments' (para admin)

    // Inyectar estilos específicos para esta página
    function _injectStyles() {
        if (document.getElementById('benefits-custom-styles')) return;

        const style = document.createElement('style');
        style.id = 'benefits-custom-styles';
        style.textContent = `
            .benefits-container {
                display: flex;
                flex-direction: column;
                gap: var(--space-6);
            }
            .employee-summary-bar {
                background: linear-gradient(135deg, rgba(30, 41, 59, 0.7) 0%, rgba(15, 23, 42, 0.8) 100%);
                border: 1px solid var(--border-color);
                border-radius: var(--radius-lg);
                padding: var(--space-5);
                display: flex;
                flex-wrap: wrap;
                gap: var(--space-6);
                align-items: center;
                backdrop-filter: blur(8px);
            }
            .employee-summary-item {
                display: flex;
                flex-direction: column;
                gap: 4px;
            }
            .employee-summary-label {
                font-size: var(--font-size-xs);
                color: var(--clr-text-500);
                text-transform: uppercase;
                letter-spacing: 0.05em;
            }
            .employee-summary-value {
                font-size: 1.1rem;
                font-weight: 600;
                color: var(--clr-text-100);
            }
            .benefits-grid {
                display: grid;
                grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
                gap: var(--space-6);
            }
            .benefit-card {
                background: var(--clr-bg-card);
                border: 1px solid var(--border-color);
                border-radius: var(--radius-lg);
                overflow: hidden;
                transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1), box-shadow 0.3s ease, border-color 0.3s ease;
                display: flex;
                flex-direction: column;
                position: relative;
            }
            .benefit-card:hover {
                transform: translateY(-4px);
                box-shadow: 0 12px 24px -10px rgba(0, 0, 0, 0.5);
                border-color: var(--clr-indigo-500);
            }
            .benefit-card--enrolled {
                border-color: var(--clr-success-500);
                box-shadow: 0 0 15px rgba(16, 185, 129, 0.15);
            }
            .benefit-card--enrolled:hover {
                border-color: var(--clr-success-400);
            }
            .benefit-card--locked {
                border-color: var(--clr-bg-700);
            }
            .benefit-card--locked:hover {
                border-color: var(--clr-danger-500);
            }
            .benefit-card__header {
                padding: var(--space-5);
                background: linear-gradient(135deg, var(--clr-indigo-700) 0%, var(--clr-indigo-900) 100%);
                color: white;
                position: relative;
            }
            .benefit-card__header--enrolled {
                background: linear-gradient(135deg, var(--clr-success-700) 0%, var(--clr-success-900) 100%);
            }
            .benefit-card__header--locked {
                background: linear-gradient(135deg, var(--clr-bg-700) 0%, var(--clr-bg-800) 100%);
                color: var(--clr-text-400);
            }
            .benefit-card__cost {
                position: absolute;
                top: var(--space-4);
                right: var(--space-4);
                background: rgba(255, 255, 255, 0.18);
                padding: var(--space-1) var(--space-3);
                border-radius: var(--radius-full);
                font-size: var(--font-size-xs);
                font-weight: 700;
                backdrop-filter: blur(4px);
            }
            .benefit-card__title {
                font-size: 1.25rem;
                font-weight: 700;
                margin-bottom: var(--space-1);
            }
            .benefit-card__body {
                padding: var(--space-5);
                display: flex;
                flex-direction: column;
                flex-grow: 1;
                gap: var(--space-4);
            }
            .benefit-card__description {
                font-size: var(--font-size-sm);
                color: var(--clr-text-400);
                line-height: 1.5;
                flex-grow: 1;
            }
            .benefit-card__rules {
                background: var(--clr-bg-600);
                border: 1px solid var(--border-color);
                border-radius: var(--radius-md);
                padding: var(--space-3) var(--space-4);
                font-size: var(--font-size-xs);
            }
            .benefit-card__rules-title {
                font-weight: 600;
                color: var(--clr-text-300);
                margin-bottom: var(--space-2);
                text-transform: uppercase;
                letter-spacing: 0.02em;
            }
            .benefit-rule-item {
                display: flex;
                align-items: center;
                gap: var(--space-2);
                margin-bottom: var(--space-1);
                color: var(--clr-text-500);
            }
            .benefit-rule-item--met {
                color: var(--clr-success-400);
            }
            .benefit-rule-item--failed {
                color: var(--clr-danger-400);
            }
            .benefit-card__status-tag {
                display: flex;
                align-items: center;
                gap: 6px;
                font-size: var(--font-size-xs);
                font-weight: 600;
                text-transform: uppercase;
            }
            .tab-btn {
                padding: var(--space-3) var(--space-5);
                font-weight: 600;
                font-size: var(--font-size-sm);
                border: none;
                background: transparent;
                color: var(--clr-text-400);
                cursor: pointer;
                border-bottom: 2px solid transparent;
                transition: all 0.2s ease;
            }
            .tab-btn--active {
                color: var(--clr-accent-400);
                border-bottom-color: var(--clr-accent-400);
            }
            .admin-form-grid {
                display: grid;
                grid-template-columns: 1fr 1fr;
                gap: var(--space-4);
            }
            .admin-catalog-grid {
                display: grid;
                grid-template-columns: 2fr 1fr;
                gap: var(--space-6);
                align-items: start;
            }
            @media (max-width: 1024px) {
                .admin-catalog-grid {
                    grid-template-columns: 1fr;
                    gap: var(--space-8);
                }
            }
            @media (max-width: 768px) {
                .admin-form-grid {
                    grid-template-columns: 1fr;
                }
            }
        `;
        document.head.appendChild(style);
    }

    async function render(container) {
        container.innerHTML = '';
        _injectStyles();

        const layout = document.createElement('div');
        layout.className = 'app-layout';

        Sidebar.render(layout);

        const main = document.createElement('main');
        main.className = 'main-content';

        Header.render(main, { 
            title: 'Beneficios Corporativos', 
            subtitle: 'Catálogo de beneficios y elegibilidad inteligente' 
        });

        const content = document.createElement('div');
        content.className = 'page-content';
        content.id = 'benefits-page-content';
        
        main.appendChild(content);
        layout.appendChild(main);
        container.appendChild(layout);

        await _initPortal();
    }

    async function _initPortal() {
        const content = document.getElementById('benefits-page-content');
        if (!content) return;

        const user = Auth.currentUser();
        if (!user) return;

        content.innerHTML = `
            <div style="padding:var(--space-8); text-align:center;">
                <div class="spinner" style="margin:0 auto var(--space-4);"></div>
                <span>Cargando portal de beneficios...</span>
            </div>
        `;

        try {
            if (user.rol === 'ADMIN') {
                await _renderAdminPortal(content);
            } else {
                await _renderWorkerPortal(content, user.id);
            }
        } catch (err) {
            console.error('Error rendering benefits portal:', err);
            content.innerHTML = `
                <div class="card">
                    <div class="empty-state">
                        <div class="empty-state__icon">⚠️</div>
                        <p class="empty-state__title">Error de conexión</p>
                        <p class="empty-state__text">${err.message || 'No se pudo cargar el portal de beneficios. Por favor, asegúrate de que el backend esté ejecutándose.'}</p>
                        <button class="btn btn--primary mt-4" onclick="BenefitsPage.render(document.getElementById('view-root'))">Reintentar</button>
                    </div>
                </div>
            `;
        }
    }

    // ==========================================================
    // RENDER: COLABORADOR / TRABAJADOR
    // ==========================================================
    async function _renderWorkerPortal(container, employeeId) {
        // Cargar datos
        const [emp, catalog] = await Promise.all([
            Api.get(`/v1/employees/${employeeId}`),
            Api.get(`/v1/benefits?employeeId=${employeeId}`)
        ]);

        _employee = emp;
        _catalog = catalog;

        // Calcular antigüedad en meses
        const altaDate = new Date(_employee.fechaAlta);
        const diffTime = Math.abs(new Date() - altaDate);
        const antiguedadMeses = Math.floor(diffTime / (1000 * 60 * 60 * 24 * 30.43));

        container.innerHTML = `
            <div class="benefits-container animate-fade-in">
                <!-- Resumen del Colaborador -->
                <div class="employee-summary-bar">
                    <div class="employee-summary-item">
                        <span class="employee-summary-label">Colaborador</span>
                        <span class="employee-summary-value">${_employee.nombre} ${_employee.apellido}</span>
                    </div>
                    <div class="employee-summary-item">
                        <span class="employee-summary-label">Cargo / Puesto</span>
                        <span class="employee-summary-value">💼 ${_employee.cargo}</span>
                    </div>
                    <div class="employee-summary-item">
                        <span class="employee-summary-label">Salario Mensual</span>
                        <span class="employee-summary-value">💵 ${Utils.formatCurrency(_employee.salario)}</span>
                    </div>
                    <div class="employee-summary-item">
                        <span class="employee-summary-label">Antigüedad</span>
                        <span class="employee-summary-value">📅 ${antiguedadMeses} meses <span style="font-size:var(--font-size-xs); font-weight:normal; color:var(--clr-text-500);">(Ingreso: ${Utils.formatDate(_employee.fechaAlta)})</span></span>
                    </div>
                </div>

                <div class="section-title-wrap" style="margin-top:var(--space-2);">
                    <h2>Catálogo de Beneficios Disponibles</h2>
                    <p style="color:var(--clr-text-500);">Evaluados automáticamente según tu perfil profesional y salarial</p>
                </div>

                <div class="benefits-grid">
                    ${_catalog.map(b => _buildBenefitCard(b, employeeId)).join('')}
                </div>
            </div>
        `;
    }

    function _buildBenefitCard(b, employeeId) {
        let cardClass = 'benefit-card';
        let headerClass = 'benefit-card__header';
        let statusTag = '';
        let buttonHtml = '';

        if (b.yaEnrolado) {
            cardClass += ' benefit-card--enrolled';
            headerClass += ' benefit-card__header--enrolled';
            statusTag = `<span class="benefit-card__status-tag benefit-rule-item--met">✅ Afiliado</span>`;
            buttonHtml = `
                <button class="btn btn--danger btn--outline mt-4" 
                        onclick="BenefitsPage.unenroll('${b.enrolamientoId}', '${employeeId}')">
                    Desafiliarse
                </button>
            `;
        } else if (b.esElegible) {
            statusTag = `<span class="benefit-card__status-tag benefit-rule-item--met">🟢 Elegible</span>`;
            buttonHtml = `
                <button class="btn btn--primary mt-4" 
                        onclick="BenefitsPage.enroll('${employeeId}', '${b.id}')">
                    Solicitar Afiliación
                </button>
            `;
        } else {
            cardClass += ' benefit-card--locked';
            headerClass += ' benefit-card__header--locked';
            statusTag = `<span class="benefit-card__status-tag benefit-rule-item--failed">🔒 No Elegible</span>`;
            buttonHtml = `
                <button class="btn btn--secondary mt-4" disabled style="cursor: not-allowed;">
                    No Elegible
                </button>
            `;
        }

        const costLabel = b.costo > 0 ? `${Utils.formatCurrency(b.costo)}/mes` : 'Gratuito';

        // Evaluar reglas individuales para indicarle al colaborador cuáles cumple y cuáles no
        const rulesListHtml = _buildRulesEvaluationList(b);

        return `
            <div class="${cardClass} animate-slide-up">
                <div class="${headerClass}">
                    <span class="benefit-card__cost">${costLabel}</span>
                    <div class="benefit-card__title">${b.nombre}</div>
                    <div class="mt-2">${statusTag}</div>
                </div>
                <div class="benefit-card__body">
                    <p class="benefit-card__description">${b.descripcion || 'Sin descripción disponible.'}</p>
                    
                    ${rulesListHtml ? `
                        <div class="benefit-card__rules">
                            <div class="benefit-card__rules-title">Reglas de Elegibilidad:</div>
                            ${rulesListHtml}
                        </div>
                    ` : ''}

                    ${buttonHtml}
                </div>
            </div>
        `;
    }

    function _buildRulesEvaluationList(b) {
        if (!b.requiereCargo && !b.requiereSalarioMin && !b.requiereSalarioMax && !b.requiereAntiguedadMeses) {
            return `<div style="color:var(--clr-text-500); font-style:italic;">Abierto para todo el personal.</div>`;
        }

        let html = '';

        // Regla Cargo
        if (b.requiereCargo) {
            const met = _employee.cargo && _employee.cargo.toLowerCase() === b.requiereCargo.toLowerCase();
            const icon = met ? '✔️' : '❌';
            const cssClass = met ? 'benefit-rule-item--met' : 'benefit-rule-item--failed';
            html += `
                <div class="benefit-rule-item ${cssClass}">
                    <span>${icon}</span>
                    <span>Solo personal con cargo <strong>${b.requiereCargo}</strong></span>
                </div>
            `;
        }

        // Regla Antigüedad
        if (b.requiereAntiguedadMeses) {
            const altaDate = new Date(_employee.fechaAlta);
            const diffTime = Math.abs(new Date() - altaDate);
            const meses = Math.floor(diffTime / (1000 * 60 * 60 * 24 * 30.43));
            const met = meses >= b.requiereAntiguedadMeses;
            const icon = met ? '✔️' : '❌';
            const cssClass = met ? 'benefit-rule-item--met' : 'benefit-rule-item--failed';
            html += `
                <div class="benefit-rule-item ${cssClass}">
                    <span>${icon}</span>
                    <span>Mínimo <strong>${b.requiereAntiguedadMeses} meses</strong> de antigüedad (tienes ${meses})</span>
                </div>
            `;
        }

        // Regla Salario Mínimo
        if (b.requiereSalarioMin) {
            const met = _employee.salario && parseFloat(_employee.salario) >= parseFloat(b.requiereSalarioMin);
            const icon = met ? '✔️' : '❌';
            const cssClass = met ? 'benefit-rule-item--met' : 'benefit-rule-item--failed';
            html += `
                <div class="benefit-rule-item ${cssClass}">
                    <span>${icon}</span>
                    <span>Sueldo mayor o igual a <strong>${Utils.formatCurrency(b.requiereSalarioMin)}</strong></span>
                </div>
            `;
        }

        // Regla Salario Máximo
        if (b.requiereSalarioMax) {
            const met = _employee.salario && parseFloat(_employee.salario) <= parseFloat(b.requiereSalarioMax);
            const icon = met ? '✔️' : '❌';
            const cssClass = met ? 'benefit-rule-item--met' : 'benefit-rule-item--failed';
            html += `
                <div class="benefit-rule-item ${cssClass}">
                    <span>${icon}</span>
                    <span>Sueldo menor o igual a <strong>${Utils.formatCurrency(b.requiereSalarioMax)}</strong></span>
                </div>
            `;
        }

        return html;
    }

    // ==========================================================
    // RENDER: ADMINISTRADOR
    // ==========================================================
    async function _renderAdminPortal(container) {
        // Cargar catálogo de beneficios y lista de empleados
        const [catalog, employees] = await Promise.all([
            Api.get('/v1/benefits'),
            Api.get('/v1/employees')
        ]);

        _catalog = catalog;

        container.innerHTML = `
            <div class="benefits-container animate-fade-in">
                <!-- Navegación por pestañas -->
                <div style="border-bottom:1px solid var(--border-color); display:flex; gap:var(--space-2); margin-bottom:var(--space-2);">
                    <button class="tab-btn ${_activeTab === 'catalog' ? 'tab-btn--active' : ''}" onclick="BenefitsPage.changeTab('catalog')">
                        📦 Catálogo de Beneficios
                    </button>
                    <button class="tab-btn ${_activeTab === 'admin-enrollments' ? 'tab-btn--active' : ''}" onclick="BenefitsPage.changeTab('admin-enrollments')">
                        👥 Afiliaciones por Colaborador
                    </button>
                </div>

                <div id="tab-content-container"></div>
            </div>
        `;

        _renderTabContent(employees);
    }

    function _renderTabContent(employees) {
        const container = document.getElementById('tab-content-container');
        if (!container) return;

        if (_activeTab === 'catalog') {
            _renderAdminCatalogTab(container);
        } else {
            _renderAdminEnrollmentsTab(container, employees);
        }
    }

    function _renderAdminCatalogTab(container) {
        container.innerHTML = `
            <div class="admin-catalog-grid">
                
                <!-- Tabla del Catálogo -->
                <div class="card animate-slide-up">
                    <div class="card__header">
                        <div class="card__title">Lista de Beneficios Corporativos</div>
                        <div class="card__subtitle">Catálogo oficial activo en el sistema</div>
                    </div>
                    
                    <div class="data-table-wrapper">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Nombre</th>
                                    <th>Costo Mensual</th>
                                    <th>Reglas de Elegibilidad</th>
                                    <th>Estado</th>
                                </tr>
                            </thead>
                            <tbody>
                                ${_catalog.map(b => `
                                    <tr>
                                        <td>
                                            <div style="font-weight:600; color:var(--clr-text-100);">${b.nombre}</div>
                                            <div style="font-size:11px; color:var(--clr-text-500); max-width:250px;">${b.descripcion || ''}</div>
                                        </td>
                                        <td><strong>${b.costo > 0 ? Utils.formatCurrency(b.costo) : 'Gratuito'}</strong></td>
                                        <td>
                                            <div style="font-size:11px; display:flex; flex-direction:column; gap:2px;">
                                                ${b.requiereCargo ? `<span>Cargo: <strong>${b.requiereCargo}</strong></span>` : ''}
                                                ${b.requiereAntiguedadMeses ? `<span>Antigüedad: <strong>&ge; ${b.requiereAntiguedadMeses} meses</strong></span>` : ''}
                                                ${b.requiereSalarioMin ? `<span>Sueldo Mín: <strong>${Utils.formatCurrency(b.requiereSalarioMin)}</strong></span>` : ''}
                                                ${b.requiereSalarioMax ? `<span>Sueldo Máx: <strong>${Utils.formatCurrency(b.requiereSalarioMax)}</strong></span>` : ''}
                                                ${(!b.requiereCargo && !b.requiereAntiguedadMeses && !b.requiereSalarioMin && !b.requiereSalarioMax) ? '<span style="color:var(--clr-text-500); font-style:italic;">Ninguna (Libre)</span>' : ''}
                                            </div>
                                        </td>
                                        <td>
                                            <span class="badge ${b.activo ? 'badge--success' : 'badge--neutral'}">
                                                ${b.activo ? 'Activo' : 'Inactivo'}
                                            </span>
                                        </td>
                                    </tr>
                                `).join('')}
                            </tbody>
                        </table>
                    </div>
                </div>

                <!-- Formulario Crear Beneficio -->
                <div class="card animate-slide-up" style="animation-delay: 0.1s;">
                    <div class="card__header">
                        <div class="card__title">Nuevo Beneficio</div>
                        <div class="card__subtitle">Agregar un beneficio al catálogo</div>
                    </div>
                    
                    <form id="create-benefit-form" style="display:flex; flex-direction:column; gap:var(--space-4);">
                        <div class="form-group">
                            <label class="form-label form-label--required" for="ben-nombre">Nombre</label>
                            <input type="text" class="form-input" id="ben-nombre" required placeholder="Ej. Bono de Estudios" />
                        </div>
                        <div class="form-group">
                            <label class="form-label" for="ben-descripcion">Descripción</label>
                            <textarea class="form-input" id="ben-descripcion" rows="3" placeholder="Detalle sobre el beneficio corporativo..."></textarea>
                        </div>
                        <div class="form-group">
                            <label class="form-label form-label--required" for="ben-costo">Costo Mensual (S/.)</label>
                            <input type="number" step="0.01" min="0" class="form-input" id="ben-costo" required value="0.00" />
                        </div>

                        <div style="border-top: 1px solid var(--border-color); padding-top: var(--space-3); margin-top: var(--space-1);">
                            <h4 style="font-size:12px; text-transform:uppercase; color:var(--clr-accent-400); margin-bottom:var(--space-3);">Reglas de Elegibilidad (Estrategias)</h4>
                            
                            <div class="form-group">
                                <label class="form-label" for="ben-cargo">Cargo Requerido</label>
                                <input type="text" class="form-input" id="ben-cargo" placeholder="Ej. Gerente (dejar vacío si es libre)" />
                            </div>
                            <div class="form-group">
                                <label class="form-label" for="ben-antiguedad">Meses Mínimos de Antigüedad</label>
                                <input type="number" min="0" class="form-input" id="ben-antiguedad" placeholder="Ej. 6 (dejar vacío si es libre)" />
                            </div>
                            <div class="form-group">
                                <label class="form-label" for="ben-salariomin">Sueldo Mínimo Requerido (S/.)</label>
                                <input type="number" min="0" step="0.01" class="form-input" id="ben-salariomin" placeholder="Ej. 5000 (dejar vacío si es libre)" />
                            </div>
                            <div class="form-group">
                                <label class="form-label" for="ben-salariomax">Sueldo Máximo Permitido (S/.)</label>
                                <input type="number" min="0" step="0.01" class="form-input" id="ben-salariomax" placeholder="Ej. 4000 (dejar vacío si es libre)" />
                            </div>
                        </div>

                        <button type="submit" class="btn btn--primary w-full mt-2" id="btn-save-benefit">
                            💾 Registrar Beneficio
                        </button>
                    </form>
                </div>
            </div>
        `;

        // Registrar evento de envío del formulario
        document.getElementById('create-benefit-form').addEventListener('submit', async (e) => {
            e.preventDefault();
            const saveBtn = document.getElementById('btn-save-benefit');
            saveBtn.classList.add('btn--loading');
            saveBtn.disabled = true;

            const name = document.getElementById('ben-nombre').value;
            const desc = document.getElementById('ben-descripcion').value;
            const cost = parseFloat(document.getElementById('ben-costo').value || 0);
            
            const cargo = document.getElementById('ben-cargo').value;
            const antiguedad = document.getElementById('ben-antiguedad').value;
            const salarioMin = document.getElementById('ben-salariomin').value;
            const salarioMax = document.getElementById('ben-salariomax').value;

            const newBenefit = {
                nombre: name,
                descripcion: desc,
                costo: cost,
                activo: true,
                requiereCargo: cargo ? cargo.trim() : null,
                requiereAntiguedadMeses: antiguedad ? parseInt(antiguedad) : null,
                requiereSalarioMin: salarioMin ? parseFloat(salarioMin) : null,
                requiereSalarioMax: salarioMax ? parseFloat(salarioMax) : null
            };

            try {
                await Api.post('/v1/benefits', newBenefit);
                Toast.success('Éxito', 'Beneficio registrado correctamente en el catálogo.');
                // Recargar portal
                _initPortal();
            } catch (err) {
                console.error('Error creating benefit:', err);
                Toast.error('Error', err.message || 'No se pudo crear el beneficio.');
            } finally {
                saveBtn.classList.remove('btn--loading');
                saveBtn.disabled = false;
            }
        });
    }

    async function _renderAdminEnrollmentsTab(container, employees) {
        container.innerHTML = `
            <div class="card animate-slide-up">
                <div class="card__header">
                    <div class="card__title">Gestión de Afiliaciones por Colaborador</div>
                    <div class="card__subtitle">Selecciona un colaborador para auditar, afiliar o cancelar sus beneficios corporativos</div>
                </div>

                <div class="card__body" style="border-bottom:1px solid var(--border-color); padding-bottom:var(--space-5);">
                    <div class="form-group" style="max-width:400px; margin:0;">
                        <label class="form-label" for="admin-select-employee">Seleccionar Colaborador</label>
                        <select class="form-input" id="admin-select-employee">
                            <option value="">-- Elige un colaborador --</option>
                            ${employees.map(e => `
                                <option value="${e.id}">${e.nombre} ${e.apellido} (${e.cargo} - ${Utils.formatCurrency(e.salario)})</option>
                            `).join('')}
                        </select>
                    </div>
                </div>

                <div id="admin-employee-benefits-panel" class="card__body" style="padding-top:var(--space-5);">
                    <div class="empty-state" style="padding:var(--space-6) 0;">
                        <div class="empty-state__icon">👤</div>
                        <p class="empty-state__title">Ningún colaborador seleccionado</p>
                        <p class="empty-state__text">Por favor elige un empleado de la lista superior para gestionar sus beneficios.</p>
                    </div>
                </div>
            </div>
        `;

        const selectEl = document.getElementById('admin-select-employee');
        selectEl.addEventListener('change', async () => {
            const employeeId = selectEl.value;
            _renderAdminEmployeePanel(employeeId);
        });
    }

    async function _renderAdminEmployeePanel(employeeId) {
        const panel = document.getElementById('admin-employee-benefits-panel');
        if (!panel) return;

        if (!employeeId) {
            panel.innerHTML = `
                <div class="empty-state" style="padding:var(--space-6) 0;">
                    <div class="empty-state__icon">👤</div>
                    <p class="empty-state__title">Ningún colaborador seleccionado</p>
                    <p class="empty-state__text">Por favor elige un empleado de la lista superior para gestionar sus beneficios.</p>
                </div>
            `;
            return;
        }

        panel.innerHTML = `
            <div style="text-align:center; padding:var(--space-6);">
                <div class="spinner" style="margin:0 auto var(--space-4);"></div>
                <span>Cargando beneficios del colaborador...</span>
            </div>
        `;

        try {
            // Cargar datos específicos del empleado evaluando elegibilidad
            const [emp, catalog] = await Promise.all([
                Api.get(`/v1/employees/${employeeId}`),
                Api.get(`/v1/benefits?employeeId=${employeeId}`)
            ]);

            _employee = emp;

            // Dividir en afiliados y no afiliados
            const enrolled = catalog.filter(b => b.yaEnrolado);
            const eligible = catalog.filter(b => !b.yaEnrolado && b.esElegible);
            const locked = catalog.filter(b => !b.yaEnrolado && !b.esElegible);

            panel.innerHTML = `
                <div class="grid" style="grid-template-columns:1fr 1fr; gap:var(--space-6); align-items:start;">
                    
                    <!-- Afiliados Actuales -->
                    <div style="display:flex; flex-direction:column; gap:var(--space-4);">
                        <h3 style="font-size:14px; font-weight:600; color:var(--clr-success-400); display:flex; align-items:center; gap:8px;">
                            <span>✔️</span> Afiliaciones Activas (${enrolled.length})
                        </h3>
                        
                        ${enrolled.length === 0 ? `
                            <div class="card" style="padding:var(--space-6); text-align:center; background:rgba(255,255,255,0.01);">
                                <span style="color:var(--clr-text-500); font-size:var(--font-size-sm);">Este colaborador no tiene beneficios activos.</span>
                            </div>
                        ` : enrolled.map(b => `
                            <div class="card" style="border-left:4px solid var(--clr-success-500); display:flex; flex-direction:column; gap:var(--space-3);">
                                <div style="display:flex; justify-content:space-between; align-items:start;">
                                    <div>
                                        <h4 style="font-weight:600; color:var(--clr-text-100);">${b.nombre}</h4>
                                        <p style="font-size:11px; color:var(--clr-text-500); margin-top:2px;">${b.descripcion || ''}</p>
                                    </div>
                                    <strong style="color:var(--clr-text-300);">${b.costo > 0 ? Utils.formatCurrency(b.costo) : 'Gratuito'}</strong>
                                </div>
                                <div style="display:flex; justify-content:space-between; align-items:center; border-top:1px solid var(--border-color); padding-top:var(--space-2); margin-top:2px;">
                                    <span style="font-size:10px; color:var(--clr-text-500);">Estado: Activo</span>
                                    <button class="btn btn--danger btn--sm btn--outline" 
                                            onclick="BenefitsPage.unenroll('${b.enrolamientoId}', '${employeeId}', true)">
                                        Cancelar Afiliación
                                    </button>
                                </div>
                            </div>
                        `).join('')}
                    </div>

                    <!-- Disponibles y Bloqueados -->
                    <div style="display:flex; flex-direction:column; gap:var(--space-5);">
                        
                        <!-- Elegibles para inscribir -->
                        <div style="display:flex; flex-direction:column; gap:var(--space-3);">
                            <h3 style="font-size:14px; font-weight:600; color:var(--clr-accent-400); display:flex; align-items:center; gap:8px;">
                                <span>➕</span> Disponibles para Afiliar (${eligible.length})
                            </h3>
                            
                            ${eligible.length === 0 ? `
                                <div class="card" style="padding:var(--space-5); text-align:center; background:rgba(255,255,255,0.01);">
                                    <span style="color:var(--clr-text-500); font-size:var(--font-size-sm);">No hay otros beneficios disponibles para afiliar.</span>
                                </div>
                            ` : eligible.map(b => `
                                <div class="card" style="display:flex; justify-content:space-between; align-items:center; gap:var(--space-4);">
                                    <div>
                                        <h4 style="font-weight:600; color:var(--clr-text-100);">${b.nombre}</h4>
                                        <span style="font-size:10px; color:var(--clr-accent-450);">${b.costo > 0 ? `${Utils.formatCurrency(b.costo)}/mes` : 'Gratuito'}</span>
                                    </div>
                                    <button class="btn btn--primary btn--sm" 
                                            onclick="BenefitsPage.enroll('${employeeId}', '${b.id}', true)">
                                        Afiliar
                                    </button>
                                </div>
                            `).join('')}
                        </div>

                        <!-- Bloqueados (No cumple reglas) -->
                        <div style="display:flex; flex-direction:column; gap:var(--space-3);">
                            <h3 style="font-size:14px; font-weight:600; color:var(--clr-text-500); display:flex; align-items:center; gap:8px;">
                                <span>🔒</span> Excluidos / No Elegibles (${locked.length})
                            </h3>
                            
                            ${locked.length === 0 ? '' : locked.map(b => `
                                <div class="card" style="opacity: 0.65; background: rgba(0,0,0,0.05); display:flex; flex-direction:column; gap:var(--space-2);">
                                    <div style="display:flex; justify-content:space-between;">
                                        <h4 style="font-weight:600; color:var(--clr-text-300);">${b.nombre}</h4>
                                        <span style="font-size:10px; color:var(--clr-text-500);">${b.costo > 0 ? `${Utils.formatCurrency(b.costo)}/mes` : 'Gratuito'}</span>
                                    </div>
                                    <div style="font-size:10px; color:var(--clr-danger-400); background:rgba(239,68,68,0.05); border:1px solid rgba(239,68,68,0.2); padding:var(--space-2); border-radius:var(--radius-sm);">
                                        <strong>No cumple reglas:</strong> ${_buildAdminRulesText(b)}
                                    </div>
                                </div>
                            `).join('')}
                        </div>

                    </div>
                </div>
            `;

        } catch (err) {
            console.error('Error loading admin employee panel:', err);
            panel.innerHTML = `
                <div style="color:var(--clr-danger-500); text-align:center; padding:var(--space-4);">
                    Error al cargar los beneficios del colaborador: ${err.message || 'Error de API'}
                </div>
            `;
        }
    }

    function _buildAdminRulesText(b) {
        const rules = [];
        if (b.requiereCargo) rules.push(`Cargo '${b.requiereCargo}'`);
        if (b.requiereAntiguedadMeses) rules.push(`Antigüedad >= ${b.requiereAntiguedadMeses} meses`);
        if (b.requiereSalarioMin) rules.push(`Salario >= ${Utils.formatCurrency(b.requiereSalarioMin)}`);
        if (b.requiereSalarioMax) rules.push(`Salario <= ${Utils.formatCurrency(b.requiereSalarioMax)}`);
        return rules.join(', ');
    }

    // ==========================================================
    // OPERACIONES / ACCIONES API
    // ==========================================================
    async function enroll(employeeId, benefitId, isAdminMode = false) {
        try {
            await Api.post('/v1/benefits/enroll', {
                employeeId: employeeId,
                benefitId: benefitId
            });
            Toast.success('Afiliación exitosa', 'El colaborador ha sido inscrito en el beneficio corporativo.');
            
            // Recargar vista
            if (isAdminMode) {
                _renderAdminEmployeePanel(employeeId);
            } else {
                _initPortal();
            }
        } catch (err) {
            console.error('Error enrolling employee:', err);
            Toast.error('Error de Afiliación', err.message || 'No se pudo registrar la afiliación.');
        }
    }

    async function unenroll(enrollmentId, employeeId, isAdminMode = false) {
        if (!confirm('¿Estás seguro de cancelar la afiliación a este beneficio corporativo?')) {
            return;
        }

        try {
            await Api.del(`/v1/benefits/enrollments/${enrollmentId}`);
            Toast.success('Cancelación exitosa', 'La afiliación al beneficio ha sido cancelada.');

            // Recargar vista
            if (isAdminMode) {
                _renderAdminEmployeePanel(employeeId);
            } else {
                _initPortal();
            }
        } catch (err) {
            console.error('Error unenrolling employee:', err);
            Toast.error('Error de Cancelación', err.message || 'No se pudo cancelar la afiliación.');
        }
    }

    function changeTab(tab) {
        _activeTab = tab;
        _initPortal();
    }

    return { render, enroll, unenroll, changeTab };

})();

window.BenefitsPage = BenefitsPage;
