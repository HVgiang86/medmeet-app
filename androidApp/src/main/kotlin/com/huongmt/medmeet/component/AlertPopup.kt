package com.huongmt.medmeet.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.huongmt.medmeet.shared.base.BaseError
import com.huongmt.medmeet.shared.base.ErrorException

@Composable
fun SuccessDialog(
    title: String = "Success",
    content: String,
    state: MutableState<Boolean>,
    cancelable: Boolean = false,
    onCanceled: (() -> Unit) = {},
    onBtnClick: (() -> Unit) = {},
    btnText: String = "OK",
) {
    if (state.value) {
        BaseNoticeDialog(
            type = DialogType.SUCCESS,
            title = title,
            text = content,
            cancelable = cancelable,
            onCancelRequest = {
                onCanceled()
                state.value = false
            },
            buttonType = ButtonType.TextButton(text = btnText) {
                onBtnClick()
                state.value = false
            },
        )
    }
}

@Composable
fun SuccessDialog(
    title: String = "Success",
    content: String,
    cancelable: Boolean = false,
    onCanceled: (() -> Unit) = {},
    onBtnClick: (() -> Unit) = {},
    btnText: String = "OK",
) {
    BaseNoticeDialog(
        type = DialogType.SUCCESS,
        title = title,
        text = content,
        cancelable = cancelable,
        onCancelRequest = {
            onCanceled()
        },
        buttonType = ButtonType.TextButton(text = btnText) {
            onBtnClick()
        },
    )
}

@Composable
fun FailDialog(
    title: String = "Fail",
    content: String,
    state: MutableState<Boolean>,
    cancelable: Boolean = true,
    onCanceled: (() -> Unit) = {},
    onBtnClick: (() -> Unit) = {},
    btnText: String = "OK",
) {
    if (state.value) {
        FailDialog(
            title = title,
            content = content,
            cancelable = cancelable,
            onCanceled = {
                state.value = false
                onCanceled()
            },
            onBtnClick = {
                state.value = false
                onBtnClick()
            },
            btnText = btnText,
        )
    }
}

@Composable
fun FailDialog(
    title: String = "Fail",
    content: String,
    cancelable: Boolean = true,
    onCanceled: (() -> Unit) = {},
    onBtnClick: (() -> Unit) = {},
    btnText: String = "OK",
) {
    BaseNoticeDialog(
        type = DialogType.FAIL,
        title = title,
        text = content,
        cancelable = cancelable,
        onCancelRequest = {
            onCanceled()
        },
        buttonType = ButtonType.PrimaryButtons(text = btnText) {
            onBtnClick()
        },
    )
}

@Composable
fun ErrorDialog(
    throwable: Throwable?,
    onDismissRequest: () -> Unit = {},
    state: MutableState<Boolean>,
) {

    if (state.value) {
        ErrorDialog(
            throwable = throwable,
            onDismissRequest = {
                state.value = false
                onDismissRequest()
            },
        )
    }
}

@Composable
fun ErrorDialog(
    throwable: Throwable?,
    onDismissRequest: () -> Unit = {},
) {
    var message = ""
    var title = "Thông báo"
    when (throwable) {
        is ErrorException -> {
            when (val error = throwable.error) {
                BaseError.ConnectionTimeout -> {
                    title = "Không thành công"
                    message = "Connection time out!"
                }

                is BaseError.HttpError -> {
                    title = "Không thành công"
                    message = error.message
                }

                is BaseError.JsonConvertException -> {
                    title = "Không thành công"
                    message = "JSON convert fail!"
                }

                BaseError.NetworkError -> {
                    title = "Không thành công"
                    message = "Network error!"
                }

                BaseError.ServerError -> {
                    title = "Không thành công"
                    message = "Server error!"
                }

                BaseError.SessionExpired -> {
                    title = "Thông báo"
                    message = "Phiên đăng nhập đã hết hạn!"
                }

                is BaseError.UnknownError -> {
                    title = "Unknown error"
                    message = error.throwable.message ?: "Unknown error!"
                }

                is BaseError.ValidationException -> {
                    title = "Thông báo"
                    message = error.message
                }
            }
        }

        else -> {
            title = "Unknown error"
            message = throwable?.message ?: "Unknown error!"
        }
    }

    BaseNoticeDialog(
        type = DialogType.ERROR,
        title = title,
        text = message,
        cancelable = true,
        onCancelRequest = {
            onDismissRequest()
        },
        buttonType = ButtonType.PrimaryButtons(text = "OK") {
            onDismissRequest()
        },
    )
}

@Composable
fun MyAlertDialog(
    title: String,
    content: String,
    leftBtnTitle: String = "Huỷ",
    rightBtnTitle: String = "Ok",
    leftBtn: (() -> Unit)? = null,
    rightBtn: (() -> Unit)? = null,
    state: MutableState<Boolean>? = null,
    cancelable: Boolean = true,
    onCanceled: (() -> Unit) = {},
) {
    val openDialog = state ?: remember { mutableStateOf(true) }

    if (openDialog.value) {
        AlertDialog(onDismissRequest = {
            if (cancelable) {
                openDialog.value = false
                onCanceled()
            }
        }, title = {
            Text(
                text = title,
                modifier = Modifier.padding(top = 8.dp),
            )
        }, text = {
            Text(
                text = content,
                modifier = Modifier.padding(8.dp),
                maxLines = 8,
            )
        }, confirmButton = {
            if (rightBtn != null) {
                Button(onClick = {
                    // Handle confirm button click here
                    rightBtn.invoke()
                    openDialog.value = false
                }, shape = RoundedCornerShape(8.dp)) {
                    Text(rightBtnTitle)
                }
            } else {
                Button(onClick = {
                    // Handle confirm button click here
                    openDialog.value = false
                }, shape = RoundedCornerShape(8.dp)) {
                    Text("OK")
                }
            }
        }, dismissButton = {
            if (leftBtn != null) {
                TextButton(onClick = {
                    // Handle dismiss button click here
                    leftBtn.invoke()
                    onCanceled()
                    openDialog.value = false
                }) {
                    Text(leftBtnTitle)
                }
            }
        }, shape = RoundedCornerShape(8.dp))
    }
}
