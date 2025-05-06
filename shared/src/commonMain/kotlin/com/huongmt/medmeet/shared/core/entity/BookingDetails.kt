package com.huongmt.medmeet.shared.core.entity

import com.huongmt.medmeet.shared.core.datasource.network.request.BookAppointment
import kotlinx.datetime.LocalDate

data class BookingDetails(
    val patientInfo: PatientInfo?,
    val clinic: Clinic?,
    val medicalService: MedicalService?,
    val examinationDate: LocalDate?,
    val clinicSchedule: ClinicSchedule?,
    val paymentMethod: PaymentMethod?
) {
    fun toBookingRequest(): BookAppointment {
        return BookAppointment(
            patientId = patientInfo?.id,
            clinicId = clinic?.id,
            examinationDate = examinationDate.toString(),
            clinicScheduleId = clinicSchedule?.id,
            examinationReason = patientInfo?.examinationReason,
            medicalFee = medicalService?.currentPrice,
            medicalServiceName = medicalService?.name,
            paymentMethod = paymentMethod?.value ?: PaymentMethod.CASH.value,
            patientName = patientInfo?.name,
            patientGender = patientInfo?.gender?.value,
            patientPhoneNumber = patientInfo?.phoneNumber,
            patientEmail = patientInfo?.email,
            patientDateOfBirth = patientInfo?.dateOfBirth.toString(),
            patientProvince = patientInfo?.province,
            patientDistrict = patientInfo?.district,
            patientCommune = patientInfo?.commune,
            patientAddress = patientInfo?.address
        )
    }
}

data class PatientInfo(
    val id: String? = "",
    val name: String? = "",
    val phoneNumber: String? = "",
    val email: String? = "",
    val gender: Gender? = Gender.MALE,
    val dateOfBirth: LocalDate? = null,
    val province: String? = "",
    val district: String? = "",
    val commune: String? = "",
    val address: String? = "",
    val examinationReason: String? = ""
)
