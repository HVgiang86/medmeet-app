package com.huongmt.medmeet.ui.profiledetail

import android.Manifest
import android.content.Intent
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil.compose.AsyncImage
import com.dokar.sonner.Toaster
import com.dokar.sonner.rememberToasterState
import com.huongmt.medmeet.R
import com.huongmt.medmeet.component.BaseInputText
import com.huongmt.medmeet.component.ErrorDialog
import com.huongmt.medmeet.component.ListPickerDialog
import com.huongmt.medmeet.component.LoadingDialog
import com.huongmt.medmeet.component.PopupDatePicker
import com.huongmt.medmeet.component.PrimaryButton
import com.huongmt.medmeet.shared.app.ProfileDetailAction
import com.huongmt.medmeet.shared.app.ProfileDetailEffect
import com.huongmt.medmeet.shared.app.ProfileDetailStore
import com.huongmt.medmeet.shared.core.entity.Gender
import com.huongmt.medmeet.shared.core.entity.UpdateProfileData
import com.huongmt.medmeet.shared.core.entity.User
import com.huongmt.medmeet.shared.utils.ext.nowDate
import com.huongmt.medmeet.shared.utils.validate.Validator
import com.huongmt.medmeet.shared.utils.validate.Validator.validateNotEmpty
import com.huongmt.medmeet.utils.ext.toDMY
import com.huongmt.medmeet.utils.ext.toDMY2
import io.github.aakira.napier.Napier
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ProfileDetailScreen : Screen, KoinComponent {
    private val store: ProfileDetailStore by inject()

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val state by store.observeState().collectAsState()
        val effect by store.observeSideEffect().collectAsState(initial = null)
        val toasterState = rememberToasterState()

        LaunchedEffect(Unit) {
            store.sendAction(ProfileDetailAction.GetUser)
        }

        LaunchedEffect(effect) {
            when (effect) {
                ProfileDetailEffect.NavigateBack -> navigator.pop()
                is ProfileDetailEffect.ShowMessage -> {
                    toasterState.show((effect as ProfileDetailEffect.ShowMessage).message)
                }

                null -> {}
            }
        }

        if (state.isLoading) {
            LoadingDialog()
        }

        if (state.error != null) {
            ErrorDialog(throwable = state.error, onDismissRequest = {
                store.sendAction(ProfileDetailAction.DismissError)
            })
        }

        Scaffold(topBar = {
            TopAppBar(title = { Text("Profile Details") }, navigationIcon = {
                IconButton(onClick = { store.sendAction(ProfileDetailAction.NavigateBack) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
            )
        }) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .systemBarsPadding()
            ) {
                Toaster(state = toasterState)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Avatar Section
                    AvatarSection(user = state.originalUser, onAvatarUpload = { uri ->
                        store.sendAction(ProfileDetailAction.UpdateAvatar(uri))
                    })

                    Spacer(modifier = Modifier.height(32.dp))

                    // User Information Section
                    UserInfoSection(user = state.originalUser,

                    )
                }
            }
        }
    }
}

