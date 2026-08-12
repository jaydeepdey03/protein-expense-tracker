package com.jaydeep.trackingapp.features.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToExpenses: () -> Unit,
    onNavigateToProtein: () -> Unit,
    onNavigateToStatus: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onLogout: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    var showLogoutDialog by remember {
        mutableStateOf(false)
    }

    // ---------------------------------------------------------
    // Logout observer
    // ---------------------------------------------------------

    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) {
            onLogout()
        }
    }

    // ---------------------------------------------------------
    // Logout dialog
    // ---------------------------------------------------------

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = {
                showLogoutDialog = false
            },
            title = {
                Text(
                    text = "Logout",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to logout?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.logout()
                    }
                ) {
                    Text(
                        text = "Logout",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // ---------------------------------------------------------
    // Screen
    // ---------------------------------------------------------

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Tracker",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Your daily overview",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },

                actions = {

                    // Status
                    IconButton(
                        onClick = onNavigateToStatus
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Status"
                        )
                    }

                    // Settings
                    IconButton(
                        onClick = onNavigateToSettings
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }

                    // Logout
                    IconButton(
                        onClick = {
                            showLogoutDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout"
                        )
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->

        // -----------------------------------------------------
        // Loading
        // -----------------------------------------------------

        if (uiState.isLoading && uiState.userName.isEmpty()) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            return@Scaffold
        }

        // -----------------------------------------------------
        // Main content
        // -----------------------------------------------------

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),

            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            // -------------------------------------------------
            // Welcome
            // -------------------------------------------------

            WelcomeCard(
                userName = uiState.userName,
            )

            // -------------------------------------------------
            // Section title
            // -------------------------------------------------

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                Text(
                    text = "Today",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Keep track of your protein and spending",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // -------------------------------------------------
            // Protein + Expenses
            // -------------------------------------------------

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                DashboardActionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.FitnessCenter,
                    title = "Protein",
                    subtitle = "Track your intake",
                    accent = MaterialTheme.colorScheme.secondary,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    onClick = onNavigateToProtein
                )

                DashboardActionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.AccountBalanceWallet,
                    title = "Expenses",
                    subtitle = "Track your spending",
                    accent = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    onClick = onNavigateToExpenses
                )
            }

            // -------------------------------------------------
            // Progress
            // -------------------------------------------------

            ProgressCard()

            Spacer(
                modifier = Modifier.height(8.dp)
            )
        }
    }
}


// =====================================================================
// Welcome Card
// =====================================================================

@Composable
private fun WelcomeCard(
    userName: String
) {
    val firstName = userName
        .trim()
        .split(" ")
        .firstOrNull()
        ?.ifEmpty { "there" }
        ?: "there"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primaryContainer
                        )
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(22.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            MaterialTheme.colorScheme.onPrimary.copy(
                                alpha = 0.18f
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = firstName
                            .firstOrNull()
                            ?.uppercaseChar()
                            ?.toString()
                            ?: "?",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Spacer(
                    modifier = Modifier.width(14.dp)
                )

                Column {
                    Text(
                        text = "Hello, $firstName 👋",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = "Let's make today count.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(
                            alpha = 0.8f
                        )
                    )
                }
            }
        }
    }
}


// =====================================================================
// Dashboard Action Card
// =====================================================================

@Composable
private fun DashboardActionCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    subtitle: String,
    accent: Color,
    containerColor: Color,
    onClick: () -> Unit
) {

    Card(
        onClick = onClick,

        modifier = modifier
            .aspectRatio(0.92f),

        shape = RoundedCornerShape(24.dp),

        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),

            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // ---------------------------------------------
            // Icon
            // ---------------------------------------------

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(
                        accent.copy(alpha = 0.15f)
                    ),

                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accent,
                    modifier = Modifier.size(25.dp)
                )
            }

            // ---------------------------------------------
            // Text
            // ---------------------------------------------

            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {

                Text(
                    text = title,

                    style = MaterialTheme.typography.titleLarge,

                    fontWeight = FontWeight.Bold,

                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = subtitle,

                    style = MaterialTheme.typography.bodySmall,

                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "Open",

                        style = MaterialTheme.typography.labelMedium,

                        fontWeight = FontWeight.SemiBold,

                        color = accent
                    )

                    Spacer(
                        modifier = Modifier.width(4.dp)
                    )

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}


// =====================================================================
// Progress Card
// =====================================================================

@Composable
private fun ProgressCard() {

    Card(
        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(24.dp),

        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {

        Column(
            modifier = Modifier.padding(20.dp),

            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ---------------------------------------------
            // Header
            // ---------------------------------------------

            Row(
                modifier = Modifier.fillMaxWidth(),

                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Your progress",

                        style = MaterialTheme.typography.titleMedium,

                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Stay consistent with your goals",

                        style = MaterialTheme.typography.bodySmall,

                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,

                    contentDescription = null,

                    tint = MaterialTheme.colorScheme.primary,

                    modifier = Modifier.size(28.dp)
                )
            }

            // ---------------------------------------------
            // Stats
            // ---------------------------------------------

            Row(
                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                MiniStat(
                    modifier = Modifier.weight(1f),
                    value = "—",
                    label = "Protein"
                )

                MiniStat(
                    modifier = Modifier.weight(1f),
                    value = "—",
                    label = "Expenses"
                )

                MiniStat(
                    modifier = Modifier.weight(1f),
                    value = "Today",
                    label = "Tracking"
                )
            }
        }
    }
}


// =====================================================================
// Mini Stat
// =====================================================================

@Composable
private fun MiniStat(
    modifier: Modifier = Modifier,
    value: String,
    label: String
) {

    Surface(
        modifier = modifier,

        shape = RoundedCornerShape(16.dp),

        color = MaterialTheme.colorScheme.surface
    ) {

        Column(
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 14.dp
            ),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = value,

                style = MaterialTheme.typography.titleMedium,

                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = label,

                style = MaterialTheme.typography.labelSmall,

                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}