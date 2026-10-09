package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.TuCanchaRepository
import com.example.ui.components.GlassCard
import com.example.ui.theme.*

@Composable
fun AuthScreen(
    onSuccess: () -> Unit
) {
    var isLoginMode by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("ingomezsergio@gmail.com") }
    var password by remember { mutableStateOf("123456") }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("ADMIN_COMPLEJO") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 440.dp)
        ) {
            // Tab Switcher
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Button(
                    onClick = { isLoginMode = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isLoginMode) EmeraldPrimary else NightCard
                    )
                ) {
                    Text(
                        "Ingresar",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isLoginMode) NightBg else TextMuted
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { isLoginMode = false },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isLoginMode) EmeraldPrimary else NightCard
                    )
                ) {
                    Text(
                        "Registrarse",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (!isLoginMode) NightBg else TextMuted
                    )
                }
            }

            Text(
                text = if (isLoginMode) "Acceso a TuCancha Pro" else "Creá tu cuenta",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )
            Text(
                text = if (isLoginMode) "Iniciá sesión para reservar o administrar complejos." else "Completá tus datos para empezar.",
                fontSize = 12.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (!isLoginMode) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre y Apellido") },
                    singleLine = true,
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
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Teléfono / WhatsApp") },
                    singleLine = true,
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
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_email_input"),
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
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_password_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = EmeraldPrimary,
                    unfocusedBorderColor = GlassBorder,
                    focusedContainerColor = NightCard,
                    unfocusedContainerColor = NightCard
                )
            )

            if (!isLoginMode) {
                Spacer(modifier = Modifier.height(12.dp))
                Text("Tipo de cuenta:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextDim)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedRole == "CLIENTE",
                        onClick = { selectedRole = "CLIENTE" },
                        label = { Text("🏃 Jugador") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = EmeraldDark)
                    )
                    FilterChip(
                        selected = selectedRole == "ADMIN_COMPLEJO",
                        onClick = { selectedRole = "ADMIN_COMPLEJO" },
                        label = { Text("⚙️ Admin Complejo") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PurpleAccent.copy(alpha = 0.3f))
                    )
                }
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = errorMessage!!, fontSize = 12.sp, color = RoseAccent)
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = {
                    if (isLoginMode) {
                        TuCanchaRepository.login(email, password).onSuccess {
                            onSuccess()
                        }.onFailure {
                            errorMessage = it.message ?: "Error al ingresar"
                        }
                    } else {
                        if (name.isBlank() || email.isBlank() || password.isBlank()) {
                            errorMessage = "Completá nombre, email y contraseña"
                        } else {
                            TuCanchaRepository.register(name, email, password, phone, selectedRole).onSuccess {
                                onSuccess()
                            }.onFailure {
                                errorMessage = it.message ?: "Error al registrarse"
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_auth_submit"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text(
                    text = if (isLoginMode) "Ingresar" else "Crear Cuenta",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = NightBg
                )
            }
        }
    }
}
