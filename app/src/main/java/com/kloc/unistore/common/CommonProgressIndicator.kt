package com.kloc.unistore.common

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
@SuppressLint("SuspiciousIndentation")
@Composable
fun CommonProgressIndicator(message: String = "Loading", buttonName: String = "Submit", onClick: (() -> Unit)? = null) {
    var dot by remember { mutableStateOf("") }
    LaunchedEffect(Unit) { while (true) { dot = if (dot.length < 3) "$dot." else "";delay(500) } }
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black.copy(alpha = 0.7f)), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            CircularProgressIndicator(color = Color.White, strokeWidth = 5.dp, trackColor = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))
            BasicText(text = "$message$dot", style = TextStyle(color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center))
            if (onClick != null) {
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onClick, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) { Text(text = buttonName, color = Color.White) }
            }
        }
    }
}