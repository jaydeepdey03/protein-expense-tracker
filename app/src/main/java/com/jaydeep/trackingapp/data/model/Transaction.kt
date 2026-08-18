package com.jaydeep.trackingapp.data.model

import androidx.compose.ui.graphics.Color
import java.time.LocalDate

data class Transaction(
    val id: String,
    val title: String,
    val subtitle: String,
    val amount: Double,
    val type: TransactionType,
    val category: Category,
    val date: LocalDate,
    val iconRes: Int? = null
)

enum class TransactionType { EXPENSE, PROTEIN }

enum class Category(val displayName: String, val color: Color) {
    SHOPPING("Shopping", Color(0xFF3B82F6)),
    FOOD_DRINK("Food & Drink", Color(0xFF22C55E)),
    SUBSCRIPTION("Subscription", Color(0xFFF59E0B)),
    EDUCATION("Education", Color(0xFFEF4444)),
    OTHERS("Others", Color(0xFF9CA3AF))
}

data class Bill(
    val title: String,
    val dueDate: LocalDate,
    val amount: Double,
    val iconRes: Int? = null
)
