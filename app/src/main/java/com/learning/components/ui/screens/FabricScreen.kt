package com.learning.components.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas as AndroidCanvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.core.graphics.createBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import androidx.core.graphics.withClip


private const val MESH_COLUMNS = 41
private const val MESH_ROWS = 25
private const val DEFORMATION_RADIUS = 320f
private const val MAX_PUSH_DISTANCE = 55f
private const val FOLD_STRENGTH = 14f
private const val PULL_REFRESH_THRESHOLD = 200f
private const val MAX_PULL_DISTANCE = 320f
private const val PULL_HORIZONTAL_AMOUNT = 0.80f
private const val PULL_WRINKLE_AMOUNT = 0.035f
private const val PULL_COMPRESSION_AMOUNT = 0.035f
private const val REFRESH_SHAKE_AMOUNT = 78f
private const val REFRESH_VERTICAL_SHAKE = 16f
private const val CRUMPLE_HORIZONTAL_AMOUNT = 16f
private const val CRUMPLE_VERTICAL_AMOUNT = 9f
private const val FABRIC_EDGE_SOFTNESS = 0.85f
private const val PULL_START_REGION = 180f
private const val DESIGN_WIDTH = 390f
private const val HORIZONTAL_PADDING = 24f
private const val CARD_RADIUS = 22f

private const val PROJECT_IMAGE_1 = "https://i.pinimg.com/1200x/d2/a6/d4/d2a6d48d14b54c52c5ccb0177247d28e.jpg"
private const val PROJECT_IMAGE_2 = "https://i.pinimg.com/736x/52/e6/b2/52e6b260f41888f24b9077aa7a0420b4.jpg"


@Composable
fun FabricScreen() {

    var touchPoint by remember {
        mutableStateOf(Offset.Unspecified)
    }

    var isTouching by remember {
        mutableStateOf(false)
    }

    var pullDistance by remember {
        mutableStateOf(0f)
    }

    var dragStartY by remember {
        mutableStateOf(0f)
    }

    var isPullGesture by remember {
        mutableStateOf(false)
    }

    var isRefreshing by remember {
        mutableStateOf(false)
    }

    val projectImage1 by produceState<Bitmap?>(
        initialValue = null,
        key1 = PROJECT_IMAGE_1
    ) {
        value = loadBitmapFromUrl(PROJECT_IMAGE_1)
    }

    val projectImage2 by produceState<Bitmap?>(
        initialValue = null,
        key1 = PROJECT_IMAGE_2
    ) {
        value = loadBitmapFromUrl(PROJECT_IMAGE_2)
    }

    val deformationAmount by animateFloatAsState(
        targetValue = if (isTouching) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = 110f
        ),
        label = "fabric_deformation"
    )

    val refreshAnimation =
        remember {
            Animatable(0f)
        }

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            refreshAnimation.snapTo(0f)
            refreshAnimation.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 1300
                )
            )
            refreshAnimation.snapTo(0f)
            isRefreshing = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        FabricFullScreen(
            touchPoint = touchPoint,
            deformationAmount = deformationAmount,
            pullDistance = pullDistance,
            refreshShake = refreshAnimation.value,

            projectImage1 = projectImage1,
            projectImage2 = projectImage2,
            onTouchStart = { position ->
                touchPoint = position
                isTouching = true
                dragStartY = position.y
                isPullGesture = position.y <= PULL_START_REGION
                pullDistance = 0f
            },

            onTouchMove = { position ->
                touchPoint = position

                if (isPullGesture) {
                    val draggedDistance = position.y - dragStartY
                    pullDistance = draggedDistance.coerceIn( 0f, MAX_PULL_DISTANCE )
                }
            },

            onRelease = {
                isTouching = false

                if ( isPullGesture && pullDistance >= PULL_REFRESH_THRESHOLD
                ) {
                    isRefreshing = true
                }

                pullDistance = 0f
                isPullGesture = false
            }
        )
    }
}

private suspend fun loadBitmapFromUrl(
    url: String
): Bitmap? {

    return withContext(Dispatchers.IO) {
        try {
            URL(url)
                .openStream()
                .use { inputStream ->

                    BitmapFactory.decodeStream(
                        inputStream
                    )
                }

        } catch (
            exception: Exception
        ) {

            null
        }
    }
}

