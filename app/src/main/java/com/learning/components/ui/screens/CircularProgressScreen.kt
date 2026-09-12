package com.learning.components.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.animation.core.Animatable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.learning.components.navigation.Screen
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CircularProgressScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF100A0A)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgress(
            modifier = Modifier.size(400.dp),
            maxValue = 1000,
            durationMs = 4000
        )
    }
}

@Composable
fun CircularProgress(
    modifier: Modifier = Modifier,
    maxValue: Int = 1000,
    durationMs: Int = 1000
) {
    val scope = rememberCoroutineScope()
    val progress = remember { Animatable(0f) }
    val interactionSource = remember { MutableInteractionSource() }

    val startAngle = 90f
    val sweepAngle = 360f
    val totalDots = 28

    Box(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                scope.launch {
                    if (progress.value >= 1f) {
                        progress.snapTo(0f)
                    }
                    progress.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(
                            durationMillis = durationMs,
                            easing = LinearEasing
                        )
                    )
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val outerRadius = size.width * 0.42f
            val innerRadius = size.width * 0.34f

            val currentProgress = progress.value
            val dotRadius = 2.dp.toPx()

            for (i in 0 until totalDots) {
                val fraction = i.toFloat() / (totalDots - 1)
                val angleDeg = startAngle + (fraction * sweepAngle)
                val angleRad = Math.toRadians(angleDeg.toDouble())

                val x = center.x + outerRadius * cos(angleRad).toFloat()
                val y = center.y + outerRadius * sin(angleRad).toFloat()

                val isActive = fraction <= currentProgress
                val color = if (isActive) Color(0xFFDFE982) else Color(0xFFC6D2D7)

                drawCircle(
                    color = color,
                    radius = dotRadius,
                    center = Offset(x, y)
                )
            }

            val strokeWidth = 2.dp.toPx()
            val arcSize = Size(innerRadius * 2, innerRadius * 2)
            val arcTopLeft = Offset(center.x - innerRadius, center.y - innerRadius)

            drawArc(
                color = Color(0x0DF1F1F8),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = arcTopLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth)
            )

            val activeSweep = sweepAngle * currentProgress
            drawArc(
                color = Color(0xFFE8CC33),
                startAngle = startAngle,
                sweepAngle = activeSweep,
                useCenter = false,
                topLeft = arcTopLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth)
            )


            val currentAngleRad = Math.toRadians((startAngle + activeSweep).toDouble())
            val thumbX = center.x + innerRadius * cos(currentAngleRad).toFloat()
            val thumbY = center.y + innerRadius * sin(currentAngleRad).toFloat()

            drawCircle(
                color = Color(0xFFC9ACAC),
                radius = 2.dp.toPx(),
                center = Offset(thumbX, thumbY)
            )

            drawCircle(
                color = Color(0xFFD9A4A4),
                radius = 8.dp.toPx(),
                center = Offset(thumbX, thumbY)
            )
        }


        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val currentValue = (progress.value * maxValue).toInt()
            Text(
                text = "$currentValue",
                color = Color(0xFFF9A825),
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Wh",
                color = Color(0xFF8A929A),
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CircularProgressScreenPreview() {
    CircularProgressScreen()
}