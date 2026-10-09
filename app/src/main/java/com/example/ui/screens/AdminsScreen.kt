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
import com.example.data.model.Administrador
import com.example.data.model.Complejo
import com.example.data.repository.TuCanchaRepository
import com.example.ui.components.GlassCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AdminsScreen(
    complejo: Complejo,
    onBack: () -> Unit
) {
    val admins = remember(complejo) {
        TuCanchaRepository.getAdministradores(complejo.id)
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var emailInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf("") }
    var roleInput by remember { mutableStateOf("ADMIN") }

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
                        Text("Administradores", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
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
                    Text("Agregar", fontSize = 11.sp, fontWeight = FontWeight.Black, color = NightBg)
                }
            }
        }

        items(admins, key = { it.email }) { a ->
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = a.nombre, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(text = a.email, fontSize = 12.sp, color = TextMuted)
                        if (a.fechaAlta.isNotBlank()) {
                            Text(text = "Alta: ${a.fechaAlta}", fontSize = 10.sp, color = TextDim)
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        StatusBadge(status = a.rango)
                        IconButton(onClick = { TuCanchaRepository.deleteAdministrador(complejo.id, a.email) }) {
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
            title = { Text("Agregar Administrador", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Nombre y Apellido") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = roleInput == "ADMIN",
                            onClick = { roleInput = "ADMIN" },
                            label = { Text("Admin") }
                        )
                        FilterChip(
                            selected = roleInput == "SUPERADMIN",
                            onClick = { roleInput = "SUPERADMIN" },
                            label = { Text("Super Admin") }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (emailInput.isNotBlank()) {
                            val admin = Administrador(
                                email = emailInput.trim(),
                                nombre = nameInput.ifBlank { emailInput.substringBefore("@") },
                                rango = roleInput,
                                fechaAlta = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                            )
                            TuCanchaRepository.saveAdministrador(complejo.id, admin)
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
