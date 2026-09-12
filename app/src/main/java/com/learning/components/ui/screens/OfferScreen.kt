package com.learning.components.ui.screens

import android.graphics.BlurMaskFilter
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.fontscaling.MathUtils.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import coil3.compose.AsyncImage
import com.learning.components.navigation.Screen
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Composable
fun OfferScreen() {
    val pagerState = rememberPagerState(initialPage = 1, pageCount = { 6 })
    var isDraggingCard by remember { mutableStateOf(false) }
    var showOfferPopup by remember { mutableStateOf(false) }

    var offsetY by remember { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()
    val animatedOffsetY = remember { Animatable(0f) }


    val images = listOf(
        "https://images.unsplash.com/photo-1500534623283-312aade485b7",
        "https://images.unsplash.com/photo-1507525428034-b723cf961d3e",
        "https://images.unsplash.com/photo-1470770841072-f978cf4d019e",
        "https://images.unsplash.com/photo-1441974231531-c6227db76b6e",
        "https://images.unsplash.com/photo-1501785888041-af3ef285b470",
        "https://images.unsplash.com/photo-1469474968028-56623f02e42e"
    )

    val dropThreshold = 1000f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(start = 100.dp, end = 40.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(620.dp)
            ) { page ->
                val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction)
                val isCenter = page == pagerState.currentPage
                val cardOffsetY = if (isCenter) animatedOffsetY.value + offsetY else 0f

                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            val pageAbsOffset = pageOffset.absoluteValue
                            rotationZ = lerp(
                                start = 0f,
                                stop = if (pageOffset > 0) 25f else -25f,
                                fraction = pageAbsOffset.coerceIn(0f, 1f)
                            )
                            scaleX = lerp(start = 1f, stop = 0.82f, fraction = pageAbsOffset)
                            scaleY = lerp(start = 1f, stop = 0.82f, fraction = pageAbsOffset)
                            translationX = lerp(
                                start = 0f,
                                stop = if (pageOffset > 0) 30f else -30f,
                                fraction = pageAbsOffset
                            )
                            cameraDistance = 12 * density
                        }
                        .offset {
                            IntOffset(0, cardOffsetY.roundToInt())
                        }
                        .then(
                            if (isCenter) {
                                Modifier.pointerInput(Unit) {
                                    detectDragGestures(
                                        onDragStart = { isDraggingCard = true },
                                        onDragEnd = {
                                            isDraggingCard = false
                                            if (offsetY > dropThreshold) {
                                                scope.launch {
                                                    animatedOffsetY.animateTo(900f)
                                                    showOfferPopup = true
                                                }
                                            } else {
                                                offsetY = 0f
                                                scope.launch { animatedOffsetY.animateTo(0f) }
                                            }
                                        },
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            offsetY = (offsetY + dragAmount.y).coerceAtLeast(0f)
                                        }
                                    )
                                }
                            } else Modifier
                        )
                ) {
                    OfferCardItem(
                        title = "",
                        isCenter = isCenter,
                        imageUrl = images[page],
                        glowColor = Color(0xFFE0DAD3)
                    )
                }
            }

            BottomPocketCard(isHighlighted = isDraggingCard)

        }


        AnimatedVisibility(
            visible = showOfferPopup,
            enter = scaleIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)),
            exit = scaleOut()
        ) {
            OfferPopupDialog(
                onDismiss = {
                    showOfferPopup = false
                    offsetY = 0f
                    scope.launch { animatedOffsetY.snapTo(0f) }
                }
            )
        }
    }
}


@Composable
fun OfferCardItem(
    imageUrl: String,
    title: String,
    isCenter: Boolean,
    glowColor: Color
) {
    Box(
        modifier = Modifier
            .size(width = 240.dp, height = 240.dp)
            .then(
                if (isCenter) {
                    Modifier.drawOuterGlow(glowColor)
                } else {
                    Modifier
                }
            )
            .clip(RoundedCornerShape(24.dp))
            .border(
                width = if (isCenter) 2.dp else 1.dp,
                color = if (isCenter) {
                    glowColor
                } else {
                    Color.White.copy(alpha = 0.15f)
                },
                shape = RoundedCornerShape(24.dp)
            )
    ) {

        AsyncImage(
            model = imageUrl,
            contentDescription = title,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(24.dp)),
            contentScale = ContentScale.Crop
        )


        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.Black.copy(alpha = 0.25f)
                )
        )

        Column(
            modifier = Modifier.align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isCenter) {
                    "Drag Down to Pocket"
                } else {
                    "Swipe to View"
                },
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun BottomPocketCard(isHighlighted: Boolean) {
    Box(
        modifier = Modifier
            .size(width = 300.dp, height = 80.dp)
            .height(140.dp)
            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF00E5FF).copy(alpha = if (isHighlighted) 0.35f else 0.12f),
                        Color(0x00161A20)
                    )
                )
            )
            .border(
                width = 1.5.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF00E5FF).copy(alpha = 0.6f),
                        Color(0x00161A20)
                    )
                ),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            ),
        contentAlignment = Alignment.TopCenter
    ) {
        Text(
            text = "DROP HERE",
            color = Color(0xFF889193),
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}


@Composable
fun OfferPopupDialog(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .size(400.dp, 620.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFFF1EEEE))
            .border(2.dp, Color(0xFF00E5FF), RoundedCornerShape(28.dp))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "OFFER UNLOCKED!",
                color = Color(0xFF9FA9AB),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "50% OFF VIP ACCESS",
                color = Color.Black,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF00E5FF))
                    .pointerInput(Unit) { detectDragGestures { _, _ -> onDismiss() } }
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text("CLAIM NOW", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}


fun Modifier.drawOuterGlow(color: Color) = this.drawWithContent {
    val shadowPaint = Paint().asFrameworkPaint().apply {
        isAntiAlias = true
        this.color = Color(0xFFD9A4A4).toArgb()
        maskFilter = BlurMaskFilter(40f, BlurMaskFilter.Blur.NORMAL)
    }
    drawIntoCanvas { canvas ->
        canvas.nativeCanvas.drawRoundRect(
            0f, 0f, size.width, size.height,
            48f, 48f, shadowPaint
        )
    }
    drawContent()
}

@Preview(showBackground = true)
@Composable
fun OfferScreenPreview() {
    OfferScreen()
}