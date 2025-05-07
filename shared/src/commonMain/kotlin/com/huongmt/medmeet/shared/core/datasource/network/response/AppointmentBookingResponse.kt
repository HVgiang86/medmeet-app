package com.huongmt.medmeet.shared.core.datasource.network.response

import com.huongmt.medmeet.shared.core.entity.Gender
import com.huongmt.medmeet.shared.core.entity.MedicalRecordStatus
import com.huongmt.medmeet.shared.core.entity.PaymentMethod
import com.huongmt.medmeet.shared.core.entity.PaymentStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppointmentBookingResponse(
    @SerialName("_id")
    val id: String,
    val patientId: String? = null,
    val clinicId: String? = null,
    val examinationDate: String? = null,
    val clinicScheduleId: String? = null,
    val examinationReason: String? = null,
    val medicalFee: Long? = null,
    val medicalServiceName: String? = null,
    val paymentMethod: Int? = PaymentMethod.CASH.value,
    val paymentStatus: Int? = PaymentStatus.SUCCESS.value,
    val status: Int? = MedicalRecordStatus.PENDING.value,
    val patientName: String? = null,
    val patientGender: Int? = Gender.MALE.value,
    val patientPhoneNumber: String? = null,
    val patientEmail: String? = null,
    val patientDateOfBirth: String? = null,
    val patientProvince: String? = null,
    val patientDistrict: String? = null,
    val patientCommune: String? = null,
    val patientAddress: String? = null,
    val code: String,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
