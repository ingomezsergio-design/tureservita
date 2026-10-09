package com.example.data.repository

import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

object TuCanchaRepository {

    const val DEFAULT_SHEET_ID = "1Na3CqEAMO2iK3ljPxtWkOfOmt9rFNLE9rvWZM30mX90"
    const val DEFAULT_DRIVE_FOLDER_ID = "1E1aNyCAi2RRF-JXfxjBaSv7CxqsAU6TI"

    // Configuration for Google Apps Script Web App URL
    var webAppEndpoint: String = ""

    // In-memory data store replicating Google Sheets tables
    private val _complejos = MutableStateFlow<List<Complejo>>(emptyList())
    val complejos: StateFlow<List<Complejo>> = _complejos.asStateFlow()

    private val _canchas = MutableStateFlow<List<Cancha>>(emptyList())
    val canchas: StateFlow<List<Cancha>> = _canchas.asStateFlow()

    private val _reservas = MutableStateFlow<List<Reserva>>(emptyList())
    val reservas: StateFlow<List<Reserva>> = _reservas.asStateFlow()

    private val _resenas = MutableStateFlow<List<Resena>>(emptyList())
    val resenas: StateFlow<List<Resena>> = _resenas.asStateFlow()

    private val _promociones = MutableStateFlow<Map<String, List<Promocion>>>(emptyMap())
    val promociones: StateFlow<Map<String, List<Promocion>>> = _promociones.asStateFlow()

    private val _administradores = MutableStateFlow<Map<String, List<Administrador>>>(emptyMap())
    val administradores: StateFlow<Map<String, List<Administrador>>> = _administradores.asStateFlow()

    private val _botFaqs = MutableStateFlow<List<BotFaq>>(emptyList())
    val botFaqs: StateFlow<List<BotFaq>> = _botFaqs.asStateFlow()

    private val _siteConfig = MutableStateFlow(SiteConfig())
    val siteConfig: StateFlow<SiteConfig> = _siteConfig.asStateFlow()

    private val _currentUser = MutableStateFlow<Usuario?>(null)
    val currentUser: StateFlow<Usuario?> = _currentUser.asStateFlow()

    init {
        seedInitialData()
    }

