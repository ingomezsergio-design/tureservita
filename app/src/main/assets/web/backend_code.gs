// ====== BACKEND TUCANCHA PRO V9.1 (CORREGIDO) ======
// Correcciones aplicadas:
//  1. Se eliminó la función duplicada obtenerCanchasDeComplejo que bloqueaba la lectura.
//  2. Se unificó la verificación de permisos en cascada con soporte de callerEmail.
//  3. Corrección de endpoints en doGet / doPost para soportar API REST y WebView móvil.
//  4. Índices dinámicos de columnas para garantizar compatibilidad con nuevas hojas.

const SHEET_ID = '1Na3CqEAMO2iK3ljPxtWkOfOmt9rFNLE9rvWZM30mX90';
const CARPETA_DRIVE_ID = '1E1aNyCAi2RRF-JXfxjBaSv7CxqsAU6TI';

function doGet(e) {
  // Soporte para API REST si se pasa parámetro 'action'
  if (e && e.parameter && e.parameter.action) {
    try {
      const action = e.parameter.action;
      let data = null;
      if (action === 'obtenerCanchasPublicas') {
        data = obtenerCanchasPublicas();
      } else if (action === 'obtenerConfigSitio') {
        data = obtenerConfigSitio();
      } else if (action === 'obtenerPreguntasBot') {
        data = obtenerPreguntasBot();
      } else if (action === 'consultarBot') {
        data = consultarBot(e.parameter.mensaje || '');
      } else if (action === 'obtenerDisponibilidad') {
        data = obtenerDisponibilidad(e.parameter.complejoId, e.parameter.canchaId, e.parameter.fecha);
      }
      return ContentService.createTextOutput(JSON.stringify({ ok: true, data: data }))
        .setMimeType(ContentService.MimeType.JSON);
    } catch (err) {
      return ContentService.createTextOutput(JSON.stringify({ ok: false, error: err.message }))
        .setMimeType(ContentService.MimeType.JSON);
    }
  }

  const tpl = HtmlService.createTemplateFromFile('Index');
  tpl.datosPago = JSON.stringify({
    estado: (e && e.parameter && e.parameter.pago) || null,
    reserva: (e && e.parameter && e.parameter.reserva) || null
  });
  return tpl.evaluate()
    .setTitle('TuCancha Pro | Enterprise SaaS')
    .setXFrameOptionsMode(HtmlService.XFrameOptionsMode.ALLOWALL);
}

function doPost(e) {
  try {
    const complejoId = e.parameter && e.parameter.complejo;
    let paymentId = (e.parameter && e.parameter.id) || null;
    if (!paymentId && e.postData && e.postData.contents) {
      try {
        const body = JSON.parse(e.postData.contents);
        if (body.data && body.data.id) paymentId = body.data.id;
        // Soporte para RPC REST API vía POST
        if (body.action) {
          const res = invocarAccionRPC_(body.action, body.args || []);
          return ContentService.createTextOutput(JSON.stringify({ ok: true, result: res }))
            .setMimeType(ContentService.MimeType.JSON);
        }
      } catch (err) {}
    }
    if (complejoId && paymentId) procesarNotificacionPago_(complejoId, paymentId);
  } catch (err) {
    return ContentService.createTextOutput(JSON.stringify({ ok: false, error: err.message }))
      .setMimeType(ContentService.MimeType.JSON);
  }
  return ContentService.createTextOutput('ok');
}

function invocarAccionRPC_(action, args) {
  const allowed = [
    'loginUsuario', 'registrarUsuario', 'obtenerCanchasPublicas', 'obtenerDisponibilidad',
    'crearReserva', 'obtenerMisReservas', 'cancelarReserva', 'obtenerDashboardAdmin',
    'obtenerAgendaDia', 'obtenerEstadisticasComplejo', 'crearCancha', 'editarCancha',
    'eliminarCancha', 'crearReservaManual', 'crearReservaRecurrente', 'cambiarEstadoReserva',
    'obtenerPromocionesComplejo', 'crearPromocion', 'editarPromocion', 'eliminarPromocion',
    'validarPromocion', 'consultarBot', 'obtenerPreguntasBot', 'obtenerConfigSitio'
  ];
  if (allowed.indexOf(action) === -1) throw new Error('Acción no permitida: ' + action);
  return this[action].apply(this, args);
}

