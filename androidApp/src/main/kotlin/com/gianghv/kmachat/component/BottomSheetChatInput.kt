package com.gianghv.kmachat.component

import android.view.ViewTreeObserver
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gianghv.kmachat.R
import com.gianghv.kmachat.theme.Grey_500
import com.gianghv.kmachat.theme.Light_Teal
import io.github.aakira.napier.Napier

@Composable
fun ChatInputSection(
    modifier: Modifier = Modifier,
    onMessageSent: (String) -> Unit = {},
    onExpandRequest: (String) -> Unit = {},
    resetScroll: () -> Unit = {},
    textState: MutableState<TextFieldValue>? = null,
    onTextChange: (TextFieldValue) -> Unit = {},
    onMicrophoneClick: () -> Unit = {},
    onReasonEnable: (Boolean) -> Unit = {},
    onSearchEnable: (Boolean) -> Unit = {},
) {
    val temp =
        rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue(""))
        }

    val inputState = textState ?: temp

    var lastFocusState by remember { mutableStateOf(false) }

    val keyboardController = rememberKeyboardController()
    val focusManager = LocalFocusManager.current

    // Used to decide if the keyboard should be shown
    var textFieldFocusState by remember { mutableStateOf(false) }

    // Add keyboard visibility listener
    val view = LocalView.current
    DisposableEffect(view) {
        val listener =
            ViewTreeObserver.OnGlobalLayoutListener {
                val isKeyboardOpen =
                    ViewCompat.getRootWindowInsets(view)?.isVisible(WindowInsetsCompat.Type.ime())
                        ?: false

                if (!isKeyboardOpen && textFieldFocusState) {
                    // Keyboard was closed, clear focus
                    focusManager.clearFocus()
                    textFieldFocusState = false
                }
            }

        view.viewTreeObserver.addOnGlobalLayoutListener(listener)
        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
    }

    val clearText = {
        Napier.d { "clearText" }
        focusManager.clearFocus()
        keyboardController?.hide()
        textFieldFocusState = false
        inputState.value = TextFieldValue("")
    }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 6.dp,
        shape =
            RoundedCornerShape(
                topStart = 32.dp,
                topEnd = 32.dp,
                bottomEnd = 0.dp,
                bottomStart = 0.dp,
            ),
        modifier =
            modifier
                .wrapContentHeight()
                .fillMaxWidth(),
        border =
            BorderStroke(
                width = 3.dp,
                color = Color(0, 0, 0, 10),
            ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(8.dp)
                    .padding(bottom = if (textFieldFocusState) 16.dp else 32.dp),
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
            ) {
                TextField(
                    value = inputState.value,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .onFocusChanged { state ->
                                if (lastFocusState != state.isFocused) {
                                    textFieldFocusState = state.isFocused
                                }
                                lastFocusState = state.isFocused
                            },
                    onValueChange = {
                        inputState.value = it
                        onTextChange(it)
                        if (it.text.isEmpty()) {
                            resetScroll()
                        }
                    },
                    enabled = true,
                    placeholder = {
                        Text(
                            "Message something...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Grey_500,
                        )
                    },
                    maxLines = 5,
                    singleLine = false,
                    shape = RoundedCornerShape(16.dp),
                    colors =
                        TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            cursorColor = MaterialTheme.colorScheme.primary,
                        ),
                    textStyle = MaterialTheme.typography.bodyMedium,
                )
            }

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 16.dp),
            ) {
                Row(
                    modifier =
                        Modifier
                            .wrapContentSize()
                            .align(Alignment.CenterVertically),
                ) {
                    ChatActionItem(actionIcon = Icons.Default.Add, onActionClick = {
                    })

                    Spacer(modifier = Modifier.width(8.dp))

                    ChatActionItem(
                        actionIcon = ImageVector.vectorResource(R.drawable.ic_global_search),
                        actionText = "Search",
                        onActionClick = {
                            onSearchEnable(it)
                        },
                        isToggleItem = true,
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    ChatActionItem(
                        actionIcon = ImageVector.vectorResource(R.drawable.ic_lamp_on),
                        actionText = "Reason",
                        onActionClick = {
                            onReasonEnable(it)
                        },
                        isToggleItem = true,
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier =
                        Modifier
                            .wrapContentSize()
                            .align(Alignment.CenterVertically),
                ) {
                    ChatActionItem(
                        actionIcon = ImageVector.vectorResource(R.drawable.ic_microphone),
                        onActionClick = {
                            onMicrophoneClick()
                        },
                    )

                    if (inputState.value.text.isNotBlank()) {
                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier =
                                Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = CircleShape,
                                    ).align(Alignment.CenterVertically)
                                    .clickable {
                                        onMessageSent(inputState.value.text)
                                        clearText()
                                    },
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_up),
                                contentDescription = null,
                                tint = Color.White,
                                modifier =
                                    Modifier
                                        .padding(4.dp)
                                        .clickable {
                                            onMessageSent(inputState.value.text)
                                            clearText()
                                        },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.ChatActionItem(
    modifier: Modifier = Modifier,
    actionIcon: ImageVector,
    actionText: String? = null,
    onActionClick: (Boolean) -> Unit = {},
    isToggleItem: Boolean = false,
) {
    val toggleState = rememberSaveable { mutableStateOf(false) }

    val contentTintColor =
        if (isToggleItem) {
            if (toggleState.value) {
                MaterialTheme.colorScheme.primary
            } else {
                LocalContentColor.current
            }
        } else {
            LocalContentColor.current
        }

    val containerColor =
        if (isToggleItem) {
            Light_Teal
            if (toggleState.value) {
                Light_Teal
            } else {
                Color.White
            }
        } else {
            Color.White
        }

    Row(
        modifier =
            modifier
                .background(
                    color = containerColor,
                    shape = CircleShape,
                ).border(
                    width = 1.dp,
                    color = Grey_500,
                    shape = RoundedCornerShape(50),
                ).align(Alignment.CenterVertically)
                .clickable {
                    if (isToggleItem) {
                        toggleState.value = !toggleState.value
                    }
                    onActionClick(toggleState.value)
                },
    ) {
        Box(
            modifier =
                Modifier
                    .padding(horizontal = if (actionText != null) 4.dp else 0.dp)
                    .background(
                        color = Color.Transparent,
                        shape = CircleShape,
                    ).align(Alignment.CenterVertically),
        ) {
            Icon(
                imageVector = actionIcon,
                contentDescription = null,
                modifier = Modifier.padding(4.dp),
                tint = contentTintColor,
            )
        }

        if (actionText != null) {
            Text(
                text = actionText,
                style = MaterialTheme.typography.bodyMedium,
                color = contentTintColor,
                modifier =
                    Modifier
                        .wrapContentSize()
                        .align(Alignment.CenterVertically)
                        .padding(end = 8.dp),
            )
        }
    }
}