    private fun seedInitialData() {
        val diasDefault = listOf(
            HorarioDia(0, "Domingo", false, 14, 23),
            HorarioDia(1, "Lunes", false, 14, 23),
            HorarioDia(2, "Martes", false, 14, 23),
            HorarioDia(3, "Miércoles", false, 14, 23),
            HorarioDia(4, "Jueves", false, 14, 23),
            HorarioDia(5, "Viernes", false, 14, 23),
            HorarioDia(6, "Sábado", false, 14, 23)
        )

        val complejo1 = Complejo(
            id = "nexus-arena-4a21",
            nombre = "Nexus Arena",
            descripcion = "El predio deportivo más moderno de la región con canchas de césped sintético europeo de 60mm y pádel panorámico techado.",
            localidad = "Resistencia",
            deporte = "futbol",
            logo = "https://images.unsplash.com/photo-1574629810360-7efbbe195018?q=80&w=400",
            portada = "https://images.unsplash.com/photo-1529900241929-57e3f8476698?q=80&w=1200",
            direccion = "Av. Sarmiento 2450",
            telefono = "3624559812",
            whatsapp = "5493624559812",
            instagram = "@nexusarena",
            emailContacto = "contacto@nexusarena.com",
            aliasCbu = "nexusarena.mp",
            reglas = "Uso obligatorio de botines de fútbol 5 sin tapones de aluminio. Llegar 10 min antes del turno.",
            politicaCancelacion = "Cancelaciones con más de 24hs de anticipación reciben reembolso total de la seña.",
            horasMinCancelacion = 24,
            senaPorcentaje = 50,
            duracionMinima = 1,
            mapsUrl = "https://maps.google.com",
            avisoDestacado = "¡Torneo nocturno de verano abierto! Inscripciones en recepción.",
            horaApertura = 14,
            horaCierre = 23,
            horariosSemana = diasDefault,
            amenities = listOf("Estacionamiento", "Vestuarios", "Duchas", "Kiosco", "Bar", "Iluminación LED", "Wi-Fi"),
            mpAccessToken = "APP_USR-876123984712-0921"
        )

        val complejo2 = Complejo(
            id = "padel-center-9b33",
            nombre = "Pádel Center Pro",
            descripcion = "Canchas panorámicas de pádel de cristal templado y piso WPT con iluminación LED libre de sombras.",
            localidad = "Corrientes",
            deporte = "padel",
            logo = "https://images.unsplash.com/photo-1554068865-24cecd4e34b8?q=80&w=400",
            portada = "https://images.unsplash.com/photo-1554068865-24cecd4e34b8?q=80&w=1200",
            direccion = "Costanera Sur 850",
            telefono = "3794129988",
            whatsapp = "5493794129988",
            instagram = "@padelcenterpro",
            emailContacto = "info@padelcenterpro.com",
            aliasCbu = "padelcenter.mp",
            reglas = "Prohibido ingresar con calzado inadecuado. Alquiler de palas disponible en el bar.",
            politicaCancelacion = "Aviso con 12hs de antelación para reprogramar turno.",
            horasMinCancelacion = 12,
            senaPorcentaje = 50,
            duracionMinima = 1,
            mapsUrl = "https://maps.google.com",
            avisoDestacado = "Se estrenó la Cancha 3 World Padel Tour.",
            horaApertura = 15,
            horaCierre = 23,
            horariosSemana = diasDefault,
            amenities = listOf("Estacionamiento", "Vestuarios", "Bar", "Tribuna", "Wi-Fi"),
            mpAccessToken = ""
        )

        val complejo3 = Complejo(
            id = "club-raqueta-11c5",
            nombre = "Club de la Raqueta",
            descripcion = "Tradición y excelencia en canchas de tenis de polvo de ladrillo y canchas de squash profesionales.",
            localidad = "Resistencia",
            deporte = "tenis",
            logo = "https://images.unsplash.com/photo-1595435934249-5df7ed86e1c0?q=80&w=400",
            portada = "https://images.unsplash.com/photo-1595435934249-5df7ed86e1c0?q=80&w=1200",
            direccion = "Ruta 16 Km 14.5",
            telefono = "3624881122",
            whatsapp = "5493624881122",
            instagram = "@clubdelaraqueta",
            emailContacto = "tenis@clubdelaraqueta.com",
            aliasCbu = "raqueta.club",
            reglas = "Regar la cancha antes de jugar y pasar el cepillo al finalizar.",
            politicaCancelacion = "Cancelación con 24hs de aviso.",
            horasMinCancelacion = 24,
            senaPorcentaje = 100,
            duracionMinima = 1,
            horaApertura = 14,
            horaCierre = 22,
            horariosSemana = diasDefault,
            amenities = listOf("Estacionamiento", "Vestuarios", "Parrilla", "Kiosco", "Duchas"),
            mpAccessToken = ""
        )

        _complejos.value = listOf(complejo1, complejo2, complejo3)

        // Seed Canchas
        val c1 = Cancha(
            id = "C-4A01",
            complejoId = complejo1.id,
            complejoNombre = complejo1.nombre,
            nombre = "Cancha 1 (Fútbol 5 Techada)",
            deporte = "futbol",
            formato = "Fútbol 5",
            superficie = "Sintético Premium",
            techo = "Techada",
            localidad = "Resistencia",
            precio = 22000.0,
            precioMin = 18000.0,
            precioMax = 24000.0,
            descripcion = "Cancha con césped importado de última generación, cerramiento perimetral de red reforzada e iluminación LED de alta potencia.",
            imagen = "https://images.unsplash.com/photo-1574629810360-7efbbe195018?q=80&w=800",
            amenities = listOf("Vestuarios", "Kiosco", "Estacionamiento", "Iluminación LED"),
            estado = "Activa",
            preciosPorHorario = listOf(
                ReglaPrecioHorario(14, 18, 18000.0, "Franja Tarde"),
                ReglaPrecioHorario(18, 23, 24000.0, "Franja Nocturna con Luz")
            ),
            direccionComplejo = complejo1.direccion,
            telefonoComplejo = complejo1.telefono,
            whatsappComplejo = complejo1.whatsapp,
            instagramComplejo = complejo1.instagram,
            reglasComplejo = complejo1.reglas,
            avisoComplejo = complejo1.avisoDestacado,
            mapsComplejo = complejo1.mapsUrl
        )

        val c2 = Cancha(
            id = "C-4A02",
            complejoId = complejo1.id,
            complejoNombre = complejo1.nombre,
            nombre = "Cancha 2 (Fútbol 7 Abierta)",
            deporte = "futbol",
            formato = "Fútbol 7",
            superficie = "Sintético Pro",
            techo = "Descubierta",
            localidad = "Resistencia",
            precio = 28000.0,
            precioMin = 24000.0,
            precioMax = 30000.0,
            descripcion = "Dimensiones reglamentarias para 7 vs 7 con bancos de suplentes cubiertos.",
            imagen = "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?q=80&w=800",
            amenities = listOf("Vestuarios", "Tribuna", "Iluminación LED"),
            estado = "Activa",
            preciosPorHorario = listOf(
                ReglaPrecioHorario(14, 18, 24000.0, "Tarde"),
                ReglaPrecioHorario(18, 23, 30000.0, "Noche")
            ),
            direccionComplejo = complejo1.direccion,
            telefonoComplejo = complejo1.telefono,
            whatsappComplejo = complejo1.whatsapp
        )

        val c3 = Cancha(
            id = "C-9B01",
            complejoId = complejo2.id,
            complejoNombre = complejo2.nombre,
            nombre = "Pista Central Panorámica WPT",
            deporte = "padel",
            formato = "Pádel Dobles",
            superficie = "Césped WPT Azul",
            techo = "Techada",
            localidad = "Corrientes",
            precio = 18000.0,
            precioMin = 15000.0,
            precioMax = 20000.0,
            descripcion = "Vidrios panorámicos sin postes intermedios en el fondo. Césped monofilamento azul oficial.",
            imagen = "https://images.unsplash.com/photo-1554068865-24cecd4e34b8?q=80&w=800",
            amenities = listOf("Bar", "Vestuarios", "Wi-Fi"),
            estado = "Activa",
            preciosPorHorario = listOf(
                ReglaPrecioHorario(15, 18, 15000.0, "Turno Tarde"),
                ReglaPrecioHorario(18, 23, 20000.0, "Turno Noche")
            ),
            direccionComplejo = complejo2.direccion,
            whatsappComplejo = complejo2.whatsapp
        )

        val c4 = Cancha(
            id = "C-11C1",
            complejoId = complejo3.id,
            complejoNombre = complejo3.nombre,
            nombre = "Cancha 1 - Polvo de Ladrillo",
            deporte = "tenis",
            formato = "Tenis Singles / Dobles",
            superficie = "Polvo de Ladrillo",
            techo = "Descubierta",
            localidad = "Resistencia",
            precio = 16000.0,
            precioMin = 14000.0,
            precioMax = 18000.0,
            descripcion = "Cancha mantenida diariamente con flejes de PVC y drenaje rápido.",
            imagen = "https://images.unsplash.com/photo-1595435934249-5df7ed86e1c0?q=80&w=800",
            amenities = listOf("Vestuarios", "Parrilla", "Estacionamiento"),
            estado = "Activa",
            direccionComplejo = complejo3.direccion,
            whatsappComplejo = complejo3.whatsapp
        )

        _canchas.value = listOf(c1, c2, c3, c4)

        // Seed Bookings
        val hoy = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val r1 = Reserva(
            id = "R-7F91",
            complejoId = complejo1.id,
            complejoNombre = complejo1.nombre,
            canchaId = c1.id,
            canchaNombre = c1.nombre,
            cliente = "Gonzalo Silva",
            email = "gonzalo@gmail.com",
            telefono = "3624890123",
            fecha = hoy,
            horaInicio = "19",
            horaFin = "20",
            precio = 24000.0,
            estado = "PAGADA",
            notas = "Pelota oficial solicitada",
            creadaEn = hoy,
            sena = 12000.0,
            senaPorcentaje = 50,
            pagoConfigurado = true
        )
        val r2 = Reserva(
            id = "R-2A18",
            complejoId = complejo1.id,
            complejoNombre = complejo1.nombre,
            canchaId = c1.id,
            canchaNombre = c1.nombre,
            cliente = "Martín Benítez",
            email = "martin@empresa.com",
            telefono = "3624119933",
            fecha = hoy,
            horaInicio = "21",
            horaFin = "22",
            precio = 24000.0,
            estado = "PENDIENTE",
            notas = "",
            creadaEn = hoy,
            sena = 12000.0,
            senaPorcentaje = 50,
            pagoConfigurado = true
        )
        _reservas.value = listOf(r1, r2)

        // Seed Reviews
        val rv1 = Resena(
            id = "RV-01",
            complejoId = complejo1.id,
            complejoNombre = complejo1.nombre,
            canchaId = c1.id,
            canchaNombre = c1.nombre,
            cliente = "Rodrigo Gómez",
            email = "rodrigo@gmail.com",
            rating = 5,
            comentario = "La mejor cancha de Resistencia. El césped no quema y las luces no encandilan nada.",
            respuestaComplejo = "¡Gracias Rodrigo! Los esperamos siempre.",
            fecha = "02/10/2026"
        )
        val rv2 = Resena(
            id = "RV-02",
            complejoId = complejo2.id,
            complejoNombre = complejo2.nombre,
            canchaId = c3.id,
            canchaNombre = c3.nombre,
            cliente = "Camila Duarte",
            email = "camila@gmail.com",
            rating = 5,
            comentario = "Pista increíble para pádel, los vidrios impecables y muy buena atención en el bar.",
            respuestaComplejo = "Muchas gracias Camila por la reseña.",
            fecha = "05/10/2026"
        )
        _resenas.value = listOf(rv1, rv2)

        // Seed Promos
        val promo1 = Promocion(
            codigo = "VERANO10",
            tipo = "PORCENTAJE",
            valor = 10.0,
            fechaInicio = "2026-01-01",
            fechaFin = "2026-12-31",
            usosMaximos = 100,
            usosActuales = 14,
            activo = true,
            descripcion = "10% de descuento de lanzamiento"
        )
        val promo2 = Promocion(
            codigo = "AMIGOS2000",
            tipo = "MONTO",
            valor = 2000.0,
            fechaInicio = "2026-01-01",
            fechaFin = "2026-12-31",
            usosMaximos = 50,
            usosActuales = 8,
            activo = true,
            descripcion = "$2000 de regalo para tu partido"
        )
        _promociones.value = mapOf(
            complejo1.id to listOf(promo1, promo2),
            complejo2.id to listOf(promo1)
        )

        // Seed Admins
        val admin1 = Administrador("ingomezsergio@gmail.com", "Sergio Gómez", "SUPERADMIN", "01/01/2026")
        val admin2 = Administrador("admin@nexusarena.com", "Mariano Encargado", "ADMIN", "15/02/2026")
        _administradores.value = mapOf(
            complejo1.id to listOf(admin1, admin2),
            complejo2.id to listOf(admin1)
        )

        // Seed Bot FAQ
        _botFaqs.value = listOf(
            BotFaq("¿Cómo reservo una cancha?", "Elegí el deporte y la cancha que te guste, tocá 'Seleccionar Turno', elegí el día y el horario, y confirmá la reserva. Necesitás tener tu cuenta iniciada.", "reservar turno cancha como quiero"),
            BotFaq("¿Cómo cancelo una reserva?", "Andá a la sección 'Mis Reservas' desde el menú, buscá tu reserva y tocá 'Cancelar'. Ojo: solo se puede cancelar mientras esté en estado Pendiente y dentro del plazo fijado por el complejo.", "cancelar cancelacion baja reserva no voy"),
            BotFaq("¿Qué formas de pago aceptan?", "Depende del complejo: podés abonar con Mercado Pago online o abonar la seña/total por transferencia Alias/CBU o en efectivo en el complejo.", "pago pagar mercadopago efectivo tarjeta transferencia alias cbu sena seña"),
            BotFaq("¿Se suspenden los turnos por lluvia?", "Las canchas techadas funcionan con normalidad bajo cualquier clima. En canchas descubiertas podés consultar al complejo directamente por WhatsApp.", "lluvia clima llover mal tiempo techada"),
            BotFaq("¿Alquilan pelotas, paletas o chalecos?", "Sí, la mayoría de los complejos cuentan con alquiler de pelotas y palas oficiales en su kiosco o bar.", "alquiler alquilan pelotas raquetas paletas chalecos pecheras"),
            BotFaq("¿Puedo tener un turno fijo todas las semanas?", "¡Sí! El administrador del complejo puede cargarte un turno fijo semanal para que quede reservado automáticamente todas las semanas.", "turno fijo semanal recurrente abono todos los viernes")
        )

        // Default login for quick start
        _currentUser.value = Usuario(
            nombre = "Sergio Gómez",
            email = "ingomezsergio@gmail.com",
            telefono = "3624123456",
            rol = "ADMIN_COMPLEJO",
            complejosAsignados = listOf(complejo1.id, complejo2.id),
            rangosComplejos = mapOf(complejo1.id to "SUPERADMIN", complejo2.id to "SUPERADMIN")
        )
    }

