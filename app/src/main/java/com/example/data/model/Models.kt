package com.example.data.model

import java.io.Serializable

data class Usuario(
    val nombre: String,
    val email: String,
    val telefono: String = "",
    val rol: String = "CLIENTE", // CLIENTE, ADMIN_COMPLEJO, SUPERADMIN
    val complejosAsignados: List<String> = emptyList(),
    val rangosComplejos: Map<String, String> = emptyMap()
) : Serializable

data class HorarioDia(
    val dia: Int, // 0: Domingo, 1: Lunes...
    val nombre: String,
    val cerrado: Boolean = false,
    val abre: Int = 14,
    val cierra: Int = 23
) : Serializable

data class ReglaPrecioHorario(
    val horaInicio: Int,
    val horaFin: Int,
    val precio: Double,
    val etiqueta: String = ""
) : Serializable

data class FechaBloqueada(
    val desde: String,
    val hasta: String = "",
    val horaInicio: Int? = null,
    val horaFin: Int? = null,
    val motivo: String = "Mantenimiento"
) : Serializable

data class ArchivoGaleria(
    val id: String = "",
    val url: String,
    val tipo: String = "imagen", // imagen, video
    val nombre: String = ""
) : Serializable

data class Complejo(
    val id: String,
    val nombre: String,
    val descripcion: String = "",
    val localidad: String = "",
    val deporte: String = "futbol",
    val logo: String = "",
    val portada: String = "",
    val direccion: String = "",
    val telefono: String = "",
    val whatsapp: String = "",
    val instagram: String = "",
    val facebook: String = "",
    val emailContacto: String = "",
    val aliasCbu: String = "",
    val reglas: String = "",
    val politicaCancelacion: String = "",
    val horasMinCancelacion: Int = 0,
    val senaPorcentaje: Int = 100,
    val duracionMinima: Int = 1,
    val mapsUrl: String = "",
    val avisoDestacado: String = "",
    val notificarEmails: String = "",
    val estado: String = "Activo", // Activo, Pausado
    val horaApertura: Int = 14,
    val horaCierre: Int = 23,
    val horariosSemana: List<HorarioDia> = emptyList(),
    val amenities: List<String> = emptyList(),
    val galeria: List<ArchivoGaleria> = emptyList(),
    val mpAccessToken: String = "",
    val rangoUsuario: String? = null
) : Serializable

data class Cancha(
    val id: String,
    val complejoId: String,
    val complejoNombre: String = "",
    val nombre: String,
    val deporte: String = "futbol",
    val formato: String = "Fútbol 5",
    val superficie: String = "Sintético",
    val techo: String = "Descubierta",
    val localidad: String = "",
    val precio: Double = 18000.0,
    val precioMin: Double = 18000.0,
    val precioMax: Double = 18000.0,
    val descripcion: String = "",
    val imagen: String = "",
    val amenities: List<String> = emptyList(),
    val estado: String = "Activa", // Activa, Inactiva
    val galeria: List<ArchivoGaleria> = emptyList(),
    val preciosPorHorario: List<ReglaPrecioHorario> = emptyList(),
    val fechasBloqueadas: List<FechaBloqueada> = emptyList(),
    // Enriched fields from complex
    val direccionComplejo: String = "",
    val telefonoComplejo: String = "",
    val whatsappComplejo: String = "",
    val instagramComplejo: String = "",
    val reglasComplejo: String = "",
    val avisoComplejo: String = "",
    val mapsComplejo: String = ""
) : Serializable

data class SlotHora(
    val hora: String,
    val minutos: Int,
    val ocupado: Boolean,
    val motivo: String? = null,
    val precio: Double
) : Serializable

data class DisponibilidadInfo(
    val slots: List<SlotHora>,
    val precioHora: Double,
    val cerrado: Boolean = false,
    val abre: Int = 14,
    val cierra: Int = 23,
    val motivoBloqueo: String? = null,
    val duracionMinima: Int = 1,
    val senaPorcentaje: Int = 100
) : Serializable

