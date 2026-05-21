package com.example.janagroandroid.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.janagroandroid.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNext: () -> Unit) {
    val logoAlpha = remember { Animatable(0f) }
    val titleAlpha = remember { Animatable(0f) }
    val subtitleAlpha = remember { Animatable(0f) }
    val logoSectionAlpha = remember { Animatable(1f) }
    val sawidScale = remember { Animatable(0.3f) }
    val sawidAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Initial animations
        logoAlpha.animateTo(1f, animationSpec = tween(700))
        titleAlpha.animateTo(1f, animationSpec = tween(700))
        subtitleAlpha.animateTo(1f, animationSpec = tween(700))

        delay(1700)

        // Exit logo section
        logoSectionAlpha.animateTo(0f, animationSpec = tween(400))

        // Sawid animation
        sawidAlpha.snapTo(1f)
        sawidScale.animateTo(5f, animationSpec = tween(1200))

        onNext()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (logoSectionAlpha.value > 0) {
            Column(
                modifier = Modifier.alpha(logoSectionAlpha.value),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground), // Replace with actual logo
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .alpha(logoAlpha.value)
                )
                Text(
                    text = "JanAgro",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32),
                    modifier = Modifier.alpha(titleAlpha.value)
                )
                Text(
                    text = "Your Farming Partner",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.alpha(subtitleAlpha.value)
                )
            }
        }

        if (sawidAlpha.value > 0) {
            Image(
                painter = painterResource(id = R.drawable.farmer), // Replace with actual sawid image if needed
                contentDescription = null,
                modifier = Modifier
                    .scale(sawidScale.value)
                    .alpha(sawidAlpha.value)
            )
        }
    }
}