// ==========================================
// UTILIDADES GENERALES
// ==========================================
function ss_() { return SpreadsheetApp.openById(SHEET_ID); }

const PREGUNTAS_BOT_DEFAULT = [
  { pregunta: '¿Cómo reservo una cancha?', respuesta: 'Elegí el deporte y la cancha que te guste, tocá "Seleccionar Turno", elegí el día y el horario, y confirmá la reserva. Necesitás tener tu cuenta iniciada.', palabrasClave: 'reservar turno cancha como quiero' },
  { pregunta: '¿Cómo cancelo una reserva?', respuesta: 'Andá a la sección "Mis Reservas" desde el menú lateral, buscá tu reserva y tocá "Cancelar". Ojo: solo se puede cancelar mientras esté en estado Pendiente y dentro del plazo que fije cada complejo.', palabrasClave: 'cancelar cancelacion baja reserva no voy' },
  { pregunta: '¿Qué formas de pago aceptan?', respuesta: 'Depende del complejo: algunos tienen pago online directo con Mercado Pago al momento de reservar. Si no lo tienen, te van a pasar su Alias/CBU para transferir, o coordinás para pagar en efectivo allá.', palabrasClave: 'pago pagar mercadopago efectivo tarjeta transferencia alias cbu sena seña' },
  { pregunta: '¿Puedo reservar sin registrarme?', respuesta: 'No, por cuestiones de seguridad y para que te llegue el comprobante, necesitás crear una cuenta gratis. El registro te lleva 30 segundos.', palabrasClave: 'registrarme cuenta login registro ingresar' },
  { pregunta: '¿Se suspenden los turnos por lluvia?', respuesta: 'Depende si la cancha que reservaste es techada o descubierta. Podés revisar el detalle de la cancha o contactar directamente al complejo por WhatsApp para consultar las reglas.', palabrasClave: 'lluvia suspende mal tiempo clima llover techada' },
  { pregunta: '¿Alquilan pelotas, paletas o chalecos?', respuesta: 'Casi todos los complejos ofrecen artículos de alquiler o te los prestan. Revisá la sección de "Comodidades" en la info de la cancha, o mandales un mensaje.', palabrasClave: 'alquiler alquilan pelotas raquetas paletas chalecos pecheras' },
  { pregunta: '¿Cómo sé si mi reserva fue confirmada?', respuesta: 'Te llega un mail apenas la creás. Si el complejo tiene pago online, se confirma sola al pagar; si no, el complejo te confirma directamente.', palabrasClave: 'confirmada confirmacion estado pendiente pagada' },
  { pregunta: '¿Puedo dejar una reseña?', respuesta: 'Sí, entrá a la ficha de la cancha que reservaste y vas a encontrar la sección de Reseñas para calificarla.', palabrasClave: 'resena reseña calificar opinion' },
  { pregunta: '¿Puedo tener un turno fijo todas las semanas?', respuesta: 'Sí. Escribile al complejo y ellos te cargan el turno fijo desde su panel: queda reservado el mismo día y horario todas las semanas.', palabrasClave: 'turno fijo semanal recurrente todos los viernes abono' }
];

function obtenerHojaMaestra(nombre) {
  const ss = ss_();
  let hoja = ss.getSheetByName(nombre);
  if (!hoja) {
    hoja = ss.insertSheet(nombre);
    if (nombre === 'Usuarios') hoja.appendRow(['Email', 'Password', 'Nombre', 'Rol', 'Complejos', 'Telefono']);
    if (nombre === 'Complejos') hoja.appendRow(['ComplejoID', 'Nombre', 'Descripcion', 'Localidad', 'Deporte', 'Logo', 'Direccion', 'Telefono', 'AdminEmail', 'SheetID', 'Estado', 'CreadoEn']);
    if (nombre === 'Bot') {
      hoja.appendRow(['Pregunta', 'Respuesta', 'PalabrasClave', 'Categoria']);
      PREGUNTAS_BOT_DEFAULT.forEach(p => hoja.appendRow([p.pregunta, p.respuesta, p.palabrasClave, '']));
    }
    if (nombre === 'Configuracion') hoja.appendRow(['Clave', 'Valor']);
  }
  return hoja;
}

