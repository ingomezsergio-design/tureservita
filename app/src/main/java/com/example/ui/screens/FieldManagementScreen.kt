package com.example.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Cancha
import com.example.data.model.Complejo
import com.example.data.model.ReglaPrecioHorario
import com.example.data.repository.TuCanchaRepository
import com.example.ui.components.GlassCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import java.util.UUID

@Composable
fun FieldManagementScreen(
    complejo: Complejo,
    onBack: () -> Unit
) {
    val allCanchas by TuCanchaRepository.canchas.collectAsState()
    val canchas = remember(allCanchas, complejo) {
        allCanchas.filter { it.complejoId == complejo.id }
    }

    var editingCancha by remember { mutableStateOf<Cancha?>(null) }
    var isNewCancha by remember { mutableStateOf(false) }

    // Form fields
    var nameInput by remember { mutableStateOf("") }
    var sportInput by remember { mutableStateOf("futbol") }
    var formatInput by remember { mutableStateOf("Fútbol 5") }
    var surfaceInput by remember { mutableStateOf("Sintético") }
    var roofInput by remember { mutableStateOf("Techada") }
    var priceInput by remember { mutableStateOf("20000") }
    var descInput by remember { mutableStateOf("") }
    var imageInput by remember { mutableStateOf("") }
    var amenitiesInput by remember { mutableStateOf("") }

    fun openEditor(c: Cancha?) {
        if (c == null) {
            isNewCancha = true
            nameInput = ""
            sportInput = complejo.deporte
            formatInput = "Fútbol 5"
            surfaceInput = "Sintético"
            roofInput = "Descubierta"
            priceInput = "20000"
            descInput = ""
            imageInput = ""
            amenitiesInput = "Vestuarios, Estacionamiento, Iluminación LED"
            editingCancha = Cancha(
                id = "C-" + UUID.randomUUID().toString().take(4).uppercase(),
                complejoId = complejo.id,
                complejoNombre = complejo.nombre,
                nombre = ""
            )
        } else {
            isNewCancha = false
            editingCancha = c
            nameInput = c.nombre
            sportInput = c.deporte
            formatInput = c.formato
            surfaceInput = c.superficie
            roofInput = c.techo
            priceInput = c.precio.toInt().toString()
            descInput = c.descripcion
            imageInput = c.imagen
            amenitiesInput = c.amenities.joinToString(", ")
        }
    }

    if (editingCancha != null) {
        // Field Editor Form
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
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { editingCancha = null }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = TextPrimary)
                    }
                    Text(
                        text = if (isNewCancha) "Nueva Cancha" else "Editar Cancha",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Nombre de la Cancha") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = GlassBorder,
                            focusedContainerColor = NightCard,
                            unfocusedContainerColor = NightCard
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = formatInput,
                            onValueChange = { formatInput = it },
                            label = { Text("Formato (F5, F7, Dobles)") },
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
                        OutlinedTextField(
                            value = priceInput,
                            onValueChange = { priceInput = it },
                            label = { Text("Precio Base ($)") },
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
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = surfaceInput,
                            onValueChange = { surfaceInput = it },
                            label = { Text("Superficie") },
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
                        OutlinedTextField(
                            value = roofInput,
                            onValueChange = { roofInput = it },
                            label = { Text("Techo (Techada/Descubierta)") },
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
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = imageInput,
                        onValueChange = { imageInput = it },
                        label = { Text("URL de la Foto") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = GlassBorder,
                            focusedContainerColor = NightCard,
                            unfocusedContainerColor = NightCard
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = amenitiesInput,
                        onValueChange = { amenitiesInput = it },
                        label = { Text("Comodidades (separadas por coma)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = GlassBorder,
                            focusedContainerColor = NightCard,
                            unfocusedContainerColor = NightCard
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = descInput,
                        onValueChange = { descInput = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = GlassBorder,
                            focusedContainerColor = NightCard,
                            unfocusedContainerColor = NightCard
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val priceVal = priceInput.toDoubleOrNull() ?: 20000.0
                            val amenitiesList = amenitiesInput.split(",").map { it.trim() }.filter { it.isNotBlank() }
                            val updated = editingCancha!!.copy(
                                nombre = nameInput.ifBlank { "Cancha ${canchas.size + 1}" },
                                deporte = sportInput,
                                formato = formatInput,
                                superficie = surfaceInput,
                                techo = roofInput,
                                precio = priceVal,
                                precioMin = priceVal,
                                precioMax = priceVal,
                                imagen = imageInput.ifBlank { "https://images.unsplash.com/photo-1574629810360-7efbbe195018?q=80&w=800" },
                                amenities = amenitiesList,
                                descripcion = descInput,
                                estado = "Activa"
                            )
                            TuCanchaRepository.saveCancha(updated)
                            editingCancha = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Text("Guardar Cancha", fontWeight = FontWeight.Bold, color = NightBg)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    } else {
        // Canchas List
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
                            Text("Canchas del Complejo", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(complejo.nombre, fontSize = 12.sp, color = EmeraldLight)
                        }
                    }

                    Button(
                        onClick = { openEditor(null) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = NightBg, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Nueva", fontSize = 11.sp, fontWeight = FontWeight.Black, color = NightBg)
                    }
                }
            }

            if (canchas.isEmpty()) {
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text("No tenés canchas creadas aún en este complejo.", fontSize = 12.sp, color = TextMuted)
                    }
                }
            } else {
                items(canchas, key = { it.id }) { c ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(c.nombre, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("${c.formato} · ${c.techo} · $${c.precio.toInt()}/h", fontSize = 12.sp, color = EmeraldLight)
                            }
                            StatusBadge(status = c.estado)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { openEditor(c) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NightCard),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
                            ) {
                                Text("Editar", fontSize = 11.sp, color = TextPrimary)
                            }

                            OutlinedButton(
                                onClick = { TuCanchaRepository.deleteCancha(c.id) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, RoseAccent.copy(alpha = 0.5f))
                            ) {
                                Text("Desactivar", fontSize = 11.sp, color = RoseAccent)
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
}
