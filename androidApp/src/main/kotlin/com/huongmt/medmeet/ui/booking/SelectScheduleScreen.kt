package com.huongmt.medmeet.ui.booking

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.huongmt.medmeet.component.DatePicker
import com.huongmt.medmeet.component.PrimaryButton
import com.huongmt.medmeet.component.SecondaryButton
import com.huongmt.medmeet.shared.app.BookingAction
import com.huongmt.medmeet.shared.app.BookingStep
import com.huongmt.medmeet.shared.app.BookingStepType
import com.huongmt.medmeet.shared.app.BookingStore
import com.huongmt.medmeet.shared.core.entity.ClinicSchedule
import com.huongmt.medmeet.shared.utils.ext.nowDate
import com.huongmt.medmeet.utils.ext.toHM

@Composable
fun SelectScheduleScreen(
    store: BookingStore,
    state: BookingStep.SelectSchedule,
) {
    SelectScheduleContent(
        store = store,
        state = state,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun SelectScheduleContent(
    store: BookingStore,
    state: BookingStep.SelectSchedule,
) {
    LaunchedEffect(Unit) {
        store.sendAction(BookingAction.SelectDate(nowDate()))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(bottom = 60.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                text = "Chọn lịch khám",
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Chọn ngày",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            DatePicker(onDateSelected = {
                store.sendAction(BookingAction.SelectDate(date = it))
            })

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Chọn giờ",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            FlowRow(
                horizontalArrangement = Arrangement.Start,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.verticalScroll(
                    rememberScrollState()
                )
            ) {

                val list = state.availableSchedule

                if (list != null) {
                    repeat(list.size) { index ->
                        val schedule = list[index]

                        HourCard(schedule = schedule,
                            isSelected = schedule.id == state.clinicSchedule?.id,
                            onClick = { pressed ->
                                store.sendAction(
                                    BookingAction.SelectSchedule(
                                        schedule = pressed
                                    )
                                )
                            })
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.BottomCenter)
                .padding(24.dp)
        ) {
            SecondaryButton(modifier = Modifier.weight(1f), onClick = {
                store.sendAction(
                    BookingAction.PreviousStep(
                        currentStep = BookingStepType.SELECT_SCHEDULE,
                        destinationStep = BookingStepType.INPUT_PATIENT_INFO
                    )
                )
            }, text = {
                Text(
                    text = "Quay lại", style = MaterialTheme.typography.labelLarge, color = Color.Black
                )
            })

            Spacer(modifier = Modifier.width(16.dp))

            PrimaryButton(modifier = Modifier.weight(1f), onClick = {
                store.sendAction(
                    BookingAction.NextStep(
                        currentStep = BookingStepType.SELECT_SCHEDULE,
                        destinationStep = BookingStepType.CONFIRMATION
                    )
                )
            }, text = {
                Text(
                    text = "Tiếp theo", style = MaterialTheme.typography.labelLarge, color = Color.White
                )
            })
        }
    }
}

@Composable
fun HourCard(
    schedule: ClinicSchedule,
    isSelected: Boolean = false,
    onClick: (ClinicSchedule) -> Unit = {},
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .wrapContentSize()
            .clickable {
                onClick(schedule)
            },
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainer,
            contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
        ),
    ) {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
//                text = "${schedule.startTime.toHM()} - ${schedule.endTime.toHM()}",
                text = schedule.startTime.toHM(),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.wrapContentSize()
            )
        }
    }
}
