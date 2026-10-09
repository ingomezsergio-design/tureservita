package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.TuCanchaRepository
import com.example.ui.components.GlassCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@Composable
fun AboutScreen() {
    val context = LocalContext.current
    val config by TuCanchaRepository.siteConfig.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            SectionHeader(
                title = config.acercaDeTitulo,
                subtitle = "Conectamos el deporte con la mejor tecnología"
            )
        }

        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "${config.nombreMarca} ${config.sufijoMarca}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = EmeraldLight
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = config.acercaDeTexto,
                    fontSize = 14.sp,
                    color = TextPrimary,
                    lineHeight = 22.sp
                )
            }
        }

        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "CANALES DE CONTACTO",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDim,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Email
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Email, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = config.contactoEmail, fontSize = 13.sp, color = TextPrimary)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // WhatsApp
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF25D366), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = config.contactoWhatsapp, fontSize = 13.sp, color = TextPrimary)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Instagram
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = RoseAccent, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = config.instagram, fontSize = 13.sp, color = TextPrimary)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val url = "https://wa.me/${config.contactoWhatsapp.replace(Regex("[^0-9]"), "")}"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                ) {
                    Text("Abrir chat de WhatsApp", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NightBg)
                }
            }
        }

        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "BASE DE DATOS EN LA NUBE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDim,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Este sistema está conectado con tu planilla Google Sheets maestra:",
                    fontSize = 12.sp,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "ID: ${TuCanchaRepository.DEFAULT_SHEET_ID}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldLight
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
