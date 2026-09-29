package com.learning.components.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.safeGestures
import androidx.compose.foundation.layout.size
import androidx.compose.ui.geometry.Size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import okhttp3.internal.platform.android.AndroidLogHandler.close

val sideCurveShape = GenericShape { size: Size, _ ->

    val sx = size.width / 24f
    val sy = size.height / 132f
    moveTo(24f * sx, 0f)

    cubicTo(
        20f * sx, 24f * sy,
        0f,
        24f * sy,
        0f,
        52f * sy
    )

    lineTo(
        0f,
        80f * sy
    )

    cubicTo(
        0f,
        105f * sy,
        24f * sx,
        117f * sy,
        24f * sx,
        132f * sy
    )

    close()
}

@Composable
fun SwipeCard(
    modifier: Modifier = Modifier
) {

    val maxRevealWidth = 70.dp
    val density = LocalDensity.current

    val maxRevealWidthPx = with(density) {
        maxRevealWidth.toPx()
    }

    val scope = rememberCoroutineScope()
    val revealWidth = remember {
        Animatable(0f)
    }

    val people = listOf(
        "Hariom",
        "Ankit",
        "Udit",
        "Yogi",
        "Suman",
        "Vinamra",
        "Yogi",
        "Suman",
        "Vinamra",

    )
    val containerShape = RoundedCornerShape(24.dp)

    Box(
        modifier = modifier

            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(500.dp)
            .clip(containerShape)
            .pointerInput(maxRevealWidthPx) {

                detectHorizontalDragGestures(

                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        val newWidth =
                            (revealWidth.value - dragAmount)
                                .coerceIn(
                                    0f,
                                    maxRevealWidthPx
                                )

                        scope.launch {
                            revealWidth.snapTo(newWidth)
                        }
                    },

                    onDragEnd = {

                        scope.launch {

                            val shouldOpen =
                                revealWidth.value >
                                        maxRevealWidthPx * 0.35f

                            revealWidth.animateTo(
                                targetValue =
                                    if (shouldOpen) {
                                        maxRevealWidthPx
                                    } else {
                                        0f
                                    },
                                animationSpec = tween(
                                    durationMillis = 220
                                )
                            )
                        }
                    },

                    onDragCancel = {

                        scope.launch {

                            revealWidth.animateTo(
                                targetValue = 0f,
                                animationSpec = tween(220)
                            )
                        }
                    }
                )
            }
    ) {

        MainCard()

        SwipeEffect(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .background(
                    color = Color(0xFFFFFFFF),
                    shape = sideCurveShape
                )
                .width(with(density) { revealWidth.value.toDp() })
                .height(400.dp),
            people = people
        )
    }
}


@Composable
private fun MainCard() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF1264E8))
            .padding(20.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(20.dp))
                ) {

                    AsyncImage(
                        model = "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee",
                        contentDescription = "Travel destination",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.7f)
                                    )
                                )
                            )
                    )

                    Row(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(14.dp)
                            .clip(RoundedCornerShape(50.dp))
                            .background(
                                Color.Black.copy(alpha = 0.28f)
                            )
                            .padding(
                                horizontal = 12.dp,
                                vertical = 7.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(Color.Green)
                        )

                        Spacer(modifier = Modifier.width(7.dp))

                        Text(
                            text = "Exploring",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {

                        Text(
                            text = "TRAVEL JOURNAL",
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )

                        Text(
                            text = "Places worth getting lost in.",
                            color = Color.White,
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {

                    Text(
                        text = "My Travel Stories",
                        color = Color.White,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Journeys, landscapes & moments along the way",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 14.sp
                    )

                    Text(
                        text = "Exploring new places, meeting new people, and collecting stories along the way.",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    InfoChip(
                        modifier = Modifier.weight(1f),
                        value = "12+",
                        label = "Places"
                    )

                    InfoChip(
                        modifier = Modifier.weight(1f),
                        value = "5",
                        label = "Countries"
                    )

                    InfoChip(
                        modifier = Modifier.weight(1f),
                        value = "8",
                        label = "Planned"
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {

                        Text(
                            text = "Currently dreaming about",
                            color = Color.White.copy(alpha = 0.55f),
                            fontSize = 11.sp
                        )

                        Text(
                            text = "Mountains • Villages • Open Roads",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable { }
                    ) {

                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Explore places",
                            tint = Color(0xFF1264E8),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoChip(
    modifier: Modifier = Modifier,
    value: String,
    label: String
) {

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(
                Color.White.copy(alpha = 0.1f)
            )
            .padding(
                horizontal = 12.dp,
                vertical = 10.dp
            )
    ) {

        Text(
            text = value,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = label,
            color = Color.White.copy(alpha = 0.55f),
            fontSize = 11.sp
        )
    }
}


@Composable
private fun SwipeEffect(
    modifier: Modifier,
    people: List<String>
) {
    Box(
        modifier = modifier
            .height(400.dp)
            .clip(sideCurveShape)
            .background(Color(0xFFE6CDF5)),
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = Modifier
                .height(300.dp)
                .fillMaxWidth()

        ) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        vertical = 24.dp,
                        horizontal = 8.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(people) { person ->
                    PersonItem(person)
                }
            }
        }
    }
}


@Composable
private fun PersonItem(
    name: String
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(
                    shape = RoundedCornerShape(50)
                ),
            contentAlignment = Alignment.Center
        ) {

            AsyncImage(
                model = "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee",
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

    }
}