@Composable
fun AvatarSection(
    user: User?,
    onAvatarUpload: (String) -> Unit,
) {
    val context = LocalContext.current
    var showImagePickerDialog by remember { mutableStateOf(false) }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // In a real app, this would save the image URI from the result
        // For now, we're mocking this by returning a dummy URI
        onAvatarUpload("https://example.com/uploads/avatar-${System.currentTimeMillis()}.jpg")
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(), contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
        ) {
            AsyncImage(
                model = user?.avatar,
                contentDescription = "Profile Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                error = painterResource(R.drawable.ic_default_avatar)
            )

            // Edit button overlay
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable { showImagePickerDialog = true }, contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Avatar",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }

    if (showImagePickerDialog) {
        AlertDialog(onDismissRequest = { showImagePickerDialog = false },
            title = { Text("Change Profile Photo") },
            text = { Text("Choose a new profile photo from your gallery.") },
            confirmButton = {
                TextButton(onClick = {
                    showImagePickerDialog = false
                    requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }) {
                    Text("Choose from Gallery")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImagePickerDialog = false }) {
                    Text("Cancel")
                }
            })
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun UserInfoSection(
    user: User?,
    onSaveClick: (UpdateProfileData) -> Unit = {},
) {
    val dataUpdate: MutableState<UpdateProfileData?> = remember { mutableStateOf(user?.toDateUpdate()) }
    val isEditing = true

    val datePickerState = rememberDatePickerState()

    val dobTextField = rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(dataUpdate.value?.dob?.toString() ?: ""))
    }

    val genderTextField = rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(dataUpdate.value?.gender?.text ?: ""))
    }

    // Update state when user is loaded or changed
    LaunchedEffect(user) {
        user?.let {
            dataUpdate.value = it.toDateUpdate()
            println("[DEUBG] user: $it")
            println("Data update: ${dataUpdate.value}")
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Personal Information",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Label
            Text(
                text = "User Code",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            println("[DEBUG] user code: ${user?.code}")
            BaseInputText(modifier = Modifier.fillMaxWidth(), default = user?.code, keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done, keyboardType = KeyboardType.Email
            ), hint = "Code",  description = "Code", leadingIcon = {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_email),
                    contentDescription = null
                )
            }, enable = false)

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Label
            Text(
                text = "Email",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            BaseInputText(modifier = Modifier.fillMaxWidth(), default = user?.email, keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done, keyboardType = KeyboardType.Email
            ), hint = "Email",  description = "Email", leadingIcon = {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_email),
                    contentDescription = null
                )
            }, enable = false)

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Label
            Text(
                text = "Name",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            BaseInputText(modifier = Modifier.fillMaxWidth(), default = dataUpdate.value?.name, keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done, keyboardType = KeyboardType.Text
            ), hint = "Name", onTextChanged = {
                dataUpdate.value = dataUpdate.value?.copy(name = it)
            }, description = "Name", onImeAction = {
                dataUpdate.value = dataUpdate.value?.copy(name = it)
                Napier.d("name: $it")
            }, validator = {
                validateNotEmpty(it)
            }, leadingIcon = {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_user),
                    contentDescription = null
                )
            }, enable = isEditing)

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Label
            Text(
                text = "Gender",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )


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

            if (showGenderPicker.value && isEditing) {
                ListPickerDialog(title = "Gender",
                    items = listGender,
                    default = listOf(dataUpdate.value?.gender),
                    onDismiss = {
                        showGenderPicker.value = false
                    },
                    onConfirm = {
                        val newGender = it.first()
                        dataUpdate.value = dataUpdate.value?.copy(gender = it.first() ?: user?.gender ?: Gender.OTHER )
                        genderTextField.value = TextFieldValue(it.first()?.text ?: "")
                        showGenderPicker.value = false
                    },
                    itemToString = {
                        it?.text ?: ""
                    })
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

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

            if (showDobPicker.value && isEditing) {
                PopupDatePicker(onDismiss = {
                    showDobPicker.value = false
                }, onDateSelected = {
                    Napier.d("Selected date: $it")
                    showDobPicker.value = false
                    dobTextField.value = TextFieldValue(it.toString())
                    dataUpdate.value = dataUpdate.value?.copy(dob = it)
                }, showModeToggle = false, state = datePickerState
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Label
            Text(
                text = "Phone Number",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            BaseInputText(modifier = Modifier.fillMaxWidth(), default = dataUpdate.value?.phoneNumber, keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done, keyboardType = KeyboardType.Text
            ), hint = "Phone number", description = "Phone number", validator = {
                validateNotEmpty(it)
            }, onTextChanged = {
                dataUpdate.value = dataUpdate.value?.copy(phoneNumber = it)
            }, onImeAction = {
                dataUpdate.value = dataUpdate.value?.copy(phoneNumber = it)
                Napier.d("phone number: $it")
            }, enable = isEditing)

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Label
            Text(
                text = "Province",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            BaseInputText(modifier = Modifier.fillMaxWidth(), default = dataUpdate.value?.province, keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done, keyboardType = KeyboardType.Text
            ), hint = "Province", description = "Province", validator = {
                validateNotEmpty(it)
            }, onTextChanged = {
                dataUpdate.value = dataUpdate.value?.copy(province = it)
            }, onImeAction = {
                dataUpdate.value = dataUpdate.value?.copy(province = it)
            }, enable = isEditing)

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Label
            Text(
                text = "District",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            BaseInputText(modifier = Modifier.fillMaxWidth(), default = dataUpdate.value?.district, keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done, keyboardType = KeyboardType.Text
            ), hint = "District", description = "District", validator = {
                validateNotEmpty(it)
            }, onTextChanged = {
                dataUpdate.value = dataUpdate.value?.copy(district = it)
            }, onImeAction = {
                dataUpdate.value = dataUpdate.value?.copy(district = it)
            }, enable = isEditing)

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Label
            Text(
                text = "Commune",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            BaseInputText(modifier = Modifier.fillMaxWidth(), default = dataUpdate.value?.commune, keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done, keyboardType = KeyboardType.Text
            ), hint = "Commune", description = "Commune", validator = {
                validateNotEmpty(it)
            }, onTextChanged = {
                dataUpdate.value = dataUpdate.value?.copy(commune = it)
            }, onImeAction = {
                dataUpdate.value = dataUpdate.value?.copy(commune = it)
            }, enable = isEditing)

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Label
            Text(
                text = "Address",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            BaseInputText(modifier = Modifier.fillMaxWidth(), default = dataUpdate.value?.address, keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done, keyboardType = KeyboardType.Text
            ), hint = "Address", description = "Address", validator = {
                validateNotEmpty(it)
            }, onTextChanged = {
                dataUpdate.value = dataUpdate.value?.copy(address = it)
            }, onImeAction = {
                dataUpdate.value = dataUpdate.value?.copy(address = it)
            }, maxLines = 3, enable = isEditing
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            val newUser = user?.copy(
                name = dataUpdate.value?.name,
                gender = dataUpdate.value?.gender ?: Gender.OTHER,
                birthday = dataUpdate.value?.dob,
                province = dataUpdate.value?.province,
                district = dataUpdate.value?.district,
                commune = dataUpdate.value?.commune,
                address = dataUpdate.value?.address,
                phoneNumber = dataUpdate.value?.phoneNumber
            )

            val enableSaveBtn = if (user == null) {
                true
            } else {
                if (newUser == null) {
                    false
                } else if (user.compareTo(newUser)) {
                    false
                } else {
                    true
                }
            }

            // Save button
            PrimaryButton(
                onClick = {
                    dataUpdate.value?.let {
                        Napier.d("Update data: $it")
                        onSaveClick(it)
                    }
                }, text = {
                    Text(
                        text = "Save",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White
                    )
                }, enabled = enableSaveBtn,
            )
        }
    }
}
