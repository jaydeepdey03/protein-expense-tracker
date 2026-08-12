package com.jaydeep.trackingapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DonutChart(
    data: List<Pair<Color, Float>>,
    totalLabel: String,
    amountLabel: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            var startAngle = -90f
            val strokeWidth = 32.dp.toPx()
            val gapAngle = 2f

            data.forEach { (color, fraction) ->
                val sweepAngle = fraction * 360f
                if (sweepAngle > gapAngle) {
                    drawArc(
                        color = color,
                        startAngle = startAngle + gapAngle / 2,
                        sweepAngle = sweepAngle - gapAngle,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
                startAngle += sweepAngle
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(amountLabel, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(totalLabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun SpendBar(
    data: List<Pair<Color, Float>>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.height(10.dp)) {
        var currentX = 0f
        val gap = 4.dp.toPx()
        val totalGaps = (data.size - 1) * gap
        val availableWidth = size.width - totalGaps

        data.forEachIndexed { index, (color, fraction) ->
            val barWidth = fraction * availableWidth
            if (barWidth > 0) {
                drawRoundRect(
                    color = color,
                    topLeft = Offset(currentX, 0f),
                    size = Size(barWidth, size.height),
                    cornerRadius = CornerRadius(size.height / 2, size.height / 2)
                )
                currentX += barWidth + gap
            }
        }
    }
}

@Composable
fun BarChart(
    data: List<Float>,
    labels: List<String>,
    selectedIndex: Int,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val selectedColor = MaterialTheme.colorScheme.onBackground
    
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val barWidth = (maxWidth - (16.dp * (data.size - 1))) / data.size
        
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            data.forEachIndexed { index, fraction ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Canvas(modifier = Modifier
                        .width(barWidth)
                        .height(150.dp * fraction)
                    ) {
                        drawRoundRect(
                            color = if (index == selectedIndex) selectedColor else primaryColor,
                            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                        )
                    }
                    Text(
                        labels[index],
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
