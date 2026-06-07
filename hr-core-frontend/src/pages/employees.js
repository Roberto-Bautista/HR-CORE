/* =============================================================
   employees.js — Página de Gestión de Empleados
   Sprint 2: Conectado al backend real (API REST)
   ============================================================= */

const EmployeesPage = (() => {

    // Ya NO hay datos mock, todo viene de la API
    let _employees = [];

    const COLUMNS = [
        {
            key: 'nombre',
            label: 'Empleado',
            render: (_, row) => `
                <div style="display:flex;align-items:center;gap:var(--space-3);">
                    <div class="avatar">${Utils.getInitials(row.nombre, row.apellido)}</div>
                    <div>
                        <div style="font-weight:500;">${row.nombre} ${row.apellido}</div>
                        <div style="font-size:var(--font-size-xs);color:var(--clr-text-500);">${row.codigo}</div>
                    </div>
                </div>`,
        },
        { key: 'cargo',        label: 'Cargo' },
        { key: 'departamento', label: 'Departamento' },
        {
            key: 'salario',
            label: 'Salario',
            render: val => `<span style="font-weight:500;">${Utils.formatCurrency(val)}</span>`,
        },
        {
            key: 'fechaAlta',
            label: 'Ingreso',
            render: val => Utils.formatDate(val),
        },
        {
            key: 'status',
            label: 'Estado',
            render: val => `<span class="badge ${Utils.statusToBadge(val)}">${Utils.statusToLabel(val)}</span>`,
        },
        {
            key: '_actions',
            label: 'Acciones',
            render: (_, row) => {
                const isCesado = row.status === 'CESADO' || row.status === 'INACTIVO';
                return `
                <div style="display:flex;gap:var(--space-2);">
                    <button class="btn btn--ghost btn--sm btn--icon" title="Ver detalle"
                            onclick="EmployeesPage.viewEmployee('${row.id}')">👁️</button>
                    <button class="btn btn--ghost btn--sm btn--icon" title="Editar"
                            onclick="EmployeesPage.editEmployee('${row.id}')">✏️</button>
                    ${!isCesado ? `
                    <button class="btn btn--ghost btn--sm btn--icon" title="Dar de baja (Cesar)"
                            onclick="EmployeesPage.terminateEmployee('${row.id}')" style="color: var(--clr-danger-500);">🛑</button>
                    ` : ''}
                </div>`;
            }
        },
    ];

    async function render(container) {
        container.innerHTML = '';

        const layout = document.createElement('div');
        layout.className = 'app-layout';

        Sidebar.render(layout);

        const main = document.createElement('main');
        main.className = 'main-content';

        Header.render(main, { title: 'Empleados', subtitle: 'Gestión del ciclo de vida del colaborador' });

        const content = document.createElement('div');
        content.className = 'page-content';

        content.innerHTML = `
            <div class="page-header animate-fade-in">
                <div class="page-header__left">
                    <h1>Empleados</h1>
                    <p id="employee-count">Cargando empleados...</p>
                </div>
                <div class="page-header__actions">
                    <button class="btn btn--secondary" id="btn-refresh">
                        🔄 Actualizar
                    </button>
                    <button class="btn btn--primary" id="btn-new-employee">
                        ➕ Nuevo Empleado
                    </button>
                </div>
            </div>

            <!-- Barra de búsqueda y filtros -->
            <div class="card animate-fade-in" style="margin-bottom:var(--space-4);padding:var(--space-4);">
                <div style="display:flex;gap:var(--space-3);align-items:center;flex-wrap:wrap;">
                    <div class="search-bar" style="flex:1;min-width:200px;">
                        <span class="search-bar__icon">🔍</span>
                        <input type="text" id="search-employees" placeholder="Buscar por nombre, cargo, departamento..." />
                    </div>
                    <select class="form-select" id="filter-status" style="width:160px;height:38px;">
                        <option value="">Todos los estados</option>
                        <option value="ACTIVO">Activo</option>
                        <option value="INACTIVO">Inactivo</option>
                        <option value="CESADO">Cesado</option>
                    </select>
                </div>
            </div>

            <!-- Tabla -->
            <div class="card animate-slide-up" style="padding:0;">
                <div id="employees-table"></div>
            </div>
        `;

        main.appendChild(content);
        layout.appendChild(main);
        container.appendChild(layout);

        _attachEvents();
        await _loadEmployees();
    }

    /** Carga empleados desde el backend */
    async function _loadEmployees() {
        const tableContainer = document.getElementById('employees-table');
        DataTable.renderSkeleton(tableContainer, 7, 5);  // Muestra skeleton mientras carga

        try {
            _employees = await Api.get('/v1/employees');
            _renderTable(_employees);
            _updateCount(_employees.length);
        } catch (err) {
            Toast.error('Error al cargar', err.message || 'No se pudo conectar al servidor.');
            _renderTable([]);
            _updateCount(0);
        }
    }

    function _renderTable(data) {
        const tableContainer = document.getElementById('employees-table');
        if (!tableContainer) return;
        DataTable.render(tableContainer, {
            columns: COLUMNS,
            data,
            emptyMessage: 'No se encontraron empleados. ¡Crea el primero con el botón de arriba!',
        });
    }

    function _updateCount(count) {
        const el = document.getElementById('employee-count');
        if (el) el.textContent = `${count} colaboradores registrados`;
    }

    function _attachEvents() {
        // Búsqueda con debounce (filtra en el frontend sobre los datos ya cargados)
        const searchInput  = document.getElementById('search-employees');
        const filterStatus = document.getElementById('filter-status');

        const applyFilters = Utils.debounce(() => {
            const q      = searchInput?.value.toLowerCase() || '';
            const status = filterStatus?.value || '';

            const filtered = _employees.filter(emp => {
                const matchQ = !q
                    || `${emp.nombre} ${emp.apellido} ${emp.cargo} ${emp.departamento}`.toLowerCase().includes(q);
                const matchStatus = !status || emp.status === status;
                return matchQ && matchStatus;
            });

            _renderTable(filtered);
            _updateCount(filtered.length);
        }, 250);

        searchInput?.addEventListener('input', applyFilters);
        filterStatus?.addEventListener('change', applyFilters);

        // Botón nuevo empleado
        document.getElementById('btn-new-employee')?.addEventListener('click', () => {
            _openFormModal(null);
        });

        // Botón refrescar
        document.getElementById('btn-refresh')?.addEventListener('click', () => {
            _loadEmployees();
        });
    }

    function _openFormModal(employee) {
        const isEdit = !!employee;

        Modal.open({
            title: isEdit ? `Editar: ${employee.nombre} ${employee.apellido}` : 'Nuevo Empleado',
            size: 'lg',
            body: `
                <form id="employee-form" novalidate>
                    ${isEdit ? `
                    <div class="form-row">
                        <div class="form-group">
                            <label class="form-label" for="emp-codigo">Código (autogenerado)</label>
                            <input class="form-input" id="emp-codigo" type="text"
                                   value="${employee.codigo}"
                                   readonly style="opacity:0.6;cursor:not-allowed;" />
                        </div>
                        <div class="form-group">
                            <label class="form-label form-label--required" for="emp-fecha-alta">Fecha de Ingreso</label>
                            <input class="form-input" id="emp-fecha-alta" type="date"
                                   value="${employee.fechaAlta}" required />
                        </div>
                    </div>
                    ` : `
                    <div class="form-row">
                        <div class="form-group">
                            <label class="form-label" for="emp-codigo">Código</label>
                            <input class="form-input" id="emp-codigo" type="text"
                                   value="EMP-${String(_employees.length + 1).padStart(3, '0')}"
                                   readonly style="opacity:0.6;cursor:not-allowed;" />
                        </div>
                        <div class="form-group">
                            <label class="form-label form-label--required" for="emp-fecha-alta">Fecha de Ingreso</label>
                            <input class="form-input" id="emp-fecha-alta" type="date"
                                   value="${new Date().toISOString().slice(0,10)}" required />
                        </div>
                    </div>
                    `}
                    <div class="form-row">
                        <div class="form-group">
                            <label class="form-label form-label--required" for="emp-nombre">Nombre</label>
                            <input class="form-input" id="emp-nombre" type="text"
                                   placeholder="Ej: Ana" value="${employee?.nombre || ''}" required />
                        </div>
                        <div class="form-group">
                            <label class="form-label form-label--required" for="emp-apellido">Apellido</label>
                            <input class="form-input" id="emp-apellido" type="text"
                                   placeholder="Ej: Torres" value="${employee?.apellido || ''}" required />
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="form-label form-label--required" for="emp-email">Email</label>
                        <input class="form-input" id="emp-email" type="email"
                               placeholder="ana@empresa.com" value="${employee?.email || ''}" required />
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label class="form-label form-label--required" for="emp-cargo">Cargo</label>
                            <input class="form-input" id="emp-cargo" type="text"
                                   placeholder="Ej: Desarrollador Senior" value="${employee?.cargo || ''}" required />
                        </div>
                        <div class="form-group">
                            <label class="form-label form-label--required" for="emp-departamento">Departamento</label>
                            <select class="form-select" id="emp-departamento">
                                <option value="TI"         ${employee?.departamento === 'TI'         ? 'selected' : ''}>TI</option>
                                <option value="RRHH"       ${employee?.departamento === 'RRHH'       ? 'selected' : ''}>RRHH</option>
                                <option value="Finanzas"   ${employee?.departamento === 'Finanzas'   ? 'selected' : ''}>Finanzas</option>
                                <option value="Gerencia"   ${employee?.departamento === 'Gerencia'   ? 'selected' : ''}>Gerencia</option>
                                <option value="Operaciones" ${employee?.departamento === 'Operaciones' ? 'selected' : ''}>Operaciones</option>
                                <option value="Marketing"  ${employee?.departamento === 'Marketing'  ? 'selected' : ''}>Marketing</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label class="form-label form-label--required" for="emp-salario">Salario (PEN)</label>
                            <input class="form-input" id="emp-salario" type="number" min="1" step="0.01"
                                   placeholder="5000.00" value="${employee?.salario || ''}" required />
                        </div>
                        <div class="form-group">
                            <label class="form-label" for="emp-telefono">Teléfono</label>
                            <input class="form-input" id="emp-telefono" type="text"
                                   placeholder="+51 999 999 999" value="${employee?.telefono || ''}" />
                        </div>
                    </div>
                </form>
            `,
            footer: `
                <button class="btn btn--secondary" onclick="Modal.close()">Cancelar</button>
                <button class="btn btn--primary" id="btn-save-employee">
                    ${isEdit ? '💾 Guardar Cambios' : '➕ Crear Empleado'}
                </button>
            `,
        });

        document.getElementById('btn-save-employee')?.addEventListener('click', () => {
            _handleSave(employee?.id || null);
        });
    }

    async function _handleSave(id) {
        const nombre       = document.getElementById('emp-nombre')?.value.trim();
        const apellido     = document.getElementById('emp-apellido')?.value.trim();
        const email        = document.getElementById('emp-email')?.value.trim();
        const telefono     = document.getElementById('emp-telefono')?.value.trim();
        const cargo        = document.getElementById('emp-cargo')?.value.trim();
        const departamento = document.getElementById('emp-departamento')?.value;
        const salario      = parseFloat(document.getElementById('emp-salario')?.value);
        const fechaAlta    = document.getElementById('emp-fecha-alta')?.value;

        if (!nombre || !apellido || !email || !cargo || !salario || !fechaAlta) {
            Toast.warning('Campos incompletos', 'Por favor completa todos los campos obligatorios.');
            return;
        }

        const btnSave = document.getElementById('btn-save-employee');
        btnSave?.classList.add('btn--loading');

        try {
            if (id) {
                // Actualizar empleado existente (PUT)
                await Api.put(`/v1/employees/${id}`, {
                    nombre, apellido, email, telefono, cargo, departamento, salario
                });
                Toast.success('Empleado actualizado', `${nombre} ${apellido} fue actualizado correctamente.`);
            } else {
                // Crear empleado nuevo (POST) — sin código, se autogenera
                await Api.post('/v1/employees', {
                    nombre, apellido, email, telefono, cargo,
                    departamento, salario, fechaAlta
                });
                Toast.success('Empleado creado', `${nombre} ${apellido} fue registrado exitosamente.`);
            }

            Modal.close();
            await _loadEmployees();  // Recargar la tabla desde la BD

        } catch (err) {
            Toast.error('Error al guardar', err.message || 'No se pudo guardar el empleado.');
        } finally {
            btnSave?.classList.remove('btn--loading');
        }
    }

    // ==========================================================
    // Vista de detalle con historial de cambios
    // ==========================================================

    async function viewEmployee(id) {
        const emp = _employees.find(e => e.id === id);
        if (!emp) return;

        // Cargar historial desde el backend
        let historyHtml = '<p style="color:var(--clr-text-500);text-align:center;padding:var(--space-4);">Cargando historial...</p>';

        Modal.open({
            title: `${emp.nombre} ${emp.apellido}`,
            size: 'lg',
            body: `
                <div style="display:flex;flex-direction:column;gap:var(--space-4);">
                    <!-- Datos del empleado -->
                    <div style="display:flex;align-items:center;gap:var(--space-4);">
                        <div class="avatar avatar--xl">${Utils.getInitials(emp.nombre, emp.apellido)}</div>
                        <div>
                            <h3 style="font-size:var(--font-size-xl);font-weight:700;">${emp.nombre} ${emp.apellido}</h3>
                            <p class="text-muted">${emp.codigo} · ${emp.cargo}</p>
                            <span class="badge ${Utils.statusToBadge(emp.status)}">${Utils.statusToLabel(emp.status)}</span>
                        </div>
                    </div>
                    <hr class="divider" />
                    <div class="grid-2" style="gap:var(--space-4);">
                        <div><span class="text-muted text-sm">Email</span><p>${emp.email}</p></div>
                        <div><span class="text-muted text-sm">Teléfono</span><p>${emp.telefono || '—'}</p></div>
                        <div><span class="text-muted text-sm">Departamento</span><p>${emp.departamento}</p></div>
                        <div><span class="text-muted text-sm">Salario</span><p>${Utils.formatCurrency(emp.salario)}</p></div>
                        <div><span class="text-muted text-sm">Fecha de Ingreso</span><p>${Utils.formatDate(emp.fechaAlta)}</p></div>
                        <div><span class="text-muted text-sm">Registrado</span><p>${Utils.formatDate(emp.createdAt)}</p></div>
                    </div>

                    <!-- Historial de cambios -->
                    <hr class="divider" />
                    <div>
                        <h4 style="font-size:var(--font-size-lg);font-weight:600;margin-bottom:var(--space-3);">
                            📋 Historial del Ciclo de Vida
                        </h4>
                        <div id="employee-history-timeline">
                            ${historyHtml}
                        </div>
                    </div>
                </div>
            `,
            footer: `
                <button class="btn btn--secondary" onclick="Modal.close()">Cerrar</button>
                <button class="btn btn--primary" onclick="Modal.close(); EmployeesPage.editEmployee('${emp.id}')">✏️ Editar</button>
            `,
        });

        // Cargar historial de forma asíncrona
        try {
            const history = await Api.get(`/v1/employees/${id}/history`);
            const container = document.getElementById('employee-history-timeline');
            if (container) {
                container.innerHTML = _renderTimeline(history);
            }
        } catch (err) {
            const container = document.getElementById('employee-history-timeline');
            if (container) {
                container.innerHTML = '<p style="color:var(--clr-danger-500);text-align:center;">Error al cargar el historial.</p>';
            }
        }
    }

    /**
     * Renderiza la línea de tiempo del historial de cambios.
     */
    function _renderTimeline(history) {
        if (!history || history.length === 0) {
            return '<p style="color:var(--clr-text-500);text-align:center;padding:var(--space-4);">No hay cambios registrados.</p>';
        }

        const iconMap = {
            'ALTA':                  '🟢',
            'CESE':                  '🔴',
            'CAMBIO_CARGO':          '📋',
            'CAMBIO_SALARIO':        '💰',
            'CAMBIO_DEPARTAMENTO':   '🏢',
            'ACTUALIZACION_DATOS':   '✏️',
            'ACTUALIZACION_PERFIL':  '📄',
            'CAMBIO_EMAIL':          '📧',
        };

        const colorMap = {
            'ALTA':                  'var(--clr-success-500, #22c55e)',
            'CESE':                  'var(--clr-danger-500, #ef4444)',
            'CAMBIO_CARGO':          'var(--clr-primary-500, #6366f1)',
            'CAMBIO_SALARIO':        'var(--clr-warning-500, #f59e0b)',
            'CAMBIO_DEPARTAMENTO':   'var(--clr-info-500, #3b82f6)',
            'ACTUALIZACION_DATOS':   'var(--clr-text-400, #94a3b8)',
            'ACTUALIZACION_PERFIL':  'var(--clr-primary-400, #818cf8)',
            'CAMBIO_EMAIL':          'var(--clr-text-400, #94a3b8)',
        };

        const items = history.map((h, i) => {
            const icon = iconMap[h.tipoCambio] || '📝';
            const color = colorMap[h.tipoCambio] || 'var(--clr-text-400)';
            const fecha = _formatHistoryDate(h.fecha);
            const isLast = i === history.length - 1;

            let detailHtml = '';
            if (h.valorAnterior && h.valorNuevo && h.tipoCambio !== 'ALTA') {
                detailHtml = `
                    <div style="margin-top:var(--space-2);padding:var(--space-2) var(--space-3);background:var(--clr-bg-200);border-radius:var(--radius-md);font-size:var(--font-size-xs);">
                        <span style="color:var(--clr-danger-500);text-decoration:line-through;">${h.valorAnterior}</span>
                        <span style="margin:0 var(--space-2);">→</span>
                        <span style="color:var(--clr-success-500);font-weight:600;">${h.valorNuevo}</span>
                    </div>`;
            }

            return `
                <div style="display:flex;gap:var(--space-3);position:relative;">
                    <!-- Línea vertical -->
                    <div style="display:flex;flex-direction:column;align-items:center;min-width:32px;">
                        <div style="width:32px;height:32px;border-radius:50%;background:${color};display:flex;align-items:center;justify-content:center;font-size:14px;flex-shrink:0;">
                            ${icon}
                        </div>
                        ${!isLast ? `<div style="width:2px;flex:1;background:var(--clr-border-200);margin:4px 0;"></div>` : ''}
                    </div>
                    <!-- Contenido -->
                    <div style="padding-bottom:${isLast ? '0' : 'var(--space-4)'};flex:1;">
                        <div style="font-weight:500;font-size:var(--font-size-sm);">${h.descripcion || h.tipoCambio}</div>
                        <div style="font-size:var(--font-size-xs);color:var(--clr-text-500);margin-top:2px;">${fecha}</div>
                        ${detailHtml}
                    </div>
                </div>`;
        });

        return `<div style="display:flex;flex-direction:column;">${items.join('')}</div>`;
    }

    /**
     * Formatea la fecha del historial de forma legible.
     */
    function _formatHistoryDate(dateStr) {
        try {
            const d = new Date(dateStr);
            return d.toLocaleDateString('es-PE', {
                year: 'numeric', month: 'long', day: 'numeric',
                hour: '2-digit', minute: '2-digit'
            });
        } catch {
            return dateStr;
        }
    }

    function editEmployee(id) {
        const emp = _employees.find(e => e.id === id);
        if (emp) _openFormModal(emp);
    }

    function terminateEmployee(id) {
        const emp = _employees.find(e => e.id === id);
        if (!emp) return;

        if (confirm(`¿Estás seguro que deseas dar de baja a ${emp.nombre} ${emp.apellido}?`)) {
            _handleTerminate(id);
        }
    }

    async function _handleTerminate(id) {
        try {
            const fechaCese = new Date().toISOString().split('T')[0];
            await Api.patch(`/v1/employees/${id}/cese?fechaCese=${fechaCese}`);
            Toast.success('Empleado cesado', 'El empleado fue dado de baja correctamente.');
            await _loadEmployees();
        } catch(err) {
            Toast.error('Error al cesar', err.message || 'No se pudo cesar al empleado.');
        }
    }

    return { render, viewEmployee, editEmployee, terminateEmployee };

})();

window.EmployeesPage = EmployeesPage;
