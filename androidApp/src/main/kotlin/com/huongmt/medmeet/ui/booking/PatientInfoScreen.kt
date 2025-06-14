package com.huongmt.medmeet.ui.booking

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.huongmt.medmeet.R
import com.huongmt.medmeet.component.BaseInputText
import com.huongmt.medmeet.component.ListPickerDialog
import com.huongmt.medmeet.component.PopupDatePicker
import com.huongmt.medmeet.component.PrimaryButton
import com.huongmt.medmeet.component.SecondaryButton
import com.huongmt.medmeet.shared.app.BookingAction
import com.huongmt.medmeet.shared.app.BookingStep
import com.huongmt.medmeet.shared.app.BookingStepType
import com.huongmt.medmeet.shared.app.BookingStore
import com.huongmt.medmeet.shared.core.entity.Gender
import com.huongmt.medmeet.shared.core.entity.PatientInfo
import com.huongmt.medmeet.shared.utils.ext.nowDate
import com.huongmt.medmeet.shared.utils.validate.Validator.validateNotEmpty
import io.github.aakira.napier.Napier
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

@Composable
fun PatientInfoScreen(
    store: BookingStore,
    state: BookingStep.InputPatientInfo,
) {
    PatientInfoContent(
        store = store, state = state
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun PatientInfoContent(
    store: BookingStore,
    state: BookingStep.InputPatientInfo,
) {
    val dob = remember { mutableStateOf(Clock.System.todayIn(TimeZone.currentSystemDefault())) }
    val dobTextField = rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(dob.value.toString()))
    }

    val gender = remember { mutableStateOf(Gender.MALE) }
    val genderTextField = rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(gender.value.text))
    }

    val datePickerState = rememberDatePickerState()

    // Local state for form fields
    val name = rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    val phoneNumber = rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    val email = rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    val province = rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    val district = rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    val commune = rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    val address = rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    val examinationReason = rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    LaunchedEffect(state.patientInfo) {
        Napier.d { "PatientInfo Reload: ${state.patientInfo}" }

        state.patientInfo?.let { patientInfo ->
            name.value = TextFieldValue(patientInfo.name ?: "")
            phoneNumber.value = TextFieldValue(patientInfo.phoneNumber ?: "")
            email.value = TextFieldValue(patientInfo.email ?: "")

            gender.value = patientInfo.gender ?: Gender.MALE
            genderTextField.value = TextFieldValue(gender.value.text)

            dob.value = patientInfo.dateOfBirth ?: nowDate()
            dobTextField.value = TextFieldValue(dob.value.toString())

            province.value = TextFieldValue(patientInfo.province ?: "")
            district.value = TextFieldValue(patientInfo.district ?: "")
            commune.value = TextFieldValue(patientInfo.commune ?: "")
            address.value = TextFieldValue(patientInfo.address ?: "")

            examinationReason.value = TextFieldValue(patientInfo.examinationReason ?: "")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(bottom = 60.dp).imePadding()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                text = "Thông tin bệnh nhân",
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black,
                textAlign = TextAlign.Center,
            )

            // Name
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                text = "Vui lòng điền thông tin bên dưới",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Black,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Tên",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
            )

            Spacer(modifier = Modifier.height(4.dp))

            BaseInputText(modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done, keyboardType = KeyboardType.Text
            ), hint = "name", description = "Name", textFieldState = name, validator = {
                validateNotEmpty(it)
            }, leadingIcon = {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_email),
                    contentDescription = null
                )
            })

            // Email
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Email",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
            )

            Spacer(modifier = Modifier.height(4.dp))

            BaseInputText(modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done, keyboardType = KeyboardType.Email
            ), hint = "email", textFieldState = email, description = "Email", validator = {
                validateNotEmpty(it)
            }, leadingIcon = {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_email),
                    contentDescription = null
                )
            })

            // Phone number
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Số điện thoại",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
            )

            Spacer(modifier = Modifier.height(4.dp))

            BaseInputText(modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done, keyboardType = KeyboardType.Phone
                ),
                textFieldState = phoneNumber,
                hint = "phoneNumber",
                description = "Phone number",
                validator = {
                    validateNotEmpty(it)
                },
                leadingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_email),
                        contentDescription = null
                    )
                })

            // DOB
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Ngày sinh",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
            )

            Spacer(modifier = Modifier.height(4.dp))

            val showDobPicker = remember { mutableStateOf(false) }

            BaseInputText(modifier = Modifier.fillMaxWidth(),
                textFieldState = dobTextField,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done, keyboardType = KeyboardType.Text
                ),
                hint = "Date of birth",
                description = "Dob",
                validator = {
                    validateNotEmpty(it)
                },
                leadingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_calendar),
                        contentDescription = null
                    )
                },
                readOnly = true,
                enable = false,
                onClick = {
                    showDobPicker.value = true
                })

            if (showDobPicker.value) {
                PopupDatePicker(onDismiss = {
                    showDobPicker.value = false
                }, onDateSelected = {
                    Napier.d("Selected date: $it")
                    showDobPicker.value = false
                    dobTextField.value = TextFieldValue(it.toString())
                    dob.value = it
                }, showModeToggle = false, state = datePickerState
                )
            }

            // Gender
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Giới tính",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
            )

            Spacer(modifier = Modifier.height(4.dp))

            val showGenderPicker = remember { mutableStateOf(false) }
            BaseInputText(modifier = Modifier.fillMaxWidth(),
                textFieldState = genderTextField,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done, keyboardType = KeyboardType.Text
                ),
                hint = "Gender",
                description = "Gender",
                validator = {
                    validateNotEmpty(it)
                },
                readOnly = true,
                enable = false,
                onClick = {
                    showGenderPicker.value = true
                })

            val listGender = listOf(Gender.MALE, Gender.FEMALE, Gender.OTHER)

            if (showGenderPicker.value) {
                ListPickerDialog(title = "Gender",
                    items = listGender,
                    default = listOf(gender.value),
                    onDismiss = {
                        showGenderPicker.value = false
                    },
                    onConfirm = {
                        gender.value = it.first()
                        genderTextField.value = TextFieldValue(it.first().text)
                        showGenderPicker.value = false
                    },
                    itemToString = {
                        it.text
                    })
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Tỉnh/Thành phố",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
            )

            Spacer(modifier = Modifier.height(4.dp))

            BaseInputText(modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done, keyboardType = KeyboardType.Text
            ), hint = "Province", description = "Province", validator = {
                validateNotEmpty(it)
            }, textFieldState = province
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Quận/Huyện",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
            )
            Spacer(modifier = Modifier.height(4.dp))

            BaseInputText(modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done, keyboardType = KeyboardType.Text
            ), hint = "District", description = "District", validator = {
                validateNotEmpty(it)
            }, textFieldState = district
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Phường/Xã",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
            )

            Spacer(modifier = Modifier.height(4.dp))

            BaseInputText(modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done, keyboardType = KeyboardType.Text
            ), hint = "Commune", description = "Commune", validator = {
                validateNotEmpty(it)
            }, textFieldState = commune
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Địa chỉ",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
            )

            Spacer(modifier = Modifier.height(4.dp))

            BaseInputText(
                modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done, keyboardType = KeyboardType.Text
                ), hint = "Address", description = "Address", validator = {
                    validateNotEmpty(it)
                }, textFieldState = address, maxLines = 3
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Lý do khám",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
            )

            Spacer(modifier = Modifier.height(4.dp))

            BaseInputText(
                modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done, keyboardType = KeyboardType.Text
                ), hint = "Examination Reason", description = "Examination Reason", validator = {
                    validateNotEmpty(it)
                }, textFieldState = examinationReason, maxLines = 3, minLines = 3
            )

            Spacer(Modifier.height(60.dp))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.BottomCenter)
                .padding(24.dp)
        ) {
            SecondaryButton(modifier = Modifier.weight(1f), onClick = {
                store.sendAction(
                    BookingAction.PreviousStep(
                        currentStep = BookingStepType.INPUT_PATIENT_INFO,
                        destinationStep = BookingStepType.SELECT_SERVICE
                    )
                )
            }, text = {
                Text(
                    text = "Quay lại", style = MaterialTheme.typography.labelLarge, color = Color.Black
                )
            })

            Spacer(modifier = Modifier.width(16.dp))

            PrimaryButton(modifier = Modifier.weight(1f), onClick = {
                val patientInfo = PatientInfo(
                    name = name.value.text,
                    phoneNumber = phoneNumber.value.text,
                    email = email.value.text,
                    gender = gender.value,
                    dateOfBirth = dob.value,
                    province = province.value.text,
                    district = district.value.text,
                    commune = commune.value.text,
                    address = address.value.text,
                    examinationReason = examinationReason.value.text,
                )
                store.sendAction(
                    BookingAction.UpdatePatientInfo(patientInfo = patientInfo)
                )
                store.sendAction(
                    BookingAction.NextStep(
                        currentStep = BookingStepType.INPUT_PATIENT_INFO,
                        destinationStep = BookingStepType.SELECT_SCHEDULE
                    )
                )
            }, text = {
                Text(
                    text = "Tiếp theo", style = MaterialTheme.typography.labelLarge, color = Color.White
                )
            })
        }
    }
}
