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
            render: (_, row) => `
                <div style="display:flex;gap:var(--space-2);">
                    <button class="btn btn--ghost btn--sm btn--icon" title="Ver detalle"
                            onclick="EmployeesPage.viewEmployee('${row.id}')">👁️</button>
                    <button class="btn btn--ghost btn--sm btn--icon" title="Editar"
                            onclick="EmployeesPage.editEmployee('${row.id}')">✏️</button>
                </div>`,
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

        // Generar código automático para nuevos empleados
        const nextCode = isEdit ? employee.codigo : `EMP-${String(_employees.length + 1).padStart(3, '0')}`;

        Modal.open({
            title: isEdit ? `Editar: ${employee.nombre} ${employee.apellido}` : 'Nuevo Empleado',
            size: 'lg',
            body: `
                <form id="employee-form" novalidate>
                    <div class="form-row">
                        <div class="form-group">
                            <label class="form-label form-label--required" for="emp-codigo">Código</label>
                            <input class="form-input" id="emp-codigo" type="text"
                                   placeholder="EMP-001" value="${nextCode}"
                                   ${isEdit ? 'readonly style="opacity:0.6;cursor:not-allowed;"' : ''} required />
                        </div>
                        <div class="form-group">
                            <label class="form-label form-label--required" for="emp-fecha-alta">Fecha de Ingreso</label>
                            <input class="form-input" id="emp-fecha-alta" type="date"
                                   value="${employee?.fechaAlta || new Date().toISOString().slice(0,10)}" required />
                        </div>
                    </div>
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
        const codigo       = document.getElementById('emp-codigo')?.value.trim();
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
                // Crear empleado nuevo (POST)
                await Api.post('/v1/employees', {
                    codigo, nombre, apellido, email, telefono, cargo,
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

    // Funciones públicas para los botones de la tabla
    function viewEmployee(id) {
        const emp = _employees.find(e => e.id === id);
        if (!emp) return;

        Modal.open({
            title: `${emp.nombre} ${emp.apellido}`,
            body: `
                <div style="display:flex;flex-direction:column;gap:var(--space-4);">
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
                </div>
            `,
            footer: `
                <button class="btn btn--secondary" onclick="Modal.close()">Cerrar</button>
                <button class="btn btn--primary" onclick="Modal.close(); EmployeesPage.editEmployee('${emp.id}')">✏️ Editar</button>
            `,
        });
    }

    function editEmployee(id) {
        const emp = _employees.find(e => e.id === id);
        if (emp) _openFormModal(emp);
    }

    return { render, viewEmployee, editEmployee };

})();

window.EmployeesPage = EmployeesPage;
