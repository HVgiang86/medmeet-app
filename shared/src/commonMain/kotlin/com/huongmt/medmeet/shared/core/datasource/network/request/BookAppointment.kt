package com.huongmt.medmeet.shared.core.datasource.network.request

import kotlinx.serialization.Serializable

@Serializable
data class BookAppointment(
    val patientId: String,
    val clinicId: String,
    val examinationDate: String,
    val clinicScheduleId: String,
    val examinationReason: String,
    val medicalFee: Int,
    val medicalServiceName: String,
    val paymentMethod: Int,
    val patientName: String,
    val patientGender: Int,
    val patientPhoneNumber: String,
    val patientEmail: String,
    val patientDateOfBirth: String,
    val patientProvince: String,
    val patientDistrict: String,
    val patientAddress: String
)
