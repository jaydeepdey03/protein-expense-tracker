package com.jaydeep.trackingapp.features.status

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.jaydeep.trackingapp.core.data.remote.dto.ExpenseEntryResponse
import com.jaydeep.trackingapp.core.data.remote.dto.ProteinEntryResponse
import com.jaydeep.trackingapp.data.model.TransactionType
import com.jaydeep.trackingapp.ui.components.TransactionItemRow
import com.jaydeep.trackingapp.ui.theme.LocalTrackerColors
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    viewModel: TransactionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Expenses", "Protein Logs")
    var showDatePicker by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Date Selection Header
        DateHeader(
            selectedDate = uiState.selectedDate,
            onClick = { showDatePicker = true }
        )

        SecondaryScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            edgePadding = 0.dp,
            divider = {}
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            when (selectedTab) {
                0 -> ExpenseEntryList(uiState.expenseEntries)
                1 -> ProteinEntryList(uiState.proteinEntries)
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis <= System.currentTimeMillis()
                }
            }
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val date = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                        viewModel.onDateSelected(date)
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun DateHeader(selectedDate: LocalDate, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Selected Date",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = selectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy")),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Icon(
            imageVector = Icons.Default.CalendarMonth,
            contentDescription = "Pick Date",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ExpenseEntryList(entries: List<ExpenseEntryResponse>) {
    if (entries.isEmpty()) {
        EmptyState("No expenses found for this date")
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(entries) { entry ->
                TransactionItemRow(
                    icon = Icons.Default.Savings,
                    iconBgColor = LocalTrackerColors.current.expense.copy(alpha = 0.2f),
                    title = entry.description,
                    subtitle = entry.category,
                    amount = "₹${entry.amount}",
                    type = TransactionType.EXPENSE
                )
            }
        }
    }
}

@Composable
fun ProteinEntryList(entries: List<ProteinEntryResponse>) {
    if (entries.isEmpty()) {
        EmptyState("No protein logs found for this date")
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(entries) { entry ->
                TransactionItemRow(
                    icon = Icons.Default.Restaurant,
                    iconBgColor = LocalTrackerColors.current.protein.copy(alpha = 0.2f),
                    title = entry.foodName,
                    subtitle = "${entry.gramsConsumed}g consumed",
                    amount = "${entry.proteinGrams}g",
                    type = TransactionType.PROTEIN
                )
            }
        }
    }
}

@Composable
fun EmptyState(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = message, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
