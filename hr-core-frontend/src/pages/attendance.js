/* =============================================================
   attendance.js — Página de Asistencia (Fase 4)
   Horario oficial: 08:00 – 13:30  |  L-V  |  Tolerancia ±5 min
   ============================================================= */

const AttendancePage = (() => {

    let currentEmployeeId = null;
    let _clockInterval    = null;

    // ─── Constantes de horario (espejo del backend) ───────────
    const HORA_ENTRADA   = { h: 8,  m: 0 };
    const HORA_SALIDA    = { h: 13, m: 0 };  // 13:00 según nuevas reglas
    const TOLERANCIA_MIN = 5;

    // ─── Helpers de tiempo ────────────────────────────────────
    function _minutosDesdeMedia(h, m) { return h * 60 + m; }

    function _ahora() {
        const n = new Date();
        return { h: n.getHours(), m: n.getMinutes(), s: n.getSeconds() };
    }

    // TODO [SPRINT FUTURO] Revertir a solo L-V: return d >= 1 && d <= 5;
    // TEMPORAL: todos los días son laborables para pruebas
    function _esDiaLaborable() { return true; }

    function _estadoVentana() {
        const { h, m } = _ahora();
        const minActual  = _minutosDesdeMedia(h, m);
        const minEntrada = _minutosDesdeMedia(HORA_ENTRADA.h, HORA_ENTRADA.m);
        const minSalida  = _minutosDesdeMedia(HORA_SALIDA.h,  HORA_SALIDA.m);

        const dentroEntrada = Math.abs(minActual - minEntrada) <= TOLERANCIA_MIN;
        const dentroSalida  = Math.abs(minActual - minSalida)  <= TOLERANCIA_MIN;

        return { dentroEntrada, dentroSalida, minActual, minEntrada, minSalida };
    }

    function _formatHora(h, m) {
        return `${String(h).padStart(2,'0')}:${String(m).padStart(2,'0')}`;
    }

    function _formatFecha(isoDate) {
        const d = new Date(isoDate + 'T00:00:00');
        return d.toLocaleDateString('es-PE', { weekday:'long', day:'2-digit', month:'short', year:'numeric' });
    }

    function _formatHoraDesdeISO(iso) {
        return new Date(iso).toLocaleTimeString('es-PE', { hour:'2-digit', minute:'2-digit' });
    }

    function _minAHorasMin(minutos) {
        if (!minutos || minutos <= 0) return '0h 0m';
        const h = Math.floor(minutos / 60);
        const m = minutos % 60;
        return h > 0 ? `${h}h ${m}m` : `${m} min`;
    }

    function _nombreDia(diaSemana) {
        const mapa = {
            'MONDAY':'Lunes','TUESDAY':'Martes','WEDNESDAY':'Miércoles',
            'THURSDAY':'Jueves','FRIDAY':'Viernes','SATURDAY':'Sábado','SUNDAY':'Domingo'
        };
        return mapa[diaSemana] || diaSemana;
    }

    // ─── Reloj en tiempo real ─────────────────────────────────
    function _startClock() {
        _stopClock();
        _updateClock();
        _clockInterval = setInterval(_updateClock, 1000);
    }

    function _stopClock() {
        if (_clockInterval) { clearInterval(_clockInterval); _clockInterval = null; }
    }

    function _updateClock() {
        const el = document.getElementById('attendance-clock');
        if (!el) { _stopClock(); return; }

        const now = new Date();
        el.textContent = now.toLocaleTimeString('es-PE', { hour:'2-digit', minute:'2-digit', second:'2-digit' });

        _updateWindowStatus();
    }

    function _updateWindowStatus() {
        const { dentroEntrada, dentroSalida, minActual, minEntrada, minSalida } = _estadoVentana();
        const esLaboral = _esDiaLaborable();
        const estadoEl  = document.getElementById('attendance-window-status');
        if (!estadoEl) return;

        if (!esLaboral) {
            estadoEl.innerHTML = `<span style="color:var(--clr-text-500)">📅 Hoy no es día laborable (solo L-V)</span>`;
            return;
        }

        if (dentroEntrada) {
            estadoEl.innerHTML = `<span style="color:var(--clr-success-400)">🟢 Ventana de ENTRADA abierta (${_formatHora(HORA_ENTRADA.h, HORA_ENTRADA.m)} ±${TOLERANCIA_MIN}min)</span>`;
        } else if (dentroSalida) {
            estadoEl.innerHTML = `<span style="color:var(--clr-accent-400)">🟡 Ventana de SALIDA abierta (${_formatHora(HORA_SALIDA.h, HORA_SALIDA.m)} ±${TOLERANCIA_MIN}min)</span>`;
        } else if (minActual < minEntrada - TOLERANCIA_MIN) {
            const falta = minEntrada - TOLERANCIA_MIN - minActual;
            estadoEl.innerHTML = `<span style="color:var(--clr-text-500)">⏳ Ventana de entrada abre en ${falta} min (${_formatHora(HORA_ENTRADA.h, HORA_ENTRADA.m - TOLERANCIA_MIN)} – ${_formatHora(HORA_ENTRADA.h, HORA_ENTRADA.m + TOLERANCIA_MIN)})</span>`;
        } else if (minActual > minEntrada + TOLERANCIA_MIN && minActual < minSalida - TOLERANCIA_MIN) {
            const falta = minSalida - TOLERANCIA_MIN - minActual;
            estadoEl.innerHTML = `<span style="color:var(--clr-text-500)">⏳ Jornada en curso. Ventana de salida abre en ${Math.max(0,falta)} min</span>`;
        } else {
            estadoEl.innerHTML = `<span style="color:var(--clr-text-500)">✅ Jornada laboral completada por hoy</span>`;
        }
    }

    // ─── Carga de datos del día ───────────────────────────────
    async function _loadToday(content) {
        const histContainer = content.querySelector('.attendance-today');
        try {
            const data = await Api.get(`/v1/attendance/employee/${currentEmployeeId}/today`);
            _renderToday(content, data);
        } catch (err) {
            console.error('Error cargando asistencia de hoy:', err);
            histContainer.innerHTML = `<p style="color:var(--clr-danger-400);padding:2rem">No se pudo conectar al backend. ¿Está corriendo el servidor?</p>`;
        }
    }

    function _renderToday(content, data) {
        const { marcaciones, resumenHoras, estadoJornada, puedeMarcarEntrada, puedeMarcarSalida } = data;

        // Actualizar botones
        const btnEntrada = content.querySelector('#btnEntrada');
        const btnSalida  = content.querySelector('#btnSalida');
        const { dentroEntrada, dentroSalida } = _estadoVentana();
        const esLaboral  = _esDiaLaborable();

        if (btnEntrada) {
            const habilitado = puedeMarcarEntrada && esLaboral;
            btnEntrada.disabled = !habilitado;
            btnEntrada.style.opacity = habilitado ? '1' : '0.45';
            btnEntrada.title = !esLaboral ? 'Solo días laborables' :
                               !puedeMarcarEntrada ? 'Ya tienes entrada registrada' :
                               'Marcar Entrada';
        }
        if (btnSalida) {
            const habilitado = puedeMarcarSalida && esLaboral;
            btnSalida.disabled = !habilitado;
            btnSalida.style.opacity = habilitado ? '1' : '0.45';
            btnSalida.title = !esLaboral ? 'Solo días laborables' :
                              !puedeMarcarSalida ? 'Primero debes marcar entrada' :
                              'Marcar Salida';
        }

        // Resumen de horas
        const efectivas = parseInt(resumenHoras?.horasEfectivasMin || 0);
        const tardanza  = parseInt(resumenHoras?.tardanzaMin       || 0);
        const fueraHor  = parseInt(resumenHoras?.tiempoFueraDeHorarioMin || 0);

        const resumenEl = content.querySelector('.attendance-today');
        resumenEl.innerHTML = `
            <div class="card animate-slide-up" style="animation-delay:0.1s">
                <div class="card__header">
                    <div>
                        <div class="card__title">Resumen de Hoy</div>
                        <div class="card__subtitle">${new Date().toLocaleDateString('es-PE', {weekday:'long', day:'2-digit', month:'long'})}</div>
                    </div>
                    <span class="badge ${estadoJornada==='COMPLETADA'?'badge--success':estadoJornada==='EN_CURSO'?'badge--warning':'badge--neutral'}">
                        ${estadoJornada==='COMPLETADA'?'Completada':estadoJornada==='EN_CURSO'?'En curso':'Sin iniciar'}
                    </span>
                </div>
                <div class="grid-stats" style="grid-template-columns:repeat(3,1fr);margin-top:var(--space-4)">
                    <div class="stat-card stat-card--success">
                        <div class="stat-card__icon stat-card__icon--success">⏱️</div>
                        <div class="stat-card__value" style="font-size:1.4rem">${_minAHorasMin(efectivas)}</div>
                        <div class="stat-card__label">Horas Efectivas</div>
                        <div style="font-size:10px;color:var(--clr-text-500);margin-top:4px">máx 5h 00m</div>
                    </div>
                    <div class="stat-card" style="background:var(--clr-danger-900,rgba(239,68,68,0.1));border-color:var(--clr-danger-800,rgba(239,68,68,0.2))">
                        <div class="stat-card__icon" style="color:var(--clr-danger-400)">⚠️</div>
                        <div class="stat-card__value" style="font-size:1.4rem;color:${tardanza>0?'var(--clr-danger-400)':'var(--clr-success-400)'}">
                            ${tardanza > 0 ? tardanza + ' min' : '0 min'}
                        </div>
                        <div class="stat-card__label">Tardanza</div>
                        <div style="font-size:10px;color:var(--clr-text-500);margin-top:4px">después de 08:05</div>
                    </div>
                    <div class="stat-card stat-card--indigo">
                        <div class="stat-card__icon" style="color:var(--clr-accent-400)">🕐</div>
                        <div class="stat-card__value" style="font-size:1.4rem">${fueraHor > 0 ? fueraHor + ' min' : '0 min'}</div>
                        <div class="stat-card__label">Tiempo Fuera de Horario</div>
                        <div style="font-size:10px;color:var(--clr-text-500);margin-top:4px">solo informativo</div>
                    </div>
                </div>
                ${resumenHoras?.horaIngresoReal ? `
                <div style="margin-top:var(--space-4);padding:var(--space-3) var(--space-4);background:var(--clr-bg-600);border-radius:var(--radius-md);font-size:var(--font-size-xs);color:var(--clr-text-500);display:flex;gap:2rem;flex-wrap:wrap">
                    <span>🕗 Ingreso real: <strong style="color:var(--clr-text-300)">${resumenHoras.horaIngresoReal}</strong></span>
                    <span>✅ Ingreso reconocido: <strong style="color:var(--clr-text-300)">${resumenHoras.horaIngresoReconocida}</strong></span>
                    ${resumenHoras.horaSalidaReal ? `<span>🕐 Salida real: <strong style="color:var(--clr-text-300)">${resumenHoras.horaSalidaReal}</strong></span>` : ''}
                </div>` : ''}
            </div>

            <div class="card animate-slide-up" style="animation-delay:0.2s;margin-top:var(--space-4)">
                <div class="card__header">
                    <div class="card__title">Marcaciones de Hoy</div>
                </div>
                ${marcaciones.length === 0
                    ? `<div class="empty-state" style="padding:2rem"><p class="empty-state__text">Aún no hay marcaciones hoy.</p></div>`
                    : `<div style="display:flex;flex-direction:column;gap:var(--space-2);padding:var(--space-4)">
                        ${marcaciones.map(m => `
                            <div style="display:flex;align-items:center;gap:var(--space-3);padding:var(--space-3);
                                        background:var(--clr-bg-600);border-radius:var(--radius-md);border:1px solid var(--border-color)">
                                <span style="font-size:1.5rem">${m.tipo==='ENTRADA'?'📥':'📤'}</span>
                                <div>
                                    <div style="font-weight:600">${m.tipo==='ENTRADA'?'Entrada':'Salida'}</div>
                                    <div style="font-size:var(--font-size-xs);color:var(--clr-text-500)">${_formatHoraDesdeISO(m.timestamp)}</div>
                                </div>
                                <span class="badge ${m.tipo==='ENTRADA'?'badge--success':'badge--warning'}" style="margin-left:auto">${m.tipo}</span>
                            </div>`).join('')}
                       </div>`}
            </div>
        `;
    }

    // ─── Historial mensual ────────────────────────────────────
    async function _loadHistory(content) {
        const el = content.querySelector('.attendance-history');
        if (!el) return;
        el.innerHTML = `<div style="padding:2rem;text-align:center"><div class="spinner" style="margin:0 auto"></div></div>`;

        try {
            const data = await Api.get(`/v1/attendance/employee/${currentEmployeeId}/history?dias=30`);
            _renderHistory(el, data);
        } catch (err) {
            el.innerHTML = `<p style="color:var(--clr-danger-400);padding:2rem">No se pudo cargar el historial.</p>`;
        }
    }

    function _renderHistory(el, registros) {
        if (!registros || registros.length === 0) {
            el.innerHTML = `
                <div class="empty-state" style="padding:3rem">
                    <div class="empty-state__icon">📋</div>
                    <p class="empty-state__title">Sin historial</p>
                    <p class="empty-state__text">Aún no hay registros de asistencia en los últimos 30 días.</p>
                </div>`;
            return;
        }

        el.innerHTML = `
            <div class="card animate-slide-up" style="animation-delay:0.3s">
                <div class="card__header">
                    <div>
                        <div class="card__title">Historial de Asistencia</div>
                        <div class="card__subtitle">Últimos 30 días laborables</div>
                    </div>
                </div>
                <div class="data-table-wrapper">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Día</th>
                                <th>Entrada (real)</th>
                                <th>Salida (real)</th>
                                <th>Horas Efectivas</th>
                                <th>Tardanza</th>
                                <th>Fuera de Horario</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${registros.map(reg => {
                                const tardMin   = parseInt(reg.tardanzaMin || 0);
                                const fueraMin  = parseInt(reg.tiempoFueraDeHorarioMin || 0);
                                const efectMin  = parseInt(reg.horasEfectivasMin || 0);
                                return `
                                    <tr>
                                        <td>
                                            <div style="font-weight:500">${_nombreDia(reg.diaSemana)}</div>
                                            <div style="font-size:var(--font-size-xs);color:var(--clr-text-500)">${Utils.formatDate(reg.fecha)}</div>
                                        </td>
                                        <td>${reg.horaIngresoReal ? `<span class="badge badge--success">${reg.horaIngresoReal.substring(0,5)}</span>` : '<span style="color:var(--clr-text-500)">—</span>'}</td>
                                        <td>${reg.horaSalidaReal  ? `<span class="badge badge--warning">${reg.horaSalidaReal.substring(0,5)}</span>`  : '<span style="color:var(--clr-text-500)">—</span>'}</td>
                                        <td><strong>${_minAHorasMin(efectMin)}</strong></td>
                                        <td>${tardMin  > 0 ? `<span style="color:var(--clr-danger-400);font-weight:600">${tardMin} min</span>`  : '<span style="color:var(--clr-success-400)">✅ 0</span>'}</td>
                                        <td>${fueraMin > 0 ? `<span style="color:var(--clr-accent-400)">${fueraMin} min</span>` : '—'}</td>
                                    </tr>`;
                            }).join('')}
                        </tbody>
                    </table>
                </div>
            </div>`;
    }

    // ─── Marcar entrada / salida ──────────────────────────────
    async function _markAttendance(tipo, content) {
        const btn = content.querySelector(tipo === 'ENTRADA' ? '#btnEntrada' : '#btnSalida');
        if (btn) { btn.disabled = true; btn.classList.add('btn--loading'); }

        try {
            await Api.post('/v1/attendance/clock', {
                empleadoId: currentEmployeeId,
                tipo: tipo
            });

            Toast.success('¡Marcación registrada!', `Tu ${tipo.toLowerCase()} fue registrada exitosamente.`);

            // Recargar hoy y historial
            setTimeout(async () => {
                await _loadToday(content);
                await _loadHistory(content);
            }, 800);

        } catch (err) {
            Toast.error('Error', err.message || 'No se pudo registrar la marcación.');
        } finally {
            if (btn) { btn.classList.remove('btn--loading'); }
        }
    }

    // ─── Render Administrador ─────────────────────────────────
    async function _renderAdminView(container) {
        _stopClock();
        
        container.innerHTML = '';
        const layout = document.createElement('div');
        layout.className = 'app-layout';

        Sidebar.render(layout);

        const main = document.createElement('main');
        main.className = 'main-content';

        Header.render(main, { title: 'Asistencia', subtitle: 'Control de asistencia general' });

        const content = document.createElement('div');
        content.className = 'page-content';

        content.innerHTML = `
            <div class="page-header animate-fade-in">
                <div class="page-header__left">
                    <h1>Asistencia de Colaboradores</h1>
                    <p>Monitoreo y consulta de marcaciones e historial de tiempos</p>
                </div>
            </div>

            <!-- Selector de empleado -->
            <div class="card animate-fade-in" style="margin-bottom:var(--space-4);padding:var(--space-4);">
                <div style="display:flex;gap:var(--space-3);align-items:center;flex-wrap:wrap;">
                    <div style="flex:1;min-width:250px;">
                        <label class="form-label" for="admin-employee-select" style="margin-bottom:var(--space-2);display:block;font-weight:600;">
                            Seleccionar Colaborador
                        </label>
                        <select class="form-select" id="admin-employee-select" style="width:100%;height:42px;">
                            <option value="">Cargando colaboradores...</option>
                        </select>
                    </div>
                </div>
            </div>

            <!-- Contenedor de Detalles de Asistencia -->
            <div id="admin-attendance-detail">
                <div class="card animate-fade-in" style="padding:4rem 2rem;text-align:center;color:var(--clr-text-500)">
                    <div style="font-size:3rem;margin-bottom:1rem">📋</div>
                    <h3>Ningún colaborador seleccionado</h3>
                    <p style="margin-top:0.5rem">Selecciona un colaborador en la parte superior para visualizar su asistencia y récord mensual.</p>
                </div>
            </div>
        `;

        main.appendChild(content);
        layout.appendChild(main);
        container.appendChild(layout);

        const selectEl = content.querySelector('#admin-employee-select');
        try {
            const employees = await Api.get('/v1/employees');
            const activeEmployees = employees.filter(e => e.status === 'ACTIVO');
            
            if (activeEmployees.length === 0) {
                selectEl.innerHTML = '<option value="">No hay colaboradores activos registrados</option>';
                return;
            }

            selectEl.innerHTML = '<option value="">Elige un colaborador...</option>' + 
                activeEmployees.map(e => `<option value="${e.id}">${e.nombre} ${e.apellido} (${e.codigo}) - ${e.cargo}</option>`).join('');

            selectEl.addEventListener('change', (e) => {
                const empId = e.target.value;
                if (!empId) {
                    content.querySelector('#admin-attendance-detail').innerHTML = `
                        <div class="card animate-fade-in" style="padding:4rem 2rem;text-align:center;color:var(--clr-text-500)">
                            <div style="font-size:3rem;margin-bottom:1rem">📋</div>
                            <h3>Ningún colaborador seleccionado</h3>
                            <p style="margin-top:0.5rem">Selecciona un colaborador en la parte superior para visualizar su asistencia y récord mensual.</p>
                        </div>
                    `;
                    return;
                }
                _loadEmployeeDetails(empId, content);
            });

        } catch (err) {
            selectEl.innerHTML = '<option value="">Error al cargar colaboradores</option>';
            Toast.error('Error', 'No se pudieron obtener los colaboradores.');
        }
    }

    async function _loadEmployeeDetails(empId, content) {
        currentEmployeeId = empId;
        const detailContainer = content.querySelector('#admin-attendance-detail');
        detailContainer.innerHTML = `
            <!-- Resumen de hoy + marcaciones de hoy -->
            <div class="attendance-today">
                <div style="padding:3rem;text-align:center;color:var(--clr-text-500)">
                    <div class="spinner" style="margin:0 auto var(--space-3)"></div>
                    Cargando resumen de hoy...
                </div>
            </div>

            <!-- Historial de asistencia -->
            <div class="attendance-history" style="margin-top:var(--space-4)"></div>
        `;

        await _loadToday(detailContainer);
        await _loadHistory(detailContainer);
    }

    // ─── Render principal ─────────────────────────────────────
    function render(container) {
        const user = Auth.currentUser();
        if (!user) {
            Router.navigate('/login');
            return;
        }

        if (user.rol === 'ADMIN') {
            _renderAdminView(container);
            return;
        } else if (user.rol !== 'WORKER') {
            Router.navigate('/dashboard');
            return;
        }
        currentEmployeeId = user.id;

        container.innerHTML = '';
        const layout = document.createElement('div');
        layout.className = 'app-layout';

        Sidebar.render(layout);

        const main = document.createElement('main');
        main.className = 'main-content';

        Header.render(main, { title: 'Asistencia', subtitle: 'Control de tiempo y marcaciones' });

        const content = document.createElement('div');
        content.className = 'page-content';

        content.innerHTML = `
            <div class="page-header animate-fade-in">
                <div class="page-header__left">
                    <h1>Control de Asistencia</h1>
                    <p>Horario: Lunes a Viernes · 08:00 – 13:00</p>
                </div>
            </div>

            <!-- Reloj y ventana de marcación -->
            <div class="card animate-fade-in" style="text-align:center;padding:2rem 1rem;margin-bottom:var(--space-4)">
                <div id="attendance-clock" style="font-size:3.5rem;font-weight:700;letter-spacing:0.05em;
                     background:linear-gradient(135deg,var(--clr-accent-400),var(--clr-indigo-400));
                     -webkit-background-clip:text;-webkit-text-fill-color:transparent;margin-bottom:0.5rem">
                    --:--:--
                </div>
                <div id="attendance-window-status" style="margin-bottom:1.5rem;font-size:0.9rem">
                    Calculando...
                </div>
                <div style="display:flex;gap:2rem;justify-content:center;flex-wrap:wrap">
                    <button id="btnEntrada" class="btn btn--primary"
                            style="padding:1.2rem 2.5rem;font-size:1.1rem;border-radius:12px;
                                   display:flex;flex-direction:column;align-items:center;gap:0.4rem;min-width:180px">
                        <span style="font-size:1.8rem">📥</span>
                        Marcar Entrada
                    </button>
                    <button id="btnSalida" class="btn btn--secondary"
                            style="padding:1.2rem 2.5rem;font-size:1.1rem;border-radius:12px;
                                   display:flex;flex-direction:column;align-items:center;gap:0.4rem;min-width:180px">
                        <span style="font-size:1.8rem">📤</span>
                        Marcar Salida
                    </button>
                </div>
                <div style="margin-top:1rem;font-size:var(--font-size-xs);color:var(--clr-text-500)">
                    Tolerancia: ±${TOLERANCIA_MIN} minutos antes/después de hora oficial
                </div>
            </div>

            <!-- Resumen de hoy + marcaciones de hoy -->
            <div class="attendance-today">
                <div style="padding:3rem;text-align:center;color:var(--clr-text-500)">
                    <div class="spinner" style="margin:0 auto var(--space-3)"></div>
                    Cargando resumen de hoy...
                </div>
            </div>

            <!-- Historial de asistencia -->
            <div class="attendance-history" style="margin-top:var(--space-4)"></div>
        `;

        main.appendChild(content);
        layout.appendChild(main);
        container.appendChild(layout);

        // Eventos de botones
        content.querySelector('#btnEntrada').addEventListener('click', () => _markAttendance('ENTRADA', content));
        content.querySelector('#btnSalida').addEventListener('click',  () => _markAttendance('SALIDA',  content));

        // Arrancar reloj
        _startClock();

        // Cargar datos
        _loadToday(content);
        _loadHistory(content);
    }

    return { render };
})();

window.AttendancePage = AttendancePage;
