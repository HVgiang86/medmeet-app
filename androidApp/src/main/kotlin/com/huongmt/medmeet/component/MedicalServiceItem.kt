package com.huongmt.medmeet.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.huongmt.medmeet.shared.core.entity.MedicalService
import com.huongmt.medmeet.theme.GreenMedMeet
import java.text.NumberFormat
import java.util.Locale

@Composable
fun MedicalServiceItem(
    service: MedicalService,
    selected: Boolean = false,
    onClick: () -> Unit,
) {
    val strokeColor = if (selected) GreenMedMeet else Color.LightGray
    val borderWidth = if (selected) 2.dp else 1.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(borderWidth, strokeColor),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp)
        ) {
            Text(
                text = service.name ?: "",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = Color.LightGray,
                thickness = 1.dp
            )

            if (service.originalPrice == 0L) {
                Text(
                    text = formatPrice(service.currentPrice?.toDouble() ?: 0.0),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Text(
                    text = formatPrice(service.originalPrice?.toDouble() ?: 0.0),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray,
                    textDecoration = TextDecoration.LineThrough
                )

                Text(
                    text = formatPrice(service.currentPrice?.toDouble() ?: 0.0),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

    }
}

fun formatPrice(price: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
    return "${formatter.format(price)} VND"
}
