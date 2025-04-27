package com.huongmt.medmeet.ui.booking

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.huongmt.medmeet.R

//class BookingSuccessScreen(
//    private val navigationActions: BookingNavigationActions
//) : Screen {
//    @OptIn(ExperimentalMaterial3Api::class)
//    @Composable
//    override fun Content() {
//        Scaffold(
//            topBar = {
//                TopAppBar(
//                    title = { Text("Booking Confirmation") }
//                )
//            }
//        ) { paddingValues ->
//            BookingSuccessContent(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(paddingValues),
//                onDone = navigationActions::onBookingCompleted
//            )
//        }
//    }
//}

@Composable
private fun BookingSuccessContent(
    modifier: Modifier = Modifier,
    onDone: () -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
//            Image(
//                painter = painterResource(id = R.drawable.ic_success),
//                contentDescription = "Success",
//                modifier = Modifier.size(120.dp)
//            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Booking Successful!",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Your appointment has been booked successfully. You will receive a confirmation via email and SMS shortly.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = onDone,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Done")
            }
        }
    }
} 