@Composable
private fun FabricFullScreen(
    touchPoint: Offset,
    deformationAmount: Float,
    pullDistance: Float,
    refreshShake: Float,

    projectImage1: Bitmap?,
    projectImage2: Bitmap?,

    onTouchStart: (Offset) -> Unit,
    onTouchMove: (Offset) -> Unit,
    onRelease: () -> Unit
) {

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .fabricTouchInteraction(
                onTouchStart = onTouchStart,
                onTouchMove = onTouchMove,
                onRelease = onRelease
            )
    ) {

        val screenWidth = size.width.toInt()
        val screenHeight = size.height.toInt()

        if ( screenWidth <= 0 || screenHeight <= 0
        ) {
            return@Canvas
        }

        val screenBitmap =
            createScreenBitmap(
                width = screenWidth,
                height = screenHeight,
                projectImage1 = projectImage1,
                projectImage2 = projectImage2
            )

        val meshPoints =
            createFabricMesh(
                width = size.width,
                height = size.height,
                touchPoint = touchPoint,
                deformationAmount = deformationAmount,
                pullDistance = pullDistance,
                refreshShake = refreshShake
            )

        drawIntoCanvas { canvas ->

            val nativeCanvas = canvas.nativeCanvas
            val bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG )

            nativeCanvas.drawBitmapMesh(
                screenBitmap,
                MESH_COLUMNS - 1,
                MESH_ROWS - 1,
                meshPoints,
                0,
                null,
                0,
                bitmapPaint
            )
        }
    }
}


private fun createScreenBitmap(
    width: Int,
    height: Int,
    projectImage1: Bitmap?,
    projectImage2: Bitmap?
): Bitmap {

    val bitmap = createBitmap( width, height )
    val canvas = AndroidCanvas(bitmap)
    val scale = width / DESIGN_WIDTH

    drawBackground(
        canvas = canvas,
        width = width,
        height = height
    )

    drawHeader(
        canvas = canvas,
        width = width,
        scale = scale
    )

    drawStatsCard(
        canvas = canvas,
        width = width,
        scale = scale
    )

    drawProjectCards(
        canvas = canvas,
        width = width,
        scale = scale,
        projectImage1 = projectImage1,
        projectImage2 = projectImage2
    )

    return bitmap
}


private fun drawBackground(
    canvas: AndroidCanvas,
    width: Int,
    height: Int
) {

    val backgroundPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color(0xFFFFFFFF).toArgb()
        }

    canvas.drawRect(
        0f,
        0f,
        width.toFloat(),
        height.toFloat(),
        backgroundPaint
    )
}


private fun drawHeader(
    width: Int,
    canvas: AndroidCanvas,
    scale: Float
) {

    val left = HORIZONTAL_PADDING * scale
    val titlePaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color(0xFF111214).toArgb()
            textSize = 30f * scale
            typeface = Typeface.DEFAULT_BOLD
        }

    canvas.drawText(
        "My Travel Journal",
        left,
        72f * scale,
        titlePaint
    )

    val subtitlePaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color(0xFF777A80).toArgb()
            textSize = 12.5f * scale
        }

    canvas.drawText(
        "Places I’ve explored, memories I’ve made.",
        left,
        96f * scale,
        subtitlePaint
    )
}


private fun drawStatsCard(
    canvas: AndroidCanvas,
    width: Int,
    scale: Float
) {

    val left = HORIZONTAL_PADDING * scale
    val right = width - HORIZONTAL_PADDING * scale
    val top = 126f * scale
    val height = 82f * scale
    val radius = 20f * scale
    val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.White.toArgb()

        }

    canvas.drawRoundRect(
        RectF(
            left,
            top,
            right,
            top + height
        ),
        radius,
        radius,
        cardPaint
    )

    val borderPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {

            color = Color(0xFFE7E7E4).toArgb()
            style = Paint.Style.STROKE
            strokeWidth = 1f * scale
        }

    canvas.drawRoundRect(
        RectF(
            left,
            top,
            right,
            top + height
        ),
        radius,
        radius,
        borderPaint
    )

    val indicatorPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {

            color = Color(0xFF5D63E8).toArgb()
        }

    canvas.drawCircle(
        left + 24f * scale,
        top + 27f * scale,
        5f * scale,
        indicatorPaint
    )

    val valuePaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {

            color = Color(0xFF151619).toArgb()
            textSize = 22f * scale
            typeface = Typeface.DEFAULT_BOLD
        }

    canvas.drawText(
        "12",
        left + 40f * scale,
        top + 34f * scale,
        valuePaint
    )

    val labelPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {

            color = Color(0xFF777A80).toArgb()
            textSize = 10f * scale
            typeface = Typeface.DEFAULT_BOLD
            letterSpacing = 0.08f
        }

    canvas.drawText(
        "Places Visited",
        left + 40f * scale,
        top + 51f * scale,
        labelPaint
    )

    val dividerPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {

            color = Color(0xFFEAEAE7).toArgb()
            strokeWidth = 1f * scale
        }

    canvas.drawLine(
        left + 150f * scale,
        top + 17f * scale,
        left + 150f * scale,
        top + height - 17f * scale,
        dividerPaint
    )

    val statusPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {

            color = Color(0xFF202124).toArgb()
            textSize = 12f * scale
            typeface = Typeface.DEFAULT_BOLD
        }

    canvas.drawText(
        "In progress",
        left + 174f * scale,
        top + 34f * scale,
        statusPaint
    )

    val progressPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {

            color = Color(0xFF8A8D92).toArgb()
            textSize = 10f * scale
        }

    canvas.drawText(
        "Santorini, Greece",
        left + 174f * scale,
        top + 52f * scale,
        progressPaint
    )
}


