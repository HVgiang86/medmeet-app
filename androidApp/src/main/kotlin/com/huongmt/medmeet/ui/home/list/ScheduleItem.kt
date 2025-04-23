package com.huongmt.medmeet.ui.home.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.huongmt.medmeet.R
import com.huongmt.medmeet.component.PrimaryButton
import com.huongmt.medmeet.component.SecondaryButton
import com.huongmt.medmeet.theme.CardShapeDefault
import com.huongmt.medmeet.theme.Grey_500

@Composable
fun ScheduleItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier.wrapContentHeight(),
        onClick = onClick,
        shape = CardShapeDefault,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        // Content goes here
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.Top) {
            Row(modifier = Modifier, verticalAlignment = Alignment.Top) {
                Column(
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .weight(1f)
                ) {
                    Text(
                        "#T250209202419KPWDF",
                        style = MaterialTheme.typography.bodySmall,
                        color = Grey_500,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        "Bệnh viện đa khoa Hồng Ngọc - Phúc Trường Minh ",
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                        modifier = Modifier.padding(top = 4.dp),
                        maxLines = 3,
                        minLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        "Khám dịch vụ khu C tầng 3 - Khám BHYT",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp),
                        maxLines = 3,
                        minLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                AsyncImage(
                    model = "https://i1.sndcdn.com/avatars-6CkzcHmzyH2x6Sd5-nhcPdg-t1080x1080.jpg",
                    modifier = Modifier
                        .size(88.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .align(Alignment.Top),
                    error = painterResource(R.drawable.png_stock1),
                    contentScale = ContentScale.Crop,
                    contentDescription = null
                )
            }

            Box(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = RoundedCornerShape(16.dp)
                    ), contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .wrapContentWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_clock),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        "14:00 - 16:00",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(8.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
            ) {
                SecondaryButton(onClick = {

                }, text = {
                    Text(
                        text = "Cancel", maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                }, modifier = Modifier.weight(1f), shape = CardShapeDefault)

                Spacer(modifier = Modifier.width(16.dp))

                PrimaryButton(onClick = {

                }, text = {
                    Text(
                        text = "Reschedule", maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                }, modifier = Modifier.weight(1f), shape = CardShapeDefault)
            }
        }
    }
}
