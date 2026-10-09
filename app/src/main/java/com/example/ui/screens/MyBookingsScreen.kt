package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.TuCanchaRepository
import com.example.ui.components.GlassCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun MyBookingsScreen() {
    val context = LocalContext.current
    val currentUser by TuCanchaRepository.currentUser.collectAsState()
    val allReservas by TuCanchaRepository.reservas.collectAsState()

    val myReservas = remember(allReservas, currentUser) {
        val userEmail = currentUser?.email ?: ""
        allReservas.filter { it.email.equals(userEmail, ignoreCase = true) || it.cliente.equals(currentUser?.nombre ?: "", ignoreCase = true) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            SectionHeader(
                title = "Mis Reservas",
                subtitle = "Historial y estado de tus partidos reservados"
            )
        }

        if (myReservas.isEmpty()) {
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "📅", fontSize = 42.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No tenés reservas registradas",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Explorá las canchas y asegurá tu turno.",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        } else {
            items(myReservas, key = { it.id }) { r ->
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = r.complejoNombre.uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = EmeraldLight
                            )
                            Text(
                                text = r.canchaNombre,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                        }
                        StatusBadge(status = r.estado)
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = GlassBorder)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "FECHA & HORARIO", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextDim)
                            Text(text = "${r.fecha} · ${r.horaInicio}:00 a ${r.horaFin}:00 hs", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "TOTAL", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextDim)
                            Text(text = "$${r.precio.toInt()}", fontSize = 15.sp, fontWeight = FontWeight.Black, color = EmeraldLight)
                        }
                    }

                    if (r.notas.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Nota: ${r.notas}", fontSize = 11.sp, color = TextMuted)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val url = TuCanchaRepository.buildWhatsAppUrl(
                                    "5493624559812",
                                    "Hola! Consulta sobre mi reserva ${r.id} en ${r.canchaNombre} (${r.fecha})"
                                )
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NightCard),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("WhatsApp", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }

                        if (r.estado == "PENDIENTE") {
                            OutlinedButton(
                                onClick = {
                                    TuCanchaRepository.cancelarReserva(r.complejoId, r.id, currentUser?.email ?: "")
                                },
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, RoseAccent.copy(alpha = 0.5f)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancelar", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RoseAccent)
                            }
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
