package com.huongmt.medmeet.ui.profile.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.composables.core.DragIndication
import com.composables.core.ModalBottomSheet
import com.composables.core.ModalBottomSheetState
import com.composables.core.Sheet
import com.composables.core.SheetDetent.Companion.FullyExpanded
import com.composables.core.SheetDetent.Companion.Hidden
import com.composables.core.rememberModalBottomSheetState
import com.huongmt.medmeet.component.BaseInputText

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SettingBottomSheet(
    state: ModalBottomSheetState? = null,
    onChangeRecommendationServer: (String) -> Unit,
    onChangeChatServer: (String) -> Unit,
) {
    val show = state ?: rememberModalBottomSheetState(
        initialDetent = FullyExpanded, detents = listOf(Hidden, FullyExpanded)
    )

    val scope = rememberCoroutineScope()


    ModalBottomSheet(state = show) {
        Sheet(
            modifier = Modifier
                .padding(top = 12.dp)
                .shadow(8.dp, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(Color.White)
                .widthIn(max = 640.dp)
                .fillMaxWidth()
                .imePadding(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp),
            ) {
                DragIndication(
                    modifier = Modifier
                        .padding(top = 22.dp)
                        .align(Alignment.CenterHorizontally)
                        .background(
                            Color.Black.copy(0.4f), RoundedCornerShape(100)
                        )
                        .width(32.dp)
                        .height(4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "App Settings",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.Black
                )
                Text(
                    "Recommendation Server URL",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )

                BaseInputText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .height(70.dp),
                    hint = "Recommendation Server URL",
                    description = "Recommendation Server URL",
                    onTextChanged = {
                        onChangeRecommendationServer(it)
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    onImeAction = {
                        onChangeRecommendationServer(it)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "AI Chat Server URL",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )

                BaseInputText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .height(70.dp),
                    hint = "AI Chat Server URL",
                    description = "AI Chat Server URL",
                    onTextChanged = {
                        onChangeChatServer(it)
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    onImeAction = {
                        onChangeChatServer(it)
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))


                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
