package com.jaydeep.trackingapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.jaydeep.trackingapp.features.dashboard.ProfileViewModel
import com.jaydeep.trackingapp.ui.theme.ThemeMode
import com.jaydeep.trackingapp.ui.theme.ThemeViewModel

@Composable
fun ProfileScreen(
    onNavigateToHealthStatus: () -> Unit,
    profileViewModel: ProfileViewModel = hiltViewModel(),
    themeViewModel: ThemeViewModel = hiltViewModel()
) {
    val uiState by profileViewModel.uiState.collectAsState()
    val themeMode by themeViewModel.themeMode.collectAsState()

    var showProteinDialog by remember { mutableStateOf(false) }
    var showBudgetDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item { 
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = onNavigateToHealthStatus,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(Icons.Default.Info, contentDescription = "Status", tint = MaterialTheme.colorScheme.primary)
                }
                ProfileHeader(uiState.name, uiState.email)
            }
        }
        
        item {
            SettingsGroup("Daily Tracking Goals") {
                SettingsItem(
                    icon = Icons.Outlined.FitnessCenter, 
                    label = "Daily Protein Goal",
                    trailing = {
                        TextButton(onClick = { showProteinDialog = true }) {
                            Text("${uiState.proteinGoal.toInt()} g")
                        }
                    }
                )
                SettingsItem(
                    icon = Icons.Outlined.Savings, 
                    label = "Monthly Expense Budget",
                    trailing = {
                        TextButton(onClick = { showBudgetDialog = true }) {
                            Text("₹${uiState.expenseBudget.toInt()}")
                        }
                    }
                )
            }
        }

        item {
            val isDark = when (themeMode) {
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }
            SettingsGroup("Preferences") {
                SettingsItem(Icons.Outlined.DarkMode, "Dark Mode", trailing = {
                    Switch(
                        checked = isDark,
                        onCheckedChange = { 
                            themeViewModel.setTheme(if (it) ThemeMode.DARK else ThemeMode.LIGHT) 
                        }
                    )
                })
                SettingsItem(Icons.Outlined.Language, "Language")
            }
        }
        item {
            SettingsGroup("Account") {
                SettingsItem(Icons.Outlined.Logout, "Sign Out", textColor = MaterialTheme.colorScheme.error)
            }
        }
    }

    if (showProteinDialog) {
        GoalEditDialog(
            title = "Daily Protein Goal",
            initialValue = uiState.proteinGoal.toInt().toString(),
            unit = "g",
            onDismiss = { showProteinDialog = false },
            onSave = { 
                profileViewModel.updateProteinGoal(it.toFloatOrNull() ?: 120f)
                showProteinDialog = false
            }
        )
    }

    if (showBudgetDialog) {
        GoalEditDialog(
            title = "Monthly Expense Budget",
            initialValue = uiState.expenseBudget.toInt().toString(),
            unit = "₹",
            onDismiss = { showBudgetDialog = false },
            onSave = { 
                profileViewModel.updateExpenseBudget(it.toFloatOrNull() ?: 25000f)
                showBudgetDialog = false
            }
        )
    }
}

@Composable
fun GoalEditDialog(
    title: String,
    initialValue: String,
    unit: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var text by remember { mutableStateOf(initialValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Value ($unit)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = { onSave(text) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ProfileHeader(name: String, email: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.firstOrNull()?.toString() ?: "?",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = name.ifBlank { "..." },
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = email.ifBlank { "..." },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(8.dp))
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp), content = content)
        }
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    label: String,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    trailing: @Composable () -> Unit = { Icon(Icons.Outlined.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) }
) {
    ListItem(
        headlineContent = { Text(label, color = textColor) },
        leadingContent = { Icon(icon, null, tint = if (textColor == MaterialTheme.colorScheme.error) textColor else MaterialTheme.colorScheme.primary) },
        trailingContent = trailing,
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}
