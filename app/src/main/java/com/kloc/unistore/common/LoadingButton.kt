package com.kloc.unistore.common

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Checkbox
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.kloc.unistore.entity.student.Student
import com.kloc.unistore.model.viewModel.MainViewModel
import com.kloc.unistore.navigation.Screen

// AJ : Implemented desaible functionality
@Composable
fun LoadingButton(
    text: String,
    isLoading: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonColor = when {
        !isEnabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f) // Lighter disabled button color
        !isLoading -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.error
    }

    val textColor = when {
        !isEnabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) // Lighter disabled text color
        else -> MaterialTheme.colorScheme.onPrimary
    }

    val buttonWidth by animateDpAsState(
        targetValue = if (isLoading) 56.dp else 280.dp,
        animationSpec = tween(durationMillis = 300, delayMillis = 50),
        label = "buttonWidth"
    )

    Button(
        onClick = { if (!isLoading && isEnabled) onClick() },
        modifier = modifier.width(buttonWidth).height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            contentColor = textColor,
            disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f), // Lighter disabled container color
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) // Lighter disabled text color
        ),
        enabled = isEnabled && !isLoading
    ) {
        Box {
            if (!isLoading) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge,
                    color = textColor
                )
            } else {
                CircularProgressIndicator(
                    modifier = Modifier.padding(top = 12.dp).align(Alignment.Center).size(24.dp),
                    color = Color.Black,
                    strokeWidth = 2.dp
                )
            }
        }
    }
}