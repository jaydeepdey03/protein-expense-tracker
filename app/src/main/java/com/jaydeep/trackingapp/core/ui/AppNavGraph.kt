package com.jaydeep.trackingapp.core.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jaydeep.trackingapp.features.auth.ui.LoginScreen
import com.jaydeep.trackingapp.features.dashboard.DashboardScreen
import com.jaydeep.trackingapp.features.expenses.ui.ExpenseEditScreen
import com.jaydeep.trackingapp.features.expenses.ui.ExpenseListScreen
import com.jaydeep.trackingapp.features.protein.ui.ProteinEditScreen
import com.jaydeep.trackingapp.features.protein.ui.ProteinListScreen
import com.jaydeep.trackingapp.features.status.StatusScreen

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screens.Login.route,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {

        // ── Auth ─────────────────────────────────────────────────────────────
        composable(Screens.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screens.Dashboard.route) {
                        popUpTo(Screens.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Dashboard ────────────────────────────────────────────────────────
        composable(Screens.Dashboard.route) {
            DashboardScreen(
                onNavigateToExpenses = { navController.navigate(Screens.ExpenseList.route) },
                onNavigateToProtein  = { navController.navigate(Screens.ProteinList.route) },
                onNavigateToStatus   = { navController.navigate(Screens.Status.route) },
                onNavigateToSettings = { navController.navigate(Screens.Settings.route) },
                onLogout = {
                    navController.navigate(Screens.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ── Status ───────────────────────────────────────────────────────────
        composable(Screens.Status.route) {
            StatusScreen(
                onNavigateToExpenses = { navController.navigate(Screens.ExpenseList.route) },
                onNavigateToProtein  = { navController.navigate(Screens.ProteinList.route) },
                onNavigateToSummary  = { navController.navigate(Screens.Dashboard.route) },
                onNavigateToSettings = { navController.navigate(Screens.Settings.route) },
                onLogout = {
                    navController.navigate(Screens.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ── Expenses ──────────────────────────────────────────────────────────
        composable(Screens.ExpenseList.route) {
            ExpenseListScreen(
                onNavigateToEdit = { id -> 
                    if (id == null) navController.navigate(Screens.ExpenseCreate.route)
                    else navController.navigate(Screens.ExpenseEdit.createRoute(id))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screens.ExpenseCreate.route) {
            ExpenseEditScreen(
                expenseId = null,
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable(
            route = Screens.ExpenseEdit.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType }),
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            ExpenseEditScreen(
                expenseId = id,
                onNavigateBack = { navController.popBackStack() },
            )
        }

        // ── Protein ───────────────────────────────────────────────────────────
        composable(Screens.ProteinList.route) {
            ProteinListScreen(
                onNavigateToEdit = { id ->
                    if (id == null) navController.navigate(Screens.ProteinCreate.route)
                    else navController.navigate(Screens.ProteinEdit.createRoute(id))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screens.ProteinCreate.route) {
            ProteinEditScreen(
                proteinId = null,
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable(
            route = Screens.ProteinEdit.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType }),
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            ProteinEditScreen(
                proteinId = id,
                onNavigateBack = { navController.popBackStack() },
            )
        }

        // ── Settings ──────────────────────────────────────────────────────────
        composable(Screens.Settings.route) {
            // Placeholder for Settings
            androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("Settings Screen (Coming Soon)")
            }
        }
    }
}