private fun drawProjectCards(
    canvas: AndroidCanvas,
    width: Int,
    scale: Float,
    projectImage1: Bitmap?,
    projectImage2: Bitmap?
) {

    val left = HORIZONTAL_PADDING * scale
    val right = width - HORIZONTAL_PADDING * scale
    val cardHeight = 218f * scale

    drawImageProjectCard(
        canvas = canvas,
        left = left,
        right = right,
        top = 232f * scale,
        height = cardHeight,
        scale = scale,
        title = "Santorini, Greece",
        subtitle = "Whitewashed villages and sunsets over the Aegean.",
        image = projectImage1,
        type = 0
    )

    drawImageProjectCard(
        canvas = canvas,
        left = left,
        right = right,
        top = 466f * scale,
        height = cardHeight,
        scale = scale,
        title = "Kyoto, Japan",
        subtitle = "Ancient streets and a timeless side of Japan.",
        image = projectImage2,
        type = 1
    )
}


private fun drawImageProjectCard(
    canvas: AndroidCanvas,
    left: Float,
    right: Float,
    top: Float,
    height: Float,
    scale: Float,
    title: String,
    subtitle: String,
    image: Bitmap?,
    type: Int
) {

    val radius = CARD_RADIUS * scale

    val cardPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {

            shader =
                LinearGradient(
                    left,
                    top,
                    right,
                    top + height,

                    if (type == 0) {
                        Color(0xFFE9E6FF).toArgb()
                    } else {
                        Color(0xFFE2F4F3).toArgb()
                    },

                    Color.White.toArgb(),

                    Shader.TileMode.CLAMP
                )
        }

    canvas.drawRoundRect(
        RectF(
            left,
            top,
            right,
            top + height
        ),
        radius,
        radius,
        cardPaint
    )

    val imageLeft = left + 12f * scale
    val imageRight = right - 12f * scale
    val imageTop = top + 12f * scale
    val imageBottom = top + 155f * scale

    val imageRect =
        RectF(
            imageLeft,
            imageTop,
            imageRight,
            imageBottom
        )


    val imagePath =
        Path().apply {

            addRoundRect(
                imageRect,
                17f * scale,
                17f * scale,
                Path.Direction.CW
            )
        }

    canvas.withClip(imagePath) {

        if (image != null) {

            drawCenterCropBitmap(
                canvas = this,
                bitmap = image,
                destination = imageRect
            )

        } else {

            val placeholderPaint =
                Paint(Paint.ANTI_ALIAS_FLAG).apply {

                    color =
                        if (type == 0) {
                            Color(0xFFE1DCFF).toArgb()
                        } else {
                            Color(0xFFDDEFEF).toArgb()
                        }
                }

            drawRect( imageRect, placeholderPaint )
        }

    }

    val titlePaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {

            color = Color(0xFF17181B).toArgb()
            textSize = 15f * scale
            typeface = Typeface.DEFAULT_BOLD
        }

    canvas.drawText(
        title,
        left + 18f * scale,
        top + 181f * scale,
        titlePaint
    )

    val subtitlePaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {

            color = Color(0xFF777A80).toArgb()
            textSize = 10.5f * scale
        }

    canvas.drawText(
        subtitle,
        left + 18f * scale,
        top + 199f * scale,
        subtitlePaint
    )

    val arrowPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {

            color = Color(0xFF202124).toArgb()
            textSize = 17f * scale
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
        }

    canvas.drawText(
        "↗",
        right - 25f * scale,
        top + 192f * scale,
        arrowPaint
    )
}

