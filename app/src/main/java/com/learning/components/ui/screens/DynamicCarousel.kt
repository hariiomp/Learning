package com.learning.components.ui.screens

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import kotlin.math.abs


data class DynamicCarouselItems(
    val id: String,
    val imageUrl: String
)


@Composable
fun DynamicCarouselScreen(
    modifier: Modifier = Modifier,
) {

    val items = remember {
        listOf(
            DynamicCarouselItems(
                id = "1",
                imageUrl = "https://i.pinimg.com/736x/64/15/cb/6415cb69f4651186cd0d6e55037da48f.jpg"
            ),
            DynamicCarouselItems(
                id = "2",
                imageUrl = "https://i.pinimg.com/1200x/fd/0c/44/fd0c44fd41b80385b1a21999a42195f9.jpg"
            ),
            DynamicCarouselItems(
                id = "3",
                imageUrl = "https://i.pinimg.com/736x/8d/ac/7d/8dac7dbbd3459e66b6ccf16ec106e082.jpg"
            ),
            DynamicCarouselItems(
                id = "4",
                imageUrl = "https://i.pinimg.com/736x/e3/b5/8e/e3b58ee8a9db80c230a7c228fb4b0c02.jpg"
            ),
            DynamicCarouselItems(
                id = "5",
                imageUrl = "https://i.pinimg.com/1200x/31/12/1d/31121d2058a5aa4da918d4eca3aec39f.jpg"
            ),
            DynamicCarouselItems(
                id = "6",
                imageUrl = "https://i.pinimg.com/1200x/98/15/32/98153206ed6bfa3a5f7acbd98aa451ea.jpg"
            )
        )
    }

    if (items.isEmpty()) return

    var currentIndex by remember {
        mutableIntStateOf(0)
    }

    var dragOffset by remember {
        mutableFloatStateOf(0f)
    }

    val density = LocalDensity.current
    val cardSpacingPx = with(density) {
        80.dp.toPx()
    }

    val dragProgress = ( dragOffset / cardSpacingPx ).coerceIn(-1f, 1f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()

            .pointerInput(items.size) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragOffset += dragAmount.x
                        dragOffset = dragOffset.coerceIn( -cardSpacingPx, cardSpacingPx )
                    },

                    onDragEnd = {
                        val swipeThreshold = cardSpacingPx * 0.35f
                        when {
                            dragOffset < -swipeThreshold -> {
                                currentIndex = nextIndex( currentIndex, items.size )
                            }
                            dragOffset > swipeThreshold -> {
                                currentIndex = previousIndex( currentIndex, items.size )
                            }
                        }
                        dragOffset = 0f
                    },

                    onDragCancel = { dragOffset = 0f }
                )
            },

        contentAlignment = Alignment.Center
    ) {

        for (relativePosition in -1..1) {

            val itemIndex = circularIndex(
                currentIndex + relativePosition,
                items.size
            )
            val item = items[itemIndex]
            DynamicCarouselCard(
                item = item,
                relativePosition = relativePosition,
                dragProgress = dragProgress
            )
        }
    }
}


@Composable
private fun DynamicCarouselCard(
    item: DynamicCarouselItems,
    relativePosition: Int,
    dragProgress: Float
) {
    val animatedPosition = relativePosition + dragProgress
    val horizontalOffset = animatedPosition * 60f
    val verticalOffset =
        if (relativePosition == 0) {
            0.dp
        } else {
            (-20).dp
        }

    val distance = abs(animatedPosition)
    val scale = (1f - distance * 0.08f).coerceIn(0.85f, 1f)
    val alpha = (1f - distance * 0.25f).coerceIn(0.7f, 1f)
    val rotation = (animatedPosition * 12f)
        .coerceIn(-12f, 12f)

    Card(
        modifier = Modifier
            .width(260.dp)
            .height(350.dp)
            .offset(
                x = horizontalOffset.dp,
                y = verticalOffset
            )
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
                rotationZ = rotation
                transformOrigin =
                    if (animatedPosition < 0f) {
                        TransformOrigin(1f, 0.5f)
                    } else {
                        TransformOrigin(0f, 0.5f)
                    }
            }
            .zIndex(
                10f - distance
            ),

        ) {
        AsyncImage(
            model = item.imageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}


private fun nextIndex(
    currentIndex: Int,
    size: Int
): Int {
    return (currentIndex + 1) % size
}


private fun previousIndex(
    currentIndex: Int,
    size: Int
): Int {
    return (currentIndex - 1 + size) % size
}


private fun circularIndex(
    index: Int,
    size: Int
): Int {
    return ((index % size) + size) % size
}

@Preview(showBackground = true)
@Composable
fun PreviewDynamicCarouselScreen() {
    DynamicCarouselScreen()
}