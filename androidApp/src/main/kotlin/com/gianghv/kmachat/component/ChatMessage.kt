package com.gianghv.kmachat.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gianghv.kmachat.R
import com.gianghv.kmachat.shared.core.datasource.network.mockMsg
import com.gianghv.kmachat.shared.core.entity.Message
import com.halilibo.richtext.commonmark.Markdown
import com.halilibo.richtext.ui.material3.RichText

@Composable
fun HumanChatMessage(
    message: Message,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .wrapContentWidth()
            .fillMaxWidth(0.7f)
            .wrapContentHeight()
            .padding(horizontal = 24.dp)
            .padding(vertical = 8.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 20.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 20.dp
            ), color = MaterialTheme.colorScheme.primary
        ) {
            Text(
                text = message.content ?: "",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(12.dp),
                textAlign = TextAlign.Justify
            )
        }
    }
}

@Composable
fun BotMessage(
    message: Message,
    modifier: Modifier = Modifier,
) {
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 24.dp)
            .padding(vertical = 8.dp), horizontalAlignment = Alignment.Start
    ) {
        RichText {
            Markdown(message.content ?: "")
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 4.dp),
            color = MaterialTheme.colorScheme.outline,
            thickness = 1.dp
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(bottom = 4.dp)
        ) {
            IconButton(modifier = Modifier.size(24.dp), onClick = {
                // Handle copy to clipboard action with annotation string
                message.getCopyableText().let { content ->
                    clipboardManager.setText(AnnotatedString(content))
                }
            }) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_copy),
                    contentDescription = "Copy to clipboard"
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            IconButton(modifier = Modifier.size(24.dp), onClick = {

            }) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_repeat),
                    contentDescription = "Re-generate"
                )
            }

        }
    }
}

@Composable
fun WavingDots(
    modifier: Modifier = Modifier,
    dotCount: Int = 3,
    dotSize: Dp = 8.dp,
    dotSpacing: Dp = 4.dp,
    animationDuration: Int = 300,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val alphaValues = List(dotCount) { index ->
        infiniteTransition.animateFloat(
            initialValue = 0.3f, targetValue = 1f, animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = animationDuration,
                    delayMillis = index * animationDuration / dotCount,
                    easing = LinearEasing
                ), repeatMode = RepeatMode.Reverse
            ), label = "DotAlpha$index"
        )
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(dotSpacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(dotCount) { index ->
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .graphicsLayer(alpha = alphaValues[index].value)
                    .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WavingDotsPreview() {
    WavingDots()
}

@Composable
fun GeneratingIndicator(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 24.dp)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Generating...", style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.width(8.dp))
        WavingDots(
            dotCount = 3, dotSize = 6.dp, dotSpacing = 4.dp, animationDuration = 300
        )
    }
}

// Preview
@Preview(showBackground = true)
@Composable
fun GeneratingIndicatorPreview() {
    GeneratingIndicator()
}

@Preview
@Composable
fun BotChatPreview() {
    BotMessage(message = mockMsg)
}

@Preview(showBackground = true)
@Composable
fun HumanChatPreview() {
    HumanChatMessage(message = mockMsg)
}