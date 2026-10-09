package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.repository.TuCanchaRepository
import com.example.ui.theme.*

data class MensajeBotUi(
    val isBot: Boolean,
    val text: String,
    val suggestions: List<String> = emptyList()
)

@Composable
fun BotDialog(
    onDismiss: () -> Unit
) {
    var inputQuery by remember { mutableStateOf("") }
    var chatMessages by remember {
        mutableStateOf(
            listOf(
                MensajeBotUi(
                    isBot = true,
                    text = "¡Hola! ⚽ Soy el asistente virtual de TuCancha Pro. Preguntame sobre reservas, pagos, horarios o elegí una de estas preguntas frecuentes:",
                    suggestions = listOf(
                        "¿Cómo reservo una cancha?",
                        "¿Qué formas de pago aceptan?",
                        "¿Cómo cancelo una reserva?",
                        "¿Se suspenden por lluvia?"
                    )
                )
            )
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 400.dp, max = 560.dp),
            shape = RoundedCornerShape(24.dp),
            color = NightSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NightCard)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(EmeraldDark.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("⚽", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Asistente TuCancha Pro", fontSize = 14.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                            Text("En línea · Respuestas automáticas", fontSize = 10.sp, color = EmeraldLight)
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = TextMuted)
                    }
                }

                // Chat Messages List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(chatMessages) { msg ->
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = if (msg.isBot) Alignment.Start else Alignment.End
                        ) {
                            Surface(
                                shape = RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = if (msg.isBot) 4.dp else 16.dp,
                                    bottomEnd = if (msg.isBot) 16.dp else 4.dp
                                ),
                                color = if (msg.isBot) NightCard else EmeraldPrimary,
                                border = if (msg.isBot) androidx.compose.foundation.BorderStroke(1.dp, GlassBorder) else null
                            ) {
                                Text(
                                    text = msg.text,
                                    fontSize = 13.sp,
                                    color = if (msg.isBot) TextPrimary else NightBg,
                                    fontWeight = if (msg.isBot) FontWeight.Normal else FontWeight.Bold,
                                    modifier = Modifier.padding(12.dp),
                                    lineHeight = 18.sp
                                )
                            }

                            // Suggestions chips
                            if (msg.suggestions.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                msg.suggestions.forEach { sugg ->
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = EmeraldDark.copy(alpha = 0.3f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.4f)),
                                        modifier = Modifier
                                            .padding(vertical = 3.dp)
                                            .clickable {
                                                val (resp, nextSuggs) = TuCanchaRepository.consultarBot(sugg)
                                                chatMessages = chatMessages + listOf(
                                                    MensajeBotUi(isBot = false, text = sugg),
                                                    MensajeBotUi(isBot = true, text = resp, suggestions = nextSuggs)
                                                )
                                            }
                                    ) {
                                        Text(
                                            text = sugg,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = EmeraldLight,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Chat Input Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = inputQuery,
                        onValueChange = { inputQuery = it },
                        placeholder = { Text("Escribí tu consulta...", fontSize = 12.sp, color = TextDim) },
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
                            if (inputQuery.isNotBlank()) {
                                val q = inputQuery.trim()
                                inputQuery = ""
                                val (resp, suggs) = TuCanchaRepository.consultarBot(q)
                                chatMessages = chatMessages + listOf(
                                    MensajeBotUi(isBot = false, text = q),
                                    MensajeBotUi(isBot = true, text = resp, suggestions = suggs)
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        modifier = Modifier.size(48.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Enviar", tint = NightBg, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
