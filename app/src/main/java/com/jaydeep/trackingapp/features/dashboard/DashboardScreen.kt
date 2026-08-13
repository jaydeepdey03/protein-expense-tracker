package com.jaydeep.trackingapp.features.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.jaydeep.trackingapp.core.ui.CategoryUtils
import com.jaydeep.trackingapp.core.ui.MainScreen
import com.jaydeep.trackingapp.ui.theme.LocalTrackerColors
import com.jaydeep.trackingapp.ui.theme.TrackingAppTheme
import androidx.navigation.compose.rememberNavController

@Composable
fun DashboardScreen(
    onNavigateToExpenseEdit: (String) -> Unit,
    onNavigateToProteinEdit: (String) -> Unit,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    
    DashboardContent(
        uiState = uiState,
        onRefresh = { viewModel.refresh() },
        onNavigateToExpenseEdit = onNavigateToExpenseEdit,
        onNavigateToProteinEdit = onNavigateToProteinEdit
    )
}

@Composable
fun DashboardContent(
    uiState: DashboardUiState,
    onRefresh: () -> Unit,
    onNavigateToExpenseEdit: (String) -> Unit,
    onNavigateToProteinEdit: (String) -> Unit,
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    PullToRefreshBox(
        isRefreshing = uiState.isLoading,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                DashboardHeader(uiState.userName, uiState.today)
            }

            item {
                ProgressSummaryCard(
                    proteinConsumed = uiState.todayProteinTotal,
                    proteinGoal = uiState.dailyProteinGoal.toDouble(),
                    expenseTotal = uiState.monthlyExpenseTotal,
                    expenseBudget = uiState.monthlyExpenseBudget.toDouble()
                )
            }

            item {
                SecondaryTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary,
                    divider = {}
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                "Protein",
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                "Expenses",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            if (selectedTab == 0) {
                if (uiState.recentProteins.isEmpty()) {
                    item {
                        Box(modifier = Modifier.height(200.dp), contentAlignment = Alignment.Center) {
                            EmptyState("No protein entries yet", "Start tracking your protein intake")
                        }
                    }
                } else {
                    items(uiState.recentProteins) { item ->
                        ListItemCard(
                            title = item.foodName,
                            subtitle = "Protein · ${item.date}",
                            value = "${item.proteinGrams.toInt()} g",
                            category = item.foodName,
                            isProtein = true,
                            onClick = { onNavigateToProteinEdit(item.id.toString()) }
                        )
                    }
                }
            } else {
                if (uiState.recentExpenses.isEmpty()) {
                    item {
                        Box(modifier = Modifier.height(200.dp), contentAlignment = Alignment.Center) {
                            EmptyState("No expenses yet", "Start tracking your spending")
                        }
                    }
                } else {
                    items(uiState.recentExpenses) { item ->
                        ListItemCard(
                            title = item.title,
                            subtitle = "${item.category} · ${item.date}",
                            value = "₹${item.amount.toInt()}",
                            category = item.category,
                            isProtein = false,
                            onClick = { onNavigateToExpenseEdit(item.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardHeader(userName: String, date: String) {
    val greeting = when (java.time.LocalTime.now().hour) {
        in 0..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        else -> "Good evening"
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = greeting,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = userName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = date,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ProgressSummaryCard(
    proteinConsumed: Double,
    proteinGoal: Double,
    expenseTotal: Double,
    expenseBudget: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressWithLabel(
                progress = (proteinConsumed / proteinGoal).toFloat(),
                label = "Protein",
                valueText = "${proteinConsumed.toInt()}g / ${proteinGoal.toInt()}g",
                color = LocalTrackerColors.current.protein
            )
            CircularProgressWithLabel(
                progress = (expenseTotal / expenseBudget).toFloat(),
                label = "Expense",
                valueText = "₹${expenseTotal.toInt()} / ₹${expenseBudget.toInt()}",
                color = LocalTrackerColors.current.expense
            )
        }
    }
}

@Composable
fun CircularProgressWithLabel(
    progress: Float,
    label: String,
    valueText: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { 1f },
                modifier = Modifier.size(100.dp),
                color = color.copy(alpha = 0.1f),
                strokeWidth = 8.dp,
                strokeCap = StrokeCap.Round
            )
            CircularProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier.size(100.dp),
                color = color,
                strokeWidth = 8.dp,
                strokeCap = StrokeCap.Round
            )
            Text(
                modifier = Modifier.padding(8.dp),
                text = "${"%.2f".format(progress * 100)}%",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = label, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
        Text(text = valueText, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun ListItemCard(
    title: String,
    subtitle: String,
    value: String,
    category: String,
    isProtein: Boolean,
    onDelete: (() -> Unit)? = null,
    onClick: () -> Unit
) {
    val trackerColors = LocalTrackerColors.current
    val categoryInfo = if (isProtein) {
        CategoryUtils.getProteinCategoryInfo(category, trackerColors)
    } else {
        CategoryUtils.getExpenseCategoryInfo(category, trackerColors)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(categoryInfo.color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = categoryInfo.icon,
                    contentDescription = null,
                    tint = categoryInfo.color,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                Text(text = subtitle, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(text = value, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
            
            if (onDelete != null) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyState(title: String, subtitle: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
        Text(text = subtitle, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DashboardWithBottomNavPreview() {
    val sampleState = DashboardUiState(
        userName = "Jaydeep Dey",
        today = "Thursday, 13 August",
        dailyProteinGoal = 120f,
        monthlyExpenseBudget = 25000f,
        todayProteinTotal = 85.0,
        monthlyExpenseTotal = 12500.0,
        recentExpenses = listOf(
            RecentExpenseItem("1", "Grocery", "Food", 500.0, "INR", "2026-08-13"),
            RecentExpenseItem("2", "Fuel", "Transport", 2000.0, "INR", "2026-08-12")
        ),
        recentProteins = listOf(
            RecentProteinItem(1, "Chicken Breast", 30.0, "2026-08-13"),
            RecentProteinItem(2, "Whey Protein", 25.0, "2026-08-13")
        )
    )
    val navController = rememberNavController()
    TrackingAppTheme {
        MainScreen(navController = navController) {
            DashboardContent(
                uiState = sampleState,
                onRefresh = {},
                onNavigateToExpenseEdit = {},
                onNavigateToProteinEdit = {}
            )
        }
    }
}
