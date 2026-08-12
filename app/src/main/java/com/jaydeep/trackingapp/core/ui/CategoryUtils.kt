package com.jaydeep.trackingapp.core.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.jaydeep.trackingapp.ui.theme.TrackerColors

data class CategoryInfo(
    val name: String,
    val icon: ImageVector,
    val color: Color
)

object CategoryUtils {
    fun getExpenseCategoryInfo(category: String, colors: TrackerColors? = null): CategoryInfo {
        val catColors = colors?.categories
        return when (category.lowercase()) {
            "food" -> CategoryInfo("Food", Icons.Default.Restaurant, catColors?.food ?: Color(0xFFFF9800))
            "travel" -> CategoryInfo("Travel", Icons.Default.DirectionsCar, catColors?.travel ?: Color(0xFF2196F3))
            "shopping" -> CategoryInfo("Shopping", Icons.Default.ShoppingBag, catColors?.shopping ?: Color(0xFFE91E63))
            "bills" -> CategoryInfo("Bills", Icons.Default.Receipt, catColors?.bills ?: Color(0xFF607D8B))
            "health" -> CategoryInfo("Health", Icons.Default.MedicalServices, catColors?.health ?: Color(0xFFF44336))
            "entertainment" -> CategoryInfo("Entertainment", Icons.Default.Movie, catColors?.entertainment ?: Color(0xFF9C27B0))
            "education" -> CategoryInfo("Education", Icons.Default.School, catColors?.education ?: Color(0xFF795548))
            "subscription" -> CategoryInfo("Subscription", Icons.Default.Subscriptions, catColors?.subscription ?: Color(0xFF3F51B5))
            "salary", "income" -> CategoryInfo("Salary", Icons.Default.AccountBalance, catColors?.income ?: Color(0xFF4CAF50))
            else -> CategoryInfo("Other", Icons.Default.Category, catColors?.other ?: Color(0xFF9E9E9E))
        }
    }

    fun getProteinCategoryInfo(category: String, colors: TrackerColors? = null): CategoryInfo {
        val catColors = colors?.categories
        return when (category.lowercase()) {
            "chicken" -> CategoryInfo("Chicken", Icons.Default.Restaurant, catColors?.chicken ?: Color(0xFFFF5722))
            "egg" -> CategoryInfo("Egg", Icons.Default.Egg, catColors?.egg ?: Color(0xFFFFC107))
            "fish" -> CategoryInfo("Fish", Icons.Default.Pets, catColors?.fish ?: Color(0xFF03A9F4))
            "whey protein" -> CategoryInfo("Whey Protein", Icons.Default.FitnessCenter, catColors?.whey ?: Color(0xFF673AB7))
            "paneer" -> CategoryInfo("Paneer", Icons.Default.Restaurant, catColors?.paneer ?: Color(0xFFCDDC39))
            "milk" -> CategoryInfo("Milk", Icons.Default.WaterDrop, catColors?.milk ?: Color(0xFF2196F3))
            "dal/lentils", "dal" -> CategoryInfo("Dal", Icons.Default.Restaurant, catColors?.dal ?: Color(0xFF8BC34A))
            "soy" -> CategoryInfo("Soy", Icons.Default.Restaurant, catColors?.soy ?: Color(0xFF4CAF50))
            "nuts" -> CategoryInfo("Nuts", Icons.Default.Restaurant, catColors?.nuts ?: Color(0xFF795548))
            else -> CategoryInfo("Other", Icons.Default.Category, catColors?.other ?: Color(0xFF9E9E9E))
        }
    }
}
