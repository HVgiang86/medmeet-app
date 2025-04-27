package com.huongmt.medmeet.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import com.huongmt.medmeet.R
import kotlin.math.roundToInt

@Composable
fun FullScreenImageDialog(
    showDialog: Boolean,
    imageUrl: String?, // Hoặc Int nếu là resource, hoặc Uri
    onDismiss: () -> Unit,
) {
    if (showDialog && imageUrl != null) {
        Dialog(
            onDismissRequest = onDismiss, properties = DialogProperties(
                usePlatformDefaultWidth = false, // Quan trọng để chiếm full màn hình
                dismissOnBackPress = true,
                dismissOnClickOutside = false // Chỉ đóng khi nhấn back hoặc nút close
            )
        ) {
            var scale by remember { mutableFloatStateOf(1f) }
            var rotation by remember { mutableFloatStateOf(0f) } // Thêm nếu muốn xoay ảnh
            var offsetX by remember { mutableFloatStateOf(0f) }
            var offsetY by remember { mutableFloatStateOf(0f) }
            val maxScale = 5f // Giới hạn phóng to tối đa
            val minScale = 0.5f // Giới hạn thu nhỏ tối thiểu

            // Lấy kích thước màn hình để giới hạn việc kéo ảnh ra ngoài
            val configuration = LocalConfiguration.current
            val screenWidthPx = with(LocalDensity.current) { configuration.screenWidthDp.dp.toPx() }
            val screenHeightPx =
                with(LocalDensity.current) { configuration.screenHeightDp.dp.toPx() }

            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f)) // Nền mờ
                .pointerInput(Unit) {
                    detectTransformGestures { centroid, pan, zoom, gestureRotation ->
                        val newScale = (scale * zoom).coerceIn(minScale, maxScale)

                        // Tính toán giới hạn offset dựa trên scale mới
                        // Công thức này cần được tinh chỉnh tùy theo kích thước ảnh thực tế và điểm neo (anchor point)
                        // Đây là một ví dụ đơn giản hóa
                        val maxX = (screenWidthPx * (newScale - 1)) / 2
                        val maxY = (screenHeightPx * (newScale - 1)) / 2

                        val newOffsetX = (offsetX + pan.x * scale).coerceIn(-maxX, maxX)
                        val newOffsetY = (offsetY + pan.y * scale).coerceIn(-maxY, maxY)

                        // Chỉ cập nhật nếu scale thay đổi đủ lớn hoặc có pan/rotate
                        if (newScale != scale || pan.x != 0f || pan.y != 0f || gestureRotation != 0f) {
                            scale = newScale
                            offsetX = newOffsetX
                            offsetY = newOffsetY
                            rotation += gestureRotation // Cộng dồn góc xoay
                        }
                    }
                }) {
                Image(painter = rememberAsyncImagePainter(
                    model = imageUrl,
                    // Placeholder và Error Drawables (Tùy chọn)
                    placeholder = painterResource(id = R.drawable.ic_oops), // Thay bằng placeholder của bạn
                    error = painterResource(id = R.drawable.ic_oops) // Thay bằng error placeholder
                ),
                    contentDescription = "Full Screen Image",
                    contentScale = ContentScale.Fit, // Fit để thấy toàn bộ ảnh ban đầu
                    modifier = Modifier
                        .align(Alignment.Center) // Căn giữa ảnh trong Box
                        .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                        .graphicsLayer(
                            scaleX = scale, scaleY = scale, rotationZ = rotation // Áp dụng góc xoay
                        )
                        .fillMaxSize() // Cho phép ảnh có thể lớn hơn màn hình khi zoom
                )

                // Nút đóng Dialog
                IconButton(
                    onClick = onDismiss, modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Dialog",
                        tint = Color.White
                    )
                }
            }
        }
    }
}