    // --- Authentication ---
    fun login(email: String, pass: String): Result<Usuario> {
        val trimmed = email.trim().lowercase()
        val user = if (trimmed == "ingomezsergio@gmail.com" || trimmed.contains("admin")) {
            Usuario(
                nombre = "Sergio Gómez",
                email = trimmed,
                telefono = "3624123456",
                rol = "ADMIN_COMPLEJO",
                complejosAsignados = _complejos.value.map { it.id },
                rangosComplejos = _complejos.value.associate { it.id to "SUPERADMIN" }
            )
        } else {
            Usuario(
                nombre = email.substringBefore("@").replaceFirstChar { it.uppercase() },
                email = trimmed,
                telefono = "",
                rol = "CLIENTE",
                complejosAsignados = emptyList(),
                rangosComplejos = emptyMap()
            )
        }
        _currentUser.value = user
        return Result.success(user)
    }

    fun register(nombre: String, email: String, pass: String, telefono: String, rol: String): Result<Usuario> {
        val user = Usuario(
            nombre = nombre.trim(),
            email = email.trim().lowercase(),
            telefono = telefono.trim(),
            rol = if (rol == "ADMIN_COMPLEJO") "ADMIN_COMPLEJO" else "CLIENTE",
            complejosAsignados = if (rol == "ADMIN_COMPLEJO") listOf(_complejos.value.firstOrNull()?.id ?: "") else emptyList(),
            rangosComplejos = if (rol == "ADMIN_COMPLEJO") mapOf((_complejos.value.firstOrNull()?.id ?: "") to "SUPERADMIN") else emptyMap()
        )
        _currentUser.value = user
        return Result.success(user)
    }

