package com.jaydeep.trackingapp.core.ui

sealed class Screens(val route : String) {
    // ── Auth ─────────────────────────────────────────────────────────────────
    data object Login : Screens("login")

    // ── Main ─────────────────────────────────────────────────────────────────
    data object Dashboard : Screens("dashboard")
    data object Status : Screens("status")
    // ── Expenses ──────────────────────────────────────────────────────────────
    data object ExpenseList : Screens("expenses")
    data object ExpenseCreate : Screens("expenses/create")

    data object ExpenseDetail : Screens("expenses/{id}") {
        fun createRoute(id: String) = "expenses/$id"
    }

    data object ExpenseEdit : Screens("expenses/{id}/edit") {
        fun createRoute(id: String) = "expenses/$id/edit"
    }

    // ── Protein ───────────────────────────────────────────────────────────────
    data object ProteinList : Screens("protein")
    data object ProteinCreate : Screens("protein/create")

    data object ProteinEdit : Screens("protein/{id}/edit") {
        fun createRoute(id: String) = "protein/$id/edit"
    }

    // ── Summary ───────────────────────────────────────────────────────────────
    data object Summary : Screens("summary")

    // ── Settings ──────────────────────────────────────────────────────────────
    data object Settings : Screens("settings")
    data object Profile : Screens("profile")
    data object HealthStatus : Screens("health_status")

}