package com.learning.components.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import kotlinx.coroutines.delay

@Composable
fun MyCardFly() {
    CardFly()
}
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun CardFly() {

    var isFlying by remember {
        mutableStateOf(false)
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight

        FlyCard(
            isFlying = isFlying,
            screenWidth = screenWidth,
            screenHeight = screenHeight,
            onClick = {
                isFlying = !isFlying
            }
        )
    }
}


@Composable
fun FlyCard(
    isFlying: Boolean,
    screenWidth: Dp,
    screenHeight: Dp,
    onClick: () -> Unit
) {

    var shouldMove by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(isFlying) {

        if (isFlying) {
            shouldMove = false
            delay(500)
            shouldMove = true

        } else {
            shouldMove = false
        }
    }


    val cardWidth = 90.dp
    val cardHeight = 90.dp
    val margin = 20.dp

    val startX = (screenWidth - cardWidth) / 2
    val startY = screenHeight - cardHeight

    val targetX = screenWidth - cardWidth - margin
    val targetY = margin

    val offsetXTarget = targetX - startX
    val offsetYTarget = targetY - startY

    val offsetX by animateDpAsState(
        targetValue = if (shouldMove) offsetXTarget else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "offsetX"
    )

    val offsetY by animateDpAsState(
        targetValue = if (shouldMove) offsetYTarget else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "offsetY"
    )
    val scale by animateFloatAsState(
        targetValue = if (isFlying) 1.08f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    val elevation by animateDpAsState(
        targetValue = if (isFlying) 24.dp else 4.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "elevation"
    )

    ImageCard(
        modifier = Modifier.offset(
            x = offsetX,
            y = offsetY
        )
        .scale(scale),
        elevation = elevation,
        onClick = onClick
    )
}


@Composable
fun ImageCard(
    modifier: Modifier = Modifier,
    elevation: Dp = 8.dp,
    onClick: () -> Unit
) {

    Card(
        modifier = modifier
            .size(90.dp, 90.dp)
            .clip(RoundedCornerShape(100.dp))
            .clickable {
                onClick()
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation
        )
    ) {

        AsyncImage(
            model = "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee",
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}