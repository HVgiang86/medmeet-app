package com.huongmt.medmeet.ui.profiledetail

import android.Manifest
import android.content.Intent
import android.os.Build
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import com.huongmt.medmeet.utils.ext.getFileName
import com.huongmt.medmeet.utils.ext.getMimeType
import com.huongmt.medmeet.utils.ext.readBytes
import com.huongmt.medmeet.utils.ext.toDMY
import com.huongmt.medmeet.utils.ext.toDMY2
import io.github.aakira.napier.Napier
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

data class AvatarUploadData(
    val fileData: ByteArray,
    val fileName: String,
    val mimeType: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AvatarUploadData

        if (!fileData.contentEquals(other.fileData)) return false
        if (fileName != other.fileName) return false
        if (mimeType != other.mimeType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fileData.contentHashCode()
        result = 31 * result + fileName.hashCode()
        result = 31 * result + mimeType.hashCode()
        return result
    }
}

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

        if (state.validateError != null) {
            ErrorDialog(throwable = state.validateError, onDismissRequest = {
                store.sendAction(ProfileDetailAction.DismissError)
            })
        }

        Scaffold(topBar = {
            TopAppBar(
                title = { Text("Profile Details") }, 
                navigationIcon = {
                    IconButton(onClick = { 
                        if (state.isEditMode) {
                            store.sendAction(ProfileDetailAction.CancelEdit)
                        } else {
                            store.sendAction(ProfileDetailAction.NavigateBack)
                        }
                    }) {
                        Icon(
                            imageVector = if (state.isEditMode) Icons.Filled.Close else Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = if (state.isEditMode) "Cancel" else "Back"
                        )
                    }
                },
                actions = {
                    if (!state.isEditMode && state.originalUser != null) {
                        IconButton(onClick = { 
                            store.sendAction(ProfileDetailAction.ToggleEditMode)
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Edit Profile"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
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
                
                // Only show content when user data is loaded
                if (state.originalUser != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp)
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))

                        // Avatar Section (always visible, can be edited independently)
                        AvatarSection(user = state.originalUser, onAvatarUpload = { avatarData ->
                            // Handle real file upload with user ID and file data
                            state.originalUser?.let { user ->
                                store.sendAction(ProfileDetailAction.UpdateAvatar(
                                    userId = user.id,
                                    fileData = avatarData.fileData,
                                    fileName = avatarData.fileName,
                                    mimeType = avatarData.mimeType
                                ))
                            }
                        })

                        Spacer(modifier = Modifier.height(32.dp))

                        // User Information Section
                        UserInfoSection(
                            user = state.originalUser,
                            pendingData = state.pendingUpdateData,
                            isEditMode = state.isEditMode,
                            onDataChanged = { updatedData ->
                                store.sendAction(ProfileDetailAction.UpdatePendingData(updatedData))
                            },
                            onSaveClick = { updateData ->
                                store.sendAction(ProfileDetailAction.ValidateAndSave(updateData))
                            }
                        )
                        
                        // Add some bottom spacing
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                } else if (!state.isLoading) {
                    // Show empty state if no user data and not loading
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Unable to load profile data",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            PrimaryButton(
                                onClick = {
                                    store.sendAction(ProfileDetailAction.GetUser)
                                },
                                text = {
                                    Text("Retry")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AvatarSection(
    user: User?,
    onAvatarUpload: (AvatarUploadData) -> Unit,
) {
    val context = LocalContext.current
    var showImagePickerDialog by remember { mutableStateOf(false) }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        Napier.d("Image picker result: $uri")
        uri?.let {
            try {
                val fileData = it.readBytes(context)
                val fileName = it.getFileName(context)
                val mimeType = it.getMimeType(context)
                
                Napier.d("File data size: ${fileData?.size}, fileName: $fileName, mimeType: $mimeType")
                
                if (fileData != null) {
                    onAvatarUpload(
                        AvatarUploadData(
                            fileData = fileData,
                            fileName = fileName,
                            mimeType = mimeType
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Napier.e("Error processing image: ${e.message}", e)
                // Handle error - could show a toast or error dialog here
            }
        } ?: run {
            Napier.d("No image selected")
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        Napier.d("Permission result: $isGranted")
        if (isGranted) {
            // Launch image picker directly
            Napier.d("Permission granted, launching image picker")
            pickImageLauncher.launch("image/*")
        } else {
            Napier.d("Permission denied")
        }
    }

    // Function to handle image selection
    val selectImage = {
        Napier.d("selectImage called")
        // For Android 13+ (API 33+), we don't need READ_EXTERNAL_STORAGE permission
        // For older versions, we need to request permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Napier.d("Launching image picker directly (Android 13+)")
            pickImageLauncher.launch("image/*")
        } else {
            Napier.d("Requesting permission first (Android < 13)")
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
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
                    selectImage()
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
    pendingData: UpdateProfileData?,
    isEditMode: Boolean,
    onDataChanged: (UpdateProfileData) -> Unit,
    onSaveClick: (UpdateProfileData) -> Unit,
) {
    // Use current user data for display when not in edit mode, pending data when in edit mode
    val displayData = if (isEditMode) pendingData else user?.toDateUpdate()

    // Initialize date picker state with current date
    val initialDateMillis = displayData?.dob?.let { localDate ->
        try {
            // Convert LocalDate to milliseconds for DatePicker
            val instant = kotlinx.datetime.Instant.parse("${localDate}T00:00:00Z")
            instant.toEpochMilliseconds()
        } catch (e: Exception) {
            null
        }
    }
    
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDateMillis
    )

    val dobTextField = rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(displayData?.dob?.toDMY() ?: ""))
    }

    val genderTextField = rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(displayData?.gender?.text ?: ""))
    }

    // Update text fields when data changes
    LaunchedEffect(displayData) {
        displayData?.let {
            dobTextField.value = TextFieldValue(it.dob.toDMY())
            genderTextField.value = TextFieldValue(it.gender.text)
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
                
                if (isEditMode) {
                    Text(
                        text = "Edit Mode",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // User Code (Always disabled)
            Text(
                text = "User Code",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            BaseInputText(
                modifier = Modifier.fillMaxWidth(), 
                default = user?.code, 
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done, 
                    keyboardType = KeyboardType.Text
                ), 
                hint = "Code",  
                description = "Code", 
                leadingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_email),
                        contentDescription = null
                    )
                }, 
                enable = false
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Email (Always disabled)
            Text(
                text = "Email",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            BaseInputText(
                modifier = Modifier.fillMaxWidth(), 
                default = user?.email, 
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done, 
                    keyboardType = KeyboardType.Email
                ), 
                hint = "Email",  
                description = "Email", 
                leadingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_email),
                        contentDescription = null
                    )
                }, 
                enable = false
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Name (Editable)
            Text(
                text = "Name",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            BaseInputText(
                modifier = Modifier.fillMaxWidth(), 
                default = displayData?.name, 
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done, 
                    keyboardType = KeyboardType.Text
                ), 
                hint = "Name", 
                onTextChanged = if (isEditMode) { newName ->
                    pendingData?.let { currentData ->
                        onDataChanged(currentData.copy(name = newName))
                    }
                } else null, 
                description = "Name", 
                onImeAction = if (isEditMode) { newName ->
                    pendingData?.let { currentData ->
                        onDataChanged(currentData.copy(name = newName))
                    }
                } else null, 
                validator = { text ->
                    if (isEditMode) { validateNotEmpty(text) } else null
                },
                leadingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_user),
                        contentDescription = null
                    )
                }, 
                enable = isEditMode
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Gender (Editable)
            Text(
                text = "Gender",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            val showGenderPicker = remember { mutableStateOf(false) }
            BaseInputText(
                modifier = Modifier.fillMaxWidth(),
                textFieldState = genderTextField,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done, 
                    keyboardType = KeyboardType.Text
                ),
                hint = if (isEditMode) "Tap to select gender" else "Gender",
                description = "Gender",
                validator = { text ->
                    if (isEditMode) { validateNotEmpty(text) } else null
                },
                readOnly = true,
                enable = false,
                onClick = if (isEditMode) {
                    { showGenderPicker.value = true }
                } else null,
                trailingIcon = if (isEditMode) {
                    {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Select Gender",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else null,
                leadingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_user),
                        contentDescription = null,
                        tint = if (isEditMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )

            val listGender = listOf(Gender.MALE, Gender.FEMALE, Gender.OTHER)

            if (showGenderPicker.value && isEditMode) {
                ListPickerDialog(
                    title = "Gender",
                    items = listGender,
                    default = listOf(pendingData?.gender ?: Gender.MALE),
                    onDismiss = {
                        showGenderPicker.value = false
                    },
                    onConfirm = { selectedGenders ->
                        val newGender = selectedGenders.firstOrNull() ?: Gender.MALE
                        pendingData?.let { currentData ->
                            onDataChanged(currentData.copy(gender = newGender))
                        }
                        genderTextField.value = TextFieldValue(newGender.text)
                        showGenderPicker.value = false
                    },
                    itemToString = { it?.text ?: "" }
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Date of Birth (Editable)
            Text(
                text = "Date of Birth",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            val showDobPicker = remember { mutableStateOf(false) }

            BaseInputText(
                modifier = Modifier.fillMaxWidth(),
                textFieldState = dobTextField,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done, 
                    keyboardType = KeyboardType.Text
                ),
                hint = if (isEditMode) "Tap to select date" else "Date of birth",
                description = "Date of Birth",
                validator = { text ->
                    if (isEditMode) { validateNotEmpty(text) } else null
                },
                leadingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_calendar),
                        contentDescription = null,
                        tint = if (isEditMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                readOnly = true,
                enable = false,
                onClick = if (isEditMode) {
                    { showDobPicker.value = true }
                } else null
            )

            if (showDobPicker.value && isEditMode) {
                PopupDatePicker(
                    onDismiss = {
                        showDobPicker.value = false
                    }, 
                    onDateSelected = { selectedDate ->
                        Napier.d("Selected date: $selectedDate")
                        showDobPicker.value = false
                        dobTextField.value = TextFieldValue(selectedDate.toDMY())
                        pendingData?.let { currentData ->
                            onDataChanged(currentData.copy(dob = selectedDate))
                        }
                    }, 
                    showModeToggle = false, 
                    state = datePickerState
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Phone Number (Editable)
            Text(
                text = "Phone Number",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            BaseInputText(
                modifier = Modifier.fillMaxWidth(), 
                default = displayData?.phoneNumber, 
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done, 
                    keyboardType = KeyboardType.Phone
                ), 
                hint = "Phone number", 
                description = "Phone number", 
                validator = { text ->
                    if (isEditMode) { validateNotEmpty(text) } else null
                },
                onTextChanged = if (isEditMode) { newPhone ->
                    pendingData?.let { currentData ->
                        onDataChanged(currentData.copy(phoneNumber = newPhone))
                    }
                } else null, 
                onImeAction = if (isEditMode) { newPhone ->
                    pendingData?.let { currentData ->
                        onDataChanged(currentData.copy(phoneNumber = newPhone))
                    }
                } else null, 
                enable = isEditMode
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Province (Editable)
            Text(
                text = "Province",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            BaseInputText(
                modifier = Modifier.fillMaxWidth(), 
                default = displayData?.province, 
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done, 
                    keyboardType = KeyboardType.Text
                ), 
                hint = "Province", 
                description = "Province", 
                validator = { text ->
                    if (isEditMode) { validateNotEmpty(text) } else null
                },
                onTextChanged = if (isEditMode) { newProvince ->
                    pendingData?.let { currentData ->
                        onDataChanged(currentData.copy(province = newProvince))
                    }
                } else null, 
                onImeAction = if (isEditMode) { newProvince ->
                    pendingData?.let { currentData ->
                        onDataChanged(currentData.copy(province = newProvince))
                    }
                } else null, 
                enable = isEditMode
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // District (Editable)
            Text(
                text = "District",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            BaseInputText(
                modifier = Modifier.fillMaxWidth(), 
                default = displayData?.district, 
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done, 
                    keyboardType = KeyboardType.Text
                ), 
                hint = "District", 
                description = "District", 
                validator = { text ->
                    if (isEditMode) { validateNotEmpty(text) } else null
                },
                onTextChanged = if (isEditMode) { newDistrict ->
                    pendingData?.let { currentData ->
                        onDataChanged(currentData.copy(district = newDistrict))
                    }
                } else null, 
                onImeAction = if (isEditMode) { newDistrict ->
                    pendingData?.let { currentData ->
                        onDataChanged(currentData.copy(district = newDistrict))
                    }
                } else null, 
                enable = isEditMode
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Commune (Editable)
            Text(
                text = "Commune",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            BaseInputText(
                modifier = Modifier.fillMaxWidth(), 
                default = displayData?.commune, 
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done, 
                    keyboardType = KeyboardType.Text
                ), 
                hint = "Commune", 
                description = "Commune", 
                validator = { text ->
                    if (isEditMode) { validateNotEmpty(text) } else null
                },
                onTextChanged = if (isEditMode) { newCommune ->
                    pendingData?.let { currentData ->
                        onDataChanged(currentData.copy(commune = newCommune))
                    }
                } else null, 
                onImeAction = if (isEditMode) { newCommune ->
                    pendingData?.let { currentData ->
                        onDataChanged(currentData.copy(commune = newCommune))
                    }
                } else null, 
                enable = isEditMode
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Address (Editable)
            Text(
                text = "Address",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            BaseInputText(
                modifier = Modifier.fillMaxWidth(), 
                default = displayData?.address, 
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done, 
                    keyboardType = KeyboardType.Text
                ), 
                hint = "Address", 
                description = "Address", 
                validator = { text ->
                    if (isEditMode) { validateNotEmpty(text) } else null
                },
                onTextChanged = if (isEditMode) { newAddress ->
                    pendingData?.let { currentData ->
                        onDataChanged(currentData.copy(address = newAddress))
                    }
                } else null, 
                onImeAction = if (isEditMode) { newAddress ->
                    pendingData?.let { currentData ->
                        onDataChanged(currentData.copy(address = newAddress))
                    }
                } else null, 
                maxLines = 3, 
                enable = isEditMode
            )

            if (isEditMode) {
                Spacer(modifier = Modifier.height(24.dp))

                // Check if data has changed
                val hasChanges = user?.let { originalUser ->
                    pendingData?.let { pending ->
                        val originalData = originalUser.toDateUpdate()
                        originalData.name != pending.name ||
                        originalData.gender != pending.gender ||
                        originalData.dob != pending.dob ||
                        originalData.phoneNumber != pending.phoneNumber ||
                        originalData.province != pending.province ||
                        originalData.district != pending.district ||
                        originalData.commune != pending.commune ||
                        originalData.address != pending.address
                    } ?: false
                } ?: false

                // Save button (only visible in edit mode)
                PrimaryButton(
                    onClick = {
                        pendingData?.let { data ->
                            Napier.d("Save data: $data")
                            onSaveClick(data)
                        }
                    }, 
                    text = {
                        Text(
                            text = "Save Changes",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White
                        )
                    }, 
                    enabled = hasChanges && pendingData != null,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