    fun logout() {
        _currentUser.value = null
    }

    // --- Availability & Pricing ---
    fun getDisponibilidad(complejoId: String, canchaId: String, fecha: String): DisponibilidadInfo {
        val comp = _complejos.value.find { it.id == complejoId }
        val cancha = _canchas.value.find { it.id == canchaId }

        val abre = comp?.horaApertura ?: 14
        val cierra = comp?.horaCierre ?: 23
        val precioBase = cancha?.precio ?: 18000.0

        val bookings = _reservas.value.filter {
            it.canchaId == canchaId && it.fecha == fecha && it.estado != "CANCELADA"
        }

        val slots = mutableListOf<SlotHora>()
        for (h in abre..cierra) {
            val isOccupied = bookings.any {
                val start = it.horaInicio.toIntOrNull() ?: -1
                val end = it.horaFin.toIntOrNull() ?: -1
                h in start until end
            }

            // Check pricing rules
            var slotPrice = precioBase
            cancha?.preciosPorHorario?.find { h in it.horaInicio until it.horaFin }?.let {
                slotPrice = it.precio
            }

            slots.add(
                SlotHora(
                    hora = "$h:00",
                    minutos = h * 60,
                    ocupado = isOccupied,
                    motivo = if (isOccupied) "Reservado" else null,
                    precio = slotPrice
                )
            )
        }

        return DisponibilidadInfo(
            slots = slots,
            precioHora = precioBase,
            cerrado = false,
            abre = abre,
            cierra = cierra,
            duracionMinima = comp?.duracionMinima ?: 1,
            senaPorcentaje = comp?.senaPorcentaje ?: 50
        )
    }

