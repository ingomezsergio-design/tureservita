package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Complejo
import com.example.data.repository.TuCanchaRepository
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DayAgendaScreen(
    complejo: Complejo,
    onBack: () -> Unit
) {
    val sdf = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    var selectedDate by remember { mutableStateOf(sdf.format(Date())) }

    // Dialog for manual booking
    var showManualDialog by remember { mutableStateOf(false) }
    var selectedCanchaId by remember { mutableStateOf("") }
    var manualClientName by remember { mutableStateOf("") }
    var manualStartHour by remember { mutableStateOf("18") }
    var manualEndHour by remember { mutableStateOf("19") }
    var manualPrice by remember { mutableStateOf("20000") }
    var manualWeeks by remember { mutableStateOf(1) }
    var manualNotes by remember { mutableStateOf("") }

    val agenda = remember(complejo, selectedDate) {
        TuCanchaRepository.getAgendaDia(complejo.id, selectedDate)
    }

    val canchas = remember(complejo) {
        TuCanchaRepository.canchas.value.filter { it.complejoId == complejo.id }
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
                        Text("Agenda del Día", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(complejo.nombre, fontSize = 12.sp, color = EmeraldLight)
                    }
                }

                Button(
                    onClick = {
                        selectedCanchaId = canchas.firstOrNull()?.id ?: ""
                        showManualDialog = true
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = NightBg, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Cargar Turno", fontSize = 11.sp, fontWeight = FontWeight.Black, color = NightBg)
                }
            }
        }

        // Date selector row
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Fecha:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    OutlinedTextField(
                        value = selectedDate,
                        onValueChange = { selectedDate = it },
                        singleLine = true,
                        modifier = Modifier.width(180.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = GlassBorder,
                            focusedContainerColor = NightCard,
                            unfocusedContainerColor = NightCard
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Legend
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(modifier = Modifier.size(10.dp), shape = RoundedCornerShape(2.dp), color = EmeraldLight) {}
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Confirmado", fontSize = 10.sp, color = TextMuted)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(modifier = Modifier.size(10.dp), shape = RoundedCornerShape(2.dp), color = GoldAccent) {}
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Pendiente", fontSize = 10.sp, color = TextMuted)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(modifier = Modifier.size(10.dp), shape = RoundedCornerShape(2.dp), color = NightCard, border = BorderStroke(1.dp, GlassBorder)) {}
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Libre", fontSize = 10.sp, color = TextMuted)
                    }
                }
            }
        }

        // Agenda Matrix
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                val scrollState = rememberScrollState()
                Column(modifier = Modifier.horizontalScroll(scrollState)) {
                    // Header Hours Row
                    Row(modifier = Modifier.padding(bottom = 8.dp)) {
                        Text(
                            text = "CANCHA",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = TextDim,
                            modifier = Modifier.width(130.dp)
                        )
                        agenda.horas.forEach { h ->
                            Text(
                                text = "$h:00",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted,
                                modifier = Modifier.width(70.dp)
                            )
                        }
                    }

                    Divider(color = GlassBorder)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Courts rows
                    agenda.canchas.forEach { court ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = court.nombre,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                modifier = Modifier.width(130.dp)
                            )

                            court.celdas.forEach { cell ->
                                val (bgColor, txtColor) = when (cell.estado) {
                                    "PAGADA", "MANUAL" -> EmeraldDark to EmeraldLight
                                    "PENDIENTE" -> GoldAccent.copy(alpha = 0.2f) to GoldAccent
                                    "BLOQUEADO" -> RoseAccent.copy(alpha = 0.2f) to RoseAccent
                                    else -> NightCard to TextDim
                                }

                                Surface(
                                    modifier = Modifier
                                        .width(66.dp)
                                        .height(38.dp)
                                        .padding(horizontal = 2.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    color = bgColor,
                                    border = BorderStroke(1.dp, if (cell.estado == "LIBRE") GlassBorder else txtColor.copy(alpha = 0.5f))
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Text(
                                            text = if (cell.cliente.isNotBlank()) cell.cliente.take(6) else if (cell.estado == "LIBRE") "+" else cell.estado.take(4),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = txtColor
                                        )
                                    }
                                }
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

    // Manual / Recurring Reservation Dialog
    if (showManualDialog) {
        AlertDialog(
            onDismissRequest = { showManualDialog = false },
            title = { Text("Cargar Reserva Manual", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Cancha:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextDim)
                    canchas.forEach { c ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = selectedCanchaId == c.id, onClick = { selectedCanchaId = c.id })
                            Text(c.nombre, fontSize = 12.sp, color = TextPrimary)
                        }
                    }

                    OutlinedTextField(
                        value = manualClientName,
                        onValueChange = { manualClientName = it },
                        label = { Text("Nombre del cliente") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = manualStartHour,
                            onValueChange = { manualStartHour = it },
                            label = { Text("Inicio (hs)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = manualEndHour,
                            onValueChange = { manualEndHour = it },
                            label = { Text("Fin (hs)") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = manualPrice,
                        onValueChange = { manualPrice = it },
                        label = { Text("Precio total ($)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = manualWeeks.toString(),
                        onValueChange = { manualWeeks = it.toIntOrNull() ?: 1 },
                        label = { Text("Semanas recurrentes (1 = solo esta)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val court = canchas.find { it.id == selectedCanchaId } ?: canchas.firstOrNull()
                        if (court != null) {
                            val priceVal = manualPrice.toDoubleOrNull() ?: 20000.0
                            if (manualWeeks > 1) {
                                TuCanchaRepository.crearReservaRecurrente(
                                    complejo.id,
                                    court.id,
                                    court.nombre,
                                    manualClientName.ifBlank { "Cliente Mostrador" },
                                    "",
                                    "",
                                    selectedDate,
                                    manualStartHour,
                                    manualEndHour,
                                    priceVal,
                                    manualWeeks
                                )
                            } else {
                                TuCanchaRepository.crearReservaManual(
                                    complejo.id,
                                    court.id,
                                    court.nombre,
                                    manualClientName.ifBlank { "Cliente Mostrador" },
                                    "",
                                    "",
                                    selectedDate,
                                    manualStartHour,
                                    manualEndHour,
                                    priceVal,
                                    "MANUAL",
                                    manualNotes
                                )
                            }
                        }
                        showManualDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("Guardar", color = NightBg, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showManualDialog = false }) {
                    Text("Cancelar", color = TextMuted)
                }
            },
            containerColor = NightSurface
        )
    }
}
