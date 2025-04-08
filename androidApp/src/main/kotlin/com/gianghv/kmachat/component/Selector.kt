package com.gianghv.kmachat.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp


@Composable
fun <T> ListPickerDialog(
    title: String,
    items: List<T>,
    default: List<T>,
    onItemSelected: (T, Boolean) -> Unit = { _, _ -> },
    onDismiss: () -> Unit = {},
    dismissOnSelect: Boolean = true,
    onConfirm: (List<T>) -> Unit,
    multiple: Boolean = false,
    itemToString: (T) -> String = { it.toString() },
    checkContain: (List<T>, T) -> Boolean = { list, item -> list.contains(item) },
) {
    val selectedItems = remember { mutableStateListOf<T>() }
    selectedItems.addAll(default.toMutableList())

    AlertDialog(onDismissRequest = onDismiss, title = {
        Text(title, style = MaterialTheme.typography.headlineSmall)
    }, text = {
        LazyColumn {
            items(items) { item ->
                val isSelected = checkContain(selectedItems, item)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {

                            if (multiple) {
                                if (isSelected) {
                                    selectedItems.remove(item)
                                } else {
                                    selectedItems.add(item)
                                }
                                onItemSelected(item, !isSelected)
                            } else {
                                selectedItems.clear()
                                selectedItems.add(item)
                                onItemSelected(item, !isSelected)
                                onConfirm(selectedItems)

                                if (dismissOnSelect) {
                                    onDismiss()
                                }
                            }

                        }
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween // Align items to start and tick to end
                ) {
                    Text(
                        text = itemToString(item),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .padding(vertical = 4.dp)
                    )
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check, // Use the tick icon
                            contentDescription = "Selected",
                            tint = MaterialTheme.colorScheme.primary // Tint the icon with primary color
                        )
                    }
                }
            }
        }
    }, confirmButton = {
        Button(onClick = { onConfirm(selectedItems) }) {
            Text("OK", style = MaterialTheme.typography.bodyMedium)
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text("Cancel", style = MaterialTheme.typography.bodyMedium)
        }
    })
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> SearchListPickerDialog(
    title: String,
    items: List<T>,
    default: List<T>,
    onItemSelected: (T, Boolean) -> Unit = { _, _ -> },
    onDismiss: () -> Unit = {},
    dismissOnSelect: Boolean = true,
    onConfirm: (List<T>) -> Unit,
    multiple: Boolean = false,
    itemToString: (T) -> String = { it.toString() },
    checkContain: (List<T>, T) -> Boolean = { list, item -> list.contains(item) },
) {
    val selectedItems = remember { mutableStateListOf<T>() }
    selectedItems.addAll(default.toMutableList())

    var searchText by remember { mutableStateOf("") } // State to hold the search text
    val filteredItems = remember(items, searchText) { // Filtered items based on search
        if (searchText.isBlank()) {
            items
        } else {
            items.filter { item ->
                itemToString(item).contains(searchText, ignoreCase = true)
            }
        }
    }

    AlertDialog(
        modifier = Modifier
            .padding(vertical = 32.dp)
            .fillMaxWidth(),
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(title, style = MaterialTheme.typography.headlineSmall)
                BaseInputText(
                    default = searchText,
                    hint = "Search...",
                    onTextChanged = { searchText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search,
                        keyboardType = KeyboardType.Text
                    ),
                )
            }
        },
        text = {
            LazyColumn {
                items(filteredItems) { item -> // Use filteredItems here
                    val isSelected = checkContain(selectedItems, item)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (multiple) {
                                    if (isSelected) {
                                        selectedItems.remove(item)
                                    } else {
                                        selectedItems.add(item)
                                    }
                                    onItemSelected(item, !isSelected)
                                } else {
                                    selectedItems.clear()
                                    selectedItems.add(item)
                                    onItemSelected(item, !isSelected)
                                    onConfirm(selectedItems)

                                    if (dismissOnSelect) {
                                        onDismiss()
                                    }
                                }
                            }
                            .padding(vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = itemToString(item),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .padding(vertical = 4.dp)
                        )
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(selectedItems) }) {
                Text("OK", style = MaterialTheme.typography.bodyMedium)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", style = MaterialTheme.typography.bodyMedium)
            }
        })
}
