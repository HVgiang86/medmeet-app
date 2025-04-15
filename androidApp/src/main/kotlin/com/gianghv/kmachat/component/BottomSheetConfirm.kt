package com.gianghv.kmachat.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmBottomSheet(
    title: String,
    content: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmButtonText: String,
    dismissButtonText: String,
    cornerRadius: Dp = 24.dp,
    showHandle: Boolean = false,
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val sheetState =
        rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
            confirmValueChange = { it != SheetValue.PartiallyExpanded },
        )

    ModalBottomSheet(
        onDismissRequest = {
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                    onDismiss()
                }
            }
        },
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            if (showHandle) {
                BottomSheetDefaults.DragHandle()
            }
        },
    ) {
        val paddingTop = if (showHandle) 0.dp else 16.dp

        Column(
            modifier =
                Modifier
                    .padding(bottom = 32.dp, top = paddingTop)
                    .padding(horizontal = 32.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .heightIn(max = 500.dp),
        ) {
            // Title
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2, // Limit title lines
                overflow = TextOverflow.Ellipsis, // Truncate with ellipsis
            )

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.surfaceTint)

            Spacer(modifier = Modifier.height(32.dp))

            // Content
            Text(
                text = content,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState),
                // Make content scrollable
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 5, // Limit content lines
                overflow = TextOverflow.Ellipsis, // Truncate with ellipsis
            )

            // Buttons
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(top = 32.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Dismiss Button

                SecondaryButton(onClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            onDismiss()
                        }
                    }
                }, text = {
                    Text(
                        text = dismissButtonText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }, modifier = Modifier.weight(1f))

                Spacer(modifier = Modifier.width(32.dp))

                PrimaryButton(
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                onConfirm()
                            }
                        }
                    },
                    text = {
                        Text(
                            text = confirmButtonText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmBottomSheet(
    title: String,
    content: @Composable () -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmButtonText: String,
    dismissButtonText: String,
    cornerRadius: Dp = 24.dp,
    showHandle: Boolean = false,
) {
    val scope = rememberCoroutineScope()

    val sheetState =
        rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
            confirmValueChange = { it != SheetValue.PartiallyExpanded },
        )

    ModalBottomSheet(
        onDismissRequest = {
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                    onDismiss()
                }
            }
        },
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        val paddingTop = if (showHandle) 0.dp else 16.dp

        Column(
            modifier =
                Modifier
                    .padding(bottom = 32.dp, top = paddingTop)
                    .padding(horizontal = 32.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .heightIn(max = 500.dp),
        ) {
            // Title
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2, // Limit title lines
                overflow = TextOverflow.Ellipsis, // Truncate with ellipsis
            )

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.surfaceTint)

            Spacer(modifier = Modifier.height(32.dp))

            // Content
            content()

            // Buttons
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(top = 32.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Dismiss Button

                SecondaryButton(onClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            onDismiss()
                        }
                    }
                }, text = {
                    Text(
                        text = dismissButtonText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }, modifier = Modifier.weight(1f))

                Spacer(modifier = Modifier.width(32.dp))

                PrimaryButton(
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                onConfirm()
                            }
                        }
                    },
                    text = {
                        Text(
                            text = confirmButtonText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
