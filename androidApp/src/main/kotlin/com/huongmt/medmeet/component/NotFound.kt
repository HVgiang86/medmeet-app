package com.huongmt.medmeet.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.huongmt.medmeet.R
import com.huongmt.medmeet.theme.CardShapeDefault
import com.huongmt.medmeet.theme.Grey_500
import org.jetbrains.compose.resources.vectorResource

@Composable
fun NotFoundCard(modifier: Modifier, text: String = "Not Found") {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        shape = CardShapeDefault,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Content goes here
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_oops),
                contentDescription = "Not Found",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                tint = Grey_500
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = Grey_500,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
