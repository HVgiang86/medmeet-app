package com.huongmt.medmeet.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.CalendarLocale
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huongmt.medmeet.constant.DD_MM_YYYY
import com.huongmt.medmeet.constant.YYYY_MM_DD
import com.huongmt.medmeet.theme.Grey_300
import com.huongmt.medmeet.theme.Grey_500
import com.huongmt.medmeet.theme.Grey_600
import com.huongmt.medmeet.utils.DateTime
import com.huongmt.medmeet.utils.ext.capitalizeWords
import com.huongmt.medmeet.utils.ext.getCurrentDate
import com.huongmt.medmeet.utils.ext.toLocalDate
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn

@Composable
fun ScrollableDatePicker(
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
    var selectedDate by remember { mutableStateOf(currentDate) }
    val firstDateOfMonth = currentDate.minus(DatePeriod(days = currentDate.dayOfMonth - 1))
    val daysInMonth = currentDate.month.length(isLeap(currentDate.year))
    val dates =
        remember {
            mutableStateListOf<LocalDate>().apply {
                for (i in 0 until daysInMonth) {
                    add(firstDateOfMonth.plus(DatePeriod(days = i)))
                }
            }
        }

    val listState = rememberLazyListState()

    // Scroll to the current date on initial composition
    LaunchedEffect(Unit) {
        val currentItemIndex = dates.indexOf(currentDate)
        if (currentItemIndex != -1) {
            val centerIndex = currentItemIndex - 2
            if (centerIndex >= 0 && centerIndex < dates.size) {
                listState.scrollToItem(centerIndex)
            }
        }

        onDateSelected(currentDate)
    }

    LazyRow(
        modifier = modifier.background(color = Color.Transparent), // Background color
        horizontalArrangement = Arrangement.spacedBy(8.dp), // Spacing between date items,
        state = listState,
    ) {
        items(dates) { date ->
            DateItem(
                date = date,
                isSelected = date == selectedDate,
                isCurrentDay = date == currentDate,
                onDateSelected = {
                    selectedDate = it
                    onDateSelected(it)
                },
            )
        }
    }
}

