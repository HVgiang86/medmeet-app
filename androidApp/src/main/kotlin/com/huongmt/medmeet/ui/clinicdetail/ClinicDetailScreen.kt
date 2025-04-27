package com.huongmt.medmeet.ui.clinicdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.huongmt.medmeet.R
import com.huongmt.medmeet.component.ErrorDialog
import com.huongmt.medmeet.component.ExpandableText
import com.huongmt.medmeet.component.FullScreenImageDialog
import com.huongmt.medmeet.component.LoadingDialog
import com.huongmt.medmeet.component.PrimaryButton
import com.huongmt.medmeet.shared.app.ClinicDetailAction
import com.huongmt.medmeet.shared.app.ClinicDetailEffect
import com.huongmt.medmeet.shared.app.ClinicDetailState
import com.huongmt.medmeet.shared.app.ClinicDetailStore
import com.huongmt.medmeet.ui.main.nav.MainScreenDestination

@Composable
fun ClinicDetailScreen(
    store: ClinicDetailStore,
    clinicId: String,
    navigateBack: () -> Unit,
    navigateTo: (MainScreenDestination) -> Unit,
) {
    val state by store.observeState().collectAsState()
    val effect by store.observeSideEffect().collectAsState(initial = null)

    var showImageDialog by rememberSaveable { mutableStateOf(false) }
    var selectedImageUrl by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(clinicId) {
        store.sendAction(ClinicDetailAction.LoadClinic(clinicId))
        store.sendAction(ClinicDetailAction.GetClinicSchedule(clinicId))
    }

    LaunchedEffect(effect) {
        when (effect) {
            ClinicDetailEffect.NavigateBack -> navigateBack()
            ClinicDetailEffect.BookAppointment -> {
                if (state.clinic != null) {
                    navigateTo(MainScreenDestination.BookingAppointment(state.clinic!!))
                }
            }

            null -> {}
        }
    }

    if (state.isLoading) {
        LoadingDialog()
    }

    if (state.error != null) {
        ErrorDialog(throwable = state.error) {
            store.sendAction(ClinicDetailAction.DismissError)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            ClinicDetailContent(state = state,
                onBackClick = { store.sendAction(ClinicDetailAction.NavigateBack) },
                onBookAppointmentClick = { store.bookAppointment() },
                onImageClick = {
                    showImageDialog = true
                    selectedImageUrl = it
                })
        }
    }

    // Gọi FullScreenImageDialog
    FullScreenImageDialog(showDialog = showImageDialog,
        imageUrl = selectedImageUrl,
        onDismiss = { showImageDialog = false })
}

@Composable
private fun ClinicDetailContent(
    state: ClinicDetailState,
    onBackClick: () -> Unit,
    onBookAppointmentClick: () -> Unit,
    onImageClick: (String) -> Unit = {},
) {
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Clinic image and back button
            Box(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = state.clinic?.logo,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.3f)
                        .clickable {
                            onImageClick(state.clinic?.logo ?: "")
                        },
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.ic_clinic_default)
                )

                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopStart)
                        .background(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Clinic details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Clinic name
                Text(
                    text = state.clinic?.name ?: "",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Clinic address
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = state.clinic?.address ?: "Unknown location",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Working hours
                Text(
                    text = "Working Time",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = state.clinicScheduleDisplay ?: "Mon-Fri, 9:00 AM - 5:00 PM",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                // About
                Text(
                    text = "About us",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                ExpandableText(
                    text = state.clinic?.description ?: "Some description.",
                    collapsedMaxLine = 3,
                    style = MaterialTheme.typography.bodyLarge,
                )

                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        PrimaryButton(onClick = onBookAppointmentClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
                .height(56.dp)
                .align(Alignment.BottomCenter),
            text = {
                Text(
                    text = "Book Appointment",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            })
    }
}

