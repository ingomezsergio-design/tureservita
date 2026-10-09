package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Complejo
import com.example.data.repository.TuCanchaRepository
import com.example.ui.components.GlassCard
import com.example.ui.theme.*

@Composable
fun ComplexEditorScreen(
    complejo: Complejo?,
    onBack: () -> Unit
) {
    val isNew = complejo == null

    var name by remember { mutableStateOf(complejo?.nombre ?: "") }
    var location by remember { mutableStateOf(complejo?.localidad ?: "Resistencia") }
    var address by remember { mutableStateOf(complejo?.direccion ?: "") }
    var phone by remember { mutableStateOf(complejo?.telefono ?: "") }
    var whatsapp by remember { mutableStateOf(complejo?.whatsapp ?: "") }
    var instagram by remember { mutableStateOf(complejo?.instagram ?: "") }
    var desc by remember { mutableStateOf(complejo?.descripcion ?: "") }
    var rules by remember { mutableStateOf(complejo?.reglas ?: "") }
    var notice by remember { mutableStateOf(complejo?.avisoDestacado ?: "") }
    var senaPct by remember { mutableStateOf((complejo?.senaPorcentaje ?: 50).toString()) }
    var cancelHours by remember { mutableStateOf((complejo?.horasMinCancelacion ?: 24).toString()) }
    var aliasCbu by remember { mutableStateOf(complejo?.aliasCbu ?: "") }
    var mpToken by remember { mutableStateOf(complejo?.mpAccessToken ?: "") }
    var status by remember { mutableStateOf(complejo?.estado ?: "Activo") }

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
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = TextPrimary)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isNew) "Nuevo Complejo" else "Editar Complejo",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }

        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text("DATOS PRINCIPALES", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextDim)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre del Complejo") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Localidad") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Dirección") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Descripción") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notice,
                    onValueChange = { notice = it },
                    label = { Text("Aviso Destacado (se muestra a todos los jugadores)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text("CONTACTO Y REDES", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextDim)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Teléfono") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = whatsapp,
                        onValueChange = { whatsapp = it },
                        label = { Text("WhatsApp (549...)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = instagram,
                    onValueChange = { instagram = it },
                    label = { Text("Instagram (@usuario)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text("REGLAS, SEÑAS Y COBROS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextDim)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = rules,
                    onValueChange = { rules = it },
                    label = { Text("Reglas del Complejo") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = senaPct,
                        onValueChange = { senaPct = it },
                        label = { Text("Seña Online (%)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = cancelHours,
                        onValueChange = { cancelHours = it },
                        label = { Text("Aviso Cancelación (hs)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = aliasCbu,
                    onValueChange = { aliasCbu = it },
                    label = { Text("Alias o CBU para Transferencias") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = mpToken,
                    onValueChange = { mpToken = it },
                    label = { Text("Mercado Pago Access Token (Opcional)") },
                    placeholder = { Text("APP_USR-...") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val newId = complejo?.id ?: (name.lowercase().replace(" ", "-") + "-" + (1000..9999).random())
                        val comp = Complejo(
                            id = newId,
                            nombre = name.ifBlank { "Nuevo Complejo" },
                            localidad = location,
                            direccion = address,
                            telefono = phone,
                            whatsapp = whatsapp,
                            instagram = instagram,
                            descripcion = desc,
                            reglas = rules,
                            avisoDestacado = notice,
                            senaPorcentaje = senaPct.toIntOrNull() ?: 50,
                            horasMinCancelacion = cancelHours.toIntOrNull() ?: 24,
                            aliasCbu = aliasCbu,
                            mpAccessToken = mpToken,
                            estado = status
                        )
                        TuCanchaRepository.saveComplejo(comp)
                        onBack()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("Guardar Cambios", fontWeight = FontWeight.Bold, color = NightBg)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
