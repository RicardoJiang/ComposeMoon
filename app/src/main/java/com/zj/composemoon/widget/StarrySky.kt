package com.zj.composemoon.widget

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.delay

@Composable
fun StarrySky(modifier: Modifier, starNum: Int) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawIntoCanvas {
                drawRect(Color.Black)
            }
        }
        Stars(starNum = starNum)
    }
}

@Composable
fun Stars(starNum: Int) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val density = LocalDensity.current.density
        val list = remember { mutableStateListOf<Star>() }
        LaunchedEffect(true) {
            for (i in 0..starNum) {
                delay(100L)
                list.add(Star(maxWidth.value * density, maxHeight.value * density))
            }
        }
        list.forEach {
            Star(it)
        }
    }
}

@Composable
fun Star(star: Star) {
    var progress: Float by remember { mutableStateOf(0f) }
    val infiniteTransition = rememberInfiniteTransition()
    progress = infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            keyframes {
                durationMillis = 2000
                0.25f.at(500)
                1f.at(1000)
                2f.at(1500)
            },
            repeatMode = RepeatMode.Restart
        ),
    ).value
    star.updateStar(progress)
    Canvas(modifier = Modifier.wrapContentSize()) {
        scale(star.scale, Offset(star.x, star.y)) {
            drawCircle(
                star.starColor,
                star.radius,
                center = Offset(star.x, star.y),
                alpha = star.alpha
            )
        }
    }
}

class Star(
    var maxWidth: Float,
    var maxHeight: Float
) {
    var x: Float = 0f
        private set
    var y: Float = 0f
        private set
    var radius: Float = 3f
        private set
    var starColor: Color = Color(0xfff9dc60)
        private set
    var alpha = 1f
        private set
    var scale = 1f
        private set

    init {
        randomInitStar()
    }

    fun updateStar(progress: Float) {
        alpha = if (progress > 1) 1f else progress
        scale = progress
    }

    private fun randomInitStar() {
        x = (0..maxWidth.toInt()).random().toFloat()
        y = (0..maxHeight.toInt()).random().toFloat()
    }
}
