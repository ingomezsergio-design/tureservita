package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Complejo
import com.example.data.repository.TuCanchaRepository
import com.example.ui.components.GlassCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@Composable
fun StatsScreen(
    complejo: Complejo,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val stats = remember(complejo) {
        TuCanchaRepository.getEstadisticas(complejo.id)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = TextPrimary)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text("Estadísticas", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(complejo.nombre, fontSize = 12.sp, color = EmeraldLight)
                    }
                }

                Button(
                    onClick = {
                        val csv = TuCanchaRepository.exportarReservasCSV(complejo.id)
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, csv)
                            putExtra(Intent.EXTRA_TITLE, "Reservas ${complejo.nombre}.csv")
                            type = "text/csv"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "Exportar Reservas CSV")
                        context.startActivity(shareIntent)
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = NightBg, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Exportar CSV", fontSize = 11.sp, fontWeight = FontWeight.Black, color = NightBg)
                }
            }
        }

        // 4 KPI Summary Cards
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GlassCard(modifier = Modifier.weight(1f)) {
                        Text("FACTURADO", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextDim)
                        Text("$${stats.resumen.ingresoTotal.toInt()}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = EmeraldLight)
                    }
                    GlassCard(modifier = Modifier.weight(1f)) {
                        Text("HORAS VENDIDAS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextDim)
                        Text("${stats.resumen.horasVendidas} hs", fontSize = 18.sp, fontWeight = FontWeight.Black, color = SkyAccent)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GlassCard(modifier = Modifier.weight(1f)) {
                        Text("TICKET PROMEDIO", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextDim)
                        Text("$${stats.resumen.ticketPromedio.toInt()}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = GoldAccent)
                    }
                    GlassCard(modifier = Modifier.weight(1f)) {
                        Text("CANCELACIONES", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextDim)
                        Text("${stats.resumen.tasaCancelacion}%", fontSize = 18.sp, fontWeight = FontWeight.Black, color = RoseAccent)
                    }
                }
            }
        }

        // Revenue by Court
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                SectionHeader(title = "Facturación por Cancha")
                if (stats.porCancha.isEmpty()) {
                    Text("Sin datos de reservas aún.", fontSize = 12.sp, color = TextMuted)
                } else {
                    stats.porCancha.forEach { c ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(c.clave, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("${c.reservas} turnos", fontSize = 11.sp, color = TextDim)
                            }
                            Text("$${c.ingresos.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EmeraldLight)
                        }
                        Divider(color = GlassBorder)
                    }
                }
            }
        }

        // Peak Hours
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                SectionHeader(title = "Horarios Pico con Mayor Demanda")
                if (stats.horariosPico.isEmpty()) {
                    Text("Sin datos suficientes.", fontSize = 12.sp, color = TextMuted)
                } else {
                    stats.horariosPico.forEach { (h, count) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("$h:00 hs", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("$count partidos", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SkyAccent)
                        }
                    }
                }
            }
        }

        // Top VIP Customers
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                SectionHeader(title = "Mejores Clientes (Fidelidad)")
                if (stats.mejoresClientes.isEmpty()) {
                    Text("Sin clientes aún.", fontSize = 12.sp, color = TextMuted)
                } else {
                    stats.mejoresClientes.forEach { cli ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(cli.clave, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("$${cli.ingresos.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
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
