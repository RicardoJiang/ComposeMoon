package com.zj.composemoon.widget

import android.graphics.Color
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import kotlinx.coroutines.delay

@Composable
fun Poetry(modifier: Modifier) {
    Box(modifier = modifier) {
        val targetList = remember { mutableStateListOf<List<Char>>() }
        LaunchedEffect(true) {
            delay(1000)
            val text = "人有悲欢离合月有阴晴圆缺"
            val list1 = text.substring(0, 6).toList()
            targetList.add(list1)
            delay(3000)
            val list2 = text.substring(6, 12).toList()
            targetList.add(list2)
        }
        var xOffset = 0f
        var yOffset = 0f
        for (i in targetList.indices) {
            PoetryColumn(list = targetList[i], offsetX = xOffset, offsetY = yOffset)
            xOffset += 120f
            yOffset += 80f
        }
    }
}

@Composable
fun PoetryColumn(
    list: List<Char>,
    offsetX: Float = 0f,
    offsetY: Float = 0f
) {
    val targetList = remember { mutableStateListOf<Char>() }
    LaunchedEffect(list) {
        targetList.clear()
        list.forEach {
            delay(500)
            targetList.add(it)
        }
    }
    //将 Jetpack Compose 环境的 Paint 对象转换为原生的 Paint 对象
    val textPaint = Paint().asFrameworkPaint().apply {
        isAntiAlias = true
        isDither = true
        typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        textAlign = android.graphics.Paint.Align.CENTER
    }
    textPaint.color = Color.parseColor("#f9dc60")
    textPaint.textSize = 70f
    Canvas(modifier = Modifier.wrapContentSize()) {
        drawIntoCanvas {
            val x = 0 - offsetX
            var y = offsetY
            val delta = 100f
            for (i in targetList.indices) {
                it.nativeCanvas.drawText(list[i].toString(), x, y, textPaint)
                y += delta
            }
        }
    }
}