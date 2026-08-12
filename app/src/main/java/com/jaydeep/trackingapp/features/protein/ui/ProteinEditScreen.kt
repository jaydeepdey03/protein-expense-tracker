package com.jaydeep.trackingapp.features.protein.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.jaydeep.trackingapp.core.ui.CategoryUtils
import com.jaydeep.trackingapp.ui.theme.LocalTrackerColors
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProteinEditScreen(
    proteinId: String?,
    onNavigateBack: () -> Unit,
    viewModel: ProteinViewModel = hiltViewModel(),
) {
    val uiState by viewModel.editUiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDatePicker by remember { mutableStateOf(false) }
    var showCategoryMenu by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    LaunchedEffect(proteinId) {
        proteinId?.toLongOrNull()?.let { viewModel.loadProteinById(it) } ?: viewModel.resetEditState()
    }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            viewModel.resetEditState()
            onNavigateBack()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onEditErrorShown()
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = uiState.dateMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { viewModel.onDateChange(it) }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Delete Entry") },
            text = { Text("Are you sure you want to delete this protein entry?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        proteinId?.toLongOrNull()?.let { viewModel.deleteProtein(it) }
                        showDeleteConfirmation = false
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (proteinId == null) "Log Protein" else "Edit Protein") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (proteinId != null) {
                        IconButton(onClick = { showDeleteConfirmation = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            // Protein Amount display
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Protein", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    text = "${uiState.proteinGrams.ifBlank { "0" }} g",
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (uiState.proteinGramsError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )
                if (uiState.proteinGramsError != null) {
                    Text(
                        text = uiState.proteinGramsError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Source and Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Source Selector (using Food Name as source)
                Box(modifier = Modifier.weight(1f)) {
                    val trackerColors = LocalTrackerColors.current
                    val categoryInfo = CategoryUtils.getProteinCategoryInfo(uiState.foodName, trackerColors)
                    OutlinedCard(
                        onClick = { showCategoryMenu = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        border = if (uiState.foodNameError != null) BorderStroke(1.dp, MaterialTheme.colorScheme.error) else CardDefaults.outlinedCardBorder()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(categoryInfo.icon, null, tint = categoryInfo.color, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(12.dp))
                            Text(if (uiState.foodName.isBlank()) "Source" else uiState.foodName, fontWeight = FontWeight.SemiBold, maxLines = 1)
                        }
                    }
                    if (uiState.foodNameError != null) {
                        Text(
                            text = uiState.foodNameError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                        )
                    }
                    DropdownMenu(expanded = showCategoryMenu, onDismissRequest = { showCategoryMenu = false }) {
                        val sources = listOf("Chicken", "Egg", "Fish", "Whey Protein", "Paneer", "Milk", "Dal", "Soy", "Nuts", "Other")
                        sources.forEach { source ->
                            DropdownMenuItem(
                                text = { Text(source) },
                                onClick = {
                                    viewModel.onFoodNameChange(source)
                                    showCategoryMenu = false
                                }
                            )
                        }
                    }
                }

                // Date Selector
                val formattedDate = Instant.ofEpochMilli(uiState.dateMillis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))

                OutlinedCard(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CalendarMonth, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(12.dp))
                        Text(formattedDate, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Grams Consumed field
            OutlinedTextField(
                value = uiState.gramsConsumed,
                onValueChange = viewModel::onGramsConsumedChange,
                label = { Text("Amount Consumed (grams)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                isError = uiState.gramsError != null,
                supportingText = uiState.gramsError?.let { { Text(it) } },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(Modifier.weight(1f))

            // Numeric keypad
            NumericKeypad(
                onKeyPress = viewModel::onKeyPress,
                onBackspace = viewModel::onBackspace
            )

            // Save Button
            val isFormValid = uiState.proteinGrams.isNotBlank() && uiState.foodName.isNotBlank() && uiState.gramsConsumed.isNotBlank()
            Button(
                onClick = { viewModel.saveProtein(proteinId?.toLongOrNull()) },
                enabled = !uiState.isLoading && isFormValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text(if (proteinId == null) "Log Protein" else "Save Changes", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun NumericKeypad(
    onKeyPress: (String) -> Unit,
    onBackspace: () -> Unit
) {
    val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", ".", "0", "⌫")
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(keys) { key ->
            Surface(
                onClick = {
                    if (key == "⌫") onBackspace() else onKeyPress(key)
                },
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.height(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = key, 
                        style = MaterialTheme.typography.titleLarge, 
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
