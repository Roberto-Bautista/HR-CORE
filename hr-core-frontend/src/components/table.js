/* =============================================================
   table.js — Componente DataTable reutilizable
   
   Uso:
     DataTable.render(container, {
       columns: [
         { key: 'nombre', label: 'Nombre' },
         { key: 'status', label: 'Estado', render: (val) => `<span class="badge">${val}</span>` },
         { key: '_actions', label: '', render: (_, row) => `<button>Ver</button>` },
       ],
       data: [...],
       emptyMessage: 'No hay empleados registrados.'
     })
   ============================================================= */

const DataTable = (() => {

    function render(container, { columns, data, emptyMessage = 'No hay datos.' }) {
        container.innerHTML = '';

        const wrapper = document.createElement('div');
        wrapper.className = 'data-table-wrapper';

        if (!data || data.length === 0) {
            wrapper.innerHTML = `
                <div class="empty-state">
                    <div class="empty-state__icon">📋</div>
                    <p class="empty-state__title">${emptyMessage}</p>
                </div>
            `;
            container.appendChild(wrapper);
            return;
        }

        const table = document.createElement('table');
        table.className = 'data-table';

        // Cabecera
        const thead = document.createElement('thead');
        thead.innerHTML = `
            <tr>
                ${columns.map(col => `<th>${col.label}</th>`).join('')}
            </tr>
        `;

        // Cuerpo
        const tbody = document.createElement('tbody');
        data.forEach(row => {
            const tr = document.createElement('tr');
            tr.innerHTML = columns.map(col => {
                const value = row[col.key];
                const cell  = col.render ? col.render(value, row) : (value ?? '—');
                return `<td>${cell}</td>`;
            }).join('');
            tbody.appendChild(tr);
        });

        table.appendChild(thead);
        table.appendChild(tbody);
        wrapper.appendChild(table);
        container.appendChild(wrapper);
    }

    /** Renderiza filas esqueleto mientras carga */
    function renderSkeleton(container, columns = 5, rows = 5) {
        const wrapper = document.createElement('div');
        wrapper.className = 'data-table-wrapper';

        const table = document.createElement('table');
        table.className = 'data-table';

        const thead = document.createElement('thead');
        thead.innerHTML = `<tr>${Array(columns).fill('<th><div class="skeleton" style="height:12px;width:80%">&nbsp;</div></th>').join('')}</tr>`;

        const tbody = document.createElement('tbody');
        for (let i = 0; i < rows; i++) {
            const tr = document.createElement('tr');
            tr.className = 'skeleton-row';
            tr.innerHTML = Array(columns).fill(`<td><div class="skeleton" style="height:16px;width:${60 + Math.random()*30}%">&nbsp;</div></td>`).join('');
            tbody.appendChild(tr);
        }

        table.appendChild(thead);
        table.appendChild(tbody);
        wrapper.appendChild(table);
        container.innerHTML = '';
        container.appendChild(wrapper);
    }

    return { render, renderSkeleton };

})();

window.DataTable = DataTable;