@Composable
fun DateItem(
    date: LocalDate,
    isSelected: Boolean,
    isCurrentDay: Boolean,
    onDateSelected: (LocalDate) -> Unit,
) {
    val boxColor =
        when {
            isSelected -> MaterialTheme.colorScheme.primary
            isSelected && isCurrentDay -> MaterialTheme.colorScheme.primary
            isCurrentDay -> Grey_500
            else -> Color.Transparent
        }

    val textColor =
        when {
            isSelected -> MaterialTheme.colorScheme.onPrimary
            isSelected && isCurrentDay -> MaterialTheme.colorScheme.onPrimary
            isCurrentDay -> MaterialTheme.colorScheme.onPrimary
            isCurrentDay -> Color.White
            else -> Grey_500
        }

    val dayOfWeekColor =
        when {
            isSelected -> MaterialTheme.colorScheme.onPrimary
            isSelected && isCurrentDay -> MaterialTheme.colorScheme.onPrimary
            isCurrentDay -> MaterialTheme.colorScheme.onPrimary
            else -> Grey_500
        }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            Modifier
                .width(48.dp) // Fixed width for each item
                .clickable { onDateSelected(date) }
                .background(shape = RoundedCornerShape(8.dp), color = boxColor),
    ) {
        // Date Circle
        Box(
            modifier =
                Modifier
                    .size(32.dp) // Fixed size for the circle
                    .clip(CircleShape)
                    .background(
                        color = Color.Transparent,
                    ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium, // Date text style
                color = dayOfWeekColor,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Day of the Week
        Text(
            text =
                date.dayOfWeek
                    .toString()
                    .substring(0, 3)
                    .capitalizeWords(),
            // Get first 3 letters
            style = MaterialTheme.typography.bodySmall, // Day of week text style
            color = textColor,
        )

        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
fun DatePicker(
    onDateSelected: (LocalDate) -> Unit,
    initialDate: LocalDate? = null,
    modifier: Modifier = Modifier,
    startDate: LocalDate? = null,
    endDate: LocalDate? = null,
    vi: Boolean = false,
    validateDate: (LocalDate) -> Boolean = { !it.isWeekend() },
) {
    val todayColor = Color(0xFFE0E0E0)
    val enableColor = MaterialTheme.colorScheme.primary

    val systemDate = Clock.System.todayIn(TimeZone.currentSystemDefault())

    var currentDate by remember { mutableStateOf(systemDate) }
    var selectedDate by remember { mutableStateOf(currentDate) }

    LaunchedEffect(Unit) {
        if (initialDate != null) {
            currentDate = initialDate
            selectedDate = initialDate
        } else {
            if (validateDate(systemDate)) {
                selectedDate = systemDate
                onDateSelected(systemDate)
            }
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier =
                modifier
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(16.dp),
        ) {
            // Month and Year Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val textMonth =
                    if (vi) {
                        "${currentDate.month.toVi()}/${currentDate.year}"
                    } else {
                        "${currentDate.month.name.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase() else it.toString()
                        }} ${currentDate.year}"
                    }.capitalizeWords()

                Text(
                    text = textMonth,
                    style =
                        TextStyle(
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
                        ),
                    modifier =
                        Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically)
                            .clickable {
                                // Jump to today
                                currentDate = systemDate

                                if (validateDate(systemDate)) {
                                    selectedDate = systemDate
                                    onDateSelected(systemDate)
                                }
                            },
                    textAlign = TextAlign.Start,
                )

                IconButton(onClick = {
                    currentDate = currentDate.previousMonth()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous Month",
                    )
                }

                IconButton(onClick = {
                    currentDate = currentDate.nextMonth()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next Month",
                    )
                }
            }

            // Days of the Week
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                val daysOfWeek =
                    if (vi) {
                        listOf("CN", "T2", "T3", "T4", "T5", "T6", "T7")
                    } else {
                        listOf(
                            "Sun",
                            "Mon",
                            "Tue",
                            "Wed",
                            "Thu",
                            "Fri",
                            "Sat",
                        )
                    }
                daysOfWeek.forEach { day ->
                    Text(
                        text = day,
                        style =
                            TextStyle(
                                color = Grey_600,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                            ),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                    )
                }
            }

            // Date Grid
            val daysInMonth = currentDate.month.length(isLeap(currentDate))
            val firstDayOfMonth = LocalDate(currentDate.year, currentDate.month, 1)
            val firstDayOfWeek = firstDayOfMonth.dayOfWeek
            val firstDayOfWeekNumber = firstDayOfWeek.isoDayNumber % 7 // Adjust for Sun = 0

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                // Empty cells for padding
                items(firstDayOfWeekNumber) {
                    Spacer(modifier = Modifier.size(40.dp))
                }

                // Date cells
                items(daysInMonth) { day ->
                    val date = LocalDate(currentDate.year, currentDate.month, day + 1)
                    val isSelected = date == selectedDate
                    val dayNumber = day + 1

                    val boxColor =
                        if (isSelected) {
                            enableColor
                        } else {
                            if (date.compare(systemDate) == 0) {
                                todayColor
                            } else {
                                Color.Transparent
                            }
                        }

                    val textColor =
                        if (isSelected) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            if (date.compare(systemDate) == 0) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                if (validateDate(date)) {
                                    Grey_500
                                } else {
                                    Grey_300
                                }
                            }
                        }

                    Box(
                        modifier =
                            Modifier
                                .size(40.dp)
                                .clip(MaterialTheme.shapes.small)
                                .background(
                                    boxColor,
                                    shape = MaterialTheme.shapes.small,
                                ).clickable {
                                    if (validateDate(date)) {
                                        currentDate = date
                                        selectedDate = date
                                        onDateSelected(date)
                                    }
                                },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = dayNumber.toString(),
                            style =
                                TextStyle(
                                    color = textColor,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                ),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

// Write extension function to validate that LocalDate not weekend
fun LocalDate.isWeekend(): Boolean = this.dayOfWeek.isoDayNumber % 7 == 6 || this.dayOfWeek.isoDayNumber % 7 == 0

/**
 * Returns a [LocalDate] in the previous month, on the same day if possible.
 * If the day exceeds the number of days in the previous month,
 * it returns the last day of the previous month.
 */
fun LocalDate.previousMonth(): LocalDate {
    val previousMonth = this.month.minus(1)
    val previousYear =
        if (previousMonth == Month.DECEMBER) {
            this.year - 1
        } else {
            this.year
        }
    val daysInPreviousMonth = previousMonth.length(isLeap(previousYear))

    val day =
        if (this.dayOfMonth > daysInPreviousMonth) {
            daysInPreviousMonth
        } else {
            this.dayOfMonth
        }

    return LocalDate(
        year = previousYear,
        month = previousMonth,
        dayOfMonth = day,
    )
}

fun LocalDate.compare(other: LocalDate): Int {
    if (this.year != other.year) {
        return this.year - other.year
    }
    if (this.month.number != other.month.number) {
        return this.month.number - other.month.number
    }
    return this.dayOfMonth - other.dayOfMonth
}

fun Month.toVi(): String = "Tháng $number"

/**
 * Returns a [LocalDate] in the next month, on the same day if possible.
 * If the day exceeds the number of days in the next month,
 * it returns the last day of the next month.
 */
fun LocalDate.nextMonth(): LocalDate {
    val nextMonth = this.month.plus(1)
    val nextYear = if (nextMonth == Month.JANUARY) this.year + 1 else this.year
    val daysInNextMonth = nextMonth.length(isLeap(nextYear))

    val day =
        if (this.dayOfMonth > daysInNextMonth) {
            daysInNextMonth
        } else {
            this.dayOfMonth
        }

    return LocalDate(
        year = nextYear,
        month = nextMonth,
        dayOfMonth = day,
    )
}

/**
 * Returns the number of days in the month for a given year.
 */
fun Month.length(isLeap: Boolean): Int =
    when (this) {
        Month.JANUARY,
        Month.MARCH,
        Month.MAY,
        Month.JULY,
        Month.AUGUST,
        Month.OCTOBER,
        Month.DECEMBER,
        -> 31

        Month.APRIL,
        Month.JUNE,
        Month.SEPTEMBER,
        Month.NOVEMBER,
        -> 30

        Month.FEBRUARY -> if (isLeap) 29 else 28
        else -> 30
    }

fun isLeap(date: LocalDate): Boolean {
    val year = date.year
    return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
}

fun isLeap(year: Int): Boolean = year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)

