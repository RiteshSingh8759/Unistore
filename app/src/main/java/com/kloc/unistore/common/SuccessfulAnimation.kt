package com.kloc.unistore.common
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
@Composable
fun SuccessfulAnimation(message: String = "Successful!", onComplete: () -> Unit ) {
    var startAnimation by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (startAnimation) 1f else 0f, animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing), label = "")
    val checkmarkAlpha by animateFloatAsState(targetValue = if (startAnimation) 1f else 0f, animationSpec = tween(durationMillis = 600, delayMillis = 600, easing = FastOutSlowInEasing), label = "")
    val glow by animateFloatAsState(targetValue = if (startAnimation) 1.2f else 1f, animationSpec = infiniteRepeatable(animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Restart), label = "")
    val messageAlpha by animateFloatAsState(targetValue = if (startAnimation) 1f else 0f, animationSpec = tween(durationMillis = 600, delayMillis = 1200, easing = LinearEasing), label = "")
    LaunchedEffect(Unit) {
        startAnimation = true
        delay(3000)
        onComplete()
    }
    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.7f)), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Box(contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(120.dp).scale(scale)) {
                    drawCircle(color = Color.Green.copy(alpha = 0.3f), radius = size.minDimension / 2 * glow, style = Fill)
                    drawCircle(color = Color.Green, radius = size.minDimension / 2, style = Fill)
                    if (checkmarkAlpha > 0f) {
                        drawLine(color = Color.White.copy(alpha = checkmarkAlpha), start = Offset(size.width * 0.3f, size.height * 0.6f), end = Offset(size.width * 0.45f, size.height * 0.75f), strokeWidth = 8f)
                        drawLine(color = Color.White.copy(alpha = checkmarkAlpha), start = Offset(size.width * 0.45f, size.height * 0.75f), end = Offset(size.width * 0.7f, size.height * 0.4f), strokeWidth = 8f)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            if (messageAlpha > 0f) { BasicText(text = message, style = TextStyle(color = Color.White.copy(alpha = messageAlpha), fontSize = 22.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)) }
        }
    }
}