package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    borderColor: Color = GlassBorder,
    backgroundColor: Color = NightGlass,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(20.dp)
    Surface(
        modifier = modifier
            .clip(shape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        shape = shape,
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

@Composable
fun StatusBadge(status: String) {
    val (bgColor, textColor) = when (status.uppercase()) {
        "PAGADA", "ACTIVO", "ACTIVA", "CONFIRMADA" -> Color(0x2610B981) to EmeraldLight
        "PENDIENTE" -> Color(0x26F59E0B) to GoldAccent
        "MANUAL" -> Color(0x260EA5E9) to SkyAccent
        "CANCELADA", "INACTIVA", "PAUSADO" -> Color(0x26F43F5E) to RoseAccent
        "SUPERADMIN" -> Color(0x26A855F7) to PurpleAccent
        else -> Color(0x1AFFFFFF) to TextMuted
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = status.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            color = textColor,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun StarsRating(
    rating: Int,
    onRatingChange: ((Int) -> Unit)? = null,
    starSize: Int = 18
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        for (i in 1..5) {
            val isFilled = i <= rating
            IconButton(
                onClick = { onRatingChange?.invoke(i) },
                enabled = onRatingChange != null,
                modifier = Modifier.size(starSize.dp + 8.dp)
            ) {
                Icon(
                    imageVector = if (isFilled) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = "Estrella $i",
                    tint = if (isFilled) GoldAccent else TextDim,
                    modifier = Modifier.size(starSize.dp)
                )
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }
        }
        if (actionText != null && onActionClick != null) {
            TextButton(onClick = onActionClick) {
                Text(
                    text = actionText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldLight
                )
            }
        }
    }
}

@Composable
fun SportPill(
    name: String,
    icon: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    Surface(
        modifier = Modifier
            .clip(shape)
            .clickable(onClick = onClick),
        shape = shape,
        color = if (isSelected) EmeraldDark.copy(alpha = 0.6f) else NightCard,
        border = BorderStroke(1.dp, if (isSelected) EmeraldLight else GlassBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = icon, fontSize = 16.sp)
            Text(
                text = name,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                color = if (isSelected) EmeraldLight else TextPrimary
            )
        }
    }
}
