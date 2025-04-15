package com.gianghv.kmachat.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.gianghv.kmachat.theme.icons.ArrowRight

@Composable
fun PrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: @Composable () -> Unit,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    shape: Shape = ButtonDefaults.shape,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        contentPadding = contentPadding,
        shape = shape,
    ) {
        text()
    }
}

@Composable
fun SecondaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: @Composable () -> Unit,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    shape: Shape = ButtonDefaults.shape,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors =
            ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            ),
        contentPadding = contentPadding,
        shape = shape,
    ) {
        text()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlackButtonIconEnd(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enable: Boolean = true,
    text: @Composable () -> Unit,
) {
    FilledTextButton(
        onClick,
        modifier = modifier.fillMaxWidth(),
        colors =
            ButtonColors(
                containerColor = Color.Black,
                contentColor = Color.White,
                disabledContentColor = MaterialTheme.colorScheme.onTertiary,
                disabledContainerColor = MaterialTheme.colorScheme.tertiary,
            ),
        enable = enable,
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
            text()
            Icon(
                imageVector = ArrowRight,
                contentDescription = null,
                modifier =
                    Modifier
                        .width(24.dp)
                        .align(Alignment.CenterEnd),
            )
        }
    }
}

@Composable
fun FilledTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enable: Boolean = true,
    colors: ButtonColors = ButtonDefaults.textButtonColors(),
    text: @Composable () -> Unit,
) {
    Button(
        enabled = enable,
        onClick = {
            onClick.invoke()
        },
        shape = RoundedCornerShape(8.dp),
        modifier = modifier,
        colors = colors,
    ) {
        text()
    }
}
