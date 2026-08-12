package com.jaydeep.trackingapp.features.protein.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.jaydeep.trackingapp.features.dashboard.EmptyState
import com.jaydeep.trackingapp.features.dashboard.ListItemCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProteinListScreen(
    onNavigateToEdit: (String?) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ProteinViewModel = hiltViewModel(),
) {
    val uiState by viewModel.listUiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Protein History") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding),
        ) {
            PullToRefreshBox(
                isRefreshing = uiState.isLoading,
                onRefresh = { viewModel.syncProteins() },
                modifier = Modifier.fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    if (uiState.proteins.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillParentMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                EmptyState("No protein entries yet", "Start tracking your protein intake")
                            }
                        }
                    } else {
                        items(uiState.proteins) { protein ->
                            ListItemCard(
                                title = protein.foodName,
                                subtitle = "Protein · ${protein.date}",
                                value = "${protein.proteinGrams.toInt()} g",
                                category = protein.foodName,
                                isProtein = true,
                                onClick = { onNavigateToEdit(protein.id.toString()) }
                            )
                        }
                    }
                }
            }
        }
    }
}
