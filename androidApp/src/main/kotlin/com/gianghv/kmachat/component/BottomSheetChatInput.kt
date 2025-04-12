package com.gianghv.kmachat.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.gianghv.kmachat.R
import com.gianghv.kmachat.theme.Grey_300

@Composable
fun ChatInputSection(
    modifier: Modifier = Modifier,
    onMessageSent: (String) -> Unit = {},
    onExpandRequest: (String) -> Unit = {},
    resetScroll: () -> Unit = {},
    textState: MutableState<TextFieldValue>? = null,
    onTextChange: (TextFieldValue) -> Unit = {},
) {
    val temp = rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    val inputState = textState ?: temp

    var lastFocusState by remember { mutableStateOf(false) }

    val keyboardController = rememberKeyboardController()
    val focusManager = LocalFocusManager.current

    // Used to decide if the keyboard should be shown
    var textFieldFocusState by remember { mutableStateOf(false) }

    Surface (
        color = Color.White,
        shadowElevation = 6.dp,
        shape = RoundedCornerShape(
            topStart = 32.dp, topEnd = 32.dp, bottomEnd = 0.dp, bottomStart = 0.dp
        ),
        modifier = modifier
            .wrapContentHeight()
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp)
                .padding(bottom = 32.dp)
        ) {
            TextField(
                value = inputState.value,
                modifier = Modifier
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
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions {
                    if (inputState.value.text.isNotBlank()) {
                        onMessageSent(inputState.value.text)
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                },
                enabled = true,
                placeholder = { Text("Message something...") },
                maxLines = 5,
                singleLine = false,
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary,
                )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .wrapContentSize()
                        .align(Alignment.CenterVertically)
                ) {
                    IconButton(modifier = Modifier
                        .padding(8.dp)
                        .background(
                            color = Color.White, shape = CircleShape
                        )
                        .border(
                            width = 1.dp, color = Grey_300, shape = CircleShape
                        )
                        .align(Alignment.CenterVertically), onClick = {

                    }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Row(
                        modifier = Modifier
                            .background(
                                color = Color.White, shape = CircleShape
                            )
                            .border(
                                width = 1.dp, color = Grey_300, shape = RoundedCornerShape(50)
                            )
                            .align(Alignment.CenterVertically)

                    ) {
                        IconButton(modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .background(
                                color = Color.White, shape = CircleShape
                            ).align(Alignment.CenterVertically), onClick = {
                            if (inputState.value.text.isNotBlank()) {
                                onMessageSent(inputState.value.text)
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            }
                        }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_global_search),
                                contentDescription = null
                            )
                        }

                        Text(
                            text = "Search",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .wrapContentSize().align(Alignment.CenterVertically).padding(end = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier
                        .wrapContentSize()
                        .align(Alignment.CenterVertically)
                ) {

                }
            }
        }
    }
}