data class Reserva(
    val id: String,
    val complejoId: String,
    val complejoNombre: String = "",
    val canchaId: String,
    val canchaNombre: String,
    val cliente: String,
    val email: String,
    val telefono: String = "",
    val fecha: String,
    val horaInicio: String,
    val horaFin: String,
    val precio: Double,
    val estado: String = "PENDIENTE", // PENDIENTE, PAGADA, MANUAL, CANCELADA
    val codigoPromo: String = "",
    val descuento: Double = 0.0,
    val notas: String = "",
    val creadaEn: String = "",
    val sena: Double = 0.0,
    val senaPorcentaje: Int = 100,
    val urlPago: String? = null,
    val pagoConfigurado: Boolean = false
) : Serializable

data class Resena(
    val id: String = "",
    val complejoId: String = "",
    val complejoNombre: String = "",
    val canchaId: String = "",
    val canchaNombre: String = "",
    val cliente: String,
    val email: String = "",
    val rating: Int = 5,
    val comentario: String = "",
    val respuestaComplejo: String = "",
    val fecha: String = ""
) : Serializable

data class Promocion(
    val codigo: String,
    val tipo: String = "PORCENTAJE", // PORCENTAJE, MONTO
    val valor: Double = 10.0,
    val fechaInicio: String = "",
    val fechaFin: String = "",
    val usosMaximos: Int? = null,
    val usosActuales: Int = 0,
    val activo: Boolean = true,
    val descripcion: String = ""
) : Serializable

data class Administrador(
    val email: String,
    val nombre: String,
    val rango: String = "ADMIN", // ADMIN, SUPERADMIN
    val fechaAlta: String = ""
) : Serializable

data class BotFaq(
    val pregunta: String,
    val respuesta: String,
    val palabrasClave: String = "",
    val categoria: String = ""
) : Serializable

data class SiteConfig(
    val nombreMarca: String = "TuCancha",
    val sufijoMarca: String = "Pro",
    val heroSubtitulo: String = "Seleccioná un deporte para ver sus canchas y complejos certificados.",
    val acercaDeTitulo: String = "Acerca de Nosotros",
    val acercaDeTexto: String = "Conectamos jugadores con los mejores complejos deportivos de la zona: reservá tu cancha, pagá online y llevá el control de tus partidos.",
    val contactoEmail: String = "contacto@tucanchapro.com",
    val contactoWhatsapp: String = "+5493624123456",
    val instagram: String = "@tucanchapro"
) : Serializable

data class DashboardMetricas(
    val ingresos: Double = 0.0,
    val pagadas: Int = 0,
    val pendientes: Int = 0,
    val total: Int = 0,
    val hoy: Int = 0,
    val ingresosMes: Double = 0.0
) : Serializable

data class EstadisticasResumen(
    val ingresoTotal: Double,
    val horasVendidas: Int,
    val reservas: Int,
    val canceladas: Int,
    val tasaCancelacion: Int,
    val ticketPromedio: Double
) : Serializable

data class ParEstadistica(
    val clave: String,
    val ingresos: Double = 0.0,
    val reservas: Int = 0
) : Serializable

data class EstadisticasComplejo(
    val ingresosPorMes: List<Pair<String, Double>>,
    val porCancha: List<ParEstadistica>,
    val horariosPico: List<Pair<Int, Int>>,
    val mejoresClientes: List<ParEstadistica>,
    val resumen: EstadisticasResumen
) : Serializable

data class AgendaCelda(
    val hora: Int,
    val estado: String, // LIBRE, PAGADA, PENDIENTE, MANUAL, BLOQUEADO
    val motivo: String = "",
    val reservaId: String? = null,
    val cliente: String = "",
    val precio: Double = 0.0
) : Serializable

data class AgendaCancha(
    val id: String,
    val nombre: String,
    val celdas: List<AgendaCelda>
) : Serializable

data class AgendaDia(
    val fecha: String,
    val cerrado: Boolean,
    val horas: List<Int>,
    val canchas: List<AgendaCancha>
) : Serializable
