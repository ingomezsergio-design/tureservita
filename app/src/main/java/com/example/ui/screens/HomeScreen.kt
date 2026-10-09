package com.example.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Cancha
import com.example.data.repository.TuCanchaRepository
import com.example.ui.components.GlassCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.SportPill
import com.example.ui.theme.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    onSelectCancha: (Cancha) -> Unit,
    onBookCancha: (Cancha) -> Unit
) {
    val canchas by TuCanchaRepository.canchas.collectAsState()
    var selectedSport by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var onlyFavorites by remember { mutableStateOf(false) }
    var favoritesList by remember { mutableStateOf(setOf<String>()) }
    var selectedLocality by remember { mutableStateOf<String?>(null) }

    val sports = listOf(
        Triple("futbol", "Fútbol", "⚽"),
        Triple("padel", "Pádel", "🎾"),
        Triple("tenis", "Tenis", "🎾"),
        Triple("voley", "Vóley", "🏐"),
        Triple("basquet", "Básquet", "🏀")
    )

    val localities = remember(canchas) {
        canchas.map { it.localidad }.filter { it.isNotBlank() }.distinct()
    }

    val filteredCanchas = remember(canchas, selectedSport, searchQuery, onlyFavorites, favoritesList, selectedLocality) {
        canchas.filter { c ->
            val matchSport = selectedSport == null || c.deporte.equals(selectedSport, ignoreCase = true)
            val matchSearch = searchQuery.isBlank() ||
                    c.nombre.contains(searchQuery, ignoreCase = true) ||
                    c.complejoNombre.contains(searchQuery, ignoreCase = true) ||
                    c.localidad.contains(searchQuery, ignoreCase = true)
            val matchFav = !onlyFavorites || favoritesList.contains(c.id)
            val matchLoc = selectedLocality == null || c.localidad.equals(selectedLocality, ignoreCase = true)
            val isActiva = c.estado.equals("Activa", ignoreCase = true)
            matchSport && matchSearch && matchFav && matchLoc && isActiva
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Hero Title
            Column {
                Text(
                    text = "Elegí tu",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
                Text(
                    text = "Disciplina & Cancha",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = EmeraldLight
                )
                Text(
                    text = "Seleccioná un deporte para ver complejos certificados y turnos disponibles.",
                    fontSize = 13.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Sport Selector Pills
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                item {
                    SportPill(
                        name = "Todas",
                        icon = "⚡",
                        isSelected = selectedSport == null,
                        onClick = { selectedSport = null }
                    )
                }
                items(sports) { (id, name, icon) ->
                    SportPill(
                        name = name,
                        icon = icon,
                        isSelected = selectedSport == id,
                        onClick = { selectedSport = if (selectedSport == id) null else id }
                    )
                }
            }
        }

        // Search Bar & Filter Row
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar cancha, complejo o localidad...", color = TextDim, fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar", tint = TextMuted) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Limpiar", tint = TextMuted)
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = GlassBorder,
                        focusedContainerColor = NightCard,
                        unfocusedContainerColor = NightCard
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_field")
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Favorite Toggle Filter
                    FilterChip(
                        selected = onlyFavorites,
                        onClick = { onlyFavorites = !onlyFavorites },
                        label = { Text("Favoritos", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        leadingIcon = {
                            Icon(
                                imageVector = if (onlyFavorites) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = if (onlyFavorites) RoseAccent else TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = RoseAccent.copy(alpha = 0.2f),
                            selectedLabelColor = RoseAccent
                        )
                    )

                    // Locality filter pills
                    localities.forEach { loc ->
                        FilterChip(
                            selected = selectedLocality == loc,
                            onClick = { selectedLocality = if (selectedLocality == loc) null else loc },
                            label = { Text(loc, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldDark.copy(alpha = 0.4f),
                                selectedLabelColor = EmeraldLight
                            )
                        )
                    }
                }
            }
        }

        // Header for results
        item {
            SectionHeader(
                title = "Canchas Disponibles (${filteredCanchas.size})",
                subtitle = if (selectedSport != null) "Filtradas por deporte: ${selectedSport?.uppercase()}" else "Todas las disciplinas"
            )
        }

        // Court Cards
        if (filteredCanchas.isEmpty()) {
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "⚽", fontSize = 42.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No se encontraron canchas",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Probá cambiando los filtros o la búsqueda.",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        } else {
            items(filteredCanchas, key = { it.id }) { cancha ->
                val isFav = favoritesList.contains(cancha.id)
                CanchaCard(
                    cancha = cancha,
                    isFavorite = isFav,
                    onToggleFavorite = {
                        favoritesList = if (isFav) favoritesList - cancha.id else favoritesList + cancha.id
                    },
                    onSelect = { onSelectCancha(cancha) },
                    onBook = { onBookCancha(cancha) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun CanchaCard(
    cancha: Cancha,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onSelect: () -> Unit,
    onBook: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("cancha_card_${cancha.id}"),
        onClick = onSelect
    ) {
        // Image Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            AsyncImage(
                model = cancha.imagen.ifBlank { "https://images.unsplash.com/photo-1574629810360-7efbbe195018?q=80&w=800" },
                contentDescription = cancha.nombre,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color(0xDD020617))
                        )
                    )
            )

            // Favorite Button
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .background(Color(0x99020617), CircleShape)
                    .size(36.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = if (isFavorite) RoseAccent else Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Format & Roof Badges
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xCC020617),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = cancha.formato,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = EmeraldLight,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xCC020617),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SkyAccent.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = cancha.techo,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = SkyAccent,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Bottom title overlay on image
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Text(
                    text = cancha.complejoNombre.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = EmeraldLight,
                    letterSpacing = 1.sp
                )
                Text(
                    text = cancha.nombre,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Locality and amenities
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Place, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = cancha.localidad, fontSize = 12.sp, color = TextMuted)
            }
            Text(
                text = cancha.superficie,
                fontSize = 12.sp,
                color = TextDim,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Price and Action Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "TARIFA POR HORA",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDim
                )
                val priceText = if (cancha.preciosPorHorario.isNotEmpty()) {
                    "Desde $${cancha.precioMin.toInt().toString().replace(Regex("(\\d)(?=(\\d{3})+\$)"), "$1.")}"
                } else {
                    "$${cancha.precio.toInt().toString().replace(Regex("(\\d)(?=(\\d{3})+\$)"), "$1.")}"
                }
                Text(
                    text = priceText,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    color = EmeraldLight
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onSelect,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
                ) {
                    Text("Info", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }

                Button(
                    onClick = onBook,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    modifier = Modifier.testTag("btn_book_${cancha.id}")
                ) {
                    Text("Reservar", fontSize = 12.sp, fontWeight = FontWeight.Black, color = NightBg)
                }
            }
        }
    }
}