/**
 * Returns the next [Month].
 * If the current month is [Month.DECEMBER], it returns [Month.JANUARY].
 */
operator fun Month.plus(value: Int): Month {
    val newValue = (this.number + value) % 12
    return Month.entries.first { it.number == if (newValue == 0) 12 else newValue }
}

/**
 * Returns the previous [Month].
 * If the current month is [Month.JANUARY], it returns [Month.DECEMBER].
 */
operator fun Month.minus(value: Int): Month {
    val newValue = (this.number - value) % 12
    return Month.entries.first {
        val correctedValue = if (newValue <= 0) newValue + 12 else newValue
        it.number == if (correctedValue == 0) 12 else correctedValue
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PopupDatePicker(
    modifier: Modifier = Modifier,
    title: String? = "Chọn ngày",
    state: DatePickerState = rememberDatePickerState(),
    colors: DatePickerColors = DatePickerDefaults.colors(),
    onDateSelected: (LocalDate) -> Unit = {},
    onDismiss: () -> Unit = {},
    allowedDateValidator: (LocalDate) -> Boolean = { true },
    showModeToggle: Boolean = true,
) {
    val openDialog = remember { mutableStateOf(true) }

    if (openDialog.value) {
        val confirmEnabled = remember { derivedStateOf { state.selectedDateMillis != null } }
        DatePickerDialog(onDismissRequest = {
            openDialog.value = false
        }, confirmButton = {
            TextButton(onClick = {
                openDialog.value = false
                state.selectedDateMillis?.let {
                    onDateSelected(it.toLocalDate())
                }
            }, enabled = confirmEnabled.value) { Text("OK") }
        }, dismissButton = {
            TextButton(onClick = {
                openDialog.value = false
                onDismiss()
            }) { Text("Huỷ") }
        }, shape = RoundedCornerShape(8.dp)) {
            DatePicker(
                modifier = Modifier.padding(16.dp),
                state = state,
                colors = colors,
                showModeToggle = showModeToggle,
                title = {
                    title?.let {
                        Text(text = it, style = MaterialTheme.typography.titleMedium)
                    }
                },
                headline = {
                    val dateFormatter =
                        object : DatePickerFormatter {
                            override fun formatDate(
                                dateMillis: Long?,
                                locale: CalendarLocale,
                                forContentDescription: Boolean,
                            ): String {
                                if (dateMillis == null) {
                                    val current = getCurrentDate()
                                    return DateTime.getFormattedDate(
                                        current.toString(),
                                        YYYY_MM_DD,
                                        DD_MM_YYYY,
                                    ) ?: "Selected date"
                                }

                                val localDate = dateMillis.toLocalDate()
                                return DateTime.getFormattedDate(
                                    localDate.toString(),
                                    YYYY_MM_DD,
                                    DD_MM_YYYY,
                                ) ?: "Selected date"
                            }

                            override fun formatMonthYear(
                                monthMillis: Long?,
                                locale: CalendarLocale,
                            ): String {
                                if (monthMillis == null) {
                                    val current = getCurrentDate()
                                    return "${current.month}/${current.year}"
                                }

                                val localDate = monthMillis.toLocalDate()
                                return "${localDate.month}/${localDate.year}"
                            }
                        }
                    DatePickerDefaults.DatePickerHeadline(
                        state.selectedDateMillis,
                        displayMode = DisplayMode.Picker,
                        dateFormatter = dateFormatter,
                    )
                },
            )
        }
    }
}
