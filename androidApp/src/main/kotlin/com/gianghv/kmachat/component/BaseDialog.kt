package com.gianghv.kmachat.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import com.gianghv.kmachat.theme.Light_Teal
import com.gianghv.kmachat.theme.icons.Exclamation
import com.gianghv.kmachat.theme.icons.Shield

enum class DialogType {
    SUCCESS,
    FAIL,
    NOTICE,
    ERROR,
}

sealed class ButtonType {
    data class TextButton(
        val text: String,
        val onClick: () -> Unit,
    ) : ButtonType()

    data class PairButton(
        val primary: String,
        val secondary: String,
        val onPrimaryClick: () -> Unit,
        val onSecondaryClick: () -> Unit,
    ) : ButtonType()

    data class PrimaryButtons(
        val text: String,
        val onClick: () -> Unit,
    ) : ButtonType()
}

@Composable
fun DialogType.getIcon() {
    val icon: ImageVector
    val iconColor: Color

    when (this) {
        DialogType.SUCCESS -> {
            icon = Icons.Filled.Check // Replace with your success icon
            iconColor = Light_Teal // Use Light_Teal color
        }

        DialogType.FAIL -> {
            icon = Icons.Filled.Close // Replace with your fail icon
            iconColor = MaterialTheme.colorScheme.error // Use Deep_Pink color
        }

        DialogType.NOTICE -> {
            icon = Exclamation // Replace with your notice icon
            iconColor = MaterialTheme.colorScheme.error
        }

        DialogType.ERROR -> {
            icon = Exclamation // Replace with your error icon
            iconColor = MaterialTheme.colorScheme.error
        }
    }

    Box(
        modifier =
            Modifier
                .background(shape = CircleShape, color = iconColor)
                .padding(all = 24.dp)
                .size(60.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier =
                Modifier
                    .align(Alignment.Center)
                    .fillMaxSize()
                    .zIndex(1f)
                    .padding(all = 12.dp)
                    .background(color = Color.Transparent),
            tint = Color.Black,
        )
        Icon(
            imageVector = Shield,
            contentDescription = null,
            modifier =
                Modifier
                    .align(Alignment.Center)
                    .fillMaxSize(),
            tint = Color.White,
        )
    }
}

@Composable
fun ColumnScope.getButtons(buttonType: ButtonType) {
    when (buttonType) {
        is ButtonType.PairButton -> {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 32.dp),
            ) {
                PrimaryButton(
                    onClick = buttonType.onPrimaryClick,
                    modifier = Modifier.fillMaxWidth(),
                    text = { Text(text = buttonType.primary) },
                )

                TextButton(
                    onClick = buttonType.onSecondaryClick,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = buttonType.secondary)
                }
            }
        }

        is ButtonType.PrimaryButtons -> {
            PrimaryButton(
                onClick = buttonType.onClick,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                text = { Text(text = buttonType.text) },
            )
        }

        is ButtonType.TextButton -> {
            Row(
                modifier =
                    Modifier
                        .wrapContentSize()
                        .align(Alignment.CenterHorizontally)
                        .clickable(true) { buttonType.onClick() },
            ) {
                TextButton(
                    onClick = buttonType.onClick,
                    modifier =
                        Modifier
                            .align(Alignment.CenterVertically)
                            .wrapContentSize(),
                ) {
                    Text(text = buttonType.text)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    modifier =
                        Modifier
                            .size(24.dp)
                            .align(Alignment.CenterVertically),
                )
            }
        }
    }
}

@Composable
fun BaseNoticeDialog(
    type: DialogType = DialogType.NOTICE,
    title: String,
    text: String,
    cancelable: Boolean = false,
    onCancelRequest: () -> Unit = {},
    buttonType: ButtonType,
) {
    Dialog(
        onDismissRequest = onCancelRequest,
        properties =
            DialogProperties(
                dismissOnBackPress = cancelable,
                dismissOnClickOutside = cancelable,
            ),
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier =
                Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                type.getIcon()

                Spacer(modifier = Modifier.height(32.dp))

                // Title
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Text
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                )

                Spacer(modifier = Modifier.height(32.dp))

                getButtons(buttonType)
            }
        }
    }
}
