package com.jaydeep.trackingapp.features.status

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.jaydeep.trackingapp.ui.theme.LocalTrackerColors

@Composable
fun StatusScreen(
    viewModel: StatusViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Today's Status",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Your tracking progress for today",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Protein Status
        StatusProgressCard(
            title = "Protein Intake",
            consumed = uiState.todayProteinConsumed,
            goal = uiState.proteinGoal,
            unit = "g",
            color = LocalTrackerColors.current.protein,
            icon = Icons.Default.TrackChanges
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Expense Status
        StatusProgressCard(
            title = "Monthly Budget",
            consumed = uiState.monthlyExpenseTotal,
            goal = uiState.monthlyBudget,
            unit = "₹",
            color = LocalTrackerColors.current.expense,
            icon = Icons.Default.ElectricBolt
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Insights Section
        Text(
            text = "Daily Insights",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))

        InsightCard(
            message = if (uiState.todayProteinConsumed >= uiState.proteinGoal) 
                "You've reached your protein goal! Great job." 
            else 
                "You need ${(uiState.proteinGoal - uiState.todayProteinConsumed).toInt()}g more protein today.",
            icon = Icons.Default.Info
        )

        Spacer(modifier = Modifier.height(12.dp))

        InsightCard(
            message = if (uiState.monthlyExpenseTotal > uiState.monthlyBudget)
                "You've exceeded your monthly budget."
            else
                "You have ₹${(uiState.monthlyBudget - uiState.monthlyExpenseTotal).toInt()} remaining for this month.",
            icon = Icons.Default.Info
        )
    }
}

@Composable
fun StatusProgressCard(
    title: String,
    consumed: Double,
    goal: Double,
    unit: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    val progress = (consumed / goal).toFloat().coerceIn(0f, 1f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(color.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (unit == "₹") "₹${consumed.toInt()}" else "${consumed.toInt()} $unit",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = color
                )
                Text(
                    text = if (unit == "₹") "Goal: ₹${goal.toInt()}" else "Goal: ${goal.toInt()} $unit",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = color,
                trackColor = color.copy(alpha = 0.1f),
                strokeCap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun InsightCard(message: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = message, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}
