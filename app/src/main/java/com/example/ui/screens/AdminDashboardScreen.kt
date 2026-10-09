package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Complejo
import com.example.data.repository.TuCanchaRepository
import com.example.ui.components.GlassCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun AdminDashboardScreen(
    onEditComplejo: (Complejo) -> Unit,
    onCreateComplejo: () -> Unit,
    onManageCanchas: (Complejo) -> Unit,
    onOpenAgenda: (Complejo) -> Unit,
    onOpenStats: (Complejo) -> Unit,
    onOpenPromos: (Complejo) -> Unit,
    onOpenAdmins: (Complejo) -> Unit
) {
    val context = LocalContext.current
    val currentUser by TuCanchaRepository.currentUser.collectAsState()
    val complejos by TuCanchaRepository.complejos.collectAsState()
    val reservas by TuCanchaRepository.reservas.collectAsState()

    var statusFilter by remember { mutableStateOf<String?>(null) }
    var searchReserva by remember { mutableStateOf("") }

    // Metrics
    val totalRevenue = remember(reservas) {
        reservas.filter { it.estado == "PAGADA" || it.estado == "MANUAL" }.sumOf { it.precio }
    }
    val paidCount = remember(reservas) {
        reservas.count { it.estado == "PAGADA" || it.estado == "MANUAL" }
    }
    val pendingCount = remember(reservas) {
        reservas.count { it.estado == "PENDIENTE" }
    }

    val filteredReservas = remember(reservas, statusFilter, searchReserva) {
        reservas.filter { r ->
            val matchStatus = statusFilter == null || r.estado.equals(statusFilter, ignoreCase = true)
            val matchQuery = searchReserva.isBlank() ||
                    r.cliente.contains(searchReserva, ignoreCase = true) ||
                    r.canchaNombre.contains(searchReserva, ignoreCase = true) ||
                    r.complejoNombre.contains(searchReserva, ignoreCase = true)
            matchStatus && matchQuery
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Panel Administrativo",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Text(
                        text = "Sesión: ${currentUser?.nombre ?: "Admin"} · ${currentUser?.rol ?: ""}",
                        fontSize = 12.sp,
                        color = EmeraldLight
                    )
                }
            }
        }

        // Metrics 4-grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GlassCard(
                        modifier = Modifier.weight(1f),
                        borderColor = EmeraldPrimary.copy(alpha = 0.4f)
                    ) {
                        Text("INGRESOS MES", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextDim)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$${totalRevenue.toInt()}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = EmeraldLight
                        )
                    }

                    GlassCard(
                        modifier = Modifier.weight(1f),
                        borderColor = SkyAccent.copy(alpha = 0.4f)
                    ) {
                        Text("CONFIRMADAS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextDim)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$paidCount",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = SkyAccent
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GlassCard(
                        modifier = Modifier.weight(1f),
                        borderColor = GoldAccent.copy(alpha = 0.4f)
                    ) {
                        Text("PENDIENTES", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextDim)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$pendingCount",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = GoldAccent
                        )
                    }

                    GlassCard(
                        modifier = Modifier.weight(1f),
                        borderColor = PurpleAccent.copy(alpha = 0.4f)
                    ) {
                        Text("TOTAL TURNOS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextDim)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${reservas.size}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                    }
                }
            }
        }

        // Complexes Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MIS COMPLEJOS (${complejos.size})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = TextDim,
                    letterSpacing = 1.sp
                )
                Button(
                    onClick = onCreateComplejo,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = NightBg)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Crear Complejo", fontSize = 11.sp, fontWeight = FontWeight.Black, color = NightBg)
                }
            }
        }

        items(complejos, key = { it.id }) { comp ->
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = comp.localidad.ifBlank { "Deportivo" }.uppercase(), fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = EmeraldLight)
                        Text(text = comp.nombre, fontSize = 17.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                        Text(text = comp.direccion.ifBlank { "Sin dirección fijada" }, fontSize = 11.sp, color = TextMuted)
                    }
                    StatusBadge(status = comp.estado)
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Actions grid for the complex
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { onManageCanchas(comp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark)
                        ) {
                            Text("Gestionar Canchas", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EmeraldLight)
                        }
                        Button(
                            onClick = { onOpenAgenda(comp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SkyAccent.copy(alpha = 0.2f))
                        ) {
                            Text("Agenda del Día", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SkyAccent)
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { onEditComplejo(comp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NightCard),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
                        ) {
                            Text("Editar Datos", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Button(
                            onClick = { onOpenStats(comp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NightCard),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
                        ) {
                            Text("Estadísticas", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { onOpenPromos(comp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent.copy(alpha = 0.15f))
                        ) {
                            Text("Promociones", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                        }
                        Button(
                            onClick = { onOpenAdmins(comp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent.copy(alpha = 0.15f))
                        ) {
                            Text("Administradores", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PurpleAccent)
                        }
                    }
                }
            }
        }

        // Recent Bookings
        item {
            SectionHeader(
                title = "Todas las Reservas (${filteredReservas.size})",
                subtitle = "Monitoreo en tiempo real de partidos registrados"
            )

            // Status filter chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(null to "Todas", "PENDIENTE" to "Pendientes", "PAGADA" to "Pagadas", "MANUAL" to "Manuales").forEach { (st, label) ->
                    FilterChip(
                        selected = statusFilter == st,
                        onClick = { statusFilter = st },
                        label = { Text(label, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldDark,
                            selectedLabelColor = EmeraldLight
                        )
                    )
                }
            }
        }

        items(filteredReservas, key = { it.id }) { r ->
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = r.cliente, fontSize = 14.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                        Text(text = "${r.complejoNombre} · ${r.canchaNombre}", fontSize = 11.sp, color = TextMuted)
                    }
                    StatusBadge(status = r.estado)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "${r.fecha} (${r.horaInicio}:00 - ${r.horaFin}:00 hs)", fontSize = 12.sp, color = TextMuted)
                    Text(text = "$${r.precio.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EmeraldLight)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Actions: Change state & WhatsApp
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Status toggles
                    OutlinedButton(
                        onClick = {
                            val next = when (r.estado) {
                                "PENDIENTE" -> "PAGADA"
                                "PAGADA" -> "MANUAL"
                                "MANUAL" -> "CANCELADA"
                                else -> "PENDIENTE"
                            }
                            TuCanchaRepository.cambiarEstadoReserva(r.id, next)
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cambiar Estado", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    if (r.telefono.isNotBlank()) {
                        Button(
                            onClick = {
                                val url = TuCanchaRepository.buildWhatsAppUrl(r.telefono, "Hola ${r.cliente}! Te escribimos del complejo por tu reserva en ${r.canchaNombre}.")
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                        ) {
                            Text("WhatsApp", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
