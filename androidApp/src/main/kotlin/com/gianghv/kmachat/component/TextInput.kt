package com.gianghv.kmachat.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.gianghv.kmachat.theme.Grey_50
import com.gianghv.kmachat.theme.Typography
import com.gianghv.kmachat.theme.icons.Visibility
import com.gianghv.kmachat.theme.icons.VisibilityOff

val KeyboardShownKey = SemanticsPropertyKey<Boolean>("KeyboardShownKey")
var SemanticsPropertyReceiver.keyboardShownProperty by KeyboardShownKey

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PasswordField(
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    textFieldState: MutableState<TextFieldValue>? = null,
    onTextChanged: ((String) -> Unit)? = null,
    hint: String? = null,
    default: String? = null,
    onImeAction: ((String) -> Unit)? = null,
    clearOnAction: Boolean = false,
    readOnly: Boolean = false,
    focusState: MutableState<Boolean>? = null,
    modifier: Modifier = Modifier,
    validator: (String) -> String? = { null },
    errorText: ((@Composable (String) -> Unit))? = {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = it,
            modifier = Modifier.padding(horizontal = 2.dp),
            style = Typography.titleSmall.copy(fontStyle = FontStyle.Italic),
            color = MaterialTheme.colorScheme.error,
        )
    },
    shape: Shape = RoundedCornerShape(size = 8.dp),
    leadingIcon: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val visualTransformation =
        if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()

    BaseInputText(
        keyboardOptions = keyboardOptions,
        description = "Password",
        default = default,
        textFieldState = textFieldState,
        onTextChanged = onTextChanged,
        onImeAction = onImeAction,
        clearOnAction = clearOnAction,
        focusState = focusState,
        modifier = modifier,
        validator = validator,
        errorText = errorText,
        shape = shape,
        enable = !readOnly,
        readOnly = readOnly,
        trailingIcon = {
            val image = if (passwordVisible) Visibility else VisibilityOff
            IconButton(onClick = {
                passwordVisible = !passwordVisible
            }) {
                Icon(imageVector = image, contentDescription = null)
            }
        },
        leadingIcon = leadingIcon,
        visualTransformation = visualTransformation,
        onClick = onClick,
        hint = hint,
    )
}

@ExperimentalFoundationApi
@Composable
fun BaseInputText(
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    default: String? = null,
    hint: String? = null,
    description: String? = null,
    textFieldState: MutableState<TextFieldValue>? = null,
    onTextChanged: ((String) -> Unit)? = null,
    onImeAction: ((String) -> Unit)? = null,
    clearOnAction: Boolean = false,
    focusState: MutableState<Boolean>? = null,
    readOnly: Boolean = false,
    enable: Boolean = true,
    modifier: Modifier = Modifier,
    maxLines: Int = 1,
    minLines: Int = 1,
    validator: (String) -> String? = { null },
    errorText: ((@Composable (String) -> Unit))? = {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = it,
            modifier = Modifier.padding(horizontal = 2.dp),
            style = Typography.titleSmall.copy(fontStyle = FontStyle.Italic),
            color = MaterialTheme.colorScheme.error,
        )
    },
    visualTransformation: VisualTransformation = VisualTransformation.None,
    shape: Shape = RoundedCornerShape(size = 8.dp),
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    val a11ylabel = description ?: "Input"

    val textFieldFocusState = focusState ?: remember { mutableStateOf(false) }

    val textState =
        textFieldState ?: rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue(default ?: ""))
        }

    var errorString by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    Column(
        modifier =
            modifier
                .wrapContentHeight()
                .fillMaxWidth(),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .align(Alignment.CenterVertically)
                    .clickable {
                        if (readOnly && !enable) onClick?.invoke()
                    },
            ) {
                BaseTextField(
                    textFieldValue = textState.value,
                    onTextChanged = {
                        textState.value = it
                        errorString = validator.invoke(it.text)
                        onTextChanged?.invoke(it.text)
                    },
                    onTextFieldFocused = { focused ->
                        textFieldFocusState.value = focused
                    },
                    keyboardOptions = keyboardOptions,
                    onImeAction = {
                        if (errorString == null) {
                            onImeAction?.invoke(textState.value.text)
                            if (clearOnAction) {
                                textState.value = TextFieldValue()
                            }
                            focusState?.value = false
                        }
                    },
                    readOnly = readOnly,
                    enable = enable,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .semantics {
                                contentDescription = a11ylabel
                                keyboardShownProperty = textFieldFocusState.value
                            },
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                    maxLines = maxLines,
                    minLines = minLines,
                    visualTransformation = visualTransformation,
                    shape = shape,
                    placeholder = {
                        hint?.let {
                            Text(text = it)
                        }
                    },
                )
            }
        }

        if (errorString != null) {
            errorText?.invoke(errorString ?: "")
        }
    }
}

@Composable
fun BoxScope.BaseTextField(
    textFieldValue: TextFieldValue,
    onTextChanged: (TextFieldValue) -> Unit,
    onTextFieldFocused: (Boolean) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onImeAction: (String) -> Unit,
    readOnly: Boolean = false,
    enable: Boolean = true,
    modifier: Modifier = Modifier,
    leadingIcon:
        @Composable()
        (() -> Unit)? = null,
    trailingIcon:
        @Composable()
        (() -> Unit)? = null,
    maxLines: Int = 1,
    singleLine: Boolean = maxLines <= 1,
    minLines: Int = 1,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    shape: Shape = RoundedCornerShape(size = 16.dp),
    placeholder: @Composable (() -> Unit)? = null,
) {
    var lastFocusState by remember { mutableStateOf(false) }

    val keyboardController = rememberKeyboardController()
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = textFieldValue,
        onValueChange = { onTextChanged(it) },
        modifier =
            modifier.onFocusChanged { state ->
                if (lastFocusState != state.isFocused) {
                    onTextFieldFocused(state.isFocused)
                }
                lastFocusState = state.isFocused
            },
        enabled = enable,
        keyboardOptions = keyboardOptions,
        keyboardActions =
            KeyboardActions {
                if (textFieldValue.text.isNotBlank()) {
                    onImeAction(textFieldValue.text)
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }
            },
        readOnly = readOnly,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        trailingIcon = trailingIcon,
        leadingIcon = leadingIcon,
        textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current),
        visualTransformation = visualTransformation,
        shape = shape,
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = Grey_50,
            ),
        placeholder = placeholder,
    )
}

private fun TextFieldValue.addText(newString: String): TextFieldValue {
    val newText =
        this.text.replaceRange(
            this.selection.start,
            this.selection.end,
            newString,
        )
    val newSelection =
        TextRange(
            start = newText.length,
            end = newText.length,
        )

    return this.copy(text = newText, selection = newSelection)
}

@Composable
fun rememberKeyboardController(): SoftwareKeyboardController? = LocalSoftwareKeyboardController.current

@Composable
fun hideKeyboardAndClearFocus(
    focusManager: FocusManager = LocalFocusManager.current,
    keyboardController: SoftwareKeyboardController? = rememberKeyboardController(),
) {
    keyboardController?.hide()
    focusManager.clearFocus()
}