private fun drawCenterCropBitmap(
    canvas: AndroidCanvas,
    bitmap: Bitmap,
    destination: RectF
) {
    val sourceWidth = bitmap.width.toFloat()
    val sourceHeight = bitmap.height.toFloat()

    if ( sourceWidth <= 0f || sourceHeight <= 0f ) {
        return
    }

    val destinationWidth = destination.width()
    val destinationHeight = destination.height()
    val sourceAspect = sourceWidth / sourceHeight
    val destinationAspect = destinationWidth / destinationHeight
    val sourceRect: android.graphics.Rect

    if (sourceAspect > destinationAspect) {

        val croppedWidth = sourceHeight * destinationAspect
        val leftCrop = (sourceWidth - croppedWidth) / 2f

        sourceRect =
            android.graphics.Rect(
                leftCrop.toInt(),
                0,
                (leftCrop + croppedWidth).toInt(),
                bitmap.height
            )

    } else {
        val croppedHeight = sourceWidth / destinationAspect
        val topCrop = (sourceHeight - croppedHeight) / 2f

        sourceRect =
            android.graphics.Rect(
                0,
                topCrop.toInt(),
                bitmap.width,
                (topCrop + croppedHeight).toInt()
            )
    }

    val imagePaint = Paint( Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG )

    canvas.drawBitmap(
        bitmap,
        sourceRect,
        destination,
        imagePaint
    )
}



