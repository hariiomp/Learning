package com.learning.components.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@Composable
fun ButtonsScreen() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        SwipeToConfirm(
            confirmationThreshold = 0.8f,
            onConfirm = {
                println("Confirmed!")
            }
        )
    }
}


@Composable
fun SwipeToConfirm(
    modifier: Modifier = Modifier,
    confirmationThreshold: Float = 0.8f,
    onConfirm: () -> Unit = {}
) {

    val offsetX = remember {
        Animatable(0f)
    }

    val thumbScale = remember {
        Animatable(1f)
    }

    var trackWidth by remember {
        mutableIntStateOf(0)
    }

    var confirmed by remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    val thumbSize = 70.dp
    val trackPadding = 4.dp

    val thumbSizePx = with(density) {
        thumbSize.toPx()
    }

    val maxOffset = (
            trackWidth - thumbSizePx
            ).coerceAtLeast(0f)

    val progress = if (maxOffset > 0f) {
        (offsetX.value / maxOffset)
            .coerceIn(0f, 1f)
    } else {
        0f
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .padding(trackPadding)
            .clip(CircleShape)
            .background(Color(0xFF4527A0))
            .onSizeChanged { size ->
                trackWidth = size.width
            },
        contentAlignment = Alignment.CenterStart
    ) {

        Text(
            text = if (confirmed) {
                "Confirmed"
            } else {
                "${(progress * 100).roundToInt()}%"
            },
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )


        Box(
            modifier = Modifier
                .size(thumbSize)
                .offset {
                    IntOffset(
                        x = offsetX.value.roundToInt(),
                        y = 0
                    )
                }

                .graphicsLayer {
                    scaleX = thumbScale.value
                    scaleY = thumbScale.value
                }

                .padding(2.dp)
                .clip(CircleShape)
                .background(Color.White)

                .pointerInput(
                    maxOffset,
                    confirmed
                ) {

                    if (!confirmed) {
                        detectDragGestures(
                            onDragStart = {
                                scope.launch {
                                    thumbScale.animateTo(
                                        targetValue = 1.25f
                                    )
                                }
                            },

                            onDragEnd = {
                                scope.launch {
                                    val currentProgress =
                                        if (maxOffset > 0f) {
                                            offsetX.value / maxOffset
                                        } else {
                                            0f
                                        }

                                    if ( currentProgress >= confirmationThreshold) {

                                        offsetX.animateTo(
                                            maxOffset
                                        )

                                        thumbScale.animateTo(
                                            1f
                                        )

                                        confirmed = true
                                        onConfirm()

                                    } else {
                                        offsetX.animateTo(0f)
                                        thumbScale.animateTo(
                                            1f
                                        )
                                    }
                                }
                            }

                        ) { change, dragAmount ->
                            change.consume()
                            scope.launch {
                                offsetX.snapTo(
                                    (
                                            offsetX.value +
                                                    dragAmount.x
                                            ).coerceIn(
                                            0f,
                                            maxOffset
                                        )
                                )
                            }
                        }
                    }
                },

            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Slide to confirm"
            )
        }
    }
}




