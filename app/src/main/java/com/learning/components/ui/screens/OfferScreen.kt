package com.learning.components.ui.screens


import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Composable
fun OfferScreen() {

    var lastDraggedImage by remember {
        mutableIntStateOf(0)
    }

    val pagerState = rememberPagerState(
        initialPage = 1,
        pageCount = { 6 }
    )

    var isDraggingCard by remember {
        mutableStateOf(false)
    }

    var showOfferPopup by remember {
        mutableStateOf(false)
    }

    var offsetY by remember {
        mutableFloatStateOf(0f)
    }

    val scope = rememberCoroutineScope()

    val animatedOffsetY = remember {
        androidx.compose.animation.core.Animatable(0f)
    }

    val images = listOf(
        "https://i.pinimg.com/1200x/15/6e/71/156e71d0d1c64f6e774ac7e548f88fcf.jpg",
        "https://i.pinimg.com/736x/9c/c5/ec/9cc5ec8f358cbb2479a1d845ba7494f9.jpg",
        "https://i.pinimg.com/1200x/02/2c/8c/022c8c3ce9953af6e2daf2b95de33d37.jpg",
        "https://i.pinimg.com/1200x/02/43/a4/0243a44a94588b1bce4c53e335ef94d5.jpg",
        "https://i.pinimg.com/736x/8c/bc/e9/8cbce971d2ddfc3049656debb598b545.jpg",
        "https://i.pinimg.com/736x/6f/80/15/6f80157b0c8db85ad38db7d3bd1fec16.jpgg"
    )

    val dropThreshold = 1000f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF020B0C))
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(
                modifier = Modifier.height(60.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(620.dp)
            ) {

                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(
                            width = 270.dp,
                            height = 270.dp
                        )

                        .border(
                            width = 1.5.dp,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0x8BF9A825).copy(
                                        alpha = 0.4f
                                    ),
                                    Color(0x8CF9A81E).copy(
                                        alpha = 0.9f
                                    ),
                                )
                            ),
                            shape = RoundedCornerShape(
                                topStart = 36.dp,
                                topEnd = 36.dp,
                                bottomEnd = 36.dp,
                                bottomStart = 36.dp
                            ),

                        ),
                )

                HorizontalPager(
                    state = pagerState,
                    contentPadding = PaddingValues(
                        start = 105.dp,
                        end = 40.dp
                    ),
                    modifier = Modifier.fillMaxSize()
                ) { page ->

                    val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction

                    val isCenter = page == pagerState.currentPage

                    val cardOffsetY =
                        if (isCenter) {
                            animatedOffsetY.value + offsetY
                        } else {
                            0f
                        }

                    Box(
                        modifier = Modifier
                            .graphicsLayer {

                                val pageAbsOffset =
                                    pageOffset.absoluteValue

                                rotationZ = lerp(
                                    start = 0f,
                                    stop = if (pageOffset > 0) {
                                        25f
                                    } else {
                                        -25f
                                    },
                                    fraction = pageAbsOffset.coerceIn(
                                        0f,
                                        1f
                                    )
                                )

                                scaleX = lerp(
                                    start = 1f,
                                    stop = 0.82f,
                                    fraction = pageAbsOffset
                                )

                                scaleY = lerp(
                                    start = 1f,
                                    stop = 0.82f,
                                    fraction = pageAbsOffset
                                )

                                translationX = lerp(
                                    start = 0f,
                                    stop = if (pageOffset > 0) {
                                        30f
                                    } else {
                                        -30f
                                    },
                                    fraction = pageAbsOffset
                                )

                                cameraDistance = 12 * density
                            }
                    ) {

                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .graphicsLayer {

                                    shape = RoundedCornerShape(24.dp)

                                    shadowElevation = 0f

                                    scaleX =
                                        if (isDraggingCard) {
                                            1.02f
                                        } else {
                                            1f
                                        }

                                    scaleY =
                                        if (isDraggingCard) {
                                            1.02f
                                        } else {
                                            1f
                                        }
                                }
                                .offset {

                                    if (isCenter) {
                                        IntOffset(
                                            x = 0,
                                            y = cardOffsetY.roundToInt()
                                        )
                                    } else {
                                        IntOffset.Zero
                                    }
                                }
                                .then(

                                    if (isCenter) {

                                        Modifier.pointerInput(Unit) {

                                            detectDragGestures(

                                                onDragStart = {
                                                    isDraggingCard = true
                                                },

                                                onDragEnd = {

                                                    isDraggingCard = false

                                                    if (offsetY > dropThreshold) {

                                                        scope.launch {
                                                            animatedOffsetY.animateTo(
                                                                targetValue = 900f,
                                                                animationSpec = tween(
                                                                    durationMillis = 150
                                                                )
                                                            )

                                                            lastDraggedImage = pagerState.currentPage
                                                            showOfferPopup = true
                                                        }

                                                    } else {

                                                        offsetY = 0f

                                                        scope.launch {

                                                            animatedOffsetY.animateTo(
                                                                targetValue = 0f,
                                                                animationSpec = androidx.compose.animation.core.spring(
                                                                    dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
                                                                    stiffness = androidx.compose.animation.core.Spring.StiffnessMedium
                                                                )
                                                            )
                                                        }
                                                    }
                                                },

                                                onDragCancel = {

                                                    isDraggingCard = false

                                                    offsetY = 0f

                                                    scope.launch {

                                                        animatedOffsetY.animateTo(
                                                            targetValue = 0f,
                                                            animationSpec = androidx.compose.animation.core.spring(
                                                                dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
                                                                stiffness = androidx.compose.animation.core.Spring.StiffnessMedium
                                                            )
                                                        )
                                                    }
                                                },

                                                onDrag = { change, dragAmount ->

                                                    change.consume()

                                                    offsetY =
                                                        (
                                                                offsetY +
                                                                        dragAmount.y
                                                                )
                                                            .coerceAtLeast(0f)
                                                }
                                            )
                                        }

                                    } else {
                                        Modifier
                                    }
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
                }
            }

            BottomPocketCard(
                isHighlighted = isDraggingCard
            )
        }
    }

    if (showOfferPopup) {

        Dialog(
            onDismissRequest = {

                showOfferPopup = false
                offsetY = 0f

                scope.launch {
                    animatedOffsetY.snapTo(0f)
                }
            },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Color.Black.copy(alpha = 0.5f)
                    ),
                contentAlignment = Alignment.Center
            ) {

                OfferPopupDialog(
                    imageUrl = images[lastDraggedImage],
                    onDismiss = {

                        showOfferPopup = false
                        offsetY = 0f

                        scope.launch {
                            animatedOffsetY.snapTo(0f)
                        }
                    }
                )
            }
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
            .size(
                width = 240.dp,
                height = 240.dp
            )
            .clip(
                RoundedCornerShape(24.dp)
            )

    ) {

        AsyncImage(
            model = imageUrl,
            contentDescription = title,
            modifier = Modifier
                .fillMaxSize()
                .clip(
                    RoundedCornerShape(24.dp)
                ),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun BottomPocketCard(
    isHighlighted: Boolean
) {

    Box(
        modifier = Modifier
            .size(
                width = 332.dp,
                height = 80.dp
            )
            .clip(
                RoundedCornerShape(
                    topStart = 32.dp,
                    topEnd = 32.dp
                )
            )
            .background(

                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF9A825).copy(
                            alpha = if (isHighlighted) {
                                0.35f
                            } else {
                                0.12f
                            }
                        ),

                        Color(0x00161A20)
                    )
                )
            )
            .border(
                width = 1.5.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x8BF9A825).copy(
                            alpha = 0.9f
                        ),
                        Color(0x00F9A825)
                    )
                ),
                shape = RoundedCornerShape(
                    topStart = 36.dp,
                    topEnd = 36.dp
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = "DROP HERE",
            color = Color(0xFF889193),
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(
                top = 16.dp
            )
        )
    }
}

@Composable
fun OfferPopupDialog(
    imageUrl: String,
    onDismiss: () -> Unit
) {

    Box(
        modifier = Modifier
            .size(
                width = 400.dp,
                height = 620.dp
            )
            .clip(
                RoundedCornerShape(28.dp)
            )
            .background(
                Color(0xFFF1EEEE)
            )
            .border(
                width = 2.dp,
                color = Color(0xFF00E5FF),
                shape = RoundedCornerShape(28.dp)
            )

    ) {

        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .clip(
                    RoundedCornerShape(24.dp)
                ),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    Color.Black.copy(alpha = 0.55f)
                )
                .clickable {
                    onDismiss()
                },
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "×",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun OfferScreenPreview() {
    OfferScreen()
}

