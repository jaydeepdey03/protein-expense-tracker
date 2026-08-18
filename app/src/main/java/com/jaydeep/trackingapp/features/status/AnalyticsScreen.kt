package com.jaydeep.trackingapp.features.status

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.jaydeep.trackingapp.ui.components.BarChart
import com.jaydeep.trackingapp.ui.theme.LocalTrackerColors

@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Text(
                text = "Weekly Analytics",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(24.dp))

            AnalyticsCard(
                title = "Expense Trends",
                data = uiState.weeklyExpenses,
                labels = uiState.labels,
                color = LocalTrackerColors.current.expense,
                unit = "₹"
            )

            Spacer(modifier = Modifier.height(24.dp))

            AnalyticsCard(
                title = "Protein Trends",
                data = uiState.weeklyProteins,
                labels = uiState.labels,
                color = LocalTrackerColors.current.protein,
                unit = "g"
            )
        }
    }
}

@Composable
fun AnalyticsCard(
    title: String,
    data: List<Double>,
    labels: List<String>,
    color: Color,
    unit: String
) {
    val maxVal = data.maxOrNull()?.takeIf { it > 0 } ?: 1.0
    val fractions = data.map { (it / maxVal).toFloat() }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(20.dp))
            
            BarChart(
                data = fractions,
                labels = labels,
                selectedIndex = 6,
                modifier = Modifier.height(180.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "Total this week",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (unit == "₹") "₹${data.sum().toInt()}" else "${data.sum().toInt()} $unit",
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
        }
    }
}
