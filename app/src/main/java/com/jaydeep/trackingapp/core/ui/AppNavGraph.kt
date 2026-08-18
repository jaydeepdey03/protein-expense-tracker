package com.jaydeep.trackingapp.core.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jaydeep.trackingapp.features.auth.ui.LoginScreen
import com.jaydeep.trackingapp.features.dashboard.DashboardScreen
import com.jaydeep.trackingapp.features.expenses.ui.ExpenseEditScreen
import com.jaydeep.trackingapp.features.expenses.ui.ExpenseListScreen
import com.jaydeep.trackingapp.features.protein.ui.ProteinEditScreen
import com.jaydeep.trackingapp.features.protein.ui.ProteinListScreen
import com.jaydeep.trackingapp.features.status.AnalyticsScreen
import com.jaydeep.trackingapp.features.status.HealthStatusScreen
import com.jaydeep.trackingapp.features.status.TransactionScreen
import com.jaydeep.trackingapp.ui.screens.ProfileScreen

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screens.Login.route,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val mainRoutes = listOf(
        Screens.Dashboard.route,
        Screens.Status.route,
        Screens.Summary.route,
        Screens.Profile.route,
    )

    val showBottomBar = currentRoute in mainRoutes

    if (showBottomBar) {
        MainScreen(navController = navController) {
            NavContent(navController, modifier, startDestination)
        }
    } else {
        NavContent(navController, modifier, startDestination)
    }
}

@Composable
fun NavContent(
    navController: NavHostController,
    modifier: Modifier,
    startDestination: String,
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
                onNavigateToExpenseEdit = { id ->
                    navController.navigate(Screens.ExpenseEdit.createRoute(id))
                },
                onNavigateToProteinEdit = { id ->
                    navController.navigate(Screens.ProteinEdit.createRoute(id))
                }
            )
        }

        // ── Status (Transactions) ───────────────────────────────────────────
        composable(Screens.Status.route) {
            TransactionScreen()
        }

        // ── Analytics (Summary) ──────────────────────────────────────────────
        composable(Screens.Summary.route) {
            AnalyticsScreen()
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

        // ── Profile ──────────────────────────────────────────────────────────
        composable(Screens.Profile.route) {
            ProfileScreen(
                onNavigateToHealthStatus = { navController.navigate(Screens.HealthStatus.route) },
                onLogout = {
                    navController.navigate(Screens.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screens.Settings.route) {
            ProfileScreen(
                onNavigateToHealthStatus = { navController.navigate(Screens.HealthStatus.route) },
                onLogout = {
                    navController.navigate(Screens.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screens.HealthStatus.route) {
            HealthStatusScreen(
                onNavigateBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Screens.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
