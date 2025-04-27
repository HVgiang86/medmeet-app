package com.huongmt.medmeet.shared.core.datasource.network.response

import com.huongmt.medmeet.shared.core.entity.ClinicSchedule
import com.huongmt.medmeet.shared.utils.ext.nowDateTime
import com.huongmt.medmeet.shared.utils.ext.parseTimeToLocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClinicScheduleResponse(
    @SerialName("_id") val id: String,
    val clinicId: String,
    val startTime: String,
    val endTime: String
) {
    fun toClinicSchedule(): ClinicSchedule {
        val now = nowDateTime()

        val startTimeDate = startTime.parseTimeToLocalDateTime() ?: now
        val endTimeDate = endTime.parseTimeToLocalDateTime() ?: now

        return ClinicSchedule(
            startTime = startTimeDate,
            endTime = endTimeDate
        )
    }
}
