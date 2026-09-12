package com.learning.components.ui.screens

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import kotlin.math.abs


data class CarouselItems(
    val id: String,
    val imageUrl: String
)


@Composable
fun CardCarousel(
    modifier: Modifier = Modifier,
) {

    val items = remember {
        listOf(
            CarouselItems(
                id = "1",
                imageUrl = "https://i.pinimg.com/736x/64/15/cb/6415cb69f4651186cd0d6e55037da48f.jpg"
            ),
            CarouselItems(
                id = "2",
                imageUrl = "https://i.pinimg.com/1200x/fd/0c/44/fd0c44fd41b80385b1a21999a42195f9.jpg"
            ),
            CarouselItems(
                id = "3",
                imageUrl = "https://i.pinimg.com/736x/8d/ac/7d/8dac7dbbd3459e66b6ccf16ec106e082.jpg"
            ),
            CarouselItems(
                id = "4",
                imageUrl = "https://i.pinimg.com/736x/e3/b5/8e/e3b58ee8a9db80c230a7c228fb4b0c02.jpg"
            ),
            CarouselItems(
                id = "5",
                imageUrl = "https://i.pinimg.com/1200x/31/12/1d/31121d2058a5aa4da918d4eca3aec39f.jpg"
            ),
            CarouselItems(
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
            .height(400.dp)

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

        contentAlignment = Alignment.BottomCenter
    ) {

        for (relativePosition in -1..1) {

            val itemIndex = circularIndex(
                currentIndex + relativePosition,
                items.size
            )
            val item = items[itemIndex]
            CarouselCard(
                item = item,
                relativePosition = relativePosition,
                dragProgress = dragProgress
            )
        }
    }
}


@Composable
private fun CarouselCard(
    item: CarouselItems,
    relativePosition: Int,
    dragProgress: Float
) {
    val animatedPosition = relativePosition + dragProgress
    val horizontalOffset = animatedPosition * 70f
    val distance = abs(animatedPosition)
    val scale = (1f - (distance * 0.1f)).coerceIn(0.8f, 1f)
    val alpha = (1f - (distance * 0.2f)).coerceIn(0.6f, 1f)

    Card(
        modifier = Modifier
            .width(260.dp)
            .height(350.dp)
            .offset( x = horizontalOffset.dp )
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .zIndex(
                -distance
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