package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.repository.TuCanchaRepository
import com.example.ui.components.GlassCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@Composable
fun WebConsoleScreen() {
    val context = LocalContext.current
    var activeTab by remember { mutableStateOf("info") } // "info", "script", "webview"
    var webAppUrlInput by remember { mutableStateOf(TuCanchaRepository.webAppEndpoint) }
    var fullScreenWeb by remember { mutableStateOf(false) }

    val correctedScriptCode = remember {
        try {
            context.assets.open("web/backend_code.gs").bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            "// Archivo de backend disponible en app/src/main/assets/web/backend_code.gs"
        }
    }

    if (fullScreenWeb) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Navegador Web Integrado", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                IconButton(onClick = { fullScreenWeb = false }) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = TextPrimary)
                }
            }
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.cacheMode = WebSettings.LOAD_DEFAULT
                        webViewClient = WebViewClient()
                        val targetUrl = webAppUrlInput.ifBlank { "https://script.google.com" }
                        loadUrl(targetUrl)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            SectionHeader(
                title = "Consola Web & Google Sheets",
                subtitle = "Conexión directa con tu planilla origen y código Apps Script corregido"
            )

            // Tabs Switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = activeTab == "info",
                    onClick = { activeTab = "info" },
                    label = { Text("Conexión Sheets") },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = EmeraldDark)
                )
                FilterChip(
                    selected = activeTab == "script",
                    onClick = { activeTab = "script" },
                    label = { Text("Código Apps Script") },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = SkyAccent.copy(alpha = 0.3f))
                )
                FilterChip(
                    selected = activeTab == "webview",
                    onClick = { activeTab = "webview" },
                    label = { Text("Web View") },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PurpleAccent.copy(alpha = 0.3f))
                )
            }
        }

        if (activeTab == "info") {
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text("BASE DE DATOS GOOGLE SHEETS ORIGEN", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextDim, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Planilla Maestra:", fontSize = 12.sp, color = TextMuted)
                    Text("ID: ${TuCanchaRepository.DEFAULT_SHEET_ID}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EmeraldLight)

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Carpeta Google Drive (Fotos y Videos):", fontSize = 12.sp, color = TextMuted)
                    Text("ID: ${TuCanchaRepository.DEFAULT_DRIVE_FOLDER_ID}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SkyAccent)

                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = {
                            val url = "https://docs.google.com/spreadsheets/d/${TuCanchaRepository.DEFAULT_SHEET_ID}/edit"
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NightCard),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
                    ) {
                        Icon(Icons.Default.OpenInBrowser, contentDescription = null, tint = EmeraldLight)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Abrir Planilla en Google Sheets", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }
            }

            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text("URL DE LA WEB APP PUBLICADA (APPS SCRIPT)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextDim, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Si publicaste tu web app como implementación web en Google Apps Script, pegá el enlace aquí:", fontSize = 12.sp, color = TextMuted)

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = webAppUrlInput,
                        onValueChange = {
                            webAppUrlInput = it
                            TuCanchaRepository.webAppEndpoint = it
                        },
                        placeholder = { Text("https://script.google.com/macros/s/.../exec", fontSize = 11.sp, color = TextDim) },
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

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (webAppUrlInput.isNotBlank()) {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(webAppUrlInput))
                                    context.startActivity(intent)
                                } else {
                                    Toast.makeText(context, "Pegá el link de tu Web App primero", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                        ) {
                            Text("Abrir en Navegador", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NightBg)
                        }

                        Button(
                            onClick = {
                                if (webAppUrlInput.isNotBlank()) {
                                    fullScreenWeb = true
                                } else {
                                    Toast.makeText(context, "Pegá el link de tu Web App primero", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SkyAccent.copy(alpha = 0.2f))
                        ) {
                            Text("Ver Dentro de la App", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SkyAccent)
                        }
                    }
                }
            }
        }

        if (activeTab == "script") {
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("BACKEND CODE.GS CORREGIDO", fontSize = 11.sp, fontWeight = FontWeight.Black, color = EmeraldLight)
                            Text("Con correcciones de funciones duplicadas y permisos", fontSize = 10.sp, color = TextMuted)
                        }

                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Code.gs", correctedScriptCode)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "¡Código copiado al portapapeles!", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copiar Todo", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldLight)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = NightCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val scrollState = rememberScrollState()
                        Text(
                            text = correctedScriptCode.take(2000) + "\n\n... (continúa archivo completo en app/src/main/assets/web/backend_code.gs)",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = TextMuted,
                            lineHeight = 16.sp,
                            modifier = Modifier
                                .padding(12.dp)
                                .horizontalScroll(scrollState)
                        )
                    }
                }
            }
        }

        if (activeTab == "webview") {
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text("VISTA WEB RÁPIDA", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextDim)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Permite visualizar cualquier implementación web en tiempo real.", fontSize = 12.sp, color = TextMuted)

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { fullScreenWeb = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Text("Iniciar Navegador Web", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NightBg)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
