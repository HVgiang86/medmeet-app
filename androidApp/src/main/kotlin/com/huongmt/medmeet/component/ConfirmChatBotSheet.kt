package com.huongmt.medmeet.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.huongmt.medmeet.R
import com.huongmt.medmeet.theme.Grey_600

@Composable
fun ConfirmChatBotSheet(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    ConfirmBottomSheet(title = "Tư vấn với trợ lý MedMeet",
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        confirmButtonText = "Đồng ý",
        dismissButtonText = "Hủy bỏ",
        content = {
            Image(
                modifier = Modifier
                    .wrapContentWidth()
                    .height(120.dp).align(Alignment.CenterHorizontally),
                contentScale = ContentScale.Fit,
                contentDescription = null,
                painter = painterResource(id = R.drawable.png_medical_robot),
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = "Bạn có muốn tư vấn với trợ lý MedMeet không?",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Text(
                text = "Lưu ý: thông tin do medmeet gợi ý mang tính chất tham khảo, luôn kiểm tra lại tính đúng đắn và liên hệ bác sĩ nếu cần thiết.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = Grey_600,
            )
        })
}