/* =============================================================
   employees.js — Página de Gestión de Empleados
   ============================================================= */

const EmployeesPage = (() => {

    // Datos de ejemplo para desarrollo sin backend
    const MOCK_EMPLOYEES = [
        { id: '1', codigo: 'EMP-001', nombre: 'Ana',    apellido: 'Torres',   cargo: 'Desarrolladora Senior', departamento: 'TI',    salario: 8500, status: 'ACTIVO',   fecha_alta: '2022-03-15' },
        { id: '2', codigo: 'EMP-002', nombre: 'Carlos', apellido: 'Ruiz',     cargo: 'Analista RRHH',         departamento: 'RRHH',  salario: 5200, status: 'ACTIVO',   fecha_alta: '2021-07-01' },
        { id: '3', codigo: 'EMP-003', nombre: 'Sofia',  apellido: 'Mendez',   cargo: 'Diseñadora UX',         departamento: 'TI',    salario: 7000, status: 'ACTIVO',   fecha_alta: '2023-01-10' },
        { id: '4', codigo: 'EMP-004', nombre: 'Juan',   apellido: 'Pérez',    cargo: 'Contador',              departamento: 'Finanzas', salario: 4800, status: 'ACTIVO', fecha_alta: '2020-05-20' },
        { id: '5', codigo: 'EMP-005', nombre: 'María',  apellido: 'González', cargo: 'Project Manager',       departamento: 'TI',    salario: 9200, status: 'INACTIVO', fecha_alta: '2019-11-03' },
    ];

    let _employees = [...MOCK_EMPLOYEES];

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
            key: 'fecha_alta',
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

    function render(container) {
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
                    <p>${_employees.length} colaboradores registrados</p>
                </div>
                <div class="page-header__actions">
                    <button class="btn btn--secondary" id="btn-export">
                        ⬇️ Exportar
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
                    <select class="form-select" id="filter-dept" style="width:180px;height:38px;">
                        <option value="">Todos los dptos.</option>
                        <option value="TI">TI</option>
                        <option value="RRHH">RRHH</option>
                        <option value="Finanzas">Finanzas</option>
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

        _renderTable(_employees);
        _attachEvents();
    }

    function _renderTable(data) {
        const tableContainer = document.getElementById('employees-table');
        if (!tableContainer) return;
        DataTable.render(tableContainer, {
            columns: COLUMNS,
            data,
            emptyMessage: 'No se encontraron empleados.',
        });
    }

    function _attachEvents() {
        // Búsqueda con debounce
        const searchInput = document.getElementById('search-employees');
        const filterStatus = document.getElementById('filter-status');
        const filterDept   = document.getElementById('filter-dept');

        const applyFilters = Utils.debounce(() => {
            const q      = searchInput?.value.toLowerCase() || '';
            const status = filterStatus?.value || '';
            const dept   = filterDept?.value || '';

            const filtered = _employees.filter(emp => {
                const matchQ = !q
                    || `${emp.nombre} ${emp.apellido} ${emp.cargo}`.toLowerCase().includes(q);
                const matchStatus = !status || emp.status === status;
                const matchDept   = !dept   || emp.departamento === dept;
                return matchQ && matchStatus && matchDept;
            });

            _renderTable(filtered);
        }, 250);

        searchInput?.addEventListener('input', applyFilters);
        filterStatus?.addEventListener('change', applyFilters);
        filterDept?.addEventListener('change', applyFilters);

        // Botón nuevo empleado
        document.getElementById('btn-new-employee')?.addEventListener('click', () => {
            _openFormModal(null);
        });

        // Botón exportar
        document.getElementById('btn-export')?.addEventListener('click', () => {
            Toast.info('Exportar', 'Funcionalidad disponible en Sprint 2.');
        });
    }

    function _openFormModal(employee) {
        const isEdit = !!employee;

        Modal.open({
            title: isEdit ? `Editar: ${employee.nombre} ${employee.apellido}` : 'Nuevo Empleado',
            size: 'lg',
            body: `
                <form id="employee-form" novalidate>
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
                                <option value="TI"       ${employee?.departamento === 'TI'       ? 'selected' : ''}>TI</option>
                                <option value="RRHH"     ${employee?.departamento === 'RRHH'     ? 'selected' : ''}>RRHH</option>
                                <option value="Finanzas" ${employee?.departamento === 'Finanzas' ? 'selected' : ''}>Finanzas</option>
                                <option value="Gerencia" ${employee?.departamento === 'Gerencia' ? 'selected' : ''}>Gerencia</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label class="form-label form-label--required" for="emp-salario">Salario (PEN)</label>
                            <input class="form-input" id="emp-salario" type="number" min="1"
                                   placeholder="5000" value="${employee?.salario || ''}" required />
                        </div>
                        <div class="form-group">
                            <label class="form-label form-label--required" for="emp-fecha-alta">Fecha de Ingreso</label>
                            <input class="form-input" id="emp-fecha-alta" type="date"
                                   value="${employee?.fecha_alta || ''}" required />
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

    function _handleSave(id) {
        const nombre       = document.getElementById('emp-nombre')?.value.trim();
        const apellido     = document.getElementById('emp-apellido')?.value.trim();
        const cargo        = document.getElementById('emp-cargo')?.value.trim();
        const departamento = document.getElementById('emp-departamento')?.value;
        const salario      = parseFloat(document.getElementById('emp-salario')?.value);
        const fecha_alta   = document.getElementById('emp-fecha-alta')?.value;

        if (!nombre || !apellido || !cargo || !salario || !fecha_alta) {
            Toast.warning('Campos incompletos', 'Por favor completa todos los campos obligatorios.');
            return;
        }

        if (id) {
            // Editar
            const idx = _employees.findIndex(e => e.id === id);
            if (idx !== -1) {
                _employees[idx] = { ..._employees[idx], nombre, apellido, cargo, departamento, salario, fecha_alta };
                Toast.success('Empleado actualizado', `${nombre} ${apellido} fue actualizado correctamente.`);
            }
        } else {
            // Crear nuevo (simulado)
            const newEmp = {
                id: Utils.uid(),
                codigo: `EMP-${String(_employees.length + 1).padStart(3, '0')}`,
                nombre, apellido, cargo, departamento, salario, fecha_alta,
                status: 'ACTIVO',
            };
            _employees.unshift(newEmp);
            Toast.success('Empleado creado', `${nombre} ${apellido} fue registrado exitosamente.`);
        }

        Modal.close();
        _renderTable(_employees);
    }

    // Funciones públicas para los botones de la tabla
    function viewEmployee(id) {
        const emp = _employees.find(e => e.id === id);
        if (!emp) return;
        Toast.info(`${emp.nombre} ${emp.apellido}`, `Cargo: ${emp.cargo} · ${emp.departamento}`);
    }

    function editEmployee(id) {
        const emp = _employees.find(e => e.id === id);
        if (emp) _openFormModal(emp);
    }

    return { render, viewEmployee, editEmployee };

})();

window.EmployeesPage = EmployeesPage;
