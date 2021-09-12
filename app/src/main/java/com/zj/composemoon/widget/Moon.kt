package com.zj.composemoon.widget

import android.graphics.BlurMaskFilter
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.dp

@Composable
fun Moon(modifier: Modifier) {
    var progress: Float by remember { mutableStateOf(0f) }

    val infiniteTransition = rememberInfiniteTransition()
    val duration = 10000
    val paint = Paint().apply {
        color = Color.Black
        isAntiAlias = true
    }
    progress = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            keyframes {
                durationMillis = duration
                delayMillis = 500
                0.5f.at((duration * 0.25).toInt())
                0.5f.at((duration * 0.3).toInt())
                1f.at((duration * 0.4).toInt())
                1f.at((duration * 0.6).toInt())
                1.5f.at((duration * 0.75).toInt())
                1.5f.at((duration * 0.8).toInt())
                2.0f.at(duration)
            },
            repeatMode = RepeatMode.Restart
        ),
    ).value
    BoxWithConstraints(modifier = modifier) {
        val canvasSize = minOf(maxWidth, maxHeight) - 40.dp
        Canvas(
            modifier = Modifier
                .size(canvasSize)
                .align(Alignment.TopCenter)
        ) {
            drawMoonCircle(this, progress)
            drawIntoCanvas {
                it.withSaveLayer(Rect(0f, 0f, size.width, size.height), paint = Paint()) {
                    if (progress != 1f) {
                        drawMoonArc(this, it, paint, progress)
                        drawMoonOval(this, it, paint, progress)
                    }
                }
            }
        }
        Text(
            text = getPhaseText(progress),
            color = Color(0xfff9dc60),
            modifier = Modifier.align(Alignment.BottomCenter),
            style = MaterialTheme.typography.h5
        )
    }
}

private fun getPhaseText(progress: Float): String {
    return when {
        progress <= 0f -> "新月"
        progress < 0.5f -> "上蛾眉月"
        progress == 0.5f -> "上弦月"
        progress < 1.0f -> "渐盈凸月"
        progress == 1.0f -> "满月"
        progress < 1.45f -> "渐亏凸月"
        progress in 1.45f..1.55f -> "下弦月"
        progress < 2.0f -> "下蛾眉月"
        else -> "晦"
    }
}

private fun drawMoonCircle(scope: DrawScope, progress: Float) {
    scope.run {
        if (progress == 1f) {
            drawIntoCanvas {
                val blurRadius = 30f
                val moonPaint = Paint().asFrameworkPaint().apply {
                    maskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.SOLID)
                    color = android.graphics.Color.parseColor("#f9dc60")
                    isAntiAlias = true
                }
                it.nativeCanvas.drawCircle(center.x, center.y, size.minDimension / 2.0f, moonPaint)
            }
        } else {
            drawCircle(Color(0xfff9dc60))
        }
    }
}

private fun drawMoonOval(scope: DrawScope, canvas: Canvas, paint: Paint, progress: Float) {
    val blendMode = when {
        progress <= 0.5f -> BlendMode.DstOver
        progress <= 1f -> BlendMode.DstOut
        progress <= 1.5f -> BlendMode.DstOut
        else -> BlendMode.DstOver
    }
    paint.blendMode = blendMode
    scope.run {
        val moonRadius = size.minDimension / 2.0f
        val ovalHRadius = getOvalHRadius(moonRadius, progress)
        val topLeft = Offset(center.x - ovalHRadius, center.y - moonRadius)
        val horizontalAxis = ovalHRadius * 2
        val verticalAxis = moonRadius * 2
        canvas.drawOval(
            Rect(offset = topLeft, size = Size(horizontalAxis, verticalAxis)),
            paint = paint
        )
    }
}

private fun drawMoonArc(scope: DrawScope, canvas: Canvas, paint: Paint, progress: Float) {
    val sweepAngle = when {
        progress <= 0.5f -> 180f
        progress <= 1f -> 180f
        progress <= 1.5f -> -180f
        else -> -180f
    }
    paint.blendMode = BlendMode.DstOver
    scope.run {
        canvas.drawArc(Rect(0f, 0f, size.width, size.height), 90f, sweepAngle, false, paint)
    }
}

//获取椭圆横轴半径
private fun getOvalHRadius(radius: Float, progress: Float): Float {
    return when {
        progress <= 0.5f -> {
            radius * (0.5f - progress) * 2
        }
        progress <= 1 -> {
            radius * (progress - 0.5f) * 2
        }
        progress <= 1.5 -> {
            radius * (1.5f - progress) * 2
        }
        else -> {
            radius * (progress - 1.5f) * 2f
        }
    }
}