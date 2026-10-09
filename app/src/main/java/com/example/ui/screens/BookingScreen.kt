package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Cancha
import com.example.data.model.Reserva
import com.example.data.repository.TuCanchaRepository
import com.example.ui.components.GlassCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BookingScreen(
    cancha: Cancha,
    onBack: () -> Unit,
    onNavigateMyBookings: () -> Unit
) {
    val context = LocalContext.current
    val currentUser by TuCanchaRepository.currentUser.collectAsState()

    val sdf = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val hoy = remember { sdf.format(Date()) }

    var selectedDate by remember { mutableStateOf(hoy) }
    var selectedStartHour by remember { mutableStateOf<Int?>(null) }
    var selectedEndHour by remember { mutableStateOf<Int?>(null) }
    var promoCodeInput by remember { mutableStateOf("") }
    var appliedPromoCode by remember { mutableStateOf("") }
    var promoDiscount by remember { mutableStateOf(0.0) }
    var promoMessage by remember { mutableStateOf<String?>(null) }

    var isSubmitting by remember { mutableStateOf(false) }
    var confirmedReservation by remember { mutableStateOf<Reserva?>(null) }

    val disponibilidad = remember(cancha, selectedDate) {
        TuCanchaRepository.getDisponibilidad(cancha.complejoId, cancha.id, selectedDate)
    }

    // Next 7 days list for date selection
    val nextDays = remember {
        val list = mutableListOf<Pair<String, String>>()
        val cal = Calendar.getInstance()
        val dayFormat = SimpleDateFormat("EEE d MMM", Locale("es", "ES"))
        for (i in 0..7) {
            val dateStr = sdf.format(cal.time)
            val label = if (i == 0) "Hoy" else dayFormat.format(cal.time).replaceFirstChar { it.uppercase() }
            list.add(Pair(dateStr, label))
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        list
    }

    // Dynamic price calculation
    val basePrice = remember(selectedStartHour, selectedEndHour, disponibilidad) {
        if (selectedStartHour != null && selectedEndHour != null && selectedEndHour!! > selectedStartHour!!) {
            var sum = 0.0
            for (h in selectedStartHour!! until selectedEndHour!!) {
                val slot = disponibilidad.slots.find { it.hora == "$h:00" }
                sum += slot?.precio ?: cancha.precio
            }
            sum
        } else 0.0
    }

    val finalPrice = maxOf(0.0, basePrice - promoDiscount)

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
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = TextPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Reservar Turno",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${cancha.nombre} · ${cancha.complejoNombre}",
                        fontSize = 12.sp,
                        color = EmeraldLight
                    )
                }
            }
        }

        if (confirmedReservation != null) {
            // CONFIRMATION VIEW
            val res = confirmedReservation!!
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = EmeraldPrimary
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(EmeraldDark.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(36.dp))
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "¡Turno Registrado!", fontSize = 22.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                        Text(text = "Código de Reserva: ${res.id}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EmeraldLight)

                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(color = GlassBorder)
                        Spacer(modifier = Modifier.height(16.dp))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Fecha:", fontSize = 13.sp, color = TextMuted)
                                Text(res.fecha, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Horario:", fontSize = 13.sp, color = TextMuted)
                                Text("${res.horaInicio}:00 a ${res.horaFin}:00 hs", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Titular:", fontSize = 13.sp, color = TextMuted)
                                Text(res.cliente, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            if (res.descuento > 0) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Descuento cupón:", fontSize = 13.sp, color = EmeraldLight)
                                    Text("-$${res.descuento.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EmeraldLight)
                                }
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total a abonar:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("$${res.precio.toInt()}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = EmeraldLight)
                            }
                            if (res.sena > 0 && res.senaPorcentaje < 100) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Seña requerida (${res.senaPorcentaje}%):", fontSize = 13.sp, color = SkyAccent)
                                    Text("$${res.sena.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SkyAccent)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Actions: Google Calendar & WhatsApp
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    val gCalUrl = TuCanchaRepository.buildGoogleCalendarUrl(
                                        "Partido en ${cancha.nombre} (${cancha.complejoNombre})",
                                        res.fecha,
                                        res.horaInicio,
                                        res.horaFin
                                    )
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(gCalUrl))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NightCard),
                                border = BorderStroke(1.dp, GlassBorder)
                            ) {
                                Icon(Icons.Default.DateRange, contentDescription = null, tint = SkyAccent)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Agregar a Google Calendar", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }

                            Button(
                                onClick = {
                                    val msg = "Hola! Confirmo mi reserva ${res.id} en ${cancha.complejoNombre} - ${cancha.nombre} para el ${res.fecha} de ${res.horaInicio}:00 a ${res.horaFin}:00 hs."
                                    val phone = cancha.whatsappComplejo.ifBlank { "5493624559812" }
                                    val waUrl = TuCanchaRepository.buildWhatsAppUrl(phone, msg)
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(waUrl))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                            ) {
                                Text("Confirmar por WhatsApp", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }

                            if (res.pagoConfigurado && res.urlPago != null) {
                                Button(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(res.urlPago))
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF009EE3))
                                ) {
                                    Text("Pagar Seña con Mercado Pago", fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color.White)
                                }
                            }

                            OutlinedButton(
                                onClick = onNavigateMyBookings,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Ver en 'Mis Reservas'", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                        }
                    }
                }
            }
        } else {
            // BOOKING STEP 1: DATE PICKER
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "PASO 1: ELEGÍ LA FECHA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = EmeraldLight,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(nextDays) { (dateStr, label) ->
                            val isSelected = selectedDate == dateStr
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) EmeraldDark.copy(alpha = 0.6f) else NightCard,
                                border = BorderStroke(1.dp, if (isSelected) EmeraldLight else GlassBorder),
                                modifier = Modifier.clickable {
                                    selectedDate = dateStr
                                    selectedStartHour = null
                                    selectedEndHour = null
                                }
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                        color = if (isSelected) EmeraldLight else TextPrimary
                                    )
                                    Text(
                                        text = dateStr.takeLast(5),
                                        fontSize = 10.sp,
                                        color = TextDim
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // BOOKING STEP 2: SLOTS GRID
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "PASO 2: DISPONIBILIDAD Y HORARIOS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = EmeraldLight,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tocá la hora de inicio y luego la hora de finalización del partido.",
                        fontSize = 12.sp,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Slot items
                    disponibilidad.slots.chunked(3).forEach { rowSlots ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowSlots.forEach { slot ->
                                val hourInt = slot.hora.replace(":00", "").toIntOrNull() ?: 0
                                val isSelectedStart = selectedStartHour == hourInt
                                val isSelectedEnd = selectedEndHour == hourInt + 1
                                val isInRange = selectedStartHour != null && selectedEndHour != null &&
                                        hourInt >= selectedStartHour!! && hourInt < selectedEndHour!!

                                val boxColor = when {
                                    slot.ocupado -> Color(0x33F43F5E)
                                    isSelectedStart || isSelectedEnd || isInRange -> EmeraldDark.copy(alpha = 0.7f)
                                    else -> NightCard
                                }
                                val borderColor = when {
                                    slot.ocupado -> RoseAccent.copy(alpha = 0.4f)
                                    isSelectedStart || isSelectedEnd || isInRange -> EmeraldLight
                                    else -> GlassBorder
                                }

                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(vertical = 4.dp)
                                        .clickable(enabled = !slot.ocupado) {
                                            if (selectedStartHour == null) {
                                                selectedStartHour = hourInt
                                                selectedEndHour = hourInt + 1
                                            } else if (selectedEndHour == null || hourInt < selectedStartHour!!) {
                                                selectedStartHour = hourInt
                                                selectedEndHour = hourInt + 1
                                            } else {
                                                selectedEndHour = hourInt + 1
                                            }
                                        },
                                    shape = RoundedCornerShape(12.dp),
                                    color = boxColor,
                                    border = BorderStroke(1.dp, borderColor)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = slot.hora,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (slot.ocupado) RoseAccent else TextPrimary
                                        )
                                        Text(
                                            text = if (slot.ocupado) "Ocupado" else "$${slot.precio.toInt()}",
                                            fontSize = 10.sp,
                                            color = if (slot.ocupado) RoseAccent.copy(alpha = 0.8f) else EmeraldLight
                                        )
                                    }
                                }
                            }
                            // Fill remaining columns if not full 3
                            repeat(3 - rowSlots.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // BOOKING STEP 3: SUMMARY & PROMO
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "PASO 3: CONFIRMACIÓN Y DESCUENTO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = EmeraldLight,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (selectedStartHour != null && selectedEndHour != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = NightCard,
                            border = BorderStroke(1.dp, GlassBorder)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Turno seleccionado:", fontSize = 12.sp, color = TextMuted)
                                    Text(
                                        "$selectedDate de ${selectedStartHour}:00 a ${selectedEndHour}:00 hs",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Duración:", fontSize = 12.sp, color = TextMuted)
                                    Text(
                                        "${selectedEndHour!! - selectedStartHour!!} hora(s)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldLight
                                    )
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "Seleccioná un horario en el Paso 2 para calcular el total.",
                            fontSize = 12.sp,
                            color = GoldAccent
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Promo code box
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = promoCodeInput,
                            onValueChange = { promoCodeInput = it.uppercase() },
                            placeholder = { Text("Código de descuento", fontSize = 12.sp, color = TextDim) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = EmeraldPrimary,
                                unfocusedBorderColor = GlassBorder,
                                focusedContainerColor = NightCard,
                                unfocusedContainerColor = NightCard
                            )
                        )

                        Button(
                            onClick = {
                                if (promoCodeInput.isNotBlank()) {
                                    val promos = TuCanchaRepository.getPromociones(cancha.complejoId)
                                    val found = promos.find { it.codigo.equals(promoCodeInput.trim(), ignoreCase = true) && it.activo }
                                    if (found != null) {
                                        appliedPromoCode = found.codigo
                                        promoDiscount = if (found.tipo == "MONTO") found.valor else (basePrice * (found.valor / 100.0))
                                        promoMessage = "¡Cupón ${found.codigo} aplicado! (-$${promoDiscount.toInt()})"
                                    } else {
                                        promoMessage = "El código ingresado no es válido o ya venció."
                                    }
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark)
                        ) {
                            Text("Aplicar", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EmeraldLight)
                        }
                    }

                    if (promoMessage != null) {
                        Text(
                            text = promoMessage!!,
                            fontSize = 12.sp,
                            color = if (appliedPromoCode.isNotEmpty()) EmeraldLight else RoseAccent,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = GlassBorder)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Final Price Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("TOTAL FINAL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextDim)
                            Text("$${finalPrice.toInt()}", fontSize = 22.sp, fontWeight = FontWeight.Black, color = EmeraldLight)
                        }

                        Button(
                            onClick = {
                                if (selectedStartHour != null && selectedEndHour != null && selectedEndHour!! > selectedStartHour!!) {
                                    isSubmitting = true
                                    val res = TuCanchaRepository.crearReserva(
                                        complejoId = cancha.complejoId,
                                        canchaId = cancha.id,
                                        canchaNombre = cancha.nombre,
                                        cliente = currentUser?.nombre ?: "Jugador TuCancha",
                                        email = currentUser?.email ?: "jugador@tucanchapro.com",
                                        telefono = currentUser?.telefono ?: "",
                                        fecha = selectedDate,
                                        horaInicio = selectedStartHour.toString(),
                                        horaFin = selectedEndHour.toString(),
                                        codigoPromo = appliedPromoCode
                                    )
                                    isSubmitting = false
                                    res.onSuccess {
                                        confirmedReservation = it
                                    }
                                }
                            },
                            enabled = selectedStartHour != null && selectedEndHour != null && selectedEndHour!! > selectedStartHour!!,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            modifier = Modifier.testTag("btn_confirm_reservation")
                        ) {
                            Text("Confirmar Reserva", fontSize = 13.sp, fontWeight = FontWeight.Black, color = NightBg)
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
