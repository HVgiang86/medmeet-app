package com.huongmt.medmeet.ui.healthrecord

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.huongmt.medmeet.component.BaseInputText
import com.huongmt.medmeet.component.PrimaryButton
import com.huongmt.medmeet.component.SecondaryButton
import com.huongmt.medmeet.shared.core.entity.BloodType
import com.huongmt.medmeet.shared.core.entity.HealthRecord
import com.huongmt.medmeet.theme.Grey_200
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun EditHealthRecordBottomSheet(
    healthRecord: HealthRecord? = null,
    onSave: (height: Int?, weight: Int?, bloodType: BloodType?, healthHistory: String?) -> Unit,
    onDismiss: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { it != SheetValue.PartiallyExpanded },
    )

    var weight by remember { mutableIntStateOf(healthRecord?.weight ?: 0) }
    var height by remember { mutableIntStateOf(healthRecord?.height ?: 0) }
    var bloodType by remember { mutableStateOf(healthRecord?.getBloodType()) }
    var healthHistory by remember { mutableStateOf(healthRecord?.healthHistory ?: "") }

    ModalBottomSheet(sheetState = sheetState, onDismissRequest = {
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                onDismiss()
            }
        }
    }, dragHandle = {

    }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Edit Health Record",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Height
            Text(
                text = "Height (cm)",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            BaseInputText(
                default = height.toString(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .height(70.dp),
                hint = "Height",
                description = "Height",
                onTextChanged = {
                    // Only accept numbers
                    if (it.isNotEmpty() && it.all { char -> char.isDigit() }) {
                        height = it.toInt()
                    } else if (it.isEmpty()) {
                        height = 0
                    } else {
                        height = 0
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Number),
                onImeAction = {
                    focusManager.clearFocus(true)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Weight
            Text(
                text = "Weight (kg)",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            BaseInputText(
                default = weight.toString(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .height(70.dp),
                hint = "Weight",
                description = "Weight",
                onTextChanged = {
                    // Only accept numbers
                    if (it.isNotEmpty() && it.all { char -> char.isDigit() }) {
                        weight = it.toInt()
                    } else if (it.isEmpty()) {
                        weight = 0
                    } else {
                        weight = 0
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Number),
                onImeAction = {
                    focusManager.clearFocus(true)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Blood Type
            Text(
                text = "Blood Type",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Grid of blood type options
            BloodTypeSelector(
                selectedBloodType = bloodType ?: BloodType.NA,
                onBloodTypeSelected = {
                    bloodType = it
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Health History
            Text(
                text = "Health History",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            BaseInputText(
                default = healthHistory,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .height(70.dp),
                hint = "Health History",
                description = "Health History",
                onTextChanged = {
                    healthHistory = it
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Text),
                onImeAction = {
                    focusManager.clearFocus(true)
                },
                maxLines = 5,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Save and Cancel buttons
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SecondaryButton(onClick = onDismiss, modifier = Modifier.weight(1f), text = {
                    Text("Cancel")
                })

                Spacer(modifier = Modifier.width(16.dp))

                PrimaryButton(onClick = {
                    onSave(
                        height,
                        weight,
                        bloodType,
                        healthHistory
                    )
                    scope.launch { sheetState.hide() }
                }, modifier = Modifier.weight(1f), text = {
                    Text("Save")
                })
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun BloodTypeSelector(
    selectedBloodType: BloodType,
    onBloodTypeSelected: (BloodType) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BloodTypeOption(
                bloodType = BloodType.A_POS,
                isSelected = selectedBloodType == BloodType.A_POS,
                onSelected = onBloodTypeSelected,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            BloodTypeOption(
                bloodType = BloodType.A_NEG,
                isSelected = selectedBloodType == BloodType.A_NEG,
                onSelected = onBloodTypeSelected,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BloodTypeOption(
                bloodType = BloodType.B_POS,
                isSelected = selectedBloodType == BloodType.B_POS,
                onSelected = onBloodTypeSelected,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            BloodTypeOption(
                bloodType = BloodType.B_NEG,
                isSelected = selectedBloodType == BloodType.B_NEG,
                onSelected = onBloodTypeSelected,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BloodTypeOption(
                bloodType = BloodType.AB_POS,
                isSelected = selectedBloodType == BloodType.AB_POS,
                onSelected = onBloodTypeSelected,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            BloodTypeOption(
                bloodType = BloodType.AB_NEG,
                isSelected = selectedBloodType == BloodType.AB_NEG,
                onSelected = onBloodTypeSelected,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BloodTypeOption(
                bloodType = BloodType.O_POS,
                isSelected = selectedBloodType == BloodType.O_POS,
                onSelected = onBloodTypeSelected,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            BloodTypeOption(
                bloodType = BloodType.O_NEG,
                isSelected = selectedBloodType == BloodType.O_NEG,
                onSelected = onBloodTypeSelected,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun BloodTypeOption(
    bloodType: BloodType,
    isSelected: Boolean,
    onSelected: (BloodType) -> Unit,
    modifier: Modifier = Modifier,
) {
    val displayText = bloodType.text.replace("Nhóm máu ", "")

    Box(modifier = modifier
        .clip(RoundedCornerShape(8.dp))
        .border(
            width = 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else Grey_200,
            shape = RoundedCornerShape(8.dp)
        )
        .background(
            color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent,
            shape = RoundedCornerShape(8.dp)
        )
        .clickable { onSelected(bloodType) }
        .padding(8.dp), contentAlignment = Alignment.Center) {
        Text(
            text = displayText,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}