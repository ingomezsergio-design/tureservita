package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.TuCanchaRepository
import com.example.ui.components.GlassCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.StarsRating
import com.example.ui.theme.*

@Composable
fun ReviewsScreen() {
    val resenas by TuCanchaRepository.resenas.collectAsState()

    val averageRating = remember(resenas) {
        if (resenas.isNotEmpty()) resenas.map { it.rating }.average() else 5.0
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
                title = "Reseñas Verificadas",
                subtitle = "Opiniones reales de jugadores de la comunidad"
            )
        }

        // Summary Rating Card
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "CALIFICACIÓN GENERAL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextDim)
                        Text(
                            text = String.format("%.1f", averageRating),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = GoldAccent
                        )
                        StarsRating(rating = averageRating.toInt(), starSize = 16)
                    }

                    Text(
                        text = "${resenas.size} opiniones\nverificadas",
                        fontSize = 12.sp,
                        color = TextMuted,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        items(resenas, key = { it.id }) { rev ->
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = rev.cliente, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(text = "${rev.complejoNombre} · ${rev.canchaNombre}", fontSize = 11.sp, color = EmeraldLight)
                    }
                    StarsRating(rating = rev.rating, starSize = 14)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = rev.comentario,
                    fontSize = 13.sp,
                    color = TextPrimary,
                    lineHeight = 18.sp
                )

                if (rev.fecha.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = rev.fecha, fontSize = 10.sp, color = TextDim)
                }

                if (rev.respuestaComplejo.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                        color = EmeraldDark.copy(alpha = 0.25f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(text = "Respuesta del complejo:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EmeraldLight)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = rev.respuestaComplejo, fontSize = 12.sp, color = TextPrimary)
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
