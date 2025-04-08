package com.gianghv.kmachat.component


import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun LoadingDialog(showDialog: MutableState<Boolean>) {

    if (showDialog.value) {
        Dialog(
            onDismissRequest = { }, DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun LoadingDialog() {
    Dialog(
        onDismissRequest = { }, DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        CircularProgressIndicator()
    }
}