    // --- Booking Creation ---
    fun crearReserva(
        complejoId: String,
        canchaId: String,
        canchaNombre: String,
        cliente: String,
        email: String,
        telefono: String,
        fecha: String,
        horaInicio: String,
        horaFin: String,
        codigoPromo: String = ""
    ): Result<Reserva> {
        val comp = _complejos.value.find { it.id == complejoId }
        val cancha = _canchas.value.find { it.id == canchaId }
        val ini = horaInicio.replace(":00", "").toIntOrNull() ?: 14
        val fin = horaFin.replace(":00", "").toIntOrNull() ?: 15

        if (fin <= ini) return Result.failure(Exception("La hora de fin debe ser mayor a la hora de inicio"))

        // Calculate price across slots
        var total = 0.0
        for (h in ini until fin) {
            var p = cancha?.precio ?: 18000.0
            cancha?.preciosPorHorario?.find { h in it.horaInicio until it.horaFin }?.let {
                p = it.precio
            }
            total += p
        }

        var descuento = 0.0
        var promoApplied = ""
        if (codigoPromo.isNotBlank()) {
            val promo = _promociones.value[complejoId]?.find {
                it.codigo.equals(codigoPromo.trim(), ignoreCase = true) && it.activo
            }
            if (promo != null) {
                descuento = if (promo.tipo == "MONTO") {
                    minOf(promo.valor, total)
                } else {
                    total * (promo.valor / 100.0)
                }
                promoApplied = promo.codigo
            }
        }

        val finalPrice = maxOf(0.0, total - descuento)
        val senaPct = comp?.senaPorcentaje ?: 100
        val senaAmount = if (senaPct < 100) (finalPrice * senaPct / 100.0) else finalPrice

        val newId = "R-" + UUID.randomUUID().toString().take(4).uppercase()
        val reserva = Reserva(
            id = newId,
            complejoId = complejoId,
            complejoNombre = comp?.nombre ?: "",
            canchaId = canchaId,
            canchaNombre = canchaNombre,
            cliente = cliente,
            email = email,
            telefono = telefono,
            fecha = fecha,
            horaInicio = ini.toString(),
            horaFin = fin.toString(),
            precio = finalPrice,
            estado = "PENDIENTE",
            codigoPromo = promoApplied,
            descuento = descuento,
            creadaEn = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date()),
            sena = senaAmount,
            senaPorcentaje = senaPct,
            pagoConfigurado = comp?.mpAccessToken?.isNotBlank() == true,
            urlPago = if (comp?.mpAccessToken?.isNotBlank() == true) "https://www.mercadopago.com.ar" else null
        )

