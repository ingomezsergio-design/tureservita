package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Cancha
import com.example.data.repository.TuCanchaRepository
import com.example.ui.components.GlassCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.StarsRating
import com.example.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FieldDetailScreen(
    cancha: Cancha,
    onBack: () -> Unit,
    onBook: () -> Unit
) {
    val context = LocalContext.current
    val allReviews by TuCanchaRepository.resenas.collectAsState()
    val courtReviews = remember(allReviews, cancha) {
        allReviews.filter { it.canchaId == cancha.id }
    }

    var selectedImage by remember { mutableStateOf(cancha.imagen.ifBlank { "https://images.unsplash.com/photo-1574629810360-7efbbe195018?q=80&w=800" }) }
    var userRating by remember { mutableStateOf(5) }
    var reviewComment by remember { mutableStateOf("") }
    var showReviewSubmitted by remember { mutableStateOf(false) }

    val currentUser by TuCanchaRepository.currentUser.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Navigation back button
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
                Text(
                    text = cancha.nombre,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }

        // Hero Image
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(24.dp))
            ) {
                AsyncImage(
                    model = selectedImage,
                    contentDescription = cancha.nombre,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Complex Name badge
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xCC020617),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary)
                ) {
                    Text(
                        text = cancha.complejoNombre.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = EmeraldLight,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Gallery thumbnails
        if (cancha.galeria.isNotEmpty()) {
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    2.dp,
                                    if (selectedImage == cancha.imagen) EmeraldPrimary else Color.Transparent,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedImage = cancha.imagen }
                        ) {
                            AsyncImage(
                                model = cancha.imagen,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                    items(cancha.galeria) { item ->
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    2.dp,
                                    if (selectedImage == item.url) EmeraldPrimary else Color.Transparent,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedImage = item.url }
                        ) {
                            AsyncImage(
                                model = item.url,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }

        // Badges: Sport, Format, Roof, Surface
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(cancha.deporte.uppercase(), cancha.formato, cancha.techo, cancha.superficie).forEach { badge ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = NightCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }

        // Notice Banner
        if (cancha.avisoComplejo.isNotBlank()) {
            item {
                GlassCard(
                    borderColor = GoldAccent.copy(alpha = 0.5f),
                    backgroundColor = GoldAccent.copy(alpha = 0.12f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "📢", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = cancha.avisoComplejo,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent
                        )
                    }
                }
            }
        }

        // Description
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "DESCRIPCIÓN DE LA CANCHA",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDim,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = cancha.descripcion.ifBlank { "Instalaciones de primer nivel listas para tu partido." },
                    fontSize = 13.sp,
                    color = TextPrimary,
                    lineHeight = 20.sp
                )
            }
        }

        // Schedule Price Rules
        if (cancha.preciosPorHorario.isNotEmpty()) {
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "TARIFAS POR FRANJA HORARIA",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDim,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    cancha.preciosPorHorario.forEach { regla ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${regla.horaInicio}:00 a ${regla.horaFin}:00 hs (${regla.etiqueta.ifBlank { "Turno" }})",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                            Text(
                                text = "$${regla.precio.toInt()}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldLight
                            )
                        }
                    }
                }
            }
        }

        // Amenities
        if (cancha.amenities.isNotEmpty()) {
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "COMODIDADES (AMENITIES)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDim,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        cancha.amenities.forEach { amenity ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = EmeraldDark.copy(alpha = 0.3f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = amenity, fontSize = 11.sp, color = EmeraldLight, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Complex Info & Contact
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "INFORMACIÓN DEL COMPLEJO",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDim,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                if (cancha.direccionComplejo.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Place, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = cancha.direccionComplejo, fontSize = 12.sp, color = TextPrimary)
                    }
                }
                if (cancha.reglasComplejo.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "Reglas: ${cancha.reglasComplejo}", fontSize = 11.sp, color = TextMuted)
                }

                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (cancha.whatsappComplejo.isNotBlank()) {
                        Button(
                            onClick = {
                                val url = TuCanchaRepository.buildWhatsAppUrl(cancha.whatsappComplejo, "Hola! Quisiera consultar por la cancha ${cancha.nombre}")
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                        ) {
                            Text("WhatsApp", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    if (cancha.mapsComplejo.isNotBlank()) {
                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(cancha.mapsComplejo))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
                        ) {
                            Text("Ver Mapa", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }
                }
            }
        }

        // Reviews Section
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                SectionHeader(
                    title = "Reseñas de Jugadores (${courtReviews.size})",
                    subtitle = "Opiniones de usuarios que ya jugaron acá"
                )

                if (courtReviews.isEmpty()) {
                    Text(
                        text = "Todavía no hay reseñas para esta cancha. ¡Sé el primero en calificarla luego de jugar!",
                        fontSize = 12.sp,
                        color = TextDim,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    courtReviews.forEach { rev ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = rev.cliente, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                StarsRating(rating = rev.rating, starSize = 14)
                            }
                            Text(text = rev.comentario, fontSize = 12.sp, color = TextMuted)
                            if (rev.respuestaComplejo.isNotBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = EmeraldDark.copy(alpha = 0.2f),
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Text(
                                        text = "Respuesta del complejo: ${rev.respuestaComplejo}",
                                        fontSize = 11.sp,
                                        color = EmeraldLight,
                                        modifier = Modifier.padding(6.dp)
                                    )
                                }
                            }
                        }
                        Divider(color = GlassBorder, modifier = Modifier.padding(vertical = 4.dp))
                    }
                }

                // Add Review box
                if (currentUser != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "DEJAR MI RESEÑA", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextDim)
                    Spacer(modifier = Modifier.height(4.dp))
                    StarsRating(rating = userRating, onRatingChange = { userRating = it })
                    OutlinedTextField(
                        value = reviewComment,
                        onValueChange = { reviewComment = it },
                        placeholder = { Text("Contá tu experiencia sobre la cancha...", fontSize = 12.sp, color = TextDim) },
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
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = {
                            if (reviewComment.isNotBlank()) {
                                TuCanchaRepository.crearResena(
                                    cancha.complejoId,
                                    cancha.id,
                                    currentUser?.nombre ?: "Jugador",
                                    currentUser?.email ?: "",
                                    userRating,
                                    reviewComment
                                )
                                reviewComment = ""
                                showReviewSubmitted = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Publicar Reseña", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NightBg)
                    }
                }
            }
        }

        // Action CTA
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(18.dp),
                color = NightSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "TARIFA BASE", fontSize = 10.sp, color = TextDim, fontWeight = FontWeight.Bold)
                        Text(text = "$${cancha.precio.toInt()}/hora", fontSize = 18.sp, fontWeight = FontWeight.Black, color = EmeraldLight)
                    }
                    Button(
                        onClick = onBook,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Seleccionar Turno", fontSize = 14.sp, fontWeight = FontWeight.Black, color = NightBg)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