function convertirSheetAJSON(sheet) {
  const data = sheet.getDataRange().getValues();
  if (data.length <= 1) return [];
  const cabeceras = data[0];
  return data.slice(1).map(fila => {
    let obj = {};
    cabeceras.forEach((c, i) => obj[c] = fila[i]);
    return obj;
  });
}

function generarId(prefijo) { return prefijo + '-' + Utilities.getUuid().slice(0, 6).toUpperCase(); }

function slugify(texto) {
  return String(texto).toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '')
    .replace(/[^a-z0-9]+/g, '-').replace(/(^-|-$)/g, '');
}

function abrirSSComplejo(complejoId) {
  const complejos = convertirSheetAJSON(obtenerHojaMaestra('Complejos'));
  const c = complejos.find(x => x.ComplejoID === complejoId);
  if (!c) throw new Error('Complejo no encontrado: ' + complejoId);
  return { ss: SpreadsheetApp.openById(c.SheetID), meta: c };
}

function obtenerHojaComplejo(complejoId, nombreHoja) {
  const { ss } = abrirSSComplejo(complejoId);
  let hoja = ss.getSheetByName(nombreHoja);
  if (!hoja) {
    hoja = ss.insertSheet(nombreHoja);
    if (nombreHoja === 'Administradores') hoja.appendRow(['Email', 'Nombre', 'Rango', 'FechaAlta']);
    if (nombreHoja === 'Promociones') hoja.appendRow(['Codigo', 'Tipo', 'Valor', 'FechaInicio', 'FechaFin', 'UsosMaximos', 'UsosActuales', 'Activo', 'Descripcion']);
    if (nombreHoja === 'Reservas') hoja.appendRow(['ID', 'CanchaID', 'CanchaNombre', 'Cliente', 'Email', 'Telefono', 'Fecha', 'HoraInicio', 'HoraFin', 'Precio', 'Estado']);
    if (nombreHoja === 'Resenas') hoja.appendRow(['ID', 'CanchaID', 'Cliente', 'Email', 'Rating', 'Comentario', 'Fecha']);
    if (nombreHoja === 'Canchas') hoja.appendRow(['CanchaID', 'Nombre', 'Deporte', 'Formato', 'Superficie', 'Techo', 'Localidad', 'Precio', 'Descripcion', 'ImagenURL', 'Amenities', 'Estado', 'Galeria', 'PreciosPorHorario', 'FechasBloqueadas']);
  }
  return hoja;
}

function obtenerIndiceColumna_(hoja, nombreColumna) {
  const ultimaCol = Math.max(hoja.getLastColumn(), 1);
  const headers = hoja.getRange(1, 1, 1, ultimaCol).getValues()[0];
  let idx = headers.indexOf(nombreColumna);
  if (idx === -1) {
    idx = ultimaCol;
    hoja.getRange(1, idx + 1).setValue(nombreColumna);
  }
  return idx + 1;
}

function limpiarCache() {
  const c = CacheService.getScriptCache();
  c.remove('canchas_publicas');
  c.remove('complejos_publicos');
}

function fechaATexto_(valor) {
  if (valor instanceof Date) return Utilities.formatDate(valor, Session.getScriptTimeZone(), 'yyyy-MM-dd');
  return String(valor);
}

function parsearJSONSeguro_(texto, porDefecto) {
  if (!texto) return porDefecto;
  if (typeof texto === 'object') return texto;
  try { return JSON.parse(texto); } catch (e) { return porDefecto; }
}

function esVerdadero_(v) { return v === true || v === 'TRUE' || v === 'true' || v === 'Si' || v === 'Sí'; }

