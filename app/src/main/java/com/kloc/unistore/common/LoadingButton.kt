package com.kloc.unistore.common

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoadingButton(text: String, isLoading: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val buttonColor = if (!isLoading) { MaterialTheme.colorScheme.primary } else { MaterialTheme.colorScheme.error }
    val buttonWidth by animateDpAsState(
        targetValue = if (isLoading) 56.dp else 280.dp,
        animationSpec = tween(durationMillis = 300, delayMillis = 50),
        label = "buttonWidth"
    )

    Button(
        onClick = { if (!isLoading) onClick() },
        modifier = modifier.width(buttonWidth).height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.secondary,
            disabledContentColor = MaterialTheme.colorScheme.onSecondary
        ),
        enabled = !isLoading
    ) {
        Box {
            if (!isLoading) { Text(text = text, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimary) } else {
                CircularProgressIndicator(
                    modifier = Modifier.padding(top = 12.dp).align(Alignment.Center).size(24.dp),
                    color = MaterialTheme.colorScheme.background,
                    strokeWidth = 2.dp
                )
            }
        }
    }
}