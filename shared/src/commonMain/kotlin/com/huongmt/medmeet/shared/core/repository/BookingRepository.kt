package com.huongmt.medmeet.shared.core.repository

import com.huongmt.medmeet.shared.base.BaseRepository
import com.huongmt.medmeet.shared.core.datasource.network.APIs
import com.huongmt.medmeet.shared.core.entity.BookingDetails
import com.huongmt.medmeet.shared.core.entity.ClinicSchedule
import com.huongmt.medmeet.shared.core.entity.MedicalConsultationHistory
import com.huongmt.medmeet.shared.core.entity.MedicalService
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

interface BookingRepository {
    suspend fun getMedicalServices(clinicId: String): Flow<List<MedicalService>>
    suspend fun getAvailableTimeSlots(
        medicalServiceId: String,
        date: LocalDate
    ): Flow<List<ClinicSchedule>>

    suspend fun booking(bookingDetails: BookingDetails): Flow<String>

    suspend fun getBookingDetail(bookingId: String): Flow<MedicalConsultationHistory>
}

class MockBookingRepository(private val api: APIs) : BookingRepository, BaseRepository() {
    override suspend fun getMedicalServices(clinicId: String) = flowContext(mapper = {
        it.items?.map { service ->
            service.toMedicalService()
        } ?: emptyList()
    }) {
        api.getMedicalServices(clinicId)
    }

    override suspend fun getAvailableTimeSlots(medicalServiceId: String, date: LocalDate) =
        flowContext(mapper = {
            it.map { schedule ->
                schedule.toClinicSchedule()
            }
        }) {
            val dateTime = LocalDateTime(
                year = date.year,
                monthNumber = date.monthNumber,
                dayOfMonth = date.dayOfMonth,
                hour = 0,
                minute = 0,
                second = 0,
                nanosecond = 0
            )
            api.getMedicalServiceSchedule(medicalServiceId, dateTime.toString())
        }

    override suspend fun booking(bookingDetails: BookingDetails): Flow<String> =
        flowContext(mapper = {
            it.id
        }) {
            val request = bookingDetails.toBookingRequest()
            api.bookAppointment(request)
        }

    override suspend fun getBookingDetail(bookingId: String): Flow<MedicalConsultationHistory> = flowContext(mapper = {
        it.toMedicalHistory()
    }) {
        api.getMedicalHistoryDetail(bookingId)
    }
}
