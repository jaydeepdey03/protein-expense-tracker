package com.jaydeep.trackingapp.data.model

import java.time.LocalDate

object SampleData {
    val transactions = listOf(
        Transaction(
            id = "1",
            title = "Adobe Illustrator",
            subtitle = "Subscription",
            amount = 32.00,
            type = TransactionType.EXPENSE,
            category = Category.SUBSCRIPTION,
            date = LocalDate.now()
        ),
        Transaction(
            id = "2",
            title = "Salary",
            subtitle = "Monthly Income",
            amount = 1980.00,
            type = TransactionType.INCOME,
            category = Category.OTHERS,
            date = LocalDate.now()
        ),
        Transaction(
            id = "3",
            title = "Starbucks",
            subtitle = "Coffee",
            amount = 5.50,
            type = TransactionType.EXPENSE,
            category = Category.FOOD_DRINK,
            date = LocalDate.now().minusDays(1)
        )
    )

    val bills = listOf(
        Bill("Indihome", LocalDate.now().plusDays(3), 120.00),
        Bill("PLN", LocalDate.now().plusDays(5), 120.00),
        Bill("Telkomsel", LocalDate.now().plusDays(7), 50.00)
    )
}