        _reservas.value = listOf(reserva) + _reservas.value
        return Result.success(reserva)
    }

    fun crearReservaManual(
        complejoId: String,
        canchaId: String,
        canchaNombre: String,
        cliente: String,
        email: String,
        telefono: String,
        fecha: String,
        horaInicio: String,
        horaFin: String,
        precio: Double,
        estado: String = "MANUAL",
        notas: String = ""
    ): Result<Reserva> {
        val comp = _complejos.value.find { it.id == complejoId }
        val newId = "R-" + UUID.randomUUID().toString().take(4).uppercase()
        val reserva = Reserva(
            id = newId,
            complejoId = complejoId,
            complejoNombre = comp?.nombre ?: "",
            canchaId = canchaId,
            canchaNombre = canchaNombre,
            cliente = cliente,
            email = email,
            telefono = telefono,
            fecha = fecha,
            horaInicio = horaInicio.replace(":00", ""),
            horaFin = horaFin.replace(":00", ""),
            precio = precio,
            estado = estado,
            notas = notas,
            creadaEn = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        )
        _reservas.value = listOf(reserva) + _reservas.value
        return Result.success(reserva)
    }

    fun crearReservaRecurrente(
        complejoId: String,
        canchaId: String,
        canchaNombre: String,
        cliente: String,
        email: String,
        telefono: String,
        fechaBase: String,
        horaInicio: String,
        horaFin: String,
        precio: Double,
        semanas: Int
    ): Pair<Int, Int> {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()
        cal.time = sdf.parse(fechaBase) ?: Date()

        var creadas = 0
        var conflictos = 0

        for (i in 0 until semanas) {
            val fechaStr = sdf.format(cal.time)
            // check conflict
            val ini = horaInicio.replace(":00", "").toIntOrNull() ?: 0
            val fin = horaFin.replace(":00", "").toIntOrNull() ?: 0
            val conflict = _reservas.value.any {
                it.canchaId == canchaId && it.fecha == fechaStr && it.estado != "CANCELADA" &&
                        (it.horaInicio.toInt() < fin && it.horaFin.toInt() > ini)
            }
            if (!conflict) {
                crearReservaManual(
                    complejoId, canchaId, canchaNombre, cliente, email, telefono,
                    fechaStr, horaInicio, horaFin, precio, "MANUAL", "Turno fijo semanal ($i/$semanas)"
                )
                creadas++
            } else {
                conflictos++
            }
            cal.add(Calendar.DAY_OF_YEAR, 7)
        }
        return Pair(creadas, conflictos)
    }

    fun cambiarEstadoReserva(id: String, nuevoEstado: String) {
        _reservas.value = _reservas.value.map {
            if (it.id == id) it.copy(estado = nuevoEstado) else it
        }
    }

    fun cancelarReserva(complejoId: String, idReserva: String, userEmail: String): Result<Boolean> {
        val r = _reservas.value.find { it.id == idReserva }
            ?: return Result.failure(Exception("Reserva no encontrada"))

        _reservas.value = _reservas.value.map {
            if (it.id == idReserva) it.copy(estado = "CANCELADA") else it
        }
        return Result.success(true)
    }

    // --- Complex Management ---
    fun saveComplejo(complejo: Complejo) {
        val list = _complejos.value.toMutableList()
        val idx = list.indexOfFirst { it.id == complejo.id }
        if (idx >= 0) {
            list[idx] = complejo
        } else {
            list.add(complejo)
        }
        _complejos.value = list
    }

    // --- Field Management ---
    fun saveCancha(cancha: Cancha) {
        val list = _canchas.value.toMutableList()
        val idx = list.indexOfFirst { it.id == cancha.id }
        if (idx >= 0) {
            list[idx] = cancha
        } else {
            list.add(cancha)
        }
        _canchas.value = list
    }

    fun deleteCancha(canchaId: String) {
        _canchas.value = _canchas.value.map {
            if (it.id == canchaId) it.copy(estado = "Inactiva") else it
        }
    }

    // --- Day Agenda ---
    fun getAgendaDia(complejoId: String, fecha: String): AgendaDia {
        val comp = _complejos.value.find { it.id == complejoId }
        val canchasList = _canchas.value.filter { it.complejoId == complejoId && it.estado == "Activa" }
        val resList = _reservas.value.filter { it.complejoId == complejoId && it.fecha == fecha && it.estado != "CANCELADA" }

        val abre = comp?.horaApertura ?: 14
        val cierra = comp?.horaCierre ?: 23
        val horas = (abre..cierra).toList()

        val canchasAgenda = canchasList.map { c ->
            val celdas = horas.map { h ->
                val r = resList.find { it.canchaId == c.id && h in it.horaInicio.toInt() until it.horaFin.toInt() }
                AgendaCelda(
                    hora = h,
                    estado = r?.estado ?: "LIBRE",
                    reservaId = r?.id,
                    cliente = r?.cliente ?: "",
                    precio = c.precio
                )
            }
            AgendaCancha(id = c.id, nombre = c.nombre, celdas = celdas)
        }

        return AgendaDia(fecha = fecha, cerrado = false, horas = horas, canchas = canchasAgenda)
    }

    // --- Statistics ---
    fun getEstadisticas(complejoId: String): EstadisticasComplejo {
        val list = _reservas.value.filter { it.complejoId == complejoId }
        val valid = list.filter { it.estado != "CANCELADA" }
        val totalRevenue = valid.sumOf { it.precio }
        val totalHours = valid.sumOf { maxOf(1, it.horaFin.toInt() - it.horaInicio.toInt()) }
        val canceladas = list.count { it.estado == "CANCELADA" }

        val porCanchaMap = valid.groupBy { it.canchaNombre }
            .map { (name, res) -> ParEstadistica(name, res.sumOf { it.precio }, res.size) }
            .sortedByDescending { it.ingresos }

        val clientesMap = valid.groupBy { it.cliente }
            .map { (name, res) -> ParEstadistica(name, res.sumOf { it.precio }, res.size) }
            .sortedByDescending { it.ingresos }

        val picoHours = valid.flatMap { r -> (r.horaInicio.toInt() until r.horaFin.toInt()).toList() }
            .groupingBy { it }.eachCount().toList().sortedByDescending { it.second }.take(6)

        val monthRevenue = valid.groupBy { it.fecha.take(7) }
            .map { (m, r) -> Pair(m, r.sumOf { it.precio }) }
            .sortedBy { it.first }

        return EstadisticasComplejo(
            ingresosPorMes = if (monthRevenue.isEmpty()) listOf(Pair("2026-10", totalRevenue)) else monthRevenue,
            porCancha = porCanchaMap,
            horariosPico = picoHours,
            mejoresClientes = clientesMap.take(8),
            resumen = EstadisticasResumen(
                ingresoTotal = totalRevenue,
                horasVendidas = totalHours,
                reservas = list.size,
                canceladas = canceladas,
                tasaCancelacion = if (list.isNotEmpty()) (canceladas * 100 / list.size) else 0,
                ticketPromedio = if (valid.isNotEmpty()) (totalRevenue / valid.size) else 0.0
            )
        )
    }

    // --- Promos ---
    fun getPromociones(complejoId: String): List<Promocion> {
        return _promociones.value[complejoId] ?: emptyList()
    }

    fun savePromocion(complejoId: String, promo: Promocion) {
        val current = (_promociones.value[complejoId] ?: emptyList()).toMutableList()
        val idx = current.indexOfFirst { it.codigo.equals(promo.codigo, ignoreCase = true) }
        if (idx >= 0) current[idx] = promo else current.add(promo)
        _promociones.value = _promociones.value + (complejoId to current)
    }

    fun deletePromocion(complejoId: String, codigo: String) {
        val current = (_promociones.value[complejoId] ?: emptyList()).filterNot { it.codigo.equals(codigo, ignoreCase = true) }
        _promociones.value = _promociones.value + (complejoId to current)
    }

    // --- Admins ---
    fun getAdministradores(complejoId: String): List<Administrador> {
        return _administradores.value[complejoId] ?: emptyList()
    }

    fun saveAdministrador(complejoId: String, admin: Administrador) {
        val current = (_administradores.value[complejoId] ?: emptyList()).toMutableList()
        val idx = current.indexOfFirst { it.email.equals(admin.email, ignoreCase = true) }
        if (idx >= 0) current[idx] = admin else current.add(admin)
        _administradores.value = _administradores.value + (complejoId to current)
    }

    fun deleteAdministrador(complejoId: String, email: String) {
        val current = (_administradores.value[complejoId] ?: emptyList()).filterNot { it.email.equals(email, ignoreCase = true) }
        _administradores.value = _administradores.value + (complejoId to current)
    }

    // --- Bot Assistant ---
    fun consultarBot(mensaje: String): Pair<String, List<String>> {
        val tokens = mensaje.lowercase().split(" ", ",", ".").filter { it.length > 2 }
        if (tokens.isEmpty()) {
            return Pair("Contame un poco más qué necesitás saber ⚽", _botFaqs.value.map { it.pregunta }.take(3))
        }

        var bestMatch: BotFaq? = null
        var maxScore = 0
        for (faq in _botFaqs.value) {
            val text = (faq.pregunta + " " + faq.palabrasClave).lowercase()
            var score = 0
            for (t in tokens) {
                if (text.contains(t)) score++
            }
            if (score > maxScore) {
                maxScore = score
                bestMatch = faq
            }
        }

        return if (bestMatch != null && maxScore > 0) {
            Pair(bestMatch.respuesta, _botFaqs.value.filter { it != bestMatch }.map { it.pregunta }.take(3))
        } else {
            Pair(
                "No encontré una respuesta específica para eso. Podés consultar directamente al complejo por WhatsApp o elegir alguna de estas preguntas frecuentes:",
                _botFaqs.value.map { it.pregunta }.take(3)
            )
        }
    }

    fun saveBotFaq(faq: BotFaq) {
        val list = _botFaqs.value.toMutableList()
        val idx = list.indexOfFirst { it.pregunta.equals(faq.pregunta, ignoreCase = true) }
        if (idx >= 0) list[idx] = faq else list.add(faq)
        _botFaqs.value = list
    }

    fun deleteBotFaq(pregunta: String) {
        _botFaqs.value = _botFaqs.value.filterNot { it.pregunta.equals(pregunta, ignoreCase = true) }
    }

    // --- Reviews ---
    fun crearResena(complejoId: String, canchaId: String, cliente: String, email: String, rating: Int, comentario: String) {
        val comp = _complejos.value.find { it.id == complejoId }
        val cancha = _canchas.value.find { it.id == canchaId }
        val r = Resena(
            id = "RV-" + UUID.randomUUID().toString().take(4).uppercase(),
            complejoId = complejoId,
            complejoNombre = comp?.nombre ?: "",
            canchaId = canchaId,
            canchaNombre = cancha?.nombre ?: "",
            cliente = cliente,
            email = email,
            rating = rating,
            comentario = comentario,
            fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        )
        _resenas.value = listOf(r) + _resenas.value
    }

    // --- Export CSV ---
    fun exportarReservasCSV(complejoId: String): String {
        val list = _reservas.value.filter { it.complejoId == complejoId }
        val sb = StringBuilder()
        sb.append("ID,Cancha,Cliente,Email,Telefono,Fecha,Desde,Hasta,Precio,Estado,CodigoPromo,Descuento,Notas\n")
        for (r in list) {
            sb.append("\"${r.id}\",\"${r.canchaNombre}\",\"${r.cliente}\",\"${r.email}\",\"${r.telefono}\",\"${r.fecha}\",\"${r.horaInicio}:00\",\"${r.horaFin}:00\",${r.precio},\"${r.estado}\",\"${r.codigoPromo}\",${r.descuento},\"${r.notas}\"\n")
        }
        return sb.toString()
    }

    // --- Helpers ---
    fun buildWhatsAppUrl(phone: String, text: String): String {
        val cleanPhone = phone.replace(Regex("[^0-9]"), "")
        val encoded = URLEncoder.encode(text, "UTF-8")
        return "https://wa.me/$cleanPhone?text=$encoded"
    }

    fun buildGoogleCalendarUrl(title: String, fecha: String, horaIni: String, horaFin: String): String {
        val cleanFecha = fecha.replace("-", "")
        val startH = horaIni.padStart(2, '0') + "0000"
        val endH = horaFin.padStart(2, '0') + "0000"
        val encodedTitle = URLEncoder.encode(title, "UTF-8")
        return "https://calendar.google.com/calendar/render?action=TEMPLATE&text=$encodedTitle&dates=${cleanFecha}T${startH}/${cleanFecha}T${endH}"
    }
}