private fun createFabricMesh(
    width: Float,
    height: Float,
    touchPoint: Offset,
    deformationAmount: Float,
    pullDistance: Float,
    refreshShake: Float
): FloatArray {

    val meshPoints = FloatArray( MESH_COLUMNS * MESH_ROWS * 2)
    val horizontalStep = width / (MESH_COLUMNS - 1).toFloat()
    val verticalStep = height / (MESH_ROWS - 1).toFloat()
    val halfWidth = width / 2f
    val halfHeight = height / 2f
    var pointIndex = 0

    for (row in 0 until MESH_ROWS) {
        for (column in 0 until MESH_COLUMNS) {

            val originalX = column.toFloat() * horizontalStep
            val originalY = row.toFloat() * verticalStep
            var pointX = originalX
            var pointY = originalY
            val normalizedX = (originalX / width).coerceIn(0f, 1f)
            val normalizedY = (originalY / height).coerceIn(0f, 1f)
            val horizontalEnvelope = sin(normalizedX * PI.toFloat() ).coerceAtLeast(0f)
            val verticalEnvelope = sin(normalizedY * PI.toFloat() ).coerceAtLeast(0f)
            val boundaryEnvelope = horizontalEnvelope * verticalEnvelope
            val centerXDistance = kotlin.math.abs(originalX - halfWidth) / halfWidth
            val centerYDistance = kotlin.math.abs(originalY - halfHeight) / halfHeight
            val centerEnvelope = (1f - centerXDistance).coerceIn(0f, 1f) * (1f - centerYDistance).coerceIn(0f, 1f)


            if ( touchPoint.isSpecified && deformationAmount > 0f ) {

                val deltaX = originalX - touchPoint.x
                val deltaY = originalY - touchPoint.y
                val distance = sqrt(deltaX * deltaX + deltaY * deltaY )

                if ( distance < DEFORMATION_RADIUS ) {

                    val normalizedDistance = distance / DEFORMATION_RADIUS
                    val influence = 1f - normalizedDistance
                    val smoothInfluence = influence * influence * ( 3f - 2f * influence )
                    val safeDistance = distance.coerceAtLeast(0.001f )
                    val directionX = deltaX / safeDistance
                    val directionY = deltaY / safeDistance

                    val fabricInfluence = smoothInfluence * ( 0.35f + 0.65f * boundaryEnvelope )
                    val pushAmount = MAX_PUSH_DISTANCE * fabricInfluence * deformationAmount

                    pointX += directionX * pushAmount
                    pointY += directionY * pushAmount

                    val perpendicularX = -directionY
                    val perpendicularY = directionX
                    val wave = sin(distance * 0.075f )
                    val foldAmount = wave * FOLD_STRENGTH * fabricInfluence * deformationAmount

                    pointX += perpendicularX * foldAmount
                    pointY += perpendicularY * foldAmount

                    val secondaryWave = cos(distance * 0.12f )
                    val wrinkleAmount = secondaryWave * 5f * fabricInfluence * deformationAmount

                    pointX += directionX * wrinkleAmount
                    pointY += directionY * wrinkleAmount
                }
            }


            if (pullDistance > 0f) {

                val normalizedPull = ( pullDistance / MAX_PULL_DISTANCE ).coerceIn( 0f, 1f )
                val topInfluence = ( 1f - normalizedY / FABRIC_EDGE_SOFTNESS ).coerceIn( 0f, 1f )
                val smoothTopInfluence = topInfluence * topInfluence * ( 3f - 2f * topInfluence )
                val topEdgeEnvelope = sin(normalizedX * PI.toFloat() )
                val pullY = pullDistance * ( 0.32f + 0.68f * smoothTopInfluence ) * topEdgeEnvelope

                pointY += pullY

                val horizontalWave = sin(normalizedY * PI.toFloat() * 3f + normalizedX * PI.toFloat() * 0.75f )
                pointX += horizontalWave * pullDistance * PULL_HORIZONTAL_AMOUNT * smoothTopInfluence * boundaryEnvelope

                val wrinkle = sin(originalY * 0.045f + originalX * 0.018f )

                pointX += wrinkle * pullDistance * PULL_WRINKLE_AMOUNT * smoothTopInfluence * boundaryEnvelope

                val distanceFromCenter = originalX - halfWidth

                pointX -= distanceFromCenter * normalizedPull * PULL_COMPRESSION_AMOUNT * smoothTopInfluence * boundaryEnvelope
            }

            if (refreshShake > 0f) {

                val progress = refreshShake.coerceIn(0f, 1f)
                val damping = 1f - progress
                val damped = damping * damping
                val primaryWave = sin(progress * PI.toFloat() * 9f)

                val secondaryWave = sin(progress * PI.toFloat() * 17f + normalizedY * PI.toFloat() * 3f)
                val spatialWave = sin(normalizedY * PI.toFloat() * 4f + progress * PI.toFloat() * 3f)
                val horizontalShake = ( primaryWave * REFRESH_SHAKE_AMOUNT + secondaryWave * 9f ) * spatialWave * damped * boundaryEnvelope

                pointX += horizontalShake

                val verticalWave = cos(progress * PI.toFloat() * 8f + normalizedX * PI.toFloat() * 3f)
                val verticalShake = verticalWave * REFRESH_VERTICAL_SHAKE * damped * boundaryEnvelope

                pointY += verticalShake

                val crumpleWave = sin(normalizedX * PI.toFloat() * 10f + normalizedY * PI.toFloat() * 14f + progress * PI.toFloat() * 11f)
                val crumpleWave2 = cos(normalizedX * PI.toFloat() * 17f - normalizedY * PI.toFloat() * 8f + progress * PI.toFloat() * 15f)
                val crumpleStrength = progress * damped * boundaryEnvelope * centerEnvelope

                pointX += crumpleWave * CRUMPLE_HORIZONTAL_AMOUNT * crumpleStrength
                pointY += crumpleWave2 * CRUMPLE_VERTICAL_AMOUNT * crumpleStrength
            }

            pointX = pointX.coerceIn(0f, width)
            pointY = pointY.coerceIn(0f, height )

            if (column == 0) {
                pointX = 0f
            }

            if ( column == MESH_COLUMNS - 1) {
                pointX = width
            }

            if (row == 0) {
                pointX = originalX
                if (refreshShake > 0f) {
                    pointY = originalY
                }
            }

            if ( row == MESH_ROWS - 1 ) {
                pointY = height
            }

            meshPoints[pointIndex++] = pointX
            meshPoints[pointIndex++] = pointY
        }
    }

    return meshPoints
}


private fun Modifier.fabricTouchInteraction(
    onTouchStart: (Offset) -> Unit,
    onTouchMove: (Offset) -> Unit,
    onRelease: () -> Unit
): Modifier {

    return pointerInput(Unit) {
        awaitEachGesture {

            val down = awaitFirstDown( requireUnconsumed = false )
            onTouchStart( down.position )

            while (true) {

                val event = awaitPointerEvent(PointerEventPass.Main )
                val pointer = event.changes.firstOrNull { it.id == down.id }

                if ( pointer == null || !pointer.pressed ) {
                    break
                }

                onTouchMove( pointer.position )
                pointer.consume()
            }

            onRelease()
        }
    }
}