package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Cancha
import com.example.data.model.Complejo
import com.example.data.repository.TuCanchaRepository
import com.example.ui.screens.*
import com.example.ui.theme.*

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Reviews : Screen("reviews")
    object MyBookings : Screen("my_bookings")
    object WebConsole : Screen("web_console")
    object Admin : Screen("admin")
    object About : Screen("about")
    object Auth : Screen("auth")

    // Subscreens
    data class FieldDetail(val cancha: Cancha) : Screen("field_detail")
    data class Booking(val cancha: Cancha) : Screen("booking")
    data class ComplexEditor(val complejo: Complejo?) : Screen("complex_editor")
    data class FieldManagement(val complejo: Complejo) : Screen("field_management")
    data class DayAgenda(val complejo: Complejo) : Screen("day_agenda")
    data class Stats(val complejo: Complejo) : Screen("stats")
    data class Promos(val complejo: Complejo) : Screen("promos")
    data class Admins(val complejo: Complejo) : Screen("admins")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TuCanchaMainScreen() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    var screenStack by remember { mutableStateOf(listOf<Screen>(Screen.Home)) }
    var showBotDialog by remember { mutableStateOf(false) }

    val currentUser by TuCanchaRepository.currentUser.collectAsState()

    fun navigateTo(newScreen: Screen) {
        screenStack = screenStack + newScreen
        currentScreen = newScreen
    }

    fun navigateBack() {
        if (screenStack.size > 1) {
            val updated = screenStack.dropLast(1)
            screenStack = updated
            currentScreen = updated.last()
        } else {
            currentScreen = Screen.Home
        }
    }

    BackHandler(enabled = screenStack.size > 1) {
        navigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = EmeraldDark.copy(alpha = 0.5f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary)
                        ) {
                            Text(
                                text = "⚡",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "TuCancha",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Pro",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    color = EmeraldLight
                                )
                            }
                            Text(
                                text = "Enterprise Cloud",
                                fontSize = 9.sp,
                                color = TextMuted,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                },
                actions = {
                    // About shortcut
                    IconButton(onClick = { navigateTo(Screen.About) }) {
                        Icon(Icons.Default.Info, contentDescription = "Acerca de", tint = TextMuted)
                    }

                    // User Profile / Auth Button
                    if (currentUser != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = NightCard,
                            border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .clickable { navigateTo(Screen.Admin) }
                                .padding(end = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "👤 ${currentUser?.nombre?.take(8)}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldLight
                                )
                            }
                        }
                    } else {
                        Button(
                            onClick = { navigateTo(Screen.Auth) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text("Ingresar", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NightBg)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NightBg
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = NightSurface,
                tonalElevation = 8.dp,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                val isHome = currentScreen is Screen.Home
                val isReviews = currentScreen is Screen.Reviews
                val isMyBookings = currentScreen is Screen.MyBookings
                val isWeb = currentScreen is Screen.WebConsole
                val isAdmin = currentScreen is Screen.Admin

                NavigationBarItem(
                    selected = isHome,
                    onClick = {
                        screenStack = listOf(Screen.Home)
                        currentScreen = Screen.Home
                    },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldLight,
                        selectedTextColor = EmeraldLight,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = EmeraldDark.copy(alpha = 0.4f)
                    )
                )

                NavigationBarItem(
                    selected = isReviews,
                    onClick = { navigateTo(Screen.Reviews) },
                    icon = { Icon(Icons.Default.Star, contentDescription = "Reseñas") },
                    label = { Text("Reseñas", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GoldAccent,
                        selectedTextColor = GoldAccent,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = GoldAccent.copy(alpha = 0.2f)
                    )
                )

                NavigationBarItem(
                    selected = isMyBookings,
                    onClick = { navigateTo(Screen.MyBookings) },
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Mis Reservas") },
                    label = { Text("Turnos", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SkyAccent,
                        selectedTextColor = SkyAccent,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = SkyAccent.copy(alpha = 0.2f)
                    )
                )

                NavigationBarItem(
                    selected = isWeb,
                    onClick = { navigateTo(Screen.WebConsole) },
                    icon = { Icon(Icons.Default.Language, contentDescription = "Modo Web") },
                    label = { Text("Web", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PurpleAccent,
                        selectedTextColor = PurpleAccent,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = PurpleAccent.copy(alpha = 0.2f)
                    )
                )

                NavigationBarItem(
                    selected = isAdmin,
                    onClick = { navigateTo(Screen.Admin) },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Admin") },
                    label = { Text("Admin", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldLight,
                        selectedTextColor = EmeraldLight,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = EmeraldDark.copy(alpha = 0.4f)
                    )
                )
            }
        },
        floatingActionButton = {
            // Floating Virtual Assistant ⚽
            FloatingActionButton(
                onClick = { showBotDialog = true },
                containerColor = EmeraldPrimary,
                contentColor = NightBg,
                shape = CircleShape,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .size(56.dp)
            ) {
                Text(text = "⚽", fontSize = 24.sp)
            }
        },
        containerColor = NightBg
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val screen = currentScreen) {
                is Screen.Home -> {
                    HomeScreen(
                        onSelectCancha = { cancha -> navigateTo(Screen.FieldDetail(cancha)) },
                        onBookCancha = { cancha -> navigateTo(Screen.Booking(cancha)) }
                    )
                }

                is Screen.FieldDetail -> {
                    FieldDetailScreen(
                        cancha = screen.cancha,
                        onBack = { navigateBack() },
                        onBook = { navigateTo(Screen.Booking(screen.cancha)) }
                    )
                }

                is Screen.Booking -> {
                    BookingScreen(
                        cancha = screen.cancha,
                        onBack = { navigateBack() },
                        onNavigateMyBookings = { navigateTo(Screen.MyBookings) }
                    )
                }

                is Screen.Reviews -> {
                    ReviewsScreen()
                }

                is Screen.MyBookings -> {
                    MyBookingsScreen()
                }

                is Screen.WebConsole -> {
                    WebConsoleScreen()
                }

                is Screen.About -> {
                    AboutScreen()
                }

                is Screen.Auth -> {
                    AuthScreen(
                        onSuccess = { navigateBack() }
                    )
                }

                is Screen.Admin -> {
                    AdminDashboardScreen(
                        onEditComplejo = { comp -> navigateTo(Screen.ComplexEditor(comp)) },
                        onCreateComplejo = { navigateTo(Screen.ComplexEditor(null)) },
                        onManageCanchas = { comp -> navigateTo(Screen.FieldManagement(comp)) },
                        onOpenAgenda = { comp -> navigateTo(Screen.DayAgenda(comp)) },
                        onOpenStats = { comp -> navigateTo(Screen.Stats(comp)) },
                        onOpenPromos = { comp -> navigateTo(Screen.Promos(comp)) },
                        onOpenAdmins = { comp -> navigateTo(Screen.Admins(comp)) }
                    )
                }

                is Screen.ComplexEditor -> {
                    ComplexEditorScreen(
                        complejo = screen.complejo,
                        onBack = { navigateBack() }
                    )
                }

                is Screen.FieldManagement -> {
                    FieldManagementScreen(
                        complejo = screen.complejo,
                        onBack = { navigateBack() }
                    )
                }

                is Screen.DayAgenda -> {
                    DayAgendaScreen(
                        complejo = screen.complejo,
                        onBack = { navigateBack() }
                    )
                }

                is Screen.Stats -> {
                    StatsScreen(
                        complejo = screen.complejo,
                        onBack = { navigateBack() }
                    )
                }

                is Screen.Promos -> {
                    PromosScreen(
                        complejo = screen.complejo,
                        onBack = { navigateBack() }
                    )
                }

                is Screen.Admins -> {
                    AdminsScreen(
                        complejo = screen.complejo,
                        onBack = { navigateBack() }
                    )
                }
            }

            if (showBotDialog) {
                BotDialog(onDismiss = { showBotDialog = false })
            }
        }
    }
}