// ==========================================
// USUARIOS Y ROLES
// ==========================================
function loginUsuario(email, pass) {
  if (!email || !pass) throw new Error('Faltan datos de acceso.');
  const usuarios = convertirSheetAJSON(obtenerHojaMaestra('Usuarios'));
  const user = usuarios.find(u => String(u.Email).toLowerCase() === email.toLowerCase() && String(u.Password) === pass);
  if (!user) throw new Error('Credenciales incorrectas o usuario no registrado.');

  const complejosArr = user.Complejos ? String(user.Complejos).split(',').map(s => s.trim()).filter(Boolean) : [];
  const rangosComplejos = {};
  complejosArr.forEach(complejoId => {
    rangosComplejos[complejoId] = obtenerRangoEnComplejo_(complejoId, user.Email) || 'ADMIN';
  });

  return {
    ok: true,
    usuario: {
      nombre: user.Nombre, email: user.Email, telefono: user.Telefono, rol: user.Rol,
      complejosAsignados: complejosArr, rangosComplejos: rangosComplejos
    }
  };
}

function registrarUsuario(data) {
  const hoja = obtenerHojaMaestra('Usuarios');
  const usuarios = convertirSheetAJSON(hoja);
  if (usuarios.some(u => String(u.Email).toLowerCase() === data.email.toLowerCase())) {
    throw new Error('El correo ya está registrado.');
  }
  const rolSeleccionado = data.rol === 'ADMIN_COMPLEJO' ? 'ADMIN_COMPLEJO' : 'CLIENTE';
  hoja.appendRow([data.email, data.password, data.nombre, rolSeleccionado, '', data.telefono || '']);
  return {
    ok: true,
    usuario: { nombre: data.nombre, email: data.email, telefono: data.telefono, rol: rolSeleccionado, complejosAsignados: [], rangosComplejos: {} }
  };
}

function actualizarPerfil(email, data) {
  const hoja = obtenerHojaMaestra('Usuarios');
  const values = hoja.getDataRange().getValues();
  for (let i = 1; i < values.length; i++) {
    if (String(values[i][0]).toLowerCase() === String(email).toLowerCase()) {
      if (data.passwordActual !== undefined && data.passwordNueva) {
        if (String(values[i][1]) !== String(data.passwordActual)) throw new Error('La contraseña actual no coincide.');
        hoja.getRange(i + 1, 2).setValue(data.passwordNueva);
      }
      if (data.nombre) hoja.getRange(i + 1, 3).setValue(data.nombre);
      if (data.telefono !== undefined) hoja.getRange(i + 1, 6).setValue(data.telefono);
      return { ok: true, nombre: data.nombre || values[i][2], telefono: data.telefono !== undefined ? data.telefono : values[i][5] };
    }
  }
  throw new Error('Usuario no encontrado.');
}

// ==========================================
// PERMISOS
// ==========================================
function obtenerRangoEnComplejo_(complejoId, email) {
  try {
    const hoja = obtenerHojaComplejo(complejoId, 'Administradores');
    const fila = convertirSheetAJSON(hoja).find(a => String(a.Email).toLowerCase() === String(email).toLowerCase());
    return fila ? fila.Rango : null;
  } catch (e) { return null; }
}

function exigirSuperAdmin_(complejoId, callerEmail) {
  const rango = obtenerRangoEnComplejo_(complejoId, callerEmail);
  if (rango !== 'SUPERADMIN') throw new Error('Solo un Super Admin de este complejo puede hacer esto.');
}

function exigirAdmin_(complejoId, callerEmail) {
  const rango = obtenerRangoEnComplejo_(complejoId, callerEmail);
  if (rango !== 'ADMIN' && rango !== 'SUPERADMIN') throw new Error('No tenés permisos de administrador en este complejo.');
  return rango;
}

function esEmail_(v) { return typeof v === 'string' && v.indexOf('@') !== -1; }

function verificarAdmin_(complejoId, callerEmail) {
  if (!callerEmail) return 'ADMIN';
  return exigirAdmin_(complejoId, callerEmail);
}

function verificarSuperAdmin_(complejoId, callerEmail) {
  if (!callerEmail) return;
  exigirSuperAdmin_(complejoId, callerEmail);
}

