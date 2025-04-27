package com.huongmt.medmeet.ui.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import org.koin.core.parameter.parametersOf

//data class ConfirmBookingScreen(
//    private val bookingId: String,
//    private val navigationActions: BookingNavigationActions
//) : Screen {
//    @OptIn(ExperimentalMaterial3Api::class)
//    @Composable
//    override fun Content() {
//        val screenModel = getScreenModel<ConfirmBookingScreenModel> { parametersOf(bookingId) }
//        val uiState by screenModel.uiState.collectAsState()
//        val bookingDetails = uiState.bookingDetails
//
//        Scaffold(
//            topBar = {
//                TopAppBar(
//                    title = { Text("Confirm Booking") },
//                    navigationIcon = {
//                        IconButton(onClick = navigationActions::navigateBack) {
//                            Icon(
//                                imageVector = Icons.Default.ArrowBack,
//                                contentDescription = "Back"
//                            )
//                        }
//                    }
//                )
//            }
//        ) { paddingValues ->
//            if (bookingDetails != null) {
//                ConfirmBookingContent(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(paddingValues),
//                    bookingDetails = bookingDetails,
//                    onConfirm = {
//                        screenModel.confirmBooking {
//                            navigationActions.navigateToBookingSuccess()
//                        }
//                    }
//                )
//            } else {
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(paddingValues),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text("Loading booking details...")
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun ConfirmBookingContent(
//    modifier: Modifier = Modifier,
//    bookingDetails: BookingDetails,
//    onConfirm: () -> Unit
//) {
//    Column(
//        modifier = modifier
//            .verticalScroll(rememberScrollState())
//            .padding(16.dp),
//        verticalArrangement = Arrangement.spacedBy(16.dp)
//    ) {
//        Text(
//            text = "Please review your booking details",
//            style = MaterialTheme.typography.titleLarge,
//            modifier = Modifier.fillMaxWidth(),
//            textAlign = TextAlign.Center
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        // Booking information section
//        BookingInfoSection(bookingDetails)
//
//        Divider()
//
//        // Patient information section
//        PatientInfoSection(bookingDetails.patient)
//
//        Divider()
//
//        // Price information
//        PriceSection(bookingDetails.price)
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Button(
//            onClick = onConfirm,
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text("Confirm Booking")
//        }
//    }
//}
//
//@Composable
//private fun PriceSection(price: Double) {
//    Column(
//        modifier = Modifier.fillMaxWidth()
//    ) {
//        Text(
//            text = "Payment",
//            style = MaterialTheme.typography.titleMedium,
//            fontWeight = FontWeight.Bold
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Text(
//                text = "Consultation Fee",
//                style = MaterialTheme.typography.bodyLarge
//            )
//            Text(
//                text = "$${String.format("%.2f", price)}",
//                style = MaterialTheme.typography.bodyLarge,
//                fontWeight = FontWeight.Bold
//            )
//        }
//
//        Spacer(modifier = Modifier.height(4.dp))
//
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f))
//                .padding(8.dp),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Text(
//                text = "Total",
//                style = MaterialTheme.typography.bodyLarge,
//                fontWeight = FontWeight.Bold
//            )
//            Text(
//                text = "$${String.format("%.2f", price)}",
//                style = MaterialTheme.typography.bodyLarge,
//                fontWeight = FontWeight.Bold,
//                color = MaterialTheme.colorScheme.primary
//            )
//        }
//    }
//}