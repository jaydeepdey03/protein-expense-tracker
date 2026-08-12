package com.jaydeep.trackingapp.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jaydeep.trackingapp.features.auth.ui.LoginScreen
import com.jaydeep.trackingapp.features.dashboard.DashboardScreen

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

        // ── Dashboard ─────────────────────────────────────────────────────────
        // ── Dashboard ─────────────────────────────────────────────────────────
        composable(Screens.Dashboard.route) {
            DashboardScreen(
                onNavigateToExpenses = { navController.navigate(Screens.ExpenseList.route) },
                onNavigateToProtein  = { navController.navigate(Screens.ProteinList.route) },
                onNavigateToSummary  = { navController.navigate(Screens.Summary.route) },
                onNavigateToSettings = { navController.navigate(Screens.Settings.route) },
                onLogout = {
                    navController.navigate(Screens.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ── Expenses ──────────────────────────────────────────────────────────
//        composable(Screens.ExpenseList.route) {
//            ExpenseListScreen(
//                onNavigateToCreate = { navController.navigate(Screens.ExpenseCreate.route) },
//                onNavigateToDetail = { id -> navController.navigate(Screens.ExpenseDetail.createRoute(id)) },
//            )
//        }
//
//        composable(Screens.ExpenseCreate.route) {
//            ExpenseEditScreen(
//                existingExpenseId = null,
//                onNavigateBack = { navController.popBackStack() },
//            )
//        }

//        composable(
//            route = Screens.ExpenseDetail.route,
//            arguments = listOf(navArgument("id") { type = NavType.StringType }),
//        ) { backStackEntry ->
//            val id = backStackEntry.arguments?.getString("id")!!
//            ExpenseDetailScreen(
//                expenseId = id,
//                onNavigateBack = { navController.popBackStack() },
//                onNavigateToEdit = { navController.navigate(Screens.ExpenseEdit.createRoute(id)) },
//            )
//        }

//        composable(
//            route = Screens.ExpenseEdit.route,
//            arguments = listOf(navArgument("id") { type = NavType.StringType }),
//        ) { backStackEntry ->
//            val id = backStackEntry.arguments?.getString("id")!!
//            ExpenseEditScreen(
//                existingExpenseId = id,
//                onNavigateBack = { navController.popBackStack() },
//            )
//        }

        // ── Protein ───────────────────────────────────────────────────────────
//        composable(Screens.ProteinList.route) {
//            ProteinListScreen(
//                onNavigateToCreate = { navController.navigate(Screens.ProteinCreate.route) },
//                onNavigateToEdit   = { id -> navController.navigate(Screens.ProteinEdit.createRoute(id)) },
//            )
//        }
//
//        composable(Screens.ProteinCreate.route) {
//            ProteinEditScreen(
//                existingId = null,
//                onNavigateBack = { navController.popBackStack() },
//            )
//        }
//
//        composable(
//            route = Screens.ProteinEdit.route,
//            arguments = listOf(navArgument("id") { type = NavType.StringType }),
//        ) { backStackEntry ->
//            val id = backStackEntry.arguments?.getString("id")!!
//            ProteinEditScreen(
//                existingId = id,
//                onNavigateBack = { navController.popBackStack() },
//            )
//        }

        // ── Summary ───────────────────────────────────────────────────────────
//        composable(Screens.Summary.route) {
//            SummaryScreen(
//                onNavigateBack = { navController.popBackStack() },
//            )
//        }
//
//        // ── Settings ──────────────────────────────────────────────────────────
//        composable(Screens.Settings.route) {
//            SettingsScreen(
//                onLogout = {
//                    navController.navigate(Screens.Login.route) {
//                        popUpTo(0) { inclusive = true }
//                    }
//                }
//            )
//        }
    }
}