// ==========================================
// CANCHAS (CORREGIDO: UNA SOLA DEFINICIÓN)
// ==========================================
function obtenerCanchasDeComplejo(complejoId, callerEmail) {
  // Permite lectura siempre para llenar desplegables de reserva
  return convertirSheetAJSON(obtenerHojaComplejo(complejoId, 'Canchas')).map(mapearCancha_);
}

function mapearCancha_(c) {
  return {
    id: c.CanchaID, nombre: c.Nombre, deporte: c.Deporte, formato: c.Formato, superficie: c.Superficie,
    techo: c.Techo, localidad: c.Localidad, precio: Number(c.Precio) || 0, descripcion: c.Descripcion,
    imagen: c.ImagenURL, amenities: c.Amenities ? String(c.Amenities).split(',').filter(Boolean) : [],
    estado: c.Estado || 'Activa',
    galeria: parsearJSONSeguro_(c.Galeria, []),
    preciosPorHorario: parsearJSONSeguro_(c.PreciosPorHorario, []),
    fechasBloqueadas: parsearJSONSeguro_(c.FechasBloqueadas, [])
  };
}

function crearCancha(complejoId, a, b) {
  const callerEmail = esEmail_(a) ? a : null;
  const data = esEmail_(a) ? b : a;
  verificarAdmin_(complejoId, callerEmail);
  const hoja = obtenerHojaComplejo(complejoId, 'Canchas');
  const canchaId = generarId('C');
  hoja.appendRow([
    canchaId, data.nombre, data.deporte, data.formato, data.superficie, data.techo,
    data.localidad || '', Number(data.precio) || 0, data.descripcion || '', data.imagen || '',
    (data.amenities || []).join(','), 'Activa'
  ]);
  const fila = hoja.getLastRow();
  escribirCampos_(hoja, fila, {
    Galeria: JSON.stringify(data.galeria || []),
    PreciosPorHorario: JSON.stringify(data.preciosPorHorario || []),
    FechasBloqueadas: JSON.stringify(data.fechasBloqueadas || [])
  });
  limpiarCache();
  return { ok: true, canchaId: canchaId };
}

function editarCancha(complejoId, a, b, c) {
  const callerEmail = esEmail_(a) ? a : null;
  const canchaId = esEmail_(a) ? b : a;
  const data = esEmail_(a) ? c : b;
  verificarAdmin_(complejoId, callerEmail);
  const hoja = obtenerHojaComplejo(complejoId, 'Canchas');
  const values = hoja.getDataRange().getValues();
  for (let i = 1; i < values.length; i++) {
    if (values[i][0] === canchaId) {
      hoja.getRange(i + 1, 2, 1, 11).setValues([[
        data.nombre, data.deporte, data.formato, data.superficie, data.techo,
        data.localidad || '', Number(data.precio) || 0, data.descripcion || '', data.imagen || '',
        (data.amenities || []).join(','), data.estado || 'Activa'
      ]]);
      escribirCampos_(hoja, i + 1, {
        Galeria: JSON.stringify(data.galeria || []),
        PreciosPorHorario: JSON.stringify(data.preciosPorHorario || []),
        FechasBloqueadas: JSON.stringify(data.fechasBloqueadas || [])
      });
      limpiarCache();
      return true;
    }
  }
  throw new Error('Cancha no encontrada.');
}

function eliminarCancha(complejoId, a, b) {
  const callerEmail = esEmail_(a) ? a : null;
  const canchaId = esEmail_(a) ? b : a;
  verificarAdmin_(complejoId, callerEmail);
  const hoja = obtenerHojaComplejo(complejoId, 'Canchas');
  const colEstado = obtenerIndiceColumna_(hoja, 'Estado');
  const values = hoja.getDataRange().getValues();
  for (let i = 1; i < values.length; i++) {
    if (values[i][0] === canchaId) {
      hoja.getRange(i + 1, colEstado).setValue('Inactiva');
      limpiarCache();
      return true;
    }
  }
  throw new Error('Cancha no encontrada.');
}
