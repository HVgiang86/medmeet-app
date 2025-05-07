package com.huongmt.medmeet.shared.core.datasource.network.request

import com.huongmt.medmeet.shared.core.entity.Gender
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BookAppointment(
    val patientId: String? = null,
    @SerialName("paymentMethod")
    val payMethod: Long? = 1,
    val thanhToan: Int = 1,
    val clinicId: String? = null,
    val examinationDate: String? = null,
    val clinicScheduleId: String? = null,
    val examinationReason: String? = null,
    val medicalFee: Long? = null,
    val medicalServiceName: String? = null,
    val patientName: String? = null,
    val patientGender: Long? = Gender.MALE.value.toLong(),
    val patientPhoneNumber: String? = null,
    val patientEmail: String? = null,
    val patientDateOfBirth: String? = null,
    val patientProvince: String? = null,
    val patientDistrict: String? = null,
    val patientCommune: String? = null,
    val patientAddress: String? = null
)
