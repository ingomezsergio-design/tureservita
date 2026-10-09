package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Complejo
import com.example.data.model.Promocion
import com.example.data.repository.TuCanchaRepository
import com.example.ui.components.GlassCard
import com.example.ui.theme.*

@Composable
fun PromosScreen(
    complejo: Complejo,
    onBack: () -> Unit
) {
    val promos = remember(complejo) {
        TuCanchaRepository.getPromociones(complejo.id)
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var codeInput by remember { mutableStateOf("") }
    var typeInput by remember { mutableStateOf("PORCENTAJE") }
    var valInput by remember { mutableStateOf("10") }
    var descInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
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
                        Text("Promociones y Descuentos", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(complejo.nombre, fontSize = 12.sp, color = EmeraldLight)
                    }
                }

                Button(
                    onClick = { showAddDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = NightBg, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Nuevo Código", fontSize = 11.sp, fontWeight = FontWeight.Black, color = NightBg)
                }
            }
        }

        if (promos.isEmpty()) {
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text("No hay promociones activas para este complejo.", fontSize = 12.sp, color = TextMuted)
                }
            }
        } else {
            items(promos, key = { it.codigo }) { p ->
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🏷️ ${p.codigo}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (p.activo) "ACTIVA" else "PAUSADA",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (p.activo) EmeraldLight else RoseAccent
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            val valText = if (p.tipo == "MONTO") "$${p.valor.toInt()} OFF" else "${p.valor.toInt()}% OFF"
                            Text(text = "$valText · Usos: ${p.usosActuales}", fontSize = 12.sp, color = GoldAccent)
                            if (p.descripcion.isNotBlank()) {
                                Text(text = p.descripcion, fontSize = 11.sp, color = TextMuted)
                            }
                        }

                        IconButton(onClick = { TuCanchaRepository.deletePromocion(complejo.id, p.codigo) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = RoseAccent)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Nueva Promoción", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = codeInput,
                        onValueChange = { codeInput = it.uppercase() },
                        label = { Text("Código (ej. VERANO2026)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = typeInput == "PORCENTAJE",
                            onClick = { typeInput = "PORCENTAJE" },
                            label = { Text("Porcentaje (%)") }
                        )
                        FilterChip(
                            selected = typeInput == "MONTO",
                            onClick = { typeInput = "MONTO" },
                            label = { Text("Monto Fijo ($)") }
                        )
                    }

                    OutlinedTextField(
                        value = valInput,
                        onValueChange = { valInput = it },
                        label = { Text(if (typeInput == "PORCENTAJE") "Valor % (ej. 15)" else "Monto $ (ej. 3000)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = descInput,
                        onValueChange = { descInput = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (codeInput.isNotBlank()) {
                            val promo = Promocion(
                                codigo = codeInput.trim(),
                                tipo = typeInput,
                                valor = valInput.toDoubleOrNull() ?: 10.0,
                                descripcion = descInput,
                                activo = true
                            )
                            TuCanchaRepository.savePromocion(complejo.id, promo)
                        }
                        showAddDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("Guardar", color = NightBg, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancelar", color = TextMuted)
                }
            },
            containerColor = NightSurface
        )
    }
}
