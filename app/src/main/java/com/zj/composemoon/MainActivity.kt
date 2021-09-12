package com.zj.composemoon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.zj.composemoon.ui.theme.ComposeMoonTheme
import com.zj.composemoon.widget.Moon
import com.zj.composemoon.widget.StarrySky

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            ComposeMoonTheme {
                rememberSystemUiController().setStatusBarColor(Color.Transparent, darkIcons = true)
                Surface(color = MaterialTheme.colors.background) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Box(modifier = Modifier.fillMaxSize()){
        StarrySky(Modifier.fillMaxSize(), 50)
        Moon(modifier = Modifier.size(200.dp).align(Alignment.Center))
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeMoonTheme {
        Greeting("Android")
    }
}