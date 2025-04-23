package com.huongmt.medmeet.ui.auth

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import com.huongmt.medmeet.R
import com.huongmt.medmeet.component.BaseInputText
import com.huongmt.medmeet.component.ListPickerDialog
import com.huongmt.medmeet.component.PasswordField
import com.huongmt.medmeet.component.PopupDatePicker
import com.huongmt.medmeet.component.PrimaryButton
import com.huongmt.medmeet.shared.app.AuthStore
import com.huongmt.medmeet.shared.core.entity.Gender
import com.huongmt.medmeet.shared.core.entity.SignUpData
import com.huongmt.medmeet.shared.utils.validate.Validator.validateNotEmpty
import io.github.aakira.napier.Napier
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier.fillMaxSize(), store: AuthStore,
) {
    BackHandler(onBack = {
//        viewModel.reducer.sendEvent(AuthEvent.DisplayLogin)
    }, enabled = true)

    SignUpScreenContent(modifier, store)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SignUpScreenContent(
    modifier: Modifier = Modifier.background(MaterialTheme.colorScheme.background),
    store: AuthStore,
) {
    val datePickerState = rememberDatePickerState()
    val genderPickedIndex = remember { mutableIntStateOf(0) }

    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }
    val name = remember { mutableStateOf("") }

    val dob = remember { mutableStateOf(Clock.System.todayIn(TimeZone.currentSystemDefault())) }
    val dobTextField = rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(dob.value.toString()))
    }

    val gender = remember { mutableStateOf(Gender.MALE) }
    val genderTextField = rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(gender.value.text))
    }

    val province = remember { mutableStateOf("") }
    val district = remember { mutableStateOf("") }
    val address = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(start = 32.dp, end = 32.dp, top = 16.dp, bottom = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Spacer(Modifier.height(32.dp))

        LogoHeader()

        Spacer(Modifier.height(32.dp))

        Text(
            "Create Account!",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(
                Alignment.CenterHorizontally
            )
        )

        Text(
            "We are here to help you!",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Normal),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(32.dp))

        Column(modifier = Modifier) {

            BaseInputText(modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Text
                ),
                hint = "Name",
                onTextChanged = {
                    name.value = it
                },
                description = "Name",
                onImeAction = {
                    name.value = it
                    Napier.d("name: $it")
                },
                validator = {
                    validateNotEmpty(it)
                },
                leadingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_user),
                        contentDescription = null
                    )
                })

            Spacer(Modifier.height(16.dp))

            BaseInputText(modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Email
                ),
                hint = "Email",
                onTextChanged = {
                    email.value = it
                },
                description = "Email",
                onImeAction = {
                    email.value = it
                    Napier.d("email: $it")
                },
//                    validator = {
//                        validateEmail(it)
//                    },
                leadingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_email),
                        contentDescription = null
                    )
                })

            Spacer(Modifier.height(16.dp))

            PasswordField(modifier = Modifier
                .fillMaxWidth()
                .fillMaxWidth(),
                hint = "Password",
                onImeAction = {
                    password.value = it
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Go,
                    keyboardType = KeyboardType.Password
                ),
                shape = RoundedCornerShape(10.dp),
                onTextChanged = {
                    password.value = it
                },
//                    validator = {
//                        validatePassword(it)
//                    },
                leadingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_lock),
                        contentDescription = null
                    )
                })

            Spacer(Modifier.height(16.dp))

            PasswordField(modifier = Modifier
                .fillMaxWidth()
                .fillMaxWidth(),
                hint = "Confirm password",
                onImeAction = {
                    confirmPassword.value = it
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Go,
                    keyboardType = KeyboardType.Password
                ),
                shape = RoundedCornerShape(10.dp),
                onTextChanged = {
                    confirmPassword.value = it
                },
//                    validator = {
//                        validatePassword(it)
//                    },
                leadingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_lock),
                        contentDescription = null
                    )
                })

            Spacer(Modifier.height(24.dp))

            val showDobPicker = remember { mutableStateOf(false) }

            BaseInputText(modifier = Modifier.fillMaxWidth(),
                textFieldState = dobTextField,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Text
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

            Spacer(Modifier.height(16.dp))

            val showGenderPicker = remember { mutableStateOf(false) }
            BaseInputText(modifier = Modifier.fillMaxWidth(),
                textFieldState = genderTextField,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Text
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
                ListPickerDialog(
                    title = "Gender",
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

            BaseInputText(modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Text
                ),
                hint = "Province",
                description = "Province",
                validator = {
                    validateNotEmpty(it)
                },
                onTextChanged = {
                    province.value = it
                },
                onImeAction = {
                    province.value = it
                })

            Spacer(Modifier.height(24.dp))

            BaseInputText(modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Text
                ),
                hint = "District",
                description = "District",
                validator = {
                    validateNotEmpty(it)
                },
                onTextChanged = {
                    district.value = it
                },
                onImeAction = {
                    district.value = it
                })

            Spacer(Modifier.height(24.dp))

            BaseInputText(
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Text
                ),
                hint = "Address",
                description = "Address",
                validator = {
                    validateNotEmpty(it)
                },
                onTextChanged = {
                    address.value = it
                },
                onImeAction = {
                    address.value = it
                },
                maxLines = 3
            )

            Spacer(Modifier.height(24.dp))

            PrimaryButton(text = {
                Text(text = "Create Account", color = Color.White)
            }, modifier = Modifier
                .fillMaxWidth()
                .height(52.dp), onClick = {
                val data = SignUpData(
                    email = email.value,
                    password = password.value,
                    confirmPassword = confirmPassword.value,
                    name = name.value,
                    dob = dob.value,
                    gender = gender.value,
                    province = province.value,
                    district = district.value,
                    address = address.value
                )
//                viewModel.signUp(data)
            })

            Spacer(Modifier.height(24.dp))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                HorizontalDivider(modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp))
                Box(modifier = Modifier
                    .wrapContentWidth()
                    .padding(8.dp)) {
                    Text(
                        text = "or",
                        color = Color.Black,
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Do you have an account? ", style = MaterialTheme.typography.bodyMedium)
                Text(
                    "Sign In",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Blue,
                    modifier = Modifier.clickable {
//                    viewModel.reducer.sendEvent(AuthEvent.DisplayLogin)
                    })
            }

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
        }

        Spacer(Modifier.height(32.dp))